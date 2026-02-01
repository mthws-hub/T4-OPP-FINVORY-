package ec.edu.espe.finvory.controller;

import ec.edu.espe.finvory.model.Address;
import ec.edu.espe.finvory.model.CompanyAccount;
import ec.edu.espe.finvory.model.FinvoryData;
import ec.edu.espe.finvory.model.PersonalAccount;
import ec.edu.espe.finvory.view.FrmMainMenu;
import ec.edu.espe.finvory.view.FrmMainMenuPersonalAccount;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        var auth = mainController.getDataBase().auth();

        // 1) LOGIN COMPANY desde Mongo
        CompanyAccount company = auth.findCompanyByUsername(username);
        if (company != null && company.checkPassword(password)) {

            if (!verifyTwoFactor(company.getTwoFactorKey())) {
                return false;
            }

            mainController.setCurrentCompanyUsername(company.getUsername());
            mainController.setUserType("COMPANY");

            FinvoryData loadedData = mainController.getDataBase().loadCompanyData(company.getUsername());
            mainController.setData(loadedData != null ? loadedData : new FinvoryData());

            // Asegura que company info esté en memoria
            mainController.getData().setCompanyInfo(company);

            return true;
        }

        // 2) LOGIN PERSONAL desde Mongo
        PersonalAccount personal = auth.findPersonalByUsername(username);
        if (personal != null && personal.checkPassword(password)) {

            if (!verifyTwoFactor(personal.getTwoFactorKey())) {
                return false;
            }

            // tu controller no tiene currentPersonalUsername, así que reutilizamos este campo
            mainController.setCurrentCompanyUsername(personal.getUsername());
            mainController.setUserType("PERSONAL");
            return true;
        }

        return false;
    }

    private boolean verifyTwoFactor(String secretKey) {
        if (secretKey == null || secretKey.isEmpty()) {
            return true;
        }

        String codeString = JOptionPane.showInputDialog(
                null,
                "Ingrese el código de 6 dígitos de Google Authenticator:",
                "Verificación de Seguridad (2FA)",
                JOptionPane.QUESTION_MESSAGE
        );

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
                data.get("companyName"),
                address,
                data.get("ruc"),
                data.get("phone"),
                data.get("email"),
                username,
                encryptedPass
        );

        newCompany.setTwoFactorKey(data.get("twoFactorKey"));

        // Guardar credenciales y perfil en Mongo
        mainController.getDataBase().auth().upsertCompanyAccount(newCompany);

        // Crear data inicial y guardarla (local + nube si hay internet)
        FinvoryData initialData = new FinvoryData();
        initialData.setCompanyInfo(newCompany);
        mainController.getDataBase().saveCompanyData(initialData, username);

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

        // Guardar en Mongo
        mainController.getDataBase().auth().upsertPersonalAccount(newPersonal);

        return true;
    }

    /**
     * Búsqueda simple: por username (desde Mongo)
     */
    public CompanyAccount findCompanyByUsername(String userName) {
        if (userName == null) {
            return null;
        }
        return mainController.getDataBase().auth().findCompanyByUsername(userName);
    }

    public PersonalAccount findPersonalByUsername(String userName) {
        if (userName == null) {
            return null;
        }
        return mainController.getDataBase().auth().findPersonalByUsername(userName);
    }

    /**
     * Mantengo este método por compatibilidad con tu código actual
     */
    public PersonalAccount getLoggedInPersonalAccount() {
        if (!"PERSONAL".equals(mainController.getUserType())) {
            return null;
        }
        String current = mainController.getCurrentCompanyUsername();
        if (current == null) {
            return null;
        }
        return mainController.getDataBase().auth().findPersonalByUsername(current);
    }

    public void handleUpdatePersonalProfile(String newFullName, String newPassword) {
        PersonalAccount account = getLoggedInPersonalAccount();
        if (account != null) {
            account.setFullName(newFullName);
            account.setPassword(mainController.caesarCipher(newPassword, 1));

            // Persistir en Mongo
            mainController.getDataBase().auth().upsertPersonalAccount(account);

            JOptionPane.showMessageDialog(null, "Perfil actualizado.");
        }
    }

    public boolean isUsernameTaken(String username) {
        return mainController.getDataBase().auth().isUsernameTaken(username);
    }

    /**
     * Guarda fotos en carpeta del usuario, no en "data/" del proyecto.
     */
    public String handleUploadPhoto(java.io.File sourceFile, String username, String oldPath) {
        try {
            if (oldPath != null) {
                java.io.File oldFile = new java.io.File(oldPath);
                if (oldFile.exists()) {
                    oldFile.delete();
                }
            }

            Path folder = getProfilesDir();
            Files.createDirectories(folder);

            String name = sourceFile.getName();
            String ext = "";
            int dot = name.lastIndexOf(".");
            if (dot >= 0) {
                ext = name.substring(dot);
            }

            Path destFile = folder.resolve(username + "_profile" + ext);
            java.nio.file.Files.copy(
                    sourceFile.toPath(),
                    destFile,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );

            return destFile.toString();
        } catch (Exception e) {
            return null;
        }
    }

    private Path getProfilesDir() {
        // Windows: LOCALAPPDATA\Finvory\profiles
        String localAppData = System.getenv("LOCALAPPDATA");
        if (localAppData != null && !localAppData.isBlank()) {
            return Paths.get(localAppData, "Finvory", "profiles");
        }
        // Fallback: user.home/.finvory/profiles
        return Paths.get(System.getProperty("user.home"), ".finvory", "profiles");
    }

    public boolean handlePasswordChange(String currentAttempt, String newPass) {
        String type = mainController.getUserType();

        if ("COMPANY".equals(type)) {
            CompanyAccount company = (mainController.getData() != null) ? mainController.getData().getCompanyInfo() : null;
            if (company != null && company.checkPassword(currentAttempt)) {
                company.setPassword(mainController.caesarCipher(newPass, 1));

                // Persistir credenciales en Mongo
                mainController.getDataBase().auth().upsertCompanyAccount(company);

                // Persistir data (local + nube si online)
                mainController.saveData();
                return true;
            }
        } else if ("PERSONAL".equals(type)) {
            PersonalAccount personal = getLoggedInPersonalAccount();
            if (personal != null && personal.checkPassword(currentAttempt)) {
                personal.setPassword(mainController.caesarCipher(newPass, 1));

                // Persistir en Mongo
                mainController.getDataBase().auth().upsertPersonalAccount(personal);

                return true;
            }
        }
        return false;
    }

    public void handleUpdatePersonalName(String newFullName) {
        PersonalAccount account = getLoggedInPersonalAccount();
        if (account != null) {
            account.setFullName(newFullName);

            // Persistir en Mongo
            mainController.getDataBase().auth().upsertPersonalAccount(account);

            JOptionPane.showMessageDialog(null, "Nombre actualizado correctamente.");
        }
    }
}
