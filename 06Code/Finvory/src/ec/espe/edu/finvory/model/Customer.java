package ec.espe.edu.finvory.model;
import com.google.gson.Gson;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Arelys Otavalo, The POOwer Rangers Of Programming
 */
public class Customer {
    private String name;
    private Address address;
    private String identification;
    private String phone;
    private String email;
    private int  typeOfCustomer;
    private int typePaymentOpcion;
    private List<Product> purchasedProducts = new ArrayList<>();

    public Customer(String name, Address address, String identification, String phone, String email, int typeOfCustomer, int typePaymentOpcion) {
        this.name = name;
        this.address = address;
        this.identification = identification;
        this.phone = phone;
        this.email = email;
        this.typeOfCustomer = typeOfCustomer;
        this.typePaymentOpcion = typePaymentOpcion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getTypeOfCustomer() {
        return typeOfCustomer;
    }

    public void setTypeOfCustomer(int typeOfCustomer) {
        this.typeOfCustomer = typeOfCustomer;
    }

    public int getTypePaymentOpcion() {
        return typePaymentOpcion;
    }

    public void setTypePaymentOpcion(int typePaymentOpcion) {
        this.typePaymentOpcion = typePaymentOpcion;
    }
    
     public List<Product> getPurchasedProducts() { return purchasedProducts; }

    public void addPurchasedProduct(Product product) { purchasedProducts.add(product); }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public void save() {
        try {
            java.io.File folder = new java.io.File("reports");
            if (!folder.exists()) folder.mkdirs();
            FileWriter file = new FileWriter("reports/customer_" + identification + ".json");
            file.write(this.toJson());
            file.close();
        } catch (IOException e) {
            System.out.println("Error saving customer JSON: " + e.getMessage());
        }
    }
}
