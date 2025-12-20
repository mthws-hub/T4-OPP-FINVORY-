package ec.edu.espe.finvory.utils;

import java.net.URL;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * * @author Mathews Pastor, The POOwer Rangers Of Programming
 */

public class InventoryValidationTest {

    public InventoryValidationTest() {
    }

    @Test
    public void testIsEmpty() {
        System.out.println("isEmpty");
        assertTrue(ValidationUtils.isEmpty(""), "Debería ser true para cadena vacía");
        assertTrue(ValidationUtils.isEmpty(null), "Debería ser true para null");
        assertTrue(ValidationUtils.isEmpty("   "), "Debería ser true para espacios en blanco");
        assertFalse(ValidationUtils.isEmpty("Hola"), "Debería ser false para texto válido");
    }

    @Test
    public void testIsValidEmail() {
        System.out.println("isValidEmail");
        assertTrue(ValidationUtils.isValidEmail("usuario@dominio.com"), "Email válido debería pasar");
        assertFalse(ValidationUtils.isValidEmail("usuario@.com"), "Email sin dominio debería fallar");
        assertFalse(ValidationUtils.isValidEmail("usuariodominio.com"), "Email sin @ debería fallar");
    }

    @Test
    public void testIsValidPhone() {
        System.out.println("isValidPhone");
        // Según tu REGEX_PHONE_GENERAL: ^\+?[0-9\s]{7,15}$
        assertTrue(ValidationUtils.isValidPhone("0991234567"), "Teléfono local válido");
        assertTrue(ValidationUtils.isValidPhone("+593 991234567"), "Teléfono internacional con espacios válido");
        assertFalse(ValidationUtils.isValidPhone("123"), "Teléfono muy corto debería fallar");
        assertFalse(ValidationUtils.isValidPhone("celular"), "Texto no numérico debería fallar");
    }

    @Test
    public void testIsValidPhone10Digits() {
        System.out.println("isValidPhone10Digits");
        assertTrue(ValidationUtils.isValidPhone10Digits("0991234567"), "10 dígitos exactos debería pasar");
        assertFalse(ValidationUtils.isValidPhone10Digits("099123456"), "9 dígitos debería fallar");
        assertFalse(ValidationUtils.isValidPhone10Digits("09912345678"), "11 dígitos debería fallar");
    }

    @Test
    public void testIsValidIdentification() {
        System.out.println("isValidIdentification");
        assertTrue(ValidationUtils.isValidIdentification("1712345678"), "Cédula (10 dígitos) válida");
        assertTrue(ValidationUtils.isValidIdentification("1712345678001"), "RUC (13 dígitos) válido");
        assertFalse(ValidationUtils.isValidIdentification("12345"), "ID incompleto debería fallar");
    }

    @Test
    public void testIsNumeric() {
        System.out.println("isNumeric");
        assertTrue(ValidationUtils.isNumeric("123"), "Entero es numérico");
        assertTrue(ValidationUtils.isNumeric("123.45"), "Decimal es numérico");
        assertTrue(ValidationUtils.isNumeric("-10"), "Negativo es numérico");
        assertFalse(ValidationUtils.isNumeric("123a"), "Alfanumérico no es numérico");
    }

    @Test
    public void testIsTextOnly() {
        System.out.println("isTextOnly");
        assertTrue(ValidationUtils.isTextOnly("Juan Perez"), "Solo letras y espacios debería pasar");
        assertTrue(ValidationUtils.isTextOnly("Ñandú"), "Caracteres especiales latinos deberían pasar");
        assertFalse(ValidationUtils.isTextOnly("Juan123"), "Números deberían fallar");
    }

    @Test
    public void testIsStrictRuc() {
        System.out.println("isStrictRuc");
        assertTrue(ValidationUtils.isStrictRuc("0012345678001"), "RUC que inicia con 001 y tiene 13 dígitos");
        assertFalse(ValidationUtils.isStrictRuc("1712345678001"), "RUC que NO inicia con 001 debería fallar (según lógica actual)");
        assertFalse(ValidationUtils.isStrictRuc("001"), "RUC incompleto debería fallar");
    }

    @Test
    public void testHasTwoWords() {
        System.out.println("hasTwoWords");
        assertTrue(ValidationUtils.hasTwoWords("Juan Perez"), "Dos palabras separadas por espacio");
        assertFalse(ValidationUtils.hasTwoWords("Juan"), "Una sola palabra debería fallar");
        assertFalse(ValidationUtils.hasTwoWords(""), "Cadena vacía debería fallar");
    }

    @Test
    public void testIsPositiveDecimal() {
        System.out.println("isPositiveDecimal");
        assertTrue(ValidationUtils.isPositiveDecimal("10.50"), "Decimal positivo válido");
        assertTrue(ValidationUtils.isPositiveDecimal("5"), "Entero positivo válido");
        assertFalse(ValidationUtils.isPositiveDecimal("0"), "Cero no es estrictamente positivo (según compareTo > 0)");
        assertFalse(ValidationUtils.isPositiveDecimal("-5.0"), "Negativo debería fallar");
        assertFalse(ValidationUtils.isPositiveDecimal("abc"), "Texto debería fallar");
    }

    @Test
    public void testIsNonNegativeInteger() {
        System.out.println("isNonNegativeInteger");
        assertTrue(ValidationUtils.isNonNegativeInteger("5"), "Entero positivo válido");
        assertTrue(ValidationUtils.isNonNegativeInteger("0"), "Cero es no-negativo válido");
        assertFalse(ValidationUtils.isNonNegativeInteger("-1"), "Negativo debería fallar");
        assertFalse(ValidationUtils.isNonNegativeInteger("5.5"), "Decimal debería fallar (se espera entero)");
    }

    @Test
    public void testValidateCustomerFields() {
        System.out.println("validateCustomerFields");

        String result = ValidationUtils.validateCustomerFields("Juan", "1712345678", "0991234567", "juan@mail.com", "VIP");
        assertNull(result, "Si todo está bien, retorna null");

        result = ValidationUtils.validateCustomerFields("", "1712345678", "0991234567", "juan@mail.com", "VIP");
        assertNotNull(result, "Debería retornar mensaje de error si falta nombre");
        assertTrue(result.contains("Nombre es obligatorio"));

        result = ValidationUtils.validateCustomerFields("Juan", "1712345678", "0991234567", "juan@mail.com", "Seleccionar...");
        assertNotNull(result);
        assertTrue(result.contains("Tipo de Cliente"));
    }

    @Test
    public void testValidateProductFields() {
        System.out.println("validateProductFields");

        String result = ValidationUtils.validateProductFields("PROD01", "Laptop", "500.00", "Dell", "10", true);
        assertNull(result, "Datos válidos retornan null");

        result = ValidationUtils.validateProductFields("PROD01", "Laptop", "-500", "Dell", "10", true);
        assertNotNull(result);
        assertTrue(result.contains("Precio Base de Costo debe ser un valor numérico positivo"));

        result = ValidationUtils.validateProductFields("PROD01", "Laptop", "500", "Dell", "-5", true);
        assertNotNull(result);
        assertTrue(result.contains("Stock Inicial debe ser un número entero no negativo"));
    }

    @Test
    public void testGetAddressFormError() {
        System.out.println("getAddressFormError");
        String result = ValidationUtils.getAddressFormError("Casa", "Ecuador", "Quito", "Av Amazonas");
        assertNull(result);

        result = ValidationUtils.getAddressFormError("Casa", "Ecuador1", "Quito", "Av Amazonas");
        assertNotNull(result);
        assertTrue(result.contains("País y Ciudad solo deben contener letras"));
    }

    @Test
    public void testGetSearchError() {
        System.out.println("getSearchError");
        assertNull(ValidationUtils.getSearchError("Computadora"));
        assertNotNull(ValidationUtils.getSearchError(""));
        String result = ValidationUtils.getSearchError("ab");
        assertNotNull(result);
        assertTrue(result.contains("Ingrese al menos 3 caracteres"));
    }

    @Test
    public void testGetPriceConfigError() {
        System.out.println("getPriceConfigError");
        String result = ValidationUtils.getPriceConfigError("0.30", "0.0", "0.05", "0.10", "0.12");
        assertNull(result, "Configuración válida retorna null");
        result = ValidationUtils.getPriceConfigError("0.05", "0.0", "0.02", "0.10", "0.12");
        assertNotNull(result);
        assertTrue(result.contains("El descuento VIP no puede ser mayor o igual a la Ganancia"));
        result = ValidationUtils.getPriceConfigError("abc", "0.0", "0.05", "0.10", "0.12");
        assertNotNull(result);
        assertTrue(result.contains("Todos los valores deben ser números positivos"));
    }

    @Test
    public void testGetScaledIcon() {
        System.out.println("getScaledIcon");
        ImageIcon result = ValidationUtils.getScaledIcon(null, 100, 100);
        assertNull(result, "URL null debe retornar null");
    }
}
