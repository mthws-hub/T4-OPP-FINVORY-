package ec.edu.espe.finvory.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Mathews Pastor, The POOwer Rangers Of Programming
 */
public class LoginValidationTest {

    public LoginValidationTest() {
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
}
