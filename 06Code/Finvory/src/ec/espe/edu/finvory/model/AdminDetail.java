package ec.espe.edu.finvory.model;

/**
 *
 * @author Arelys Otavalo, The POOwer Rangers Of Programming
 */
public class AdminDetail {
    private String fullName;
    private String birthDate;
    private String nickname;
    private String password;

    public AdminDetail(String fullName, String birthDate, String nickname, String password) {
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.nickname = nickname;
        this.password = password;
    }

    
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
 
}
