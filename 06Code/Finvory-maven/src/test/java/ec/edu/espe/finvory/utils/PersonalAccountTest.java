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
public class PersonalAccountTest {

    public PersonalAccountTest() {
    }

    @Test
    public void testPersonalNameWithNumbers() {
        String invalidName = "Usuario123";
        assertFalse(ValidationUtils.isTextOnly(invalidName),
                "ERROR: La cuenta personal no debería aceptar números en el nombre.");
    }

    @Test
    public void testPersonalNameWithSpecialCharacters() {
        String invalidName = "Admin_@Espe";
        assertFalse(ValidationUtils.isTextOnly(invalidName),
                "ERROR: La cuenta personal no debería aceptar caracteres especiales en el nombre.");
    }

    @Test
    public void testValidPersonalName() {
        assertTrue(ValidationUtils.isTextOnly("Carlos Perez"),
                "DEBERÍA PASAR: Un nombre con solo letras y espacios es correcto.");
    }

}
