package ec.edu.espe.finvory.mongo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ec.edu.espe.finvory.model.Customer;
import ec.edu.espe.finvory.model.SystemUsers;
import ec.edu.espe.finvory.model.FinvoryData;
import ec.edu.espe.finvory.model.Inventory;
import ec.edu.espe.finvory.model.InvoiceSim;
import ec.edu.espe.finvory.model.Product;
import ec.edu.espe.finvory.model.Supplier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
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

    private static final Path ROOT_DATA_FOLDER = resolveDataRoot();

    private static final String UTILS_FOLDER = "utils";
    private static final String USERS_JSON_NAME = "users.json";

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
        try {
            Files.createDirectories(ROOT_DATA_FOLDER);
            Files.createDirectories(ROOT_DATA_FOLDER.resolve(UTILS_FOLDER));
        } catch (IOException e) {
            System.err.println("Error creando carpetas data/utils: " + e.getMessage());
        }
    }

    private Path usersFilePath() {
        return ROOT_DATA_FOLDER.resolve(UTILS_FOLDER).resolve(USERS_JSON_NAME);
    }

    public SystemUsers loadUsers() {
        Path path = usersFilePath();
        if (!Files.exists(path)) {
            System.err.println("ERROR CRÍTICO: No se encuentra el archivo de usuarios.");
            System.err.println("Ruta buscada: " + path.toAbsolutePath());
            System.err.println("user.dir = " + System.getProperty("user.dir"));
            return new SystemUsers();
        }

        try (Reader reader = new FileReader(path.toFile())) {
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
        Path path = usersFilePath();
        try (Writer writer = new FileWriter(path.toFile())) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            System.err.println("Error guardando usuarios: " + e.getMessage());
        }
    }

    public FinvoryData loadCompanyDataLocal(String companyUsername) {
        Path folder = companyFolder(companyUsername);
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
        Path folder = companyFolder(companyUsername);
        folder.toFile().mkdirs();
        saveJson(data, folder);
        saveCustomersCsv(data.getCustomers(), folder);
        saveSuppliersCsv(data.getSuppliers(), folder);
    }

    public void markPendingOffline(String companyUsername) {
        try {
            Path folder = companyFolder(companyUsername);
            folder.toFile().mkdirs();
            Path flag = folder.resolve(PENDING_FILE);
            try (Writer writer = new FileWriter(flag.toFile())) {
                writer.write("pending");
            }
        } catch (IOException ignored) {
        }
    }

    public boolean hasPendingOffline(String companyUsername) {
        Path flag = companyFolder(companyUsername).resolve(PENDING_FILE);
        return flag.toFile().exists();
    }

    public void clearPendingOffline(String companyUsername) {
        Path flag = companyFolder(companyUsername).resolve(PENDING_FILE);
        File f = flag.toFile();
        if (f.exists()) {
            f.delete();
        }
    }

    private Path companyFolder(String companyUsername) {
        return ROOT_DATA_FOLDER.resolve(companyUsername);
    }

    private void saveJson(FinvoryData data, Path folder) {
        Path file = folder.resolve("finvory_database.json");
        try (Writer writer = new FileWriter(file.toFile())) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            System.err.println("Error guardando JSON local: " + e.getMessage());
        }
    }

    private FinvoryData loadJson(Path folder) {
        Path file = folder.resolve("finvory_database.json");
        if (!file.toFile().exists()) {
            return new FinvoryData();
        }
        try (Reader reader = new FileReader(file.toFile())) {
            FinvoryData data = gson.fromJson(reader, FinvoryData.class);
            return data != null ? data : new FinvoryData();
        } catch (IOException e) {
            return new FinvoryData();
        }
    }

    private void saveCustomersCsv(List<Customer> customers, Path folder) {
        Path file = folder.resolve("clients.csv");
        try (PrintWriter printWriter = new PrintWriter(new FileWriter(file.toFile()))) {
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

    private ArrayList<Customer> loadCustomersCsv(Path folder) {
        ArrayList<Customer> list = new ArrayList<>();
        Path file = folder.resolve("clients.csv");
        if (!file.toFile().exists()) {
            return list;
        }

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file.toFile()))) {
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

    private void saveSuppliersCsv(List<Supplier> suppliers, Path folder) {
        Path file = folder.resolve("suppliers.csv");
        try (PrintWriter printWriter = new PrintWriter(new FileWriter(file.toFile()))) {
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

    private ArrayList<Supplier> loadSuppliersCsv(Path folder) {
        ArrayList<Supplier> list = new ArrayList<>();
        Path file = folder.resolve("suppliers.csv");
        if (!file.toFile().exists()) {
            return list;
        }

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file.toFile()))) {
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

    private void mergeCustomersFromCsv(FinvoryData localData, Path folder) {
        for (Customer customer : loadCustomersCsv(folder)) {
            if (!existsCustomer(localData.getCustomers(), customer.getIdentification())) {
                localData.addCustomer(customer);
            }
        }
    }

    private void mergeSuppliersFromCsv(FinvoryData localData, Path folder) {
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

    private static Path resolveDataRoot() {
        try {
            Path jarDir = Paths.get(LocalFileService.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI()).getParent();

            Path p1 = jarDir.resolve("data");
            Path p2 = jarDir.getParent().resolve("data");

            if (Files.exists(p1)) {
                return p1;
            }
            if (Files.exists(p2)) {
                return p2;
            }

            return Paths.get("data").toAbsolutePath();
        } catch (Exception e) {
            return Paths.get("data").toAbsolutePath();
        }
    }
}
