package ec.espe.edu.finvory.model;
/**
 *
 * @author User
 */
public class PersonalAccount {
    private String personalFullName;
    private String personalIdentification;
    private Address personalAddress;
    private String personalBirthDate;
    private String personalRuc;
    private String personalPassword;
    private String personalNickName;

    public PersonalAccount(String personalFullName, String personalIdentification, Address personalAddress, String personalBirthDate, String personalRuc, String personalPassword, String personalNickName) {
        this.personalFullName = personalFullName;
        this.personalIdentification = personalIdentification;
        this.personalAddress = personalAddress;
        this.personalBirthDate = personalBirthDate;
        this.personalRuc = personalRuc;
        this.personalPassword = personalPassword;
        this.personalNickName = personalNickName;
    }

    public String getPersonalFullName() {
        return personalFullName;
    }

    public void setPersonalFullName(String personalFullName) {
        this.personalFullName = personalFullName;
    }

    public String getPersonalIdentification() {
        return personalIdentification;
    }

    public void setPersonalIdentification(String personalIdentification) {
        this.personalIdentification = personalIdentification;
    }

    public Address getPersonalAddress() {
        return personalAddress;
    }

    public void setPersonalAddress(Address personalAddress) {
        this.personalAddress = personalAddress;
    }

    public String getPersonalBirthDate() {
        return personalBirthDate;
    }

    public void setPersonalBirthDate(String personalBirthDate) {
        this.personalBirthDate = personalBirthDate;
    }

    public String getPersonalRuc() {
        return personalRuc;
    }

    public void setPersonalRuc(String personalRuc) {
        this.personalRuc = personalRuc;
    }

    public String getPersonalPassword() {
        return personalPassword;
    }

    public void setPersonalPassword(String personalPassword) {
        this.personalPassword = personalPassword;
    }

    public String getPersonalNickName() {
        return personalNickName;
    }

    public void setPersonalNickName(String personalNickName) {
        this.personalNickName = personalNickName;
    }
    
}
