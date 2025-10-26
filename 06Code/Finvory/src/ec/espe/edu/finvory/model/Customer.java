
package ec.espe.edu.finvory.model;

/**
 *
 * @author User
 */
public class Customer {
    private String customerName;
    private Address customerAddress;
    private String customerIdentification;
    private String customerPhone;
    private String customerEmail;
    private int  customerType;

    public Customer(String customerName, Address customerAddress, String customerIdentification, String customerPhone, String customerEmail, int customerType) {
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.customerIdentification = customerIdentification;
        this.customerPhone = customerPhone;
        this.customerEmail = customerEmail;
        this.customerType = customerType;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Address getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(Address customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerIdentification() {
        return customerIdentification;
    }

    public void setCustomerIdentification(String customerIdentification) {
        this.customerIdentification = customerIdentification;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public int getCustomerType() {
        return customerType;
    }

    public void setCustomerType(int customerType) {
        this.customerType = customerType;
    }
           
    
}
