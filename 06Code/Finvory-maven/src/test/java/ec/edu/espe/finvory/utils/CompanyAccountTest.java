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
        System.out.println("Test 1: Celular 10 Digitos");
        String validPhone = "0991234567";
        String invalidPhoneLength = "099123456";
        String invalidPhoneChar = "09912A4567";

        assertTrue(ValidationUtils.isValidPhone10Digits(validPhone), "El celular de 10 digitos deberia ser valido");
        assertFalse(ValidationUtils.isValidPhone10Digits(invalidPhoneLength), "El celular incompleto deberia ser invalido");
        assertFalse(ValidationUtils.isValidPhone10Digits(invalidPhoneChar), "El celular con letras deberia ser invalido");
    }

    @Test
    public void testValidatePhone_ForeignFormat() {
        System.out.println("Test 2: Celular Formato Extranjero");
        String validForeignPhone = "+593 991234567";
        String validForeignPhoneNoSpace = "+593991234567";

        assertTrue(ValidationUtils.isValidPhone(validForeignPhone), "El celular con codigo de pais (+) deberia ser valido");
        assertTrue(ValidationUtils.isValidPhone(validForeignPhoneNoSpace), "El celular con (+) sin espacios deberia ser valido");
    }

    @Test
    public void testValidateEmail_CorrectFormat() {
        System.out.println("Test 3: Email Formato Correcto");
        String validEmail = "empresa@finvory.com";

        assertTrue(ValidationUtils.isValidEmail(validEmail), "El email con formato correcto debe ser valido");
    }

    @Test
    public void testValidateEmail_IncorrectFormat() {
        System.out.println("Test 4: Email Formato Incorrecto");
        String invalidEmailNoAt = "empresafinvory.com";
        String invalidEmailNoDomain = "empresa@";

        assertFalse(ValidationUtils.isValidEmail(invalidEmailNoAt), "El email sin arroba debe ser invalido");
        assertFalse(ValidationUtils.isValidEmail(invalidEmailNoDomain), "El email sin dominio debe ser invalido");
    }

    @Test
    public void testValidateRuc_StrictFormat() {
        System.out.println("Test 5: RUC Estricto (13 digitos y empieza con 001)");
        String validRuc = "0012345678001";
        String invalidRucStart = "1712345678001";
        String invalidRucLength = "0012345678"; 

        assertTrue(ValidationUtils.isStrictRuc(validRuc), "El RUC que inicia con 001 y tiene 13 digitos debe ser valido");
        assertFalse(ValidationUtils.isStrictRuc(invalidRucStart), "El RUC que no inicia con 001 debe ser invalido");
        assertFalse(ValidationUtils.isStrictRuc(invalidRucLength), "El RUC con longitud incorrecta debe ser invalido");
    }

    @Test
    public void testValidatePersonalName_NoNumbers() {
        System.out.println("Test 6: Nombre Personal sin Numeros");
        String validName = "Juan Perez";
        String invalidNameWithNumber = "Juan Perez 123";
        String invalidNameWithSymbol = "Juan_Perez";

        assertTrue(ValidationUtils.isTextOnly(validName), "El nombre solo con letras deberia ser valido");
        assertFalse(ValidationUtils.isTextOnly(invalidNameWithNumber), "El nombre con numeros deberia ser invalido");
        assertFalse(ValidationUtils.isTextOnly(invalidNameWithSymbol), "El nombre con simbolos deberia ser invalido");
    }
    
    @Test
    public void testCompanyRuc_Validation() {
        String emptyRuc = "";
        String shortRuc = "1712345678";
        String validRuc = "1712345678001";
        
        assertFalse(ValidationUtils.isValidIdentification(emptyRuc), "El RUC no puede estar vacío");
        assertFalse(ValidationUtils.isStrictRuc(shortRuc), "Un RUC de empresa debe tener 13 dígitos");
        assertTrue(ValidationUtils.isValidIdentification(validRuc), "Debería aceptar un RUC válido de 13 dígitos");
    }

    @Test
    public void testCompanyAddress_MissingFields() {
        String error = ValidationUtils.getAddressFormError("Empresa S.A.", "", "Quito", "Av. Amazonas");
        
        assertNotNull(error, "Debería retornar error si falta el país");
        assertEquals("Todos los campos (Nombre, País, Ciudad, Calle) son obligatorios.", error, "El mensaje de error debe ser el definido en ValidationUtils");
    }

    @Test
    public void testCompanyName_HasTwoWords() {
        String singleWord = "Finvory";
        String twoWords = "Finvory Solutions";
        
        assertFalse(ValidationUtils.hasTwoWords(singleWord), "El nombre de la empresa debería tener al menos dos palabras para ser formal");
        assertTrue(ValidationUtils.hasTwoWords(twoWords), "Debería aceptar nombres compuestos");
    }

    @Test
    public void testCompanyLocation_TextOnly() {
        String invalidCountry = "Ecuador2025";
        String validCity = "Guayaquil";
        
        assertFalse(ValidationUtils.isTextOnly(invalidCountry), "El país no debe contener números");
        assertTrue(ValidationUtils.isTextOnly(validCity), "La ciudad con solo letras debe ser válida");
    }
    
    @Test
    public void testCompanyStreetNumber_ComplexFormat() {
        String validStreetNum = "N45-23 #12/B";
        String invalidStreetNum = "Calle 100%";

        assertTrue(ValidationUtils.isValidStreetNumber(validStreetNum), 
                   "Debería permitir símbolos como #, / y - en el número de calle");
        assertFalse(ValidationUtils.isValidStreetNumber(invalidStreetNum), 
                    "No debería permitir caracteres especiales como %");
    }

    @Test
    public void testCompanyRegion_SpanishCharacters() {
        String accentedRegion = "Manabí";
        String enieRegion = "Cañar";

        assertTrue(ValidationUtils.isValidRegion(accentedRegion), "Debería aceptar tildes en el nombre de la región");
        assertTrue(ValidationUtils.isValidRegion(enieRegion), "Debería aceptar la letra 'ñ' en el nombre de la región");
    }

    @Test
    public void testCompanyEmail_ComplexUserPart() {
        String complexEmail = "gerencia.ventas_2024@mi-empresa.com.ec";
        
        assertTrue(ValidationUtils.isValidEmail(complexEmail), 
                   "El regex de email debe soportar subdominios, puntos y guiones bajos");
    }

    @Test
    public void testCompanyIdentification_CedulaFormat() {
        String validCedula = "1723456789";
        
        assertTrue(ValidationUtils.isValidIdentification(validCedula), 
                   "REGEX_ID debe aceptar formatos de 10 dígitos para personas naturales");
    }

    @Test
    public void testCompanyFields_WhitespaceOnly() {
        String whitespace = "   ";
        
        assertTrue(ValidationUtils.isEmpty(whitespace), 
                   "Un campo con solo espacios debe considerarse vacío");
    }

    @Test
    public void testCompanyName_AccentedLetters() {
        String accentedName = "Corporación El Rosado";
        
        assertTrue(ValidationUtils.isTextOnly(accentedName), 
                   "El nombre de la empresa debe permitir tildes");
    }

    @Test
    public void testCompanyPhone_InternalSpaces() {
        String phoneWithSpaces = "02 2991 700";
        
        assertTrue(ValidationUtils.isValidPhone(phoneWithSpaces), 
                   "Debería aceptar números de teléfono con espacios internos");
    }

    @Test
    public void testAddressFormError_NumericLocation() {
        String error = ValidationUtils.getAddressFormError("Empresa S.A.", "Ecuador123", "Quito", "Calle A");
        
        assertNotNull(error);
        assertEquals("País y Ciudad solo deben contener letras.", error);
    }
}
