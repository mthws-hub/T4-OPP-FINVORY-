package ec.edu.espe.finvory.model;

import ec.edu.espe.finvory.utils.ValidationUtils;

/**
 *
 * @author Arelys Otavalo, The POOwer Rangers of Programming
 */
public class PersonalAccount {

    private String fullName;
    private String username;
    private String password;
    private String profilePhotoPath;
    private String twoFactorKey; 

    public PersonalAccount() {
    }

    public PersonalAccount(String fullName, String username, String password) {
        this.fullName = fullName;
        this.username = username;
        this.password = password;
    }

    public boolean checkPassword(String attempt) {
        if (this.password == null || attempt == null) {
            return false;
        }
        String encryptedAttempt = ValidationUtils.caesarCipher(attempt, 1);
        return this.password.equals(encryptedAttempt);
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

    public String getTwoFactorKey() {
        return twoFactorKey;
    }

    public void setTwoFactorKey(String twoFactorKey) {
        this.twoFactorKey = twoFactorKey;
    }

}
