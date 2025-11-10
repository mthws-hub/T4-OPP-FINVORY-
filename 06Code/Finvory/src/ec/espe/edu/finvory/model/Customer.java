
package ec.espe.edu.finvory.model;

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

 
}
