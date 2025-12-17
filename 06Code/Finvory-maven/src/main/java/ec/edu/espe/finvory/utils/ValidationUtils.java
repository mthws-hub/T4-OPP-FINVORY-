package ec.edu.espe.finvory.utils;

import java.util.regex.Pattern;

/**
 *
 * @author Arelys
 */
public class ValidationUtils {

    public static final Pattern REGEX_EMAIL = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    public static final Pattern REGEX_PHONE = Pattern.compile("^\\+?[0-9\\s]{7,15}$");
    public static final Pattern REGEX_ID = Pattern.compile("^\\d{10}$|^\\d{13}$");
    public static final Pattern REGEX_NUMERIC = Pattern.compile("^-?[0-9]+(\\.[0-9]+)?$");
    public static final Pattern REGEX_TEXT_ONLY = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$");
    public static final Pattern REGEX_RUC_STRICT = Pattern.compile("^\\d{13}$");

    public static boolean validate(String value, Pattern regex) {
        if (value == null) {
            return false;
        }
        return regex.matcher(value).matches();
    }

    public static boolean isValidEmail(String email) {
        return validate(email, REGEX_EMAIL);
    }

    public static boolean isValidPhone(String phone) {
        return validate(phone, REGEX_PHONE);
    }

    public static boolean isValidIdentification(String id) {
        return validate(id, REGEX_ID);
    }

    public static boolean isNumeric(String value) {
        return validate(value, REGEX_NUMERIC);
    }

    public static String validateCustomerFields(String name, String identification, String phone, String email, String clientType) {

        if (name == null || name.trim().isEmpty()) {
            return "Error: El campo Nombre es obligatorio.";
        }
        if (identification == null || identification.trim().isEmpty()) {
            return "Error: El campo Identificación es obligatorio.";
        }
        if (phone == null || phone.trim().isEmpty()) {
            return "Error: El campo Teléfono es obligatorio.";
        }
        if (email == null || email.trim().isEmpty()) {
            return "Error: El campo Email es obligatorio.";
        }
        if (clientType == null || clientType.trim().isEmpty() || clientType.equals("Seleccionar...")) {
            return "Error: Debe seleccionar un Tipo de Cliente.";
        }

        if (!isValidIdentification(identification.trim())) {
            return "Error: La identificación (ID) debe tener 10 o 13 dígitos numéricos.";
        }

        if (!isValidEmail(email.trim())) {
            return "Error: El formato del correo electrónico es incorrecto.";
        }

        if (!isValidPhone(phone.trim())) {
            return "Error: El formato del teléfono es incorrecto (mínimo 7 dígitos, permite '+' o espacios).";
        }

        return null;
    }

    public static boolean isPositiveDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        try {
            java.math.BigDecimal decimal = new java.math.BigDecimal(value.trim());
            return decimal.compareTo(java.math.BigDecimal.ZERO) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNonNegativeInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        try {
            int i = Integer.parseInt(value.trim());
            return i >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String validateProductFields(String id, String name, String costPrice, String supplierName, String initialStock, boolean addingStock) {
        if (id == null || id.trim().isEmpty()) {
            return "Error: El campo ID del producto es obligatorio.";
        }
        if (name == null || name.trim().isEmpty()) {
            return "Error: El campo Nombre del producto es obligatorio.";
        }
        if (costPrice == null || costPrice.trim().isEmpty()) {
            return "Error: El campo Precio Base de Costo es obligatorio.";
        }
        if (supplierName == null || supplierName.trim().isEmpty() || supplierName.equals("Seleccionar...")) {
            return "Error: Debe seleccionar un Proveedor.";
        }

        if (!isPositiveDecimal(costPrice.trim())) {
            return "Error: El Precio Base de Costo debe ser un valor numérico positivo (ej: 10.50).";
        }

        if (addingStock) {
            if (initialStock == null || initialStock.trim().isEmpty()) {
                return "Error: El campo Stock Inicial es obligatorio al añadir a un inventario.";
            }
            if (!isNonNegativeInteger(initialStock.trim())) {
                return "Error: El Stock Inicial debe ser un número entero no negativo (0 o más).";
            }
        }

        return null;
    }

    public static boolean isTextOnly(String text) {
        return validate(text, REGEX_TEXT_ONLY);
    }

    public static boolean isStrictRuc(String ruc) {
        return validate(ruc, REGEX_RUC_STRICT) && ruc.endsWith("001");
    }

    private static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static String getPersonalFormError(String fullName, String user, String pass) {
        if (isEmpty(fullName) || isEmpty(user) || isEmpty(pass)) {
            return "Todos los campos son obligatorios.";
        }

        if (!isTextOnly(fullName)) {
            return "El nombre solo debe contener letras";
        }

        return null;
    }

    public static String getCompanyFormError(String name, String ruc, String phone, String email, String country, String city, String street, String user, String pass) {
        if (isEmpty(name) || isEmpty(ruc) || isEmpty(phone) || isEmpty(email)
                || isEmpty(country) || isEmpty(city) || isEmpty(street) || isEmpty(user) || isEmpty(pass)) {
            return "Todos los campos son obligatorios";
        }
        if (!isTextOnly(name)) {
            return "El nombre solo debería contener letras";
        }
        if (!isTextOnly(country)) {
            return "El país solo debe contener letras";
        }
        if (!isTextOnly(city)) {
            return "La ciudad solo debe contener letras";
        }
        if (!isStrictRuc(ruc)) {
            return "RUC debe tener 13 dígitos y empezar con '001'.";
        }
        if (!validate(phone, Pattern.compile("^\\d+$"))) {
            return "El celular solo debe tener numeros";
        }
        if (phone.length() != 10) {
            return "El celular debe tener 10 dígitos";
        }

        if (!validate(email, REGEX_EMAIL)) {
            return "Formato de email inválido (revise el @)";
        }

        return null;
    }

    public static String getAddressFormError(String name, String country, String city, String street) {
        if (isEmpty(name) || isEmpty(country) || isEmpty(city) || isEmpty(street)) {
            return "Todos los campos (Nombre, País, Ciudad, Calle) son obligatorios.";
        }
        if (!isTextOnly(country) || !isTextOnly(city)) {
            return "País y Ciudad solo deben contener letras.";
        }
        return null;
    }

    public static String getPriceConfigError(String profitString, String standardString, String premiumString, String vipString, String taxString) {

        if (isEmpty(profitString) || isEmpty(standardString) || isEmpty(premiumString) || isEmpty(vipString) || isEmpty(taxString)) {
            return "Todos los campos son obligatorios.";
        }
        if (!isPositiveDecimal(profitString) || !isPositiveDecimal(standardString)
                || !isPositiveDecimal(premiumString) || !isPositiveDecimal(vipString) || !isPositiveDecimal(taxString)) {
            return "Todos los valores deben ser números positivos (Ej: 0.15).";
        }

        try {
            float profit = Float.parseFloat(profitString);
            float standard = Float.parseFloat(standardString);
            float premium = Float.parseFloat(premiumString);
            float vip = Float.parseFloat(vipString);
            float tax = Float.parseFloat(taxString);

            if (profit > 2.0f) {
                return "La ganancia no debería exceder el 200% (2.0).";
            }
            if (standard > 1.0f || premium > 1.0f || vip > 1.0f || tax > 1.0f) {
                return "Los porcentajes (Descuentos/Impuesto) deben estar entre 0 y 1 (Ej: 0.12 para 12%).";
            }
            if (tax <= 0) {
                return "La tasa de impuesto debe ser mayor a 0.";
            }
            if (profit <= 0) {
                return "La ganancia debe ser mayor a 0.";
            }
            if (standard >= premium) {
                return "El descuento Standard debe ser menor que el Premium.";
            }
            if (premium >= vip) {
                return "El descuento Premium debe ser menor que el VIP.";
            }
            if (vip >= profit) {
                return "El descuento VIP no puede ser mayor o igual a la Ganancia (Margen negativo).";
            }

        } catch (NumberFormatException e) {
            return "Formato numérico inválido.";
        }
        return null;
    }

    public static javax.swing.ImageIcon getScaledIcon(java.net.URL url, int containerWidth, int containerHeight) {
        if (url == null) {
            return null;
        }

        javax.swing.ImageIcon original = new javax.swing.ImageIcon(url);
        int imgW = original.getIconWidth();
        int imgH = original.getIconHeight();
        double ratio = Math.min((double) containerWidth / imgW, (double) containerHeight / imgH);
        int newWidth = (int) (imgW * ratio);
        int newHeight = (int) (imgH * ratio);
        java.awt.Image scaled = original.getImage().getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH);
        return new javax.swing.ImageIcon(scaled);
    }

    public static String getSearchError(String query) {
        if (isEmpty(query)) {
            return "El campo de búsqueda no puede estar vacío.";
        }
        if (query.length() < 3) {
            return "Ingrese al menos 3 caracteres para buscar.";
        }
        return null;
    }
}
