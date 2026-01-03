package ec.edu.espe.finvory.model;

/**
 *
 * @author Arelys Otavalo, The POOwer Rangers of Programming
 */
public class CompanyAccount {

    private String name;
    private Address address;
    private String ruc;
    private String phone;
    private String email;
    private String username;
    private String password;
    private String logoPath;

    public CompanyAccount() {
    }

    public CompanyAccount(String name, Address address, String ruc, String phone, String email, String username, String password) {
        this.name = name;
        this.address = address;
        this.ruc = ruc;
        this.phone = phone;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public boolean checkPassword(String attempt) {
        return this.password != null && this.password.equals(attempt);
    }

    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    public String getRuc() {
        return ruc;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
}
