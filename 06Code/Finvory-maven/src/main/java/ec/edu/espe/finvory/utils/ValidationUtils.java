package ec.edu.espe.finvory.utils;

import ec.edu.espe.finvory.model.Address;
import java.util.regex.Pattern;
import java.math.BigDecimal;

/**
 *
 *
 * * @author Arelys, The POOwer Rangers Of Programming
 */
public class ValidationUtils {

    public static final Pattern REGEX_EMAIL = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    public static final Pattern REGEX_PHONE_GENERAL = Pattern.compile("^\\+?[0-9\\s]{7,15}$");
    public static final Pattern REGEX_PHONE_10_DIGITS = Pattern.compile("^\\d{10}$");
    public static final Pattern REGEX_ID = Pattern.compile("^\\d{10}$|^\\d{13}$");
    public static final Pattern REGEX_NUMERIC = Pattern.compile("^-?[0-9]+(\\.[0-9]+)?$");
    public static final Pattern REGEX_INTEGER_ONLY = Pattern.compile("^\\d+$");
    public static final Pattern REGEX_TEXT_ONLY = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$");
    public static final Pattern REGEX_RUC_STRICT = Pattern.compile("^\\d{13}$");

    public static boolean validate(String value, Pattern regex) {
        if (value == null) {
            return false;
        }
        return regex.matcher(value.trim()).matches();
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return validate(email, REGEX_EMAIL);
    }

    public static boolean isValidPhone(String phone) {
        return validate(phone, REGEX_PHONE_GENERAL);
    }

    public static boolean isValidPhone10Digits(String phone) {
        return validate(phone, REGEX_PHONE_10_DIGITS);
    }

    public static boolean isValidIdentification(String id) {
        return validate(id, REGEX_ID);
    }
    
    public static boolean isValidInteger(String value){
        return validate(value, REGEX_INTEGER_ONLY);
    }

    public static boolean isNumeric(String value) {
        return validate(value, REGEX_NUMERIC);
    }

    public static boolean isTextOnly(String text) {
        return validate(text, REGEX_TEXT_ONLY);
    }

    public static boolean isStrictRuc(String ruc) {
        return validate(ruc, REGEX_RUC_STRICT) && ruc.trim().startsWith("001");
    }

    public static boolean hasTwoWords(String text) {
        if (isEmpty(text)) {
            return false;
        }
        String t = text.trim();
        return t.indexOf(" ") > 0 && t.indexOf(" ") < t.length() - 1 && isTextOnly(t);
    }

    public static boolean isPositiveDecimal(String value) {
        if (isEmpty(value)) {
            return false;
        }
        try {
            BigDecimal decimal = new BigDecimal(value.trim());
            return decimal.compareTo(BigDecimal.ZERO) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNonNegativeInteger(String value) {
        if (isEmpty(value)) {
            return false;
        }
        try {
            int i = Integer.parseInt(value.trim());
            return i >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String validateCustomerFields(String name, String identification, String phone, String email, String clientType) {
        if (isEmpty(name)) {
            return "Error: El campo Nombre es obligatorio.";
        }
        if (isEmpty(identification)) {
            return "Error: El campo Identificación es obligatorio.";
        }
        if (isEmpty(phone)) {
            return "Error: El campo Teléfono es obligatorio.";
        }
        if (isEmpty(email)) {
            return "Error: El campo Email es obligatorio.";
        }
        if (isEmpty(clientType) || clientType.equals("Seleccionar...")) {
            return "Error: Debe seleccionar un Tipo de Cliente.";
        }

        if (!isValidIdentification(identification)) {
            return "Error: La identificación (ID) debe tener 10 o 13 dígitos numéricos.";
        }
        if (!isValidEmail(email)) {
            return "Error: El formato del correo electrónico es incorrecto.";
        }
        if (!isValidPhone(phone)) {
            return "Error: El formato del teléfono es incorrecto (mínimo 7 dígitos, permite '+' o espacios).";
        }
        return null;
    }

    public static String validateProductFields(String id, String name, String costPrice, String supplierName, String initialStock, boolean addingStock) {
        if (isEmpty(id)) {
            return "Error: El campo ID del producto es obligatorio.";
        }
        if (isEmpty(name)) {
            return "Error: El campo Nombre del producto es obligatorio.";
        }
        if (isEmpty(costPrice)) {
            return "Error: El campo Precio Base de Costo es obligatorio.";
        }
        if (isEmpty(supplierName) || supplierName.equals("Seleccionar...")) {
            return "Error: Debe seleccionar un Proveedor.";
        }

        if (!isPositiveDecimal(costPrice)) {
            return "Error: El Precio Base de Costo debe ser un valor numérico positivo (ej: 10.50).";
        }

        if (addingStock) {
            if (isEmpty(initialStock)) {
                return "Error: El campo Stock Inicial es obligatorio al añadir a un inventario.";
            }
            if (!isNonNegativeInteger(initialStock)) {
                return "Error: El Stock Inicial debe ser un número entero no negativo.";
            }
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

    public static String getSearchError(String query) {
        if (isEmpty(query)) {
            return "El campo de búsqueda no puede estar vacío.";
        }
        if (query.trim().length() < 3) {
            return "Ingrese al menos 3 caracteres para buscar.";
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
                return "Los porcentajes deben estar entre 0 y 1.";
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
                return "El descuento VIP no puede ser mayor o igual a la Ganancia.";
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
    public static final Pattern REGEX_STREET_NUMBER = Pattern.compile("^[a-zA-Z0-9\\s#\\-\\/\\.]+$");

    public static boolean isValidRegion(String region) {
        return validate(region, REGEX_TEXT_ONLY);
    }

    public static boolean isValidStreetNumber(String streetNumber) {
        return validate(streetNumber, REGEX_STREET_NUMBER);
    }

    public static String validateInventoryAddress(Address address) {
        if (address == null) {
            return "La dirección no puede ser nula.";
        }

        if (!isTextOnly(address.getRegion())) {
            return "Error: La región solo debe contener letras.";
        }

        if (!isValidStreetNumber(address.getStreetNumber())) {
            return "Error: El número de calle contiene caracteres no permitidos.";
        }
        return getAddressFormError("Inventario", address.getCountry(), address.getCity(), address.getStreet());
    }
    
    public static boolean isValidQuantity(String value) {
        if (isEmpty(value)) {
            return false;
        }
        try {
            int quantity = Integer.parseInt(value.trim());
            return quantity > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
