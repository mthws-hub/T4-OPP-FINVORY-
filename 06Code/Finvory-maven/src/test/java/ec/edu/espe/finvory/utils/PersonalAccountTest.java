package ec.edu.espe.finvory.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**

 *

 * @author Maryuri Quiña, @ESPE

 */

public class PersonalAccountTest {

    public PersonalAccountTest() {
    }

    @Test
    public void testPersonalNameWithNumbers() {
        String invalidName = "Usuario123";
        assertFalse(ValidationUtils.isTextOnly(invalidName),
                "Personal account names should not allow numeric characters.");
    }

    @Test
    public void testPersonalNameWithSpecialCharacters() {
        String invalidName = "Admin_@Espe";
        assertFalse(ValidationUtils.isTextOnly(invalidName),
                "Personal account names should not allow special symbols or underscores.");
    }

    @Test
    public void testValidPersonalName() {
        assertTrue(ValidationUtils.isTextOnly("Carlos Perez"),
                "A name containing only letters and spaces must be considered valid.");
    }
    
    @Test
    public void testFullNameHasAtLeastTwoWords() {
        String invalidNameOneWord = "Arelys";
        String invalidNameEmpty = "";
        String validFullName = "Arelys Otavalo";
        String validMultipleNames = "Joseph Anthony Medina";

        assertFalse(ValidationUtils.hasTwoWords(invalidNameOneWord), 
                    "Validation should fail if the name consists of only one word");
        
        assertFalse(ValidationUtils.hasTwoWords(invalidNameEmpty), 
                    "An empty string cannot be a valid full name");

        assertTrue(ValidationUtils.hasTwoWords(validFullName), 
                   "Should accept a standard name and surname combination");
        assertTrue(ValidationUtils.hasTwoWords(validMultipleNames), 
                   "Should accept names with more than two words");
    }

    @Test
    public void testPersonalNameWithAccentedCharacters() {
        String accentedName = "María José Peña";
        assertTrue(ValidationUtils.isTextOnly(accentedName), 
                   "Names with accents and the letter ñ should be accepted as valid text");
    }

    @Test
    public void testNameWithLeadingAndTrailingSpaces() {
        String nameWithSpaces = "  John Doe  ";
        assertTrue(ValidationUtils.hasTwoWords(nameWithSpaces), 
                   "The system should handle leading and trailing whitespaces correctly");
    }

    @Test
    public void testNameWithMultipleInternalSpaces() {
        String messySpacing = "John    Doe";
        assertTrue(ValidationUtils.hasTwoWords(messySpacing), 
                   "Multiple spaces between words should not invalidate a full name");
    }

    @Test
    public void testNameWithOnlyWhitespaces_ShouldFail() {
        String whiteSpaceOnly = "    ";
        assertFalse(ValidationUtils.hasTwoWords(whiteSpaceOnly), 
                    "A string containing only spaces should be rejected as a name");
    }

    @Test
    public void testNameWithMixedCaseValidation() {
        String mixedCaseName = "jOHN dOE";
        assertTrue(ValidationUtils.isTextOnly(mixedCaseName), 
                   "The text-only validation should be case insensitive");
    }
}