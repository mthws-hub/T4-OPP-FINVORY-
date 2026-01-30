package ec.edu.espe.finvory.controller;

import ec.edu.espe.finvory.utils.GoogleAuthService;
import ec.edu.espe.finvory.view.FrmQRCode;
import java.awt.Component;
import javax.swing.SwingUtilities;

/**
 *
 * @author Arelys Otavalo
 */
public class AuthenticatorController {

    private final FinvoryController mainController;

    public AuthenticatorController(FinvoryController mainController) {
        this.mainController = mainController;
    }

    public String generateAndShowKey(Component parent, String username) {

        String secret = GoogleAuthService.generateSecretKey();

        try {
            java.awt.Frame parentFrame =
                    (java.awt.Frame) SwingUtilities.getWindowAncestor(parent);
            FrmQRCode qrFrame =
                    new FrmQRCode(parentFrame, true, username, secret);

            qrFrame.setVisible(true);

        } catch (Exception e) {
            System.err.println("Error al mostrar QR: " + e.getMessage());
            e.printStackTrace();
        }
        return secret;
    }

    public boolean verifyCode(String secretKey, String codeStr) {
        try {
            int code = Integer.parseInt(codeStr.trim());
            return GoogleAuthService.verifyCode(secretKey, code);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
