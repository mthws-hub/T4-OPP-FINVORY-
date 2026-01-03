package ec.edu.espe.finvory.model;

import java.time.LocalDate;

/**
 *
 * @author Arelys Otavalo, The POOwer Rangers Of Programming
 */

public class AdminDetail {

    private String fullName;
    private LocalDate birthDate;
    private String nickname;
    private String email;
    private String password;

    public AdminDetail() {
    }

    public AdminDetail(String fullName, LocalDate birthDate, String nickname, String email, String password) {
        this.fullName = fullName;
        this.birthDate = birthDate;
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
