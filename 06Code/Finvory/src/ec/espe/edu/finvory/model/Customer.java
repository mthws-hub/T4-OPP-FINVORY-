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
    private String identification;
    private String phone;
    private String email;
    private String clientType; 

    public Customer() {}

    public Customer(String name, String identification, String phone, String email, String clientType) {
        this.name = name;
        this.identification = identification;
        this.phone = phone;
        this.email = email;
        this.clientType = clientType;
    }
    
    public String getName() { 
        return name; 
    }
    public String getIdentification() { 
        return identification; 
    }
    public String getPhone() { 
        return phone; 
    }
    
    public String getEmail() { 
        return email; 
    }
    
    public String getClientType() { 
        return clientType; 
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setClientType(String clientType) {
        this.clientType = clientType;
    }
}

