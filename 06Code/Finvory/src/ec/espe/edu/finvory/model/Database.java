package ec.espe.edu.finvory.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*; 
import java.util.ArrayList;
/**
 *
 * @author Mathews Pastor, The POOwer Rangers Of Programming
 */
public class Database {

    private static final String DATA_FOLDER = "data";
    private static final String UTILS_FOLDER = "utils";

    private static final String CLIENTS_FILE = DATA_FOLDER + File.separator + "clients.csv";
    private static final String SUPPLIERS_FILE = DATA_FOLDER + File.separator + "suppliers.csv";
    private static final String DATABASE_FILE = UTILS_FOLDER + File.separator + "finvory_database.json";
    
    private static final String DELIMITER = ";";

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public FinvoryData load() {
        FinvoryData data = loadDatabaseJson();
        data.getCustomers().addAll(loadCustomersCsv());
        data.getSuppliers().addAll(loadSuppliersCsv());
        return data;
    }

    public void save(FinvoryData data) {
        saveCustomersCsv(data.getCustomers());
        saveSuppliersCsv(data.getSuppliers());
        saveDatabaseJson(data);
    }
    
    private FinvoryData loadDatabaseJson() {
        File file = new File(DATABASE_FILE);
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                FinvoryData data = gson.fromJson(reader, FinvoryData.class);
                if (data != null) {
                    return data; 
                }
            } catch (IOException e) {
                System.err.println("Error al cargar " + DATABASE_FILE + ": " + e.getMessage());
            }
        }
        return new FinvoryData();
    }

    private void saveDatabaseJson(FinvoryData data) {
        try {
            File file = new File(DATABASE_FILE);
            new File(file.getParent()).mkdirs(); 
            
            try (Writer writer = new FileWriter(file)) {
                gson.toJson(data, writer);
            }
        } catch (IOException e) {
            System.err.println("Error al guardar " + DATABASE_FILE + ": " + e.getMessage());
        }
    }

    private void saveCustomersCsv(ArrayList<Customer> customers) {
        try {
            File file = new File(CLIENTS_FILE);
            new File(file.getParent()).mkdirs();

            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                pw.println("Identification" + DELIMITER + "FullName" + DELIMITER + "Phone" + DELIMITER + "Email" + DELIMITER + "ClientType");
                for (Customer c : customers) {
                    pw.println(
                        c.getIdentification() + DELIMITER + c.getName() + DELIMITER +
                        c.getPhone() + DELIMITER + c.getEmail() + DELIMITER + c.getClientType()
                    );
                }
            }
        } catch (IOException e) {
            System.err.println("Error al guardar " + CLIENTS_FILE + ": " + e.getMessage());
        }
    }

    private ArrayList<Customer> loadCustomersCsv() {
        ArrayList<Customer> customers = new ArrayList<>();
        File file = new File(CLIENTS_FILE);
        if (!file.exists()) return customers; 

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            bufferedReader.readLine(); 
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split(DELIMITER);
                if (parts.length == 5) {
                    customers.add(new Customer(parts[1], parts[0], parts[2], parts[3], parts[4]));
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar " + CLIENTS_FILE + ": " + e.getMessage());
        }
        return customers;
    }

    private void saveSuppliersCsv(ArrayList<Supplier> suppliers) {
        try {
            File file = new File(SUPPLIERS_FILE);
            new File(file.getParent()).mkdirs();

            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                pw.println("ID1" + DELIMITER + "ID2_Opcional" + DELIMITER + "FullName" + DELIMITER + "Phone" + DELIMITER + "Email" + DELIMITER + "Description");
                for (Supplier s : suppliers) {
                    pw.println(
                        s.getId1() + DELIMITER + s.getId2() + DELIMITER + s.getFullName() + DELIMITER +
                        s.getPhone() + DELIMITER + s.getEmail() + DELIMITER + s.getDescription()
                    );
                }
            }
        } catch (IOException e) {
            System.err.println("Error al guardar " + SUPPLIERS_FILE + ": " + e.getMessage());
        }
    }

    private ArrayList<Supplier> loadSuppliersCsv() {
        ArrayList<Supplier> suppliers = new ArrayList<>();
        File file = new File(SUPPLIERS_FILE);
        if (!file.exists()) return suppliers;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); 
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(DELIMITER);
                if (parts.length == 6) {
                    Supplier s = new Supplier(parts[2], parts[0], parts[3], parts[4], parts[5]);
                    s.setId2(parts[1]); 
                    suppliers.add(s);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar " + SUPPLIERS_FILE + ": " + e.getMessage());
        }
        return suppliers;
    }
}
