package ec.edu.espe.finvory.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**

 *

 * @author Mathews Pastor, The POOwer Rangers Of Programming

 */

public class CompanyAccountTest {

    public CompanyAccountTest() {
    }

    @Test
    public void testValidatePhone_Strict10Digits() {
        System.out.println("Test 1: 10-Digit Mobile Phone");
        String validPhone = "0991234567";
        String invalidPhoneLength = "099123456";
        String invalidPhoneChar = "09912A4567";

        assertTrue(ValidationUtils.isValidPhone10Digits(validPhone), "The 10-digit phone number should be valid");
        assertFalse(ValidationUtils.isValidPhone10Digits(invalidPhoneLength), "The incomplete phone number should be invalid");
        assertFalse(ValidationUtils.isValidPhone10Digits(invalidPhoneChar), "The phone number with letters should be invalid");
    }

    @Test
    public void testValidatePhone_ForeignFormat() {
        System.out.println("Test 2: Foreign Format Phone");
        String validForeignPhone = "+593 991234567";
        String validForeignPhoneNoSpace = "+593991234567";

        assertTrue(ValidationUtils.isValidPhone(validForeignPhone), "The phone with country code (+) should be valid");
        assertTrue(ValidationUtils.isValidPhone(validForeignPhoneNoSpace), "The phone with (+) and no spaces should be valid");
    }

    @Test
    public void testValidateEmail_CorrectFormat() {
        System.out.println("Test 3: Correct Email Format");
        String validEmail = "empresa@finvory.com";

        assertTrue(ValidationUtils.isValidEmail(validEmail), "The email with correct format must be valid");
    }

    @Test
    public void testValidateEmail_IncorrectFormat() {
        System.out.println("Test 4: Incorrect Email Format");
        String invalidEmailNoAt = "empresafinvory.com";
        String invalidEmailNoDomain = "empresa@";

        assertFalse(ValidationUtils.isValidEmail(invalidEmailNoAt), "The email without '@' must be invalid");
        assertFalse(ValidationUtils.isValidEmail(invalidEmailNoDomain), "The email without domain must be invalid");
    }

    @Test
    public void testValidateRuc_StrictFormat() {
        System.out.println("Test 5: Strict RUC (13 digits starting with 001)");
        String validRuc = "0012345678001";
        String invalidRucStart = "1712345678001";
        String invalidRucLength = "0012345678"; 

        assertTrue(ValidationUtils.isStrictRuc(validRuc), "The RUC starting with 001 and having 13 digits must be valid");
        assertFalse(ValidationUtils.isStrictRuc(invalidRucStart), "The RUC not starting with 001 must be invalid");
        assertFalse(ValidationUtils.isStrictRuc(invalidRucLength), "The RUC with incorrect length must be invalid");
    }

    @Test
    public void testValidatePersonalName_NoNumbers() {
        System.out.println("Test 6: Personal Name without Numbers");
        String validName = "Juan Perez";
        String invalidNameWithNumber = "Juan Perez 123";
        String invalidNameWithSymbol = "Juan_Perez";

        assertTrue(ValidationUtils.isTextOnly(validName), "The name with only letters should be valid");
        assertFalse(ValidationUtils.isTextOnly(invalidNameWithNumber), "The name with numbers should be invalid");
        assertFalse(ValidationUtils.isTextOnly(invalidNameWithSymbol), "The name with symbols should be invalid");
    }
    
    @Test
    public void testCompanyRuc_Validation() {
        String emptyRuc = "";
        String shortRuc = "1712345678";
        String validRuc = "1712345678001";
        
        assertFalse(ValidationUtils.isValidIdentification(emptyRuc), "The RUC cannot be empty");
        assertFalse(ValidationUtils.isStrictRuc(shortRuc), "A company RUC must have 13 digits");
        assertTrue(ValidationUtils.isValidIdentification(validRuc), "It should accept a valid 13-digit RUC");
    }

    @Test
    public void testCompanyAddress_MissingFields() {
        String error = ValidationUtils.getAddressFormError("Empresa S.A.", "", "Quito", "Av. Amazonas");
        
        assertNotNull(error, "It should return an error if the country is missing");
        assertEquals("Todos los campos (Nombre, País, Ciudad, Calle) son obligatorios.", error, "The error message must match the one defined in ValidationUtils");
    }

    @Test
    public void testCompanyName_HasTwoWords() {
        String singleWord = "Finvory";
        String twoWords = "Finvory Solutions";
        
        assertFalse(ValidationUtils.hasTwoWords(singleWord), "The company name should have at least two words to be formal");
        assertTrue(ValidationUtils.hasTwoWords(twoWords), "It should accept compound names");
    }

    @Test
    public void testCompanyLocation_TextOnly() {
        String invalidCountry = "Ecuador2025";
        String validCity = "Guayaquil";
        
        assertFalse(ValidationUtils.isTextOnly(invalidCountry), "The country name must not contain numbers");
        assertTrue(ValidationUtils.isTextOnly(validCity), "The city name with only letters should be valid");
    }
    
    @Test
    public void testCompanyStreetNumber_ComplexFormat() {
        String validStreetNum = "N45-23 #12/B";
        String invalidStreetNum = "Calle 100%";

        assertTrue(ValidationUtils.isValidStreetNumber(validStreetNum), 
                   "It should allow symbols like #, / and - in the street number");
        assertFalse(ValidationUtils.isValidStreetNumber(invalidStreetNum), 
                    "It should not allow special characters like %");
    }

    @Test
    public void testCompanyRegion_SpanishCharacters() {
        String accentedRegion = "Manabí";
        String enieRegion = "Cañar";

        assertTrue(ValidationUtils.isValidRegion(accentedRegion), "It should accept accented characters in the region name");
        assertTrue(ValidationUtils.isValidRegion(enieRegion), "It should accept the character 'ñ' in the region name");
    }

    @Test
    public void testCompanyEmail_ComplexUserPart() {
        String complexEmail = "gerencia.ventas_2024@mi-empresa.com.ec";
        
        assertTrue(ValidationUtils.isValidEmail(complexEmail), 
                   "The email regex must support subdomains, dots, and underscores");
    }

    @Test
    public void testCompanyIdentification_CedulaFormat() {
        String validCedula = "1723456789";
        
        assertTrue(ValidationUtils.isValidIdentification(validCedula), 
                   "REGEX_ID should accept 10-digit formats for natural persons");
    }

    @Test
    public void testCompanyFields_WhitespaceOnly() {
        String whitespace = "   ";
        
        assertTrue(ValidationUtils.isEmpty(whitespace), 
                   "A field with only spaces must be considered empty");
    }

    @Test
    public void testCompanyName_AccentedLetters() {
        String accentedName = "Corporación El Rosado";
        
        assertTrue(ValidationUtils.isTextOnly(accentedName), 
                   "The company name should allow accented characters");
    }

    @Test
    public void testCompanyPhone_InternalSpaces() {
        String phoneWithSpaces = "02 2991 700";
        
        assertTrue(ValidationUtils.isValidPhone(phoneWithSpaces), 
                   "It should accept phone numbers with internal spaces");
    }

    @Test
    public void testAddressFormError_NumericLocation() {
        String error = ValidationUtils.getAddressFormError("Empresa S.A.", "Ecuador123", "Quito", "Calle A");
        
        assertNotNull(error, "An error should be returned for numeric locations");
        assertEquals("País y Ciudad solo deben contener letras.", error, "The error message must match the validation rule for alphabetic locations");
    }
    
    @Test
    public void testCompanyName_HasAtLeastTwoWords() {
        String singleWord = "Application";
        String twoWords = "Applications Solutions";
        
        assertFalse(ValidationUtils.hasTwoWords(singleWord), "The company name should have at least two words to be formal");
        assertTrue(ValidationUtils.hasTwoWords(twoWords), "It should accept compound names");
    }
    
    @Test
    public void testCompanyFields_Whitespace() {
        String whitespace = "   ";
        
        assertTrue(ValidationUtils.isEmpty(whitespace), 
                   "A field with only spaces must be considered empty");
    }
}