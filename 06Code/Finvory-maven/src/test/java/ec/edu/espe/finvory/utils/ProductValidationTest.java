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
public class ProductValidationTest {
    
    public ProductValidationTest() {
    }
    
    @Test
    public void testBarcodeOnlyNumbers() {
        String validBarcode = "1234567890123";
        String invalidBarcode = "123456A890123";
        assertTrue(ValidationUtils.validate(validBarcode, ValidationUtils.REGEX_INTEGER_ONLY), 
                   "El código de barras debería ser válido (solo números)");
        assertFalse(ValidationUtils.validate(invalidBarcode, ValidationUtils.REGEX_INTEGER_ONLY), 
                    "El código de barras no debería permitir letras");
    }

    @Test
    public void testPriceNoCommasAllowed() {
        String invalidPrice = "10,50";
        String validPrice = "10.50";
        
        assertFalse(ValidationUtils.isNumeric(invalidPrice), "El precio no debe permitir comas");
        assertTrue(ValidationUtils.isNumeric(validPrice), "El precio debe permitir puntos decimales");
    }

    @Test
    public void testPriceNoNegatives() {
        String negativePrice = "-5.00";
        String positivePrice = "15.75";
        
        assertFalse(ValidationUtils.isPositiveDecimal(negativePrice), "El precio no puede ser negativo");
        assertTrue(ValidationUtils.isPositiveDecimal(positivePrice), "El precio positivo debe ser válido");
    }

    @Test
    public void testStockNoNegatives() {
        String negativeStock = "-10";
        String zeroStock = "0";
        
        assertFalse(ValidationUtils.isNonNegativeInteger(negativeStock), "El stock no puede ser negativo");
        assertTrue(ValidationUtils.isNonNegativeInteger(zeroStock), "El stock cero debe ser válido");
    }

    @Test
    public void testStockOnlyIntegers() {
        String decimalStock = "10.5";
        String integerStock = "50";
        
        assertFalse(ValidationUtils.validate(decimalStock, ValidationUtils.REGEX_INTEGER_ONLY), 
                    "El stock no debe permitir decimales");
        assertTrue(ValidationUtils.validate(integerStock, ValidationUtils.REGEX_INTEGER_ONLY), 
                   "El stock debe ser un número entero");
    }


}
