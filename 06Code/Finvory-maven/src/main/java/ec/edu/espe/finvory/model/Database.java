package ec.edu.espe.finvory.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import ec.edu.espe.finvory.mongo.MongoDBConnection;
import org.bson.Document;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;

/**
 * 
 * @author Joseph Medina
 */
public class Database {

    private static final String ROOT_DATA_FOLDER = "data";
    private static final String UTILS_FOLDER = "utils";
    private static final String USERS_FILE = UTILS_FOLDER + File.separator + "users.json";
    private static final String DELIMITER = ";";
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public FinvoryData loadCompanyData(String companyUsername) {
        System.out.println("--- SINCRONIZACIÓN DE DATOS ---");

        FinvoryData cloudData = loadDataFromCloud(companyUsername);
        
        if (cloudData != null) {
            System.out.println("Datos descargados de MongoDB Atlas.");
            System.out.println("   -> Inventarios: " + cloudData.getInventories().size());
            System.out.println("   -> Productos: " + cloudData.getProducts().size());
            saveCompanyData(cloudData, companyUsername);
            return cloudData;
        }

        System.out.println("No se pudo cargar de la nube (o está vacía). Usando datos locales.");
        String folder = ROOT_DATA_FOLDER + File.separator + companyUsername;
        FinvoryData localData = loadJson(folder);
        localData.getCustomers().addAll(loadCustomersCsv(folder));
        localData.getSuppliers().addAll(loadSuppliersCsv(folder));
        
        return localData;
    }

    private FinvoryData loadDataFromCloud(String username) {
        try {
            if (MongoDBConnection.getCollection("products") == null) {
                return null;
            }

            FinvoryData data = new FinvoryData();

            
            MongoCollection<Document> prodCol = MongoDBConnection.getCollection("products");
            for (Document doc : prodCol.find(Filters.eq("companyUsername", username))) {
                Product p = new Product(
                    doc.getString("productId"), 
                    doc.getString("name"),
                    doc.getString("description"),
                    doc.getString("barcode"), 
                    doc.getDouble("baseCostPrice").floatValue(),
                    doc.getString("supplierId")
                );
                if(p.getId() == null) p = new Product(doc.getString("id"), doc.getString("name"), doc.getString("description"), doc.getString("barcode"), doc.getDouble("baseCostPrice").floatValue(), doc.getString("supplierId"));
                
                data.getProducts().add(p);
            }

            MongoCollection<Document> invCol = MongoDBConnection.getCollection("inventories");
            for (Document doc : invCol.find(Filters.eq("companyUsername", username))) {
                Document addrDoc = (Document) doc.get("address");
                Address addr = null;
                if (addrDoc != null) {
                    addr = new Address(
                        addrDoc.getString("country"),
                        addrDoc.getString("city"),
                        addrDoc.getString("street")
                    );
                }
                
                Inventory inv = new Inventory(doc.getString("name"), addr);
                
                Document stockDoc = (Document) doc.get("productStock");
                if (stockDoc != null) {
                    for (String key : stockDoc.keySet()) {
                        Number qty = (Number) stockDoc.get(key);
                        inv.setStock(key, qty.intValue());
                    }
                }
                data.getInventories().add(inv);
            }

            MongoCollection<Document> cliCol = MongoDBConnection.getCollection("customers");
            if (cliCol != null) {
                for (Document doc : cliCol.find(Filters.eq("companyUsername", username))) {
                    Customer c = new Customer(
                        doc.getString("name"),
                        doc.getString("identification"),
                        doc.getString("phone"),
                        doc.getString("email"),
                        doc.getString("clientType")
                    );
                    data.getCustomers().add(c);
                }
            }

            MongoCollection<Document> supCol = MongoDBConnection.getCollection("suppliers");
            if (supCol != null) {
                for (Document doc : supCol.find(Filters.eq("companyUsername", username))) {
                    Supplier s = new Supplier(
                        doc.getString("fullName"),
                        doc.getString("id1"),
                        doc.getString("phone"),
                        doc.getString("email"),
                        doc.getString("description")
                    );
                    s.setId2(doc.getString("id2"));
                    data.getSuppliers().add(s);
                }
            }
            
            return data;

        } catch (Exception e) {
            System.err.println("Error descargando de MongoDB: " + e.getMessage());
            return null; // Fallback a local
        }
    }

    public void saveCompanyData(FinvoryData data, String companyUsername) {
        String folder = ROOT_DATA_FOLDER + File.separator + companyUsername;
        new File(folder).mkdirs(); 
        
        saveJson(data, folder);
        saveCustomersCsv(data.getCustomers(), folder);
        saveSuppliersCsv(data.getSuppliers(), folder);
    }

    private FinvoryData loadJson(String folder) {
        File file = new File(folder + File.separator + "finvory_database.json");
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                return gson.fromJson(reader, FinvoryData.class);
            } catch (IOException e) { }
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
                if (users != null) return users;
            } catch (IOException e) { }
        }
        return new SystemUsers(); 
    }

    public void saveUsers(SystemUsers users) {
        try {
            File file = new File(USERS_FILE);
            new File(file.getParent()).mkdirs();
            try (Writer writer = new FileWriter(file)) {
                gson.toJson(users, writer);
            }
        } catch (IOException e) { }
    }
    
    private void saveCustomersCsv(ArrayList<Customer> customers, String folder) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(folder + File.separator + "clients.csv"))) {
            pw.println("Identification" + DELIMITER + "FullName" + DELIMITER + "Phone" + DELIMITER + "Email" + DELIMITER + "ClientType");
            for (Customer c : customers) {
                pw.println(c.getIdentification() + DELIMITER + c.getName() + DELIMITER + c.getPhone() + DELIMITER + c.getEmail() + DELIMITER + c.getClientType());
            }
        } catch (IOException e) { }
    }

    private ArrayList<Customer> loadCustomersCsv(String folder) {
        ArrayList<Customer> list = new ArrayList<>();
        File file = new File(folder + File.separator + "clients.csv");
        if (!file.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(DELIMITER);
                if (parts.length == 5) list.add(new Customer(parts[1], parts[0], parts[2], parts[3], parts[4]));
            }
        } catch (IOException e) { }
        return list;
    }

    private void saveSuppliersCsv(ArrayList<Supplier> suppliers, String folder) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(folder + File.separator + "suppliers.csv"))) {
            pw.println("ID1" + DELIMITER + "ID2" + DELIMITER + "FullName" + DELIMITER + "Phone" + DELIMITER + "Email" + DELIMITER + "Description");
            for (Supplier s : suppliers) {
                pw.println(s.getId1() + DELIMITER + s.getId2() + DELIMITER + s.getFullName() + DELIMITER + s.getPhone() + DELIMITER + s.getEmail() + DELIMITER + s.getDescription());
            }
        } catch (IOException e) { }
    }

    private ArrayList<Supplier> loadSuppliersCsv(String folder) {
        ArrayList<Supplier> list = new ArrayList<>();
        File file = new File(folder + File.separator + "suppliers.csv");
        if (!file.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(DELIMITER);
                if (parts.length == 6) {
                    Supplier s = new Supplier(parts[2], parts[0], parts[3], parts[4], parts[5]);
                    s.setId2(parts[1]);
                    list.add(s);
                }
            }
        } catch (IOException e) { }
        return list;
    }
    
    public boolean exportToCsv(String title, HashMap<String, ? extends Object> data, String companyUsername) {
        String fileName = ROOT_DATA_FOLDER + File.separator + companyUsername + File.separator + title + ".csv";
        try {
            File file = new File(fileName);
            new File(file.getParent()).mkdirs();
            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                pw.println("Key" + DELIMITER + "Value");
                for (Map.Entry<String, ? extends Object> entry : data.entrySet()) {
                    String val = String.valueOf(entry.getValue());
                    if (entry.getValue() instanceof Float) val = String.format(Locale.US, "%.2f", (Float) entry.getValue());
                    pw.println(entry.getKey() + DELIMITER + val);
                }
            }
            return true;
        } catch (IOException e) { return false; }
    }
}