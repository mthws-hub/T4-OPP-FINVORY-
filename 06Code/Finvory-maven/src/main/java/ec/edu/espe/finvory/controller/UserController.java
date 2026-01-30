package ec.edu.espe.finvory.controller;

import ec.edu.espe.finvory.model.*;
import ec.edu.espe.finvory.mongo.MongoDBConnection;
import ec.edu.espe.finvory.mongo.MongoDataExporter;
import ec.edu.espe.finvory.view.FrmMainMenu;
import ec.edu.espe.finvory.view.FrmMainMenuPersonalAccount;
import java.util.HashMap;
import javax.swing.JOptionPane;

/**
 *
 * @author Arelys Otavalo, the POOwer Rangers of Programming
 */
public class UserController {

    private final FinvoryController mainController;

    public UserController(FinvoryController mainController) {
        this.mainController = mainController;
    }

    public boolean handleLoginGUI(String username, String password) {
        SystemUsers users = mainController.getUsers();
        for (CompanyAccount company : users.getCompanyAccounts()) {
            if (company.getUsername().equals(username) && company.checkPassword(password)) {

                if (!verifyTwoFactor(company.getTwoFactorKey())) {
                    return false;
                }  

                mainController.setCurrentCompanyUsername(username);
                mainController.setUserType("COMPANY");

                FinvoryData loadedData = mainController.getDataBase().loadCompanyData(username);
                if (loadedData != null) {
                    mainController.setData(loadedData);
                } else {
                    mainController.setData(new FinvoryData());
                }

                mainController.getData().setCompanyInfo(company);
                return true;
            }
        }

        for (PersonalAccount personal : users.getPersonalAccounts()) {
            if (personal.getUsername().equals(username) && personal.checkPassword(password)) {
                
                if (!verifyTwoFactor(personal.getTwoFactorKey())) {
                    return false;
                }

                mainController.setCurrentCompanyUsername(username);
                mainController.setUserType("PERSONAL");
                mainController.setData(null);
                return true;
            }
        }
        return false;
    }

    private boolean verifyTwoFactor(String secretKey) {
        if (secretKey == null || secretKey.isEmpty()) {
            return true;
        }

        String codeString = JOptionPane.showInputDialog(null,
                "Ingrese el código de 6 dígitos de Google Authenticator:",
                "Verificación de Seguridad (2FA)",
                JOptionPane.QUESTION_MESSAGE);
        if (codeString == null) {
            return false;
        }

        return mainController.authenticatorcontroller.verifyCode(secretKey, codeString);
    }

    public void startMainMenuPublic() {
        String type = mainController.getUserType();
        if ("COMPANY".equals(type)) {
            FrmMainMenu menu = new FrmMainMenu(mainController);
            menu.setVisible(true);
        } else if ("PERSONAL".equals(type)) {
            FrmMainMenuPersonalAccount menu = new FrmMainMenuPersonalAccount(mainController);
            menu.setVisible(true);
        }
    }

    public boolean registerCompanyGUI(HashMap<String, String> data, Address address) {
        String username = data.get("username");
        if (isUsernameTaken(username)) {
            return false;
        }

        String encryptedPass = mainController.caesarCipher(data.get("password"), 1);
        CompanyAccount newCompany = new CompanyAccount(
                data.get("companyName"), address, data.get("ruc"),
                data.get("phone"), data.get("email"), username, encryptedPass
        );

        newCompany.setTwoFactorKey(data.get("twoFactorKey"));

        mainController.getUsers().getCompanyAccounts().add(newCompany);
        mainController.getDataBase().saveUsers(mainController.getUsers());

        FinvoryData initialData = new FinvoryData();
        initialData.setCompanyInfo(newCompany);
        mainController.getDataBase().saveCompanyData(initialData, username);

        syncUsersToCloud();
        return true;
    }

    public boolean registerPersonalGUI(HashMap<String, String> data) {
        String username = data.get("username");
        if (isUsernameTaken(username)) {
            return false;
        }

        String encryptedPass = mainController.caesarCipher(data.get("password"), 1);
        PersonalAccount newPersonal = new PersonalAccount(data.get("fullName"), username, encryptedPass);

        newPersonal.setTwoFactorKey(data.get("twoFactorKey")); 

        mainController.getUsers().getPersonalAccounts().add(newPersonal);
        mainController.getDataBase().saveUsers(mainController.getUsers());

        syncUsersToCloud();
        return true;
    }

    public CompanyAccount findCompanyByName(String companyName) {
        if (companyName == null) {
            return null;
        }
        String query = companyName.toLowerCase().trim();
        for (CompanyAccount company : mainController.getUsers().getCompanyAccounts()) {
            if (company.getName().toLowerCase().contains(query)) {
                return company;
            }
        }
        return null;
    }

    public CompanyAccount findCompanyByUsername(String userName) {
        if (userName == null) {
            return null;
        }
        for (CompanyAccount companyAccount : mainController.getUsers().getCompanyAccounts()) {
            if (companyAccount.getUsername().equals(userName)) {
                return companyAccount;
            }
        }
        return null;
    }

    public PersonalAccount findPersonalByUsername(String userName) {
        if (userName == null) {
            return null;
        }
        for (PersonalAccount personalAccount : mainController.getUsers().getPersonalAccounts()) {
            if (personalAccount.getUsername().equals(userName)) {
                return personalAccount;
            }
        }
        return null;
    }

    public PersonalAccount getLoggedInPersonalAccount() {
        String current = mainController.getCurrentCompanyUsername();
        if (current == null) {
            return null;
        }
        for (PersonalAccount personalAccount : mainController.getUsers().getPersonalAccounts()) {
            if (personalAccount.getUsername().equals(current)) {
                return personalAccount;
            }
        }
        return null;
    }

    public void handleUpdatePersonalProfile(String newFullName, String newPassword) {
        PersonalAccount account = getLoggedInPersonalAccount();
        if (account != null) {
            account.setFullName(newFullName);
            account.setPassword(mainController.caesarCipher(newPassword, 1));
            mainController.getDataBase().saveUsers(mainController.getUsers());
            syncUsersToCloud();
            JOptionPane.showMessageDialog(null, "Perfil actualizado.");
        }
    }

    public boolean isUsernameTaken(String username) {
        if (username == null) {
            return false;
        }
        String user = username.trim();
        SystemUsers su = mainController.getUsers();
        for (CompanyAccount c : su.getCompanyAccounts()) {
            if (c.getUsername().equalsIgnoreCase(user)) {
                return true;
            }
        }
        for (PersonalAccount p : su.getPersonalAccounts()) {
            if (p.getUsername().equalsIgnoreCase(user)) {
                return true;
            }
        }
        return false;
    }

    public String handleUploadPhoto(java.io.File sourceFile, String username, String oldPath) {
        try {
            if (oldPath != null) {
                java.io.File oldFile = new java.io.File(oldPath);
                if (oldFile.exists()) {
                    oldFile.delete();
                }
            }
            java.io.File folder = new java.io.File("data/profiles");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            String ext = sourceFile.getName().substring(sourceFile.getName().lastIndexOf("."));
            java.io.File destFile = new java.io.File(folder, username + "_profile" + ext);
            java.nio.file.Files.copy(sourceFile.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return destFile.getPath();
        } catch (java.io.IOException e) {
            return null;
        }
    }

    private void syncUsersToCloud() {
        try {
            String uri = System.getenv("MONGODB_URI");
            if (uri != null && !uri.isEmpty()) {
                MongoDBConnection connection = new MongoDBConnection();
                MongoDataExporter.exportUsers(mainController.getUsers(), connection.getDatabaseInstance());
                connection.close();
            }
        } catch (Exception e) {
        }
    }

    public boolean handlePasswordChange(String currentAttempt, String newPass) {
        String type = mainController.getUserType();

        if ("COMPANY".equals(type)) {
            CompanyAccount company = mainController.getData().getCompanyInfo();
            if (company != null && company.checkPassword(currentAttempt)) {
                company.setPassword(mainController.caesarCipher(newPass, 1));
                mainController.saveData();
                return true;
            }
        } else if ("PERSONAL".equals(type)) {
            PersonalAccount personal = getLoggedInPersonalAccount();
            if (personal != null && personal.checkPassword(currentAttempt)) {
                personal.setPassword(mainController.caesarCipher(newPass, 1));
                mainController.getDataBase().saveUsers(mainController.getUsers());
                syncUsersToCloud();
                return true;
            }
        }
        return false;
    }

    public void handleUpdatePersonalName(String newFullName) {
        PersonalAccount account = getLoggedInPersonalAccount();
        if (account != null) {
            account.setFullName(newFullName);
            mainController.getDataBase().saveUsers(mainController.getUsers());
            syncUsersToCloud();
            JOptionPane.showMessageDialog(null, "Nombre actualizado correctamente.");
        }
    }
}
