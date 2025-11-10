package ec.espe.edu.finvory.model;

/**
 *
 * @author Arelys Otavalo, The POOwer Rangers Of Programming
 */
public class PersonalAccount {
    private String fullName;
    private String identification;
    private Address address;
    private String birthDate;
    private String ruc;
    private String password;
    private String nickName;

    public PersonalAccount(String fullName, String identification, Address address, String birthDate, String ruc, String password, String nickName) {
        this.fullName = fullName;
        this.identification = identification;
        this.address = address;
        this.birthDate = birthDate;
        this.ruc = ruc;
        this.password = password;
        this.nickName = nickName;
    }
    
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    
}
