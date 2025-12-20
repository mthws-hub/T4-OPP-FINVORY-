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
    
    public InventoryValidationTest() {
    }
    
    @Test
    public void testRegion_SimpleName_Valid() {
        assertTrue(ValidationUtils.isValidRegion("Pichincha"));
    }

    @Test
    public void testRegion_CompositeName_Valid() {
        assertTrue(ValidationUtils.isValidRegion("Santo Domingo"));
    }

    @Test
    public void testRegion_WithAccents_Valid() {
        assertTrue(ValidationUtils.isValidRegion("Manabí"));
    }
    
    @Test
    public void testRegion_WithEnie_Valid() {
        assertTrue(ValidationUtils.isValidRegion("Cañar"));
    }

    @Test
    public void testRegion_WithHyphen_Valid() {
        assertTrue(ValidationUtils.isValidRegion("Morona-Santiago"), 
                   "Should accept region names with hyphens (Ex: Morona-Santiago)");
    }

    @Test
    public void testRegion_WithNumbers_Invalid() {
        assertFalse(ValidationUtils.isValidRegion("Pichincha1"));
    }

    @Test
    public void testRegion_SpecialCharacters_Invalid() {
        assertFalse(ValidationUtils.isValidRegion("Azuay@"));
    }
    
    @Test
    public void testRegion_Empty_Invalid() {
        assertFalse(ValidationUtils.isValidRegion(""));
    }

    @Test
    public void testStreet_OnlyNumbers_Valid() {
        assertTrue(ValidationUtils.isValidStreetNumber("123"));
    }

    @Test
    public void testStreet_NomenclatureFormat_Valid() {
        assertTrue(ValidationUtils.isValidStreetNumber("N45-23"));
    }

    @Test
    public void testStreet_NoNumber_Valid() {
        assertTrue(ValidationUtils.isValidStreetNumber("S/N"));
    }

    @Test
    public void testStreet_WithHashSymbol_Valid() {
        assertTrue(ValidationUtils.isValidStreetNumber("Casa #45"));
    }
    
    @Test
    public void testStreet_WithDot_Valid() {
        assertTrue(ValidationUtils.isValidStreetNumber("Av. 6 de Diciembre"));
    }

    @Test
    public void testStreet_WithComma_Valid() {
        assertTrue(ValidationUtils.isValidStreetNumber("N45-23, PB"),
                   "Should accept commas in the address to separate details.");
    }

    @Test
    public void testStreet_ForbiddenDollarChar_Invalid() {
        assertFalse(ValidationUtils.isValidStreetNumber("N45$23"));
    }

    @Test
    public void testStreet_ForbiddenPercentChar_Invalid() {
        assertFalse(ValidationUtils.isValidStreetNumber("Calle 100%"));
    }
    
    @Test
    public void testStreet_Empty_Invalid() {
        assertFalse(ValidationUtils.isValidStreetNumber(""));
    }
    
    @Test
    public void testAddress_CompleteAndCorrect_ReturnsNull() {
        Address addr = new Address();
        addr.setCountry("Ecuador");
        addr.setCity("Quito");
        addr.setStreet("Av Amazonas");
        addr.setRegion("Pichincha");
        addr.setStreetNumber("N45-23");
        
        assertNull(ValidationUtils.validateInventoryAddress(addr));
    }

    @Test
    public void testAddress_InvalidRegion_ReturnsError() {
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
    public void testAddress_InvalidStreet_ReturnsError() {
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
    public void testAddress_NullObject_ReturnsError() {
        String result = ValidationUtils.validateInventoryAddress(null);
        assertNotNull(result);
    }
    
    @Test
    public void testIsTextOnly_PositiveCase() {
        assertTrue(ValidationUtils.isTextOnly("Juan Perez"), "Debería aceptar nombres con solo letras y espacios.");
    }

    @Test
    public void testIsTextOnly_InvalidName() {
        assertFalse(ValidationUtils.isTextOnly("Bodega123!"), "No debería aceptar números o símbolos en campos de solo texto.");
    }

    @Test
    public void testIsTextOnly_InvalidCountry() {
        assertFalse(ValidationUtils.isTextOnly("Ecuador2024"), "El país no debe contener números.");
    }

    @Test
    public void testStreetName_PositiveCase() {
        assertTrue(ValidationUtils.isTextOnly("Avenida Amazonas"), "Debería aceptar nombres de calle con letras.");
    }

    @Test
    public void testStreetName_InvalidWithNumbers() {
        assertFalse(ValidationUtils.isTextOnly("Calle 123"), "La calle no debe tener números según la validación actual de texto.");
    }

    @Test
    public void testIsNonNegativeInteger_PositiveCase() {
        assertTrue(ValidationUtils.isNonNegativeInteger("455"), "Debería aceptar '455' como un número entero válido.");
    }

    @Test
    public void testIsNonNegativeInteger_InvalidWithLetters() {
        assertFalse(ValidationUtils.isNonNegativeInteger("CasaDiez"), "El campo numérico no debe contener letras.");
    }

    @Test
    public void testIsNonNegativeInteger_InvalidDecimal() {
        assertFalse(ValidationUtils.isNonNegativeInteger("123.5"), "El número debe ser entero, no decimal.");
    }

    @Test
    public void testIsTextOnly_InvalidRegion() {
        assertFalse(ValidationUtils.isTextOnly("Sierra@"), "La región no debe tener caracteres especiales.");
    }
    
    @Test
    public void testGetAddressFormError_EmptyFields() {
        String error = ValidationUtils.getAddressFormError("", "Ecuador", "Quito", "");
        assertNotNull(error, "Debería retornar un mensaje de error si hay campos vacíos.");
        assertEquals("Todos los campos (Nombre, País, Ciudad, Calle) son obligatorios.", error, "El mensaje de error no coincide.");
    }
}