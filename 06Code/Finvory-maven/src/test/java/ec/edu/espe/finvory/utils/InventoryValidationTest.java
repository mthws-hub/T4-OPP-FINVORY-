package ec.edu.espe.finvory.utils;

import ec.edu.espe.finvory.model.Address;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 *
 * * @author Maryuri Quiña, @ESPE
 */

public class InventoryValidationTest {

    @Test
    public void testRegion_SimpleName() {
        assertTrue(ValidationUtils.isValidRegion("Pichincha"));
    }

    @Test
    public void testRegion_CompositeName() {
        assertTrue(ValidationUtils.isValidRegion("Santo Domingo"));
    }

    @Test
    public void testRegion_WithAccents() {
        assertTrue(ValidationUtils.isValidRegion("Manabí"));
    }

    @Test
    public void testRegion_WithEnie() {
        assertTrue(ValidationUtils.isValidRegion("Cañar"));
    }

    @Test
    public void testRegion_WithNumbers() {
        assertFalse(ValidationUtils.isValidRegion("Pichincha1"));
    }

    @Test
    public void testRegion_SpecialCharacters() {
        assertFalse(ValidationUtils.isValidRegion("Azuay@"));
    }

    @Test
    public void testRegion_Empty() {
        assertFalse(ValidationUtils.isValidRegion(""));
    }

    @Test
    public void testStreet_OnlyNumbers() {
        assertTrue(ValidationUtils.isValidStreetNumber("123"));
    }

    @Test
    public void testStreet_NomenclatureFormat() {
        assertTrue(ValidationUtils.isValidStreetNumber("N45-23"));
    }

    @Test
    public void testStreet_NoNumber() {
        assertTrue(ValidationUtils.isValidStreetNumber("S/N"));
    }

    @Test
    public void testStreet_WithHashSymbol() {
        assertTrue(ValidationUtils.isValidStreetNumber("Casa #45"));
    }

    @Test
    public void testStreet_WithDot() {
        assertTrue(ValidationUtils.isValidStreetNumber("Av. 6 de Diciembre"));
    }

    @Test
    public void testStreet_ForbiddenDollarChar() {
        assertFalse(ValidationUtils.isValidStreetNumber("N45$23"));
    }

    @Test
    public void testStreet_ForbiddenPercentChar() {
        assertFalse(ValidationUtils.isValidStreetNumber("Calle 100%"));
    }

    @Test
    public void testStreet_Empty() {
        assertFalse(ValidationUtils.isValidStreetNumber(""));
    }

    @Test
    public void testAddress_CompleteAndCorrect() {
        Address addr = new Address();
        addr.setCountry("Ecuador");
        addr.setCity("Quito");
        addr.setStreet("Av Amazonas");
        addr.setRegion("Pichincha");
        addr.setStreetNumber("N45-23");

        assertNull(ValidationUtils.validateInventoryAddress(addr));
    }

    @Test
    public void testAddress_InvalidRegion() {
        Address addr = new Address();
        addr.setCountry("Ecuador");
        addr.setCity("Quito");
        addr.setStreet("Av Amazonas");
        addr.setRegion("Pichincha123");
        addr.setStreetNumber("N45-23");

        String result = ValidationUtils.validateInventoryAddress(addr);
        assertNotNull(result);
        assertTrue(result.toLowerCase().contains("región") || result.contains("Region"));
    }

    @Test
    public void testAddress_InvalidStreet() {
        Address addr = new Address();
        addr.setCountry("Ecuador");
        addr.setCity("Quito");
        addr.setStreet("Av Amazonas");
        addr.setRegion("Pichincha");
        addr.setStreetNumber("N45$23");

        String result = ValidationUtils.validateInventoryAddress(addr);
        assertNotNull(result);
        assertTrue(result.toLowerCase().contains("calle") || result.contains("Street"));
    }

    @Test
    public void testAddress_NullObject() {
        String result = ValidationUtils.validateInventoryAddress(null);
        assertNotNull(result);
    }

        @Test
        public void testIsTextOnly_WithValidName() {
            assertTrue(ValidationUtils.isTextOnly("Juan Perez"),
                    "Should accept names containing only letters and spaces");
        }

        @Test
        public void testIsTextOnly_WithAlphanumericInput() {
            assertFalse(ValidationUtils.isTextOnly("Bodega123!"),
                    "Should reject input containing numbers or special symbols");
        }

        @Test
        public void testIsTextOnly_WithInvalidCountryName() {
            assertFalse(ValidationUtils.isTextOnly("Ecuador2024"),
                    "Country names should not contain numeric characters");
        }

        @Test
        public void testIsTextOnly_WithValidStreetName() {
            assertTrue(ValidationUtils.isTextOnly("Avenida Amazonas"),
                    "Should accept street names consisting solely of letters");
        }

        @Test
        public void testIsTextOnly_WithNumericStreetName() {
            assertFalse(ValidationUtils.isTextOnly("Calle 123"),
                    "Should reject street names that include numbers under strict text validation");
        }

        @Test
        public void testIsNonNegativeInteger_WithValidInput() {
            assertTrue(ValidationUtils.isNonNegativeInteger("455"),
                    "Should identify '455' as a valid non-negative integer");
        }

        @Test
        public void testIsNonNegativeInteger_WithAlphabeticInput() {
            assertFalse(ValidationUtils.isNonNegativeInteger("CasaDiez"),
                    "Numeric fields must not contain alphabetic characters");
        }

        @Test
        public void testIsNonNegativeInteger_WithDecimalInput() {
            assertFalse(ValidationUtils.isNonNegativeInteger("123.5"),
                    "The validator should reject decimal numbers for integer-only fields");
        }

        @Test
        public void testIsTextOnly_WithSpecialCharactersInRegion() {
            assertFalse(ValidationUtils.isTextOnly("Sierra@"),
                    "Region names should be rejected if they contain special characters");
        }

        @Test
        public void testGetAddressFormError_WithMissingRequiredFields() {
            String error = ValidationUtils.getAddressFormError("", "Ecuador", "Quito", "");
            assertNotNull(error, "An error message should be returned when required fields are empty");
            assertEquals("Todos los campos (Nombre, País, Ciudad, Calle) son obligatorios.", error,
                    "The error message must match the predefined system validation message");
        }

        @Test
        public void testIsTextOnly_WithEmptyString() {
            assertFalse(ValidationUtils.isTextOnly(""),
                    "An empty string should not be considered valid text only input");
        }

        @Test
        public void testIsNonNegativeInteger_WithNegativeValue() {
            assertFalse(ValidationUtils.isNonNegativeInteger("-10"),
                    "Negative values should be rejected by the non-negative integer validator");
        }
    
}
