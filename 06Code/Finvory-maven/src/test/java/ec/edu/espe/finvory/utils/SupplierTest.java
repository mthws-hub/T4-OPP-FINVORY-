package ec.edu.espe.finvory.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Maryuri Quiña, @ESPE
 */
public class SupplierTest {
    
    public SupplierTest() {
    }
    
    @Test
    public void testEmptyIDShouldFail() {
        assertFalse(ValidationUtils.isValidIdentification(""), "El ID no puede estar vacío.");
    }

    @Test
    public void testSingleWordNameShouldFail() {
        assertFalse(ValidationUtils.hasTwoWords("Juan"), "El nombre debe contener al menos dos palabras (un espacio).");
    }

    @Test
    public void testTwoWordNameShouldPass() {
        assertTrue(ValidationUtils.hasTwoWords("Distribuidora Central"), "Debería aceptar nombres con dos o más palabras.");
    }

    @Test
    public void testPhoneTooShort() {
        assertFalse(ValidationUtils.isValidPhone("09876"), "El teléfono debe tener al menos 7 dígitos.");
    }

    @Test
    public void testPhoneExceedsLimit() {
        assertFalse(ValidationUtils.isValidPhone("09876543210123456"), "El teléfono no debe exceder los 15 dígitos.");
    }

    @Test
    public void testValidPhone() {
        assertTrue(ValidationUtils.isValidPhone("0999888777"), "Debería aceptar un teléfono válido.");
    }

    @Test
    public void testEmailMissingAtSymbol() {
        assertFalse(ValidationUtils.isValidEmail("proveedor.gmail.com"), "El email debe contener un '@'.");
    }

    @Test
    public void testEmailMissingDot() {
        assertFalse(ValidationUtils.isValidEmail("proveedor@gmailcom"), "El email debe contener un punto '.'.");
    }

    @Test
    public void testValidEmail() {
        assertTrue(ValidationUtils.isValidEmail("contacto@empresa.com"), "Debería aceptar un email con estructura correcta.");
    }
}
