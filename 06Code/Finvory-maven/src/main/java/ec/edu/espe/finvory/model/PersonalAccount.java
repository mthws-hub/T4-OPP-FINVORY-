package ec.edu.espe.finvory.model;

/**
 *
 * @author Arelys Otavalo, The POOwer Rangers of Programming
 */

public class PersonalAccount {
    
    private String fullName;
    private String username; 
    private String password; 
    private String profilePhotoPath;
    
    public PersonalAccount() {}
    
    public PersonalAccount(String fullName, String username, String password) {
        this.fullName = fullName;
        this.username = username;
        this.password = password;
    }

    public boolean checkPassword(String attempt) {
        return this.password != null && this.password.equals(attempt);
    }
    

    public String getFullName() { 
        return fullName; 
    }
    
    public String getUsername() { 
        return username; 
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getProfilePhotoPath() {
        return profilePhotoPath;
    }

    public void setProfilePhotoPath(String profilePhotoPath) {
        this.profilePhotoPath = profilePhotoPath;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
}