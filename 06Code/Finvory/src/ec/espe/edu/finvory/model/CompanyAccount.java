package ec.espe.edu.finvory.model;

/**
 *
 * @author Mathews Pastor
 */
public class CompanyAccount {
    private String companyName;
    private Address companyAddress;
    private String companyRuc;
    private String companyPhone;
    private String companyEmail;

    public CompanyAccount(String companyName, Address companyAddress, String companyRuc, String companyPhone, String companyEmail) {
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.companyRuc = companyRuc;
        this.companyPhone = companyPhone;
        this.companyEmail = companyEmail;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Address getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(Address companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCompanyRuc() {
        return companyRuc;
    }

    public void setCompanyRuc(String companyRuc) {
        this.companyRuc = companyRuc;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }
    
    
}
