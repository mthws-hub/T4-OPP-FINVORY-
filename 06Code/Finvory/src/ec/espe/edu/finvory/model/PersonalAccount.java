package ec.espe.edu.finvory.model;

/**
 *
 * @author Arelys Otavalo, The POOwer Rangers of Programming
 */

public class PersonalAccount {
    
    private String fullName;
    private String username;
    private String password;
    
    public PersonalAccount() {}
    
    public PersonalAccount(String fullName, String username, String password) {
        this.fullName = fullName;
        this.username = username;
        this.password = password;
    }
    
    public String getFullName() { 
        return fullName; 
    }
    
    public String getUsername() { 
        return username; 
    }
    
    public boolean checkPassword(String attempt) {
        return this.password != null && this.password.equals(attempt);
    }
}