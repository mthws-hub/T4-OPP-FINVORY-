package ec.espe.edu.finvory.model;

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
    
    
}
