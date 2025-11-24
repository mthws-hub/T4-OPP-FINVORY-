package ec.espe.edu.finvory.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;
/**
 *
 * @author Joseph Medina, The POOwer Rangers of Programming
 */

public class Database {

    private static final String ROOT_DATA_FOLDER = "data";
    private static final String UTILS_FOLDER = "utils";
    private static final String USERS_FILE = UTILS_FOLDER + File.separator + "users.json";
    
    private static final String DELIMITER = ";";
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public SystemUsers loadUsers() {
        File file = new File(USERS_FILE);
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                SystemUsers users = gson.fromJson(reader, SystemUsers.class);
                if (users != null) return users;
            } catch (IOException e) {
                System.err.println("Error cargando usuarios: " + e.getMessage());
            }
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
        } catch (IOException e) {
            System.err.println("Error guardando usuarios: " + e.getMessage());
        }
    }

    public FinvoryData loadCompanyData(String companyUsername) {
        String folder = ROOT_DATA_FOLDER + File.separator + companyUsername;
        FinvoryData data = loadJson(folder);
        
        data.getCustomers().addAll(loadCustomersCsv(folder));
        data.getSuppliers().addAll(loadSuppliersCsv(folder));
        
        return data;
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