package ec.edu.espe.finvory.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import ec.edu.espe.finvory.mongo.MongoDBConnection;
import ec.edu.espe.finvory.mongo.MongoDataExporter;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.Document;
import java.util.HashSet;
import java.util.Set;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * Estrategia de sincronización: - Si hay conexión: la nube es la fuente de
 * verdad. Se carga nube y se sobreescribe local. - Si no hay conexión: se
 * trabaja local y se marca "pendiente". - Al reconectar (siguiente load con
 * conexión): si hay pendiente, se publica local a nube (reemplazo total) y
 * luego se recarga nube para garantizar consistencia.
 */
public class Database {

    private static final String ROOT_DATA_FOLDER = "data";
    private static final String UTILS_FOLDER = "utils";
    private static final String USERS_FILE = UTILS_FOLDER + File.separator + "users.json";
    private static final String DELIMITER = ";";
    private static final String PENDING_FILE = ".pending_offline_upload";

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new TypeAdapter<LocalDate>() {
                @Override
                public void write(JsonWriter out, LocalDate value) throws IOException {
                    out.value(value == null ? null : value.toString());
                }

                @Override
                public LocalDate read(JsonReader in) throws IOException {
                    String s = in.nextString();
                    return (s == null || s.isBlank()) ? null : LocalDate.parse(s.trim());
                }
            })
            .setPrettyPrinting()
            .create();

    /**
     * Punto único de entrada para UI/controladores.
     */
    public FinvoryData loadCompanyData(String companyUsername) {
        System.out.println("--- SINCRONIZACIÓN DE DATOS ---");

        boolean online = MongoDBConnection.isOnline();
        if (online) {
            if (hasPendingOffline(companyUsername)) {
                System.out.println("Detectado pendiente offline. Publicando local a la nube...");
                boolean published = publishLocalToCloud(companyUsername);
                if (published) {
                    clearPendingOffline(companyUsername);
                    System.out.println("Publicación offline completada.");
                } else {
                    System.out.println("No se pudo publicar local. Se forzará carga desde nube.");
                }
            }

            FinvoryData cloudData = loadDataFromCloud(companyUsername);
            if (cloudData != null) {
                System.out.println("Datos descargados de MongoDB Atlas.");
                System.out.println("    -> Inventarios: " + safeSize(cloudData.getInventories()));
                System.out.println("    -> Productos: " + safeSize(cloudData.getProducts()));
                System.out.println("    -> Facturas: " + safeSize(cloudData.getInvoices()));

                saveCompanyDataLocalOnly(cloudData, companyUsername);
                return cloudData;
            }

            System.out.println("No se pudo cargar de la nube. Usando datos locales.");
            return loadCompanyDataLocal(companyUsername);
        }

        System.out.println("Sin conexión. Usando datos locales.");
        return loadCompanyDataLocal(companyUsername);
    }

    public void saveCompanyData(FinvoryData data, String companyUsername) {
        saveCompanyDataLocalOnly(data, companyUsername);

        if (!MongoDBConnection.isOnline()) {
            markPendingOffline(companyUsername);
            return;
        }

        boolean exported = exportCompanyToCloud(data, companyUsername);
        if (!exported) {
            markPendingOffline(companyUsername);
        } else {
            clearPendingOffline(companyUsername);
        }
    }

    public SystemUsers loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            return new SystemUsers();
        }

        try (Reader reader = new FileReader(file)) {
            SystemUsers users = gson.fromJson(reader, SystemUsers.class);
            return users != null ? users : new SystemUsers();
        } catch (IOException e) {
            System.err.println("Error leyendo usuarios: " + e.getMessage());
            return new SystemUsers();
        }
    }

    public void saveUsers(SystemUsers users) {
        File folder = new File(UTILS_FOLDER);
        if (!folder.exists() && !folder.mkdirs()) {
            return;
        }

        try (Writer writer = new FileWriter(USERS_FILE)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            System.err.println("Error al guardar usuarios en JSON: " + e.getMessage());
        }
    }

    public boolean exportToCsv(String title, HashMap<String, ? extends Object> data, String companyUsername) {
        String fileName = ROOT_DATA_FOLDER + File.separator + companyUsername + File.separator + title + ".csv";
        try {
            File file = new File(fileName);
            new File(file.getParent()).mkdirs();

            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                pw.println("Key" + DELIMITER + "Value");
                for (Map.Entry<String, ? extends Object> e : data.entrySet()) {
                    pw.println(e.getKey() + DELIMITER + formatValue(e.getValue()));
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void markPendingOffline(String companyUsername) {
        try {
            String folder = companyFolder(companyUsername);
            new File(folder).mkdirs();
            File flag = new File(folder + File.separator + PENDING_FILE);
            try (Writer w = new FileWriter(flag)) {
                w.write("pending");
            }
        } catch (IOException ignored) {
        }
    }

    private boolean hasPendingOffline(String companyUsername) {
        File flag = new File(companyFolder(companyUsername) + File.separator + PENDING_FILE);
        return flag.exists();
    }

    private void clearPendingOffline(String companyUsername) {
        File flag = new File(companyFolder(companyUsername) + File.separator + PENDING_FILE);
        if (flag.exists()) {

            flag.delete();
        }
    }

    private FinvoryData loadDataFromCloud(String username) {
        FinvoryData data = new FinvoryData();
        try {
            
            loadCompanyInfoFromCloud(data, username);
            loadConfigurationsFromCloud(data, username);
            loadProductsFromCloud(data, username);
            loadCustomersFromCloud(data, username);
            loadSuppliersFromCloud(data, username);
            loadInventoriesFromCloud(data, username);
            loadObsoleteInventoryFromCloud(data, username);
            loadInvoicesFromCloud(data, username);
            loadReturnsFromCloud(data, username);

            if (data != null) {
                dedupeCustomers(data);
                dedupeSuppliers(data);
                dedupeProducts(data);
                dedupeInventories(data);
                dedupeInvoices(data);
                dedupeReturns(data);
            }

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error crítico en sincronización: " + e.getMessage());
            return null;
        }
    }

    private void loadConfigurationsFromCloud(FinvoryData data, String username) {
        MongoCollection<Document> col = MongoDBConnection.getCollection("configurations");
        if (col == null) {
            return;
        }

        Document doc = col.find(Filters.eq("companyUsername", username)).first();
        if (doc == null) {
            return;
        }

        data.setTaxRate(BigDecimal.valueOf(getDoubleSafe(doc, "taxRate",
                data.getTaxRate() != null ? data.getTaxRate().doubleValue() : 0.0)));
        data.setProfitPercentage(BigDecimal.valueOf(getDoubleSafe(doc, "profitPercentage",
                data.getProfitPercentage() != null ? data.getProfitPercentage().doubleValue() : 0.0)));
        data.setDiscountStandard(BigDecimal.valueOf(getDoubleSafe(doc, "discountStandard",
                data.getDiscountStandard() != null ? data.getDiscountStandard().doubleValue() : 0.0)));
        data.setDiscountPremium(BigDecimal.valueOf(getDoubleSafe(doc, "discountPremium",
                data.getDiscountPremium() != null ? data.getDiscountPremium().doubleValue() : 0.0)));
        data.setDiscountVip(BigDecimal.valueOf(getDoubleSafe(doc, "discountVip",
                data.getDiscountVip() != null ? data.getDiscountVip().doubleValue() : 0.0)));
    }

    private void loadProductsFromCloud(FinvoryData data, String username) {
        MongoCollection<Document> col = MongoDBConnection.getCollection("products");
        if (col == null) {
            return;
        }

        for (Document doc : col.find(Filters.eq("companyUsername", username))) {
            String productId = doc.getString("productId");
            String name = doc.getString("name");
            String description = doc.getString("description");
            String barcode = doc.getString("barcode");
            String supplierId = doc.getString("supplierId");
            BigDecimal cost = BigDecimal.valueOf(getDoubleSafe(doc, "baseCostPrice", 0.0));

            data.addProduct(new Product(productId, name, description, barcode, cost, supplierId));
        }
    }

    private void loadCustomersFromCloud(FinvoryData data, String username) {
        MongoCollection<Document> col = MongoDBConnection.getCollection("customers");
        if (col == null) {
            return;
        }

        for (Document doc : col.find(Filters.eq("companyUsername", username))) {
            data.addCustomer(new Customer(
                    doc.getString("name"),
                    doc.getString("identification"),
                    doc.getString("phone"),
                    doc.getString("email"),
                    doc.getString("clientType")
            ));
        }
    }

    private void loadSuppliersFromCloud(FinvoryData data, String username) {
        MongoCollection<Document> col = MongoDBConnection.getCollection("suppliers");
        if (col == null) {
            return;
        }

        for (Document doc : col.find(Filters.eq("companyUsername", username))) {
            Supplier s = new Supplier(
                    doc.getString("fullName"),
                    doc.getString("id1"),
                    doc.getString("phone"),
                    doc.getString("email"),
                    doc.getString("description")
            );
            s.setId2(doc.getString("id2"));
            data.addSupplier(s);
        }
    }

    private void loadInventoriesFromCloud(FinvoryData data, String username) {
        MongoCollection<Document> col = MongoDBConnection.getCollection("inventories");
        if (col == null) {
            return;
        }

        for (Document doc : col.find(Filters.eq("companyUsername", username))) {
            String name = doc.getString("name");
            if (norm(name).isEmpty()) {
                continue;
            }

            Address address = toAddress((Document) doc.get("address"));
            Inventory inventory = new Inventory(name, address);

            Object stockRaw = doc.get("productStock");
            if (stockRaw instanceof Document stockDoc) {
                for (Map.Entry<String, Object> entry : stockDoc.entrySet()) {
                    int qty = toInt(entry.getValue(), 0);
                    if (qty > 0) {
                        inventory.setStock(entry.getKey(), qty);
                    }
                }
            }

            data.addInventory(inventory);
        }
    }

    private void loadObsoleteInventoryFromCloud(FinvoryData data, String username) {
        if (data == null || data.getObsoleteInventory() == null) {
            return;
        }

        MongoCollection<Document> col = MongoDBConnection.getCollection("obsolete_inventory");
        if (col == null) {
            return;
        }

        Document doc = col.find(Filters.eq("companyUsername", username)).first();
        if (doc == null) {
            return;
        }

        InventoryOfObsolete obs = data.getObsoleteInventory();

        Address address = toAddress((Document) doc.get("address"));
        if (address != null) {
            obs.setAddress(address);
        }

        Map<String, Integer> map = obs.getProductStock();
        map.clear();

        Object stockRaw = doc.get("productStock");
        if (stockRaw instanceof Document stockDoc) {
            for (Map.Entry<String, Object> entry : stockDoc.entrySet()) {
                int qty = toInt(entry.getValue(), 0);
                if (qty > 0) {
                    obs.setStock(entry.getKey(), qty);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void loadInvoicesFromCloud(FinvoryData data, String username) {
        MongoCollection<Document> col = MongoDBConnection.getCollection("invoices");
        if (col == null) {
            return;
        }

        for (Document doc : col.find(Filters.eq("companyUsername", username))) {
            String invoiceId = doc.getString("invoiceId");
            LocalDate date = toLocalDate(doc.getDate("date"));

            Customer customer = buildCustomerFromDoc((Document) doc.get("customer"));

            ArrayList<InvoiceLineSim> lines = new ArrayList<>();
            List<Document> linesDoc = (List<Document>) doc.get("lines");
            if (linesDoc != null) {
                for (Document l : linesDoc) {
                    int qty = toInt(l.get("quantity"), 0);
                    double price = getDoubleSafe(l, "priceApplied", getDoubleSafe(l, "price", 0.0));
                    lines.add(new InvoiceLineSim(
                            l.getString("productId"),
                            l.getString("productName"),
                            qty,
                            BigDecimal.valueOf(price)
                    ));
                }
            }

            BigDecimal taxRate = BigDecimal.valueOf(getDoubleSafe(doc, "tax", getDoubleSafe(doc, "taxRate", 0.0)));
            InvoiceSim invoice = new InvoiceSim(invoiceId, date, date, customer, lines, taxRate, BigDecimal.ZERO);
            invoice.complete();
            data.addInvoice(invoice);
        }
    }

    private FinvoryData loadCompanyDataLocal(String companyUsername) {
        String folder = companyFolder(companyUsername);

        FinvoryData localData = loadJson(folder);

        // Limpieza
        dedupeCustomers(localData);
        dedupeSuppliers(localData);
        dedupeProducts(localData);

        // Merge desde CSV
        mergeCustomersFromCsv(localData, folder);
        mergeSuppliersFromCsv(localData, folder);

        // Limpieza final
        dedupeCustomers(localData);
        dedupeSuppliers(localData);
        dedupeProducts(localData);

        return localData;
    }

    private void saveCompanyDataLocalOnly(FinvoryData data, String companyUsername) {
        String folder = companyFolder(companyUsername);
        new File(folder).mkdirs();
        saveJson(data, folder);
        saveCustomersCsv(data.getCustomers(), folder);
        saveSuppliersCsv(data.getSuppliers(), folder);
    }

    private boolean publishLocalToCloud(String companyUsername) {
        if (!MongoDBConnection.isOnline()) {
            return false;
        }

        try {
            FinvoryData local = loadCompanyDataLocal(companyUsername);
            return exportCompanyToCloud(local, companyUsername);
        } catch (Exception e) {
            System.err.println("Error publicando local -> cloud: " + e.getMessage());
            return false;
        }
    }

    private boolean exportCompanyToCloud(FinvoryData data, String companyUsername) {
        try {
            if (MongoDBConnection.getDatabase() == null) {
                return false;
            }
            MongoDataExporter.exportCompanyData(companyUsername, data, MongoDBConnection.getDatabase());
            return true;
        } catch (Exception e) {
            System.err.println("Error exportando a cloud: " + e.getMessage());
            return false;
        }
    }

    private void saveJson(FinvoryData data, String folder) {
        try (Writer writer = new FileWriter(folder + File.separator + "finvory_database.json")) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            System.err.println("Error guardando JSON: " + e.getMessage());
        }
    }

    private FinvoryData loadJson(String folder) {
        File file = new File(folder + File.separator + "finvory_database.json");
        if (!file.exists()) {
            return new FinvoryData();
        }

        try (Reader reader = new FileReader(file)) {
            FinvoryData data = gson.fromJson(reader, FinvoryData.class);
            return data != null ? data : new FinvoryData();
        } catch (IOException e) {
            System.err.println("Error leyendo JSON local: " + e.getMessage());
            return new FinvoryData();
        }
    }

    private ArrayList<Customer> loadCustomersCsv(String folder) {
        ArrayList<Customer> list = new ArrayList<>();
        File file = new File(folder + File.separator + "clients.csv");
        if (!file.exists()) {
            return list;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(DELIMITER);
                if (parts.length == 5) {
                    list.add(new Customer(
                            parts[1].trim(),
                            parts[0].trim(),
                            parts[2].trim(),
                            parts[3].trim(),
                            parts[4].trim()
                    ));
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo CSV de clientes: " + e.getMessage());
        }
        return list;
    }

    private void saveCustomersCsv(List<Customer> customers, String folder) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(folder + File.separator + "clients.csv"))) {
            pw.println("Identification" + DELIMITER + "FullName" + DELIMITER + "Phone" + DELIMITER + "Email" + DELIMITER + "ClientType");
            if (customers == null) {
                return;
            }
            for (Customer c : customers) {
                if (c == null) {
                    continue;
                }
                pw.println(
                        safe(c.getIdentification()) + DELIMITER
                        + safe(c.getName()) + DELIMITER
                        + safe(c.getPhone()) + DELIMITER
                        + safe(c.getEmail()) + DELIMITER
                        + safe(c.getClientType())
                );
            }
        } catch (IOException e) {
            System.err.println("Error guardando CSV de clientes: " + e.getMessage());
        }
    }

    private ArrayList<Supplier> loadSuppliersCsv(String folder) {
        ArrayList<Supplier> list = new ArrayList<>();
        File file = new File(folder + File.separator + "suppliers.csv");
        if (!file.exists()) {
            return list;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(DELIMITER);
                if (parts.length == 6) {
                    Supplier s = new Supplier(
                            parts[2].trim(),
                            parts[0].trim(),
                            parts[3].trim(),
                            parts[4].trim(),
                            parts[5].trim()
                    );
                    s.setId2(parts[1].trim());
                    list.add(s);
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo CSV de proveedores: " + e.getMessage());
        }
        return list;
    }

    private void saveSuppliersCsv(List<Supplier> suppliers, String folder) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(folder + File.separator + "suppliers.csv"))) {
            pw.println("ID1" + DELIMITER + "ID2" + DELIMITER + "FullName" + DELIMITER + "Phone" + DELIMITER + "Email" + DELIMITER + "Description");
            if (suppliers == null) {
                return;
            }

            for (Supplier s : suppliers) {
                if (s == null) {
                    continue;
                }
                pw.println(
                        safe(s.getId1()) + DELIMITER
                        + safe(s.getId2()) + DELIMITER
                        + safe(s.getFullName()) + DELIMITER
                        + safe(s.getPhone()) + DELIMITER
                        + safe(s.getEmail()) + DELIMITER
                        + safe(s.getDescription())
                );
            }
        } catch (IOException e) {
            System.err.println("Error guardando CSV de proveedores: " + e.getMessage());
        }
    }

    private void mergeCustomersFromCsv(FinvoryData localData, String folder) {
        for (Customer customer : loadCustomersCsv(folder)) {
            if (!existsCustomer(localData.getCustomers(), customer.getIdentification())) {
                localData.addCustomer(customer);
            }
        }
    }

    private void mergeSuppliersFromCsv(FinvoryData localData, String folder) {
        for (Supplier supplier : loadSuppliersCsv(folder)) {
            if (!existsSupplier(localData.getSuppliers(), supplier.getId1())) {
                localData.addSupplier(supplier);
            }
        }
    }

    private boolean existsCustomer(List<Customer> customers, String identification) {
        String id = norm(identification);
        if (id.isEmpty()) {
            return false;
        }
        if (customers == null) {
            return false;
        }
        for (Customer customer : customers) {
            if (customer != null && norm(customer.getIdentification()).equals(id)) {
                return true;
            }
        }
        return false;
    }

    private boolean existsSupplier(List<Supplier> suppliers, String id1) {
        String id = norm(id1);
        if (id.isEmpty()) {
            return false;
        }
        if (suppliers == null) {
            return false;
        }
        for (Supplier supplier : suppliers) {
            if (supplier != null && norm(supplier.getId1()).equals(id)) {
                return true;
            }
        }
        return false;
    }

    private void dedupeCustomers(FinvoryData data) {
        if (data == null || data.getCustomers() == null) {
            return;
        }

        Map<String, Customer> byId = new LinkedHashMap<>();
        for (Customer customer : new ArrayList<>(data.getCustomers())) {
            if (customer == null) {
                continue;
            }
            String id = norm(customer.getIdentification());
            byId.putIfAbsent(id.isEmpty() ? UUID.randomUUID().toString() : id, customer);
        }

        data.getCustomers().clear();
        data.getCustomers().addAll(byId.values());
    }

    private void dedupeSuppliers(FinvoryData data) {
        if (data == null || data.getSuppliers() == null) {
            return;
        }

        Map<String, Supplier> byId = new LinkedHashMap<>();
        for (Supplier supplier : new ArrayList<>(data.getSuppliers())) {
            if (supplier == null) {
                continue;
            }
            String id = norm(supplier.getId1());
            byId.putIfAbsent(id.isEmpty() ? UUID.randomUUID().toString() : id, supplier);
        }

        data.getSuppliers().clear();
        data.getSuppliers().addAll(byId.values());
    }

    private void dedupeProducts(FinvoryData data) {
        if (data == null || data.getProducts() == null) {
            return;
        }

        Map<String, Product> byId = new LinkedHashMap<>();
        for (Product product : new ArrayList<>(data.getProducts())) {
            if (product == null) {
                continue;
            }
            String id = norm(product.getId());
            byId.putIfAbsent(id.isEmpty() ? UUID.randomUUID().toString() : id, product);
        }

        data.getProducts().clear();
        data.getProducts().addAll(byId.values());
    }

    private void dedupeInventories(FinvoryData data) {
        if (data == null || data.getInventories() == null) {
            return;
        }

        Map<String, Inventory> byName = new LinkedHashMap<>();
        for (Inventory inventory : new ArrayList<>(data.getInventories())) {
            if (inventory == null) {
                continue;
            }
            String key = norm(inventory.getName()).toLowerCase(Locale.ROOT);
            byName.putIfAbsent(key.isEmpty() ? UUID.randomUUID().toString() : key, inventory);
        }

        data.getInventories().clear();
        data.getInventories().addAll(byName.values());
    }

    private void dedupeInvoices(FinvoryData data) {
        if (data == null || data.getInvoices() == null) {
            return;
        }

        Map<String, InvoiceSim> byId = new LinkedHashMap<>();
        for (InvoiceSim invoiceSim : new ArrayList<>(data.getInvoices())) {
            if (invoiceSim == null) {
                continue;
            }
            String key = norm(invoiceSim.getId());
            byId.putIfAbsent(key.isEmpty() ? UUID.randomUUID().toString() : key, invoiceSim);
        }

        data.getInvoices().clear();
        data.getInvoices().addAll(byId.values());
    }

    private Customer buildCustomerFromDoc(Document custDoc) {
        if (custDoc == null) {
            return new Customer("Consumidor Final", "9999999999", "", "", "Final");
        }
        return new Customer(
                custDoc.getString("name"),
                custDoc.getString("identification"),
                custDoc.getString("phone"),
                custDoc.getString("email"),
                custDoc.getString("clientType")
        );
    }

    private Address toAddress(Document document) {
        if (document == null) {
            return null;
        }
        return new Address(
                document.getString("country"),
                document.getString("city"),
                document.getString("street")
        );
    }

    private static String formatValue(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof BigDecimal bd) {
            return String.format(Locale.US, "%.2f", bd);
        }
        if (value instanceof Float floatnumber) {
            return String.format(Locale.US, "%.2f", floatnumber);
        }
        return String.valueOf(value);
    }

    private static LocalDate toLocalDate(Date date) {
        if (date == null) {
            return LocalDate.now();
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static String norm(String string) {
        return string == null ? "" : string.trim();
    }

    private static String safe(String string) {
        return string == null ? "" : string;
    }

    private String companyFolder(String companyUsername) {
        return ROOT_DATA_FOLDER + File.separator + companyUsername;
    }

    private static int safeSize(List<?> list) {
        return list == null ? 0 : list.size();
    }

    private static double getDoubleSafe(Document doc, String key, double def) {
        if (doc == null) {
            return def;
        }
        Object v = doc.get(key);
        if (v == null) {
            return def;
        }
        if (v instanceof Number number) {
            return number.doubleValue();
        }
        if (v instanceof String string) {
            try {
                return Double.parseDouble(string.trim());
            } catch (Exception ignored) {
                return def;
            }
        }
        return def;
    }

    private static int toInt(Object value, int defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value instanceof String string) {
            try {
                String trimmed = string.trim();
                if (trimmed.contains(".")) {
                    return (int) Math.round(Double.parseDouble(trimmed));
                }
                return Integer.parseInt(trimmed);
            } catch (Exception ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private void loadReturnsFromCloud(FinvoryData data, String username) {
        try {
            MongoDatabase db = MongoDBConnection.getDatabase();
            if (db == null) {
                return;
            }

            MongoCollection<Document> returnsCol = db.getCollection("returns");
            if (returnsCol == null) {
                return;
            }

            for (Document doc : returnsCol.find(com.mongodb.client.model.Filters.eq("companyUsername", username))) {
                String productId = doc.getString("productId");
                if (productId == null) {
                    continue;
                }
                Product product = null;
                if (data.getProducts() != null) {
                    for (Product products : data.getProducts()) {
                        if (products != null && products.getId() != null && products.getId().equalsIgnoreCase(productId)) {
                            product = products;
                            break;
                        }
                    }
                }

                if (product != null) {
                    int quantity = toInt(doc.get("quantity"), 0);
                    String reason = doc.getString("reason");
                    ReturnedProduct rp = new ReturnedProduct(product, quantity, reason);
                    data.addReturn(rp);
                }
            }
        } catch (Exception e) {
            System.err.println("Error no crítico cargando devoluciones: " + e.getMessage());
        }
    }

    private void dedupeReturns(FinvoryData data) {
        if (data == null || data.getReturns() == null) {
            return;
        }

        List<ReturnedProduct> currentReturns = data.getReturns();
        if (currentReturns.isEmpty()) {
            return;
        }

        List<ReturnedProduct> unique = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (ReturnedProduct returnedProduct : currentReturns) {
            if (returnedProduct != null && returnedProduct.getProduct() != null) {
                String key = returnedProduct.getProduct().getId() + "-" + returnedProduct.getQuantity() + "-" + returnedProduct.getReason();
                if (seen.add(key)) {
                    unique.add(returnedProduct);
                }
            }
        }

        try {
            currentReturns.clear();
            currentReturns.addAll(unique);
        } catch (UnsupportedOperationException e) {

            System.err.println("Error: La lista de retornos es inmodificable.");
        }
    }

    private void loadCompanyInfoFromCloud(FinvoryData data, String username) {
        MongoCollection<Document> col = MongoDBConnection.getCollection("companies");
        if (col == null) {
            return;
        }

        Document doc = col.find(Filters.eq("companyUsername", username)).first();
        if (doc == null) {
            return;
        }

        CompanyAccount company = data.getCompanyInfo();
        if (company == null) {
            company = new CompanyAccount();
            data.setCompanyInfo(company);
        }

        company.setName(doc.getString("name"));
        company.setPhone(doc.getString("phone"));
        company.setEmail(doc.getString("email"));
        company.setLogoPath(doc.getString("logoPath"));
    }
}
