package ec.espe.edu.finvory.model;
/**
 *
 * @author Arelys Otavalo, The POOwer Rangers Of Programming
 */

public class AdminDetail {
    private String fullName;
    private String birthDate;
    private String nickname;
    private String email;
    private String password;

    public AdminDetail() {}

    public AdminDetail(String fullName, String nickname, String email, String password) {
        this.fullName = fullName;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
    }
    
    public boolean checkPassword(String attempt) {
        return this.password != null && this.password.equals(attempt);
    }
    
    public String getFullName() {
        return fullName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }
}

