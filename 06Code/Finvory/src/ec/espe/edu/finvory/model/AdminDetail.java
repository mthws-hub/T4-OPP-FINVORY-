package ec.espe.edu.finvory.model;

/**
 *
 * @author Arelys Otavalo
 */
public class AdminDetail {
    private String adminFullName;
    private String adminBirthDate;
    private String adminNickname;
    private String adminPassword;

    public AdminDetail(String adminFullName, String adminBirthDate, String adminNickname, String adminPassword) {
        this.adminFullName = adminFullName;
        this.adminBirthDate = adminBirthDate;
        this.adminNickname = adminNickname;
        this.adminPassword = adminPassword;
    }

    public String getAdminFullName() {
        return adminFullName;
    }

    public void setAdminFullName(String adminFullName) {
        this.adminFullName = adminFullName;
    }

    public String getAdminBirthDate() {
        return adminBirthDate;
    }

    public void setAdminBirthDate(String adminBirthDate) {
        this.adminBirthDate = adminBirthDate;
    }

    public String getAdminNickname() {
        return adminNickname;
    }

    public void setAdminNickname(String adminNickname) {
        this.adminNickname = adminNickname;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }
    
    
}
