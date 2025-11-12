package ec.espe.edu.finvory.model;

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
    
    public CompanyAccount() {}

    public CompanyAccount(String name, Address address, String ruc, String phone, String email) {
        this.name = name;
        this.address = address;
        this.ruc = ruc;
        this.phone = phone;
        this.email = email;
    }
    
}
