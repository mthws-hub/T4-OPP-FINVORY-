package ec.edu.espe.finvory.utils;

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
        assertFalse(ValidationUtils.isValidIdentification(""), "Identification field should not be empty.");
    }

    @Test
    public void testSingleWordName() {
        assertFalse(ValidationUtils.hasTwoWords("Juan"), "Supplier name must contain at least two words.");
    }

    @Test
    public void testTwoWordName() {
        assertTrue(ValidationUtils.hasTwoWords("Distribuidora Central"), "Should accept names with two or more words.");
    }

    @Test
    public void testPhoneTooShort() {
        assertFalse(ValidationUtils.isValidPhone("09876"), "Phone number must have at least 7 digits.");
    }

    @Test
    public void testPhoneExceedsLimit() {
        assertFalse(ValidationUtils.isValidPhone("09876543210123456"), "Phone number should not exceed 15 digits.");
    }

    @Test
    public void testValidPhone() {
        assertTrue(ValidationUtils.isValidPhone("0999888777"), "A standard valid phone number should be accepted.");
    }

    @Test
    public void testEmailMissingAtSymbol() {
        assertFalse(ValidationUtils.isValidEmail("proveedor.gmail.com"), "Email addresses must contain an '@' symbol.");
    }

    @Test
    public void testEmailMissingDot() {
        assertFalse(ValidationUtils.isValidEmail("proveedor@gmailcom"), "Email addresses must contain a dot '.' in the domain part.");
    }

    @Test
    public void testValidEmail() {
        assertTrue(ValidationUtils.isValidEmail("contacto@empresa.com"), "A correctly structured email address should be valid.");
    }

    @Test
    public void testInternationalPhoneFormat() {
        assertTrue(ValidationUtils.isValidPhone("+593987654321"), "Should support international phone formats starting with '+'.");
    }

    @Test
    public void testNameWithLeadingSpaces_Trimming() {
        assertTrue(ValidationUtils.hasTwoWords("  Global Supplies  "), "Should handle names with leading or trailing whitespaces correctly.");
    }

    @Test
    public void testEmailWithInvalidCharacters() {
        assertFalse(ValidationUtils.isValidEmail("user#name@company.com"), "Emails containing invalid special characters should be rejected.");
    }


    @Test
    public void testMultipleSpaceNameSeparation() {
        assertTrue(ValidationUtils.hasTwoWords("Heavy    Industry"), "Multiple spaces between words should still count as at least two words.");
    }

}