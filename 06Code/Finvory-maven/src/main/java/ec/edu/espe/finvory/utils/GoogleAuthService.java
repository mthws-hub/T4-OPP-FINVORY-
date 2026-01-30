package ec.edu.espe.finvory.utils;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

public class GoogleAuthService {

    private static final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    public static String generateSecretKey() {
        final GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    public static boolean verifyCode(String secretKey, int code) {
        if (secretKey == null || secretKey.isEmpty()) {
            return false;
        }
        return gAuth.authorize(secretKey, code);
    }
}
