package ec.espe.edu.finvory.model;

/**
 *
 * @author Arelys Otavalo, The POOwer Rangers of Programming
 */
public class PersonalAccount {
    
    private String fullName;
    private String nickname;
    private String password;
    public PersonalAccount() {} 
    
    public PersonalAccount(String fullName, String username, String password) {
        this.fullName = fullName;
        this.nickname = username;
        this.password = password;
    }

    public String getFullName() { return fullName; }
    public String getNickname() { return nickname; }

    public boolean checkPassword(String attempt) {
        return this.password != null && this.password.equals(attempt);
    }
}
