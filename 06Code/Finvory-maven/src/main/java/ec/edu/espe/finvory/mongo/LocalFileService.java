package ec.edu.espe.finvory.mongo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ec.edu.espe.finvory.model.Customer;
import ec.edu.espe.finvory.model.SystemUsers;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import ec.edu.espe.finvory.model.FinvoryData;
import ec.edu.espe.finvory.model.Inventory;
import ec.edu.espe.finvory.model.InvoiceSim;
import ec.edu.espe.finvory.model.Product;
import ec.edu.espe.finvory.model.Supplier;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author Mathews Pastor, The POOwer Rangers Of Programming
 */
public class LocalFileService {

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
                    String string = in.nextString();
                    return (string == null || string.isBlank()) ? null : LocalDate.parse(string.trim());
                }
            })
            .setPrettyPrinting()
            .create();

    public LocalFileService() {
        checkAndCreateDataFolder();
    }

    private void checkAndCreateDataFolder() {
        File root = new File(ROOT_DATA_FOLDER);
        if (!root.exists()) {
            root.mkdir();
        }
        File utils = new File(ROOT_DATA_FOLDER + File.separator + UTILS_FOLDER);
        if (!utils.exists()) {
            utils.mkdirs();
        }
    }

    public SystemUsers loadUsers() {
        File file = new File(ROOT_DATA_FOLDER + File.separator + USERS_FILE);

        if (!file.exists()) {
            System.err.println("ERROR CRÍTICO: No se encuentra el archivo de usuarios.");
            return new SystemUsers();
        }

        try (Reader reader = new FileReader(file)) {
            SystemUsers users = gson.fromJson(reader, SystemUsers.class);
            if (users == null) {
                System.err.println("El archivo existe pero está vacío o corrupto.");
                return new SystemUsers();
            }
            return users;
        } catch (IOException e) {
            System.err.println("Excepción leyendo usuarios: " + e.getMessage());
            return new SystemUsers();
        }
    }

    public void saveUsers(SystemUsers users) {
        try (Writer writer = new FileWriter(ROOT_DATA_FOLDER + File.separator + USERS_FILE)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            System.err.println("Error guardando usuarios: " + e.getMessage());
        }
    }

    public FinvoryData loadCompanyDataLocal(String companyUsername) {
        String folder = companyFolder(companyUsername);
        FinvoryData localData = loadJson(folder);

        mergeCustomersFromCsv(localData, folder);
        mergeSuppliersFromCsv(localData, folder);

        dedupeCustomers(localData);
        dedupeSuppliers(localData);
        dedupeProducts(localData);
        dedupeInventories(localData);
        dedupeInvoices(localData);

        return localData;
    }

    public void saveCompanyDataLocal(FinvoryData data, String companyUsername) {
        String folder = companyFolder(companyUsername);
        new File(folder).mkdirs();
        saveJson(data, folder);
        saveCustomersCsv(data.getCustomers(), folder);
        saveSuppliersCsv(data.getSuppliers(), folder);
    }

    public void markPendingOffline(String companyUsername) {
        try {
            String folder = companyFolder(companyUsername);
            new File(folder).mkdirs();
            File flag = new File(folder + File.separator + PENDING_FILE);
            try (Writer writer = new FileWriter(flag)) {
                writer.write("pending");
            }
        } catch (IOException ignored) {
        }
    }

    public boolean hasPendingOffline(String companyUsername) {
        File flag = new File(companyFolder(companyUsername) + File.separator + PENDING_FILE);
        return flag.exists();
    }

    public void clearPendingOffline(String companyUsername) {
        File flag = new File(companyFolder(companyUsername) + File.separator + PENDING_FILE);
        if (flag.exists()) {
            flag.delete();
        }
    }

    private String companyFolder(String companyUsername) {
        return ROOT_DATA_FOLDER + File.separator + companyUsername;
    }

    private void saveJson(FinvoryData data, String folder) {
        try (Writer writer = new FileWriter(folder + File.separator + "finvory_database.json")) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            System.err.println("Error guardando JSON local: " + e.getMessage());
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
            return new FinvoryData();
        }
    }

    private void saveCustomersCsv(List<Customer> customers, String folder) {
        try (PrintWriter printWriter = new PrintWriter(new FileWriter(folder + File.separator + "clients.csv"))) {
            printWriter.println("Identification" + DELIMITER + "FullName" + DELIMITER + "Phone" + DELIMITER + "Email" + DELIMITER + "ClientType");
            if (customers != null) {
                for (Customer c : customers) {
                    if (c != null) {
                        printWriter.println(safe(c.getIdentification()) + DELIMITER + safe(c.getName()) + DELIMITER
                                + safe(c.getPhone()) + DELIMITER + safe(c.getEmail()) + DELIMITER + safe(c.getClientType()));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error guardando CSV clientes: " + e.getMessage());
        }
    }

    private ArrayList<Customer> loadCustomersCsv(String folder) {
        ArrayList<Customer> list = new ArrayList<>();
        File file = new File(folder + File.separator + "clients.csv");
        if (!file.exists()) {
            return list;
        }

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            bufferedReader.readLine();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split(DELIMITER);
                if (parts.length >= 5) {
                    list.add(new Customer(parts[1].trim(), parts[0].trim(), parts[2].trim(), parts[3].trim(), parts[4].trim()));
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo CSV clientes: " + e.getMessage());
        }
        return list;
    }

    private void saveSuppliersCsv(List<Supplier> suppliers, String folder) {
        try (PrintWriter printWriter = new PrintWriter(new FileWriter(folder + File.separator + "suppliers.csv"))) {
            printWriter.println("ID1" + DELIMITER + "ID2" + DELIMITER + "FullName" + DELIMITER + "Phone" + DELIMITER + "Email" + DELIMITER + "Description");
            if (suppliers != null) {
                for (Supplier s : suppliers) {
                    if (s != null) {
                        printWriter.println(safe(s.getId1()) + DELIMITER + safe(s.getId2()) + DELIMITER + safe(s.getFullName()) + DELIMITER
                                + safe(s.getPhone()) + DELIMITER + safe(s.getEmail()) + DELIMITER + safe(s.getDescription()));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error guardando CSV proveedores: " + e.getMessage());
        }
    }

    private ArrayList<Supplier> loadSuppliersCsv(String folder) {
        ArrayList<Supplier> list = new ArrayList<>();
        File file = new File(folder + File.separator + "suppliers.csv");
        if (!file.exists()) {
            return list;
        }

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            bufferedReader.readLine();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split(DELIMITER);
                if (parts.length >= 6) {
                    Supplier s = new Supplier(parts[2].trim(), parts[0].trim(), parts[3].trim(), parts[4].trim(), parts[5].trim());
                    s.setId2(parts[1].trim());
                    list.add(s);
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo CSV proveedores: " + e.getMessage());
        }
        return list;
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
        if (id.isEmpty() || customers == null) {
            return false;
        }
        for (Customer c : customers) {
            if (c != null && norm(c.getIdentification()).equals(id)) {
                return true;
            }
        }
        return false;
    }

    private boolean existsSupplier(List<Supplier> suppliers, String id1) {
        String id = norm(id1);
        if (id.isEmpty() || suppliers == null) {
            return false;
        }
        for (Supplier s : suppliers) {
            if (s != null && norm(s.getId1()).equals(id)) {
                return true;
            }
        }
        return false;
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private String norm(String s) {
        return s == null ? "" : s.trim();
    }

    private void dedupeCustomers(FinvoryData data) {
        if (data == null || data.getCustomers() == null) {
            return;
        }
        Map<String, Customer> map = new LinkedHashMap<>();
        for (Customer customer : new ArrayList<>(data.getCustomers())) {
            if (customer != null) {
                map.putIfAbsent(norm(customer.getIdentification()), customer);
            }
        }
        data.getCustomers().clear();
        data.getCustomers().addAll(map.values());
    }

    private void dedupeSuppliers(FinvoryData data) {
        if (data == null || data.getSuppliers() == null) {
            return;
        }
        Map<String, Supplier> map = new LinkedHashMap<>();
        for (Supplier supplier : new ArrayList<>(data.getSuppliers())) {
            if (supplier != null) {
                map.putIfAbsent(norm(supplier.getId1()), supplier);
            }
        }
        data.getSuppliers().clear();
        data.getSuppliers().addAll(map.values());
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
}
