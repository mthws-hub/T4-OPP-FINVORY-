package ec.edu.espe.finvory.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import ec.edu.espe.finvory.mongo.MongoDBConnection;
import org.bson.Document;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;
import java.util.List;

/**
 * * @author Joseph Medina
 */
public class Database {

    private static final String ROOT_DATA_FOLDER = "data";
    private static final String UTILS_FOLDER = "utils";
    private static final String USERS_FILE = UTILS_FOLDER + File.separator + "users.json";
    private static final String DELIMITER = ";";

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new TypeAdapter<LocalDate>() {
                @Override
                public void write(JsonWriter jsonWriter, LocalDate localDate) throws IOException {
                    jsonWriter.value(localDate == null ? null : localDate.toString());
                }
                @Override
                public LocalDate read(JsonReader jsonReader) throws IOException {
                    String string = jsonReader.nextString();
                    return (string == null || string.isEmpty()) ? null : LocalDate.parse(string);
                }
            })
            .setPrettyPrinting()
            .create();

    public FinvoryData loadCompanyData(String companyUsername) {
        System.out.println("--- SINCRONIZACIÓN DE DATOS ---");

        FinvoryData cloudData = loadDataFromCloud(companyUsername);

        if (cloudData != null) {
            System.out.println("Datos descargados de MongoDB Atlas.");
            System.out.println("    -> Inventarios: " + cloudData.getInventories().size());
            System.out.println("    -> Productos: " + cloudData.getProducts().size());
            saveCompanyData(cloudData, companyUsername);
            return cloudData;
        }

        System.out.println("No se pudo cargar de la nube (o está vacía). Usando datos locales.");
        String folder = ROOT_DATA_FOLDER + File.separator + companyUsername;
        FinvoryData localData = loadJson(folder);

        for (Customer customer : loadCustomersCsv(folder)) {
            localData.addCustomer(customer);
        }
        for (Supplier supplier : loadSuppliersCsv(folder)) {
            localData.addSupplier(supplier);
        }

        return localData;
    }

    private FinvoryData loadDataFromCloud(String username) {
        try {
            if (MongoDBConnection.getCollection("products") == null) {
                return null;
            }

            FinvoryData data = new FinvoryData();
            MongoCollection<Document> prodCol = MongoDBConnection.getCollection("products");
            for (Document document : prodCol.find(Filters.eq("companyUsername", username))) {

                Double baseCostPriceDouble = document.getDouble("baseCostPrice");
                BigDecimal baseCostPrice = (baseCostPriceDouble != null)
                        ? BigDecimal.valueOf(baseCostPriceDouble)
                        : BigDecimal.ZERO;

                Product product = new Product(
                        document.getString("productId"),
                        document.getString("name"),
                        document.getString("description"),
                        document.getString("barcode"),
                        baseCostPrice,
                        document.getString("supplierId")
                );
                if (product.getId() == null) {
                    product = new Product(document.getString("id"), document.getString("name"), document.getString("description"), document.getString("barcode"), baseCostPrice, document.getString("supplierId"));
                }

                data.addProduct(product);
            }

            MongoCollection<Document> invoiceCollection = MongoDBConnection.getCollection("inventories");
            for (Document document : invoiceCollection.find(Filters.eq("companyUsername", username))) {
                Document addressDocument = (Document) document.get("address");
                Address address = null;
                if (addressDocument != null) {
                    address = new Address(
                            addressDocument.getString("country"),
                            addressDocument.getString("city"),
                            addressDocument.getString("street")
                    );
                }

                Inventory inv = new Inventory(document.getString("name"), address);

                Document stockDoc = (Document) document.get("productStock");
                if (stockDoc != null) {
                    for (String key : stockDoc.keySet()) {
                        Number quantity = (Number) stockDoc.get(key);
                        inv.setStock(key, quantity.intValue());
                    }
                }
                data.addInventory(inv);
            }

            MongoCollection<Document> cliCol = MongoDBConnection.getCollection("customers");
            if (cliCol != null) {
                for (Document document : cliCol.find(Filters.eq("companyUsername", username))) {
                    Customer customer = new Customer(
                            document.getString("name"),
                            document.getString("identification"),
                            document.getString("phone"),
                            document.getString("email"),
                            document.getString("clientType")
                    );
                    data.addCustomer(customer);
                }
            }

            MongoCollection<Document> supCollection = MongoDBConnection.getCollection("suppliers");
            if (supCollection != null) {
                for (Document document : supCollection.find(Filters.eq("companyUsername", username))) {
                    Supplier supplier = new Supplier(
                            document.getString("fullName"),
                            document.getString("id1"),
                            document.getString("phone"),
                            document.getString("email"),
                            document.getString("description")
                    );
                    supplier.setId2(document.getString("id2"));
                    data.addSupplier(supplier);
                }
            }

            return data;

        } catch (Exception e) {
            System.err.println("Error descargando de MongoDB: " + e.getMessage());
            return null;
        }
    }

    public void saveCompanyData(FinvoryData data, String companyUsername) {
        String folder = ROOT_DATA_FOLDER + File.separator + companyUsername;
        new File(folder).mkdirs();

        saveJson(data, folder);
        saveCustomersCsv(data.getCustomers(), folder);
        saveSuppliersCsv(data.getSuppliers(), folder);
    }

    public void saveUsers(SystemUsers users) {
        try {
            File file = new File(USERS_FILE);
            new File(file.getParent()).mkdirs();
            try (Writer writer = new FileWriter(file)) {
                gson.toJson(users, writer);
            }
        } catch (IOException e) {
            System.err.println("Error guardando usuarios: " + e.getMessage());
        }
    }

    private FinvoryData loadJson(String folder) {
        File file = new File(folder + File.separator + "finvory_database.json");
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                return gson.fromJson(reader, FinvoryData.class);
            } catch (IOException e) {
                System.err.println("Error leyendo JSON: " + e.getMessage());
            }
        }
        return new FinvoryData();
    }

    private void saveJson(FinvoryData data, String folder) {
        try (Writer writer = new FileWriter(folder + File.separator + "finvory_database.json")) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            System.err.println("Error guardando JSON: " + e.getMessage());
        }
    }

    public SystemUsers loadUsers() {
        File file = new File(USERS_FILE);
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                SystemUsers users = gson.fromJson(reader, SystemUsers.class);
                if (users != null) {
                    return users;
                }
            } catch (IOException e) {
                System.err.println("Error leyendo usuarios: " + e.getMessage());
            }
        }
        return new SystemUsers();
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
                    list.add(new Customer(parts[1], parts[0], parts[2], parts[3], parts[4]));
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo CSV de clientes: " + e.getMessage());
        }
        return list;
    }

    private void saveCustomersCsv(List<Customer> customers, String folder) {
        try (PrintWriter printWritter = new PrintWriter(new FileWriter(folder + File.separator + "clients.csv"))) {
            printWritter.println("Identification" + DELIMITER + "FullName" + DELIMITER + "Phone" + DELIMITER + "Email" + DELIMITER + "ClientType");
            for (Customer customer : customers) {
                printWritter.println(customer.getIdentification() + DELIMITER + customer.getName() + DELIMITER + customer.getPhone() + DELIMITER + customer.getEmail() + DELIMITER + customer.getClientType());
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
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            bufferedReader.readLine();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split(DELIMITER);
                if (parts.length == 6) {
                    Supplier supplier = new Supplier(parts[2], parts[0], parts[3], parts[4], parts[5]);
                    supplier.setId2(parts[1]);
                    list.add(supplier);
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo CSV de proveedores: " + e.getMessage());
        }
        return list;
    }

    private void saveSuppliersCsv(List<Supplier> suppliers, String folder) {
        try (PrintWriter printWritter = new PrintWriter(new FileWriter(folder + File.separator + "suppliers.csv"))) {
            printWritter.println("ID1" + DELIMITER + "ID2" + DELIMITER + "FullName" + DELIMITER + "Phone" + DELIMITER + "Email" + DELIMITER + "Description");
            for (Supplier supplier : suppliers) {
                printWritter.println(supplier.getId1() + DELIMITER + supplier.getId2() + DELIMITER + supplier.getFullName() + DELIMITER + supplier.getPhone() + DELIMITER + supplier.getEmail() + DELIMITER + supplier.getDescription());
            }
        } catch (IOException e) {
            System.err.println("Error guardando CSV de proveedores: " + e.getMessage());
        }
    }

    public boolean exportToCsv(String title, HashMap<String, ? extends Object> data, String companyUsername) {
        String fileName = ROOT_DATA_FOLDER + File.separator + companyUsername + File.separator + title + ".csv";
        try {
            File file = new File(fileName);
            new File(file.getParent()).mkdirs();
            try (PrintWriter printWritter = new PrintWriter(new FileWriter(file))) {
                printWritter.println("Key" + DELIMITER + "Value");
                for (Map.Entry<String, ? extends Object> entry : data.entrySet()) {
                    String value = String.valueOf(entry.getValue());

                    if (entry.getValue() instanceof BigDecimal) {
                        value = String.format(Locale.US, "%.2f", (BigDecimal) entry.getValue());
                    } else if (entry.getValue() instanceof Float) {
                        value = String.format(Locale.US, "%.2f", (Float) entry.getValue());
                    }
                    printWritter.println(entry.getKey() + DELIMITER + value);
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
