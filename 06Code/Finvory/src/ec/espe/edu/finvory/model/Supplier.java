package ec.espe.edu.finvory.model;
import com.google.gson.Gson;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author @author Arelys Otavalo, The POOwer Rangers Of Programming
 */
public class Supplier {
    private String fullName;
    private String identification1;
    private String identification2;
    private String phoneNumber;
    private String email;
    private String description;
    private List<Product> suppliedProducts = new ArrayList<>();

    public Supplier(String fullName, String identification1, String identification2, String phoneNumber, String email, String description) {
        this.fullName = fullName;
        this.identification1 = identification1;
        this.identification2 = identification2;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.description = description;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getIdentification1() {
        return identification1;
    }

    public void setIdentification1(String identification1) {
        this.identification1 = identification1;
    }

    public String getIdentification2() {
        return identification2;
    }

    public void setIdentification2(String identification2) {
        this.identification2 = identification2;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public void addSuppliedProduct(Product product) { suppliedProducts.add(product); }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public void save() {
        try {
            java.io.File folder = new java.io.File("reports");
            if (!folder.exists()) folder.mkdirs();
            FileWriter file = new FileWriter("reports/supplier_" + identification1 + ".json");
            file.write(this.toJson());
            file.close();
        } catch (IOException e) {
            System.out.println("Error saving supplier JSON: " + e.getMessage());
        }
    }
    
}
