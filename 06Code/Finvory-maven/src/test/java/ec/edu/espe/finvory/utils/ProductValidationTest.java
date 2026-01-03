package ec.edu.espe.finvory.utils;

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
                "The barcode should be valid when it contains only numbers");
        assertFalse(ValidationUtils.validate(invalidBarcode, ValidationUtils.REGEX_INTEGER_ONLY),
                "The barcode should be invalid if it contains alphabetic characters");
    }

    @Test
    public void testPriceNoCommasAllowed() {
        String invalidPrice = "10,50";
        String validPrice = "10.50";

        assertFalse(ValidationUtils.isNumeric(invalidPrice), "Prices should not allow commas as decimal separators");
        assertTrue(ValidationUtils.isNumeric(validPrice), "Prices must use dots for decimal representation");
    }

    @Test
    public void testPriceNoNegatives() {
        String negativePrice = "-5.00";
        String positivePrice = "15.75";

        assertFalse(ValidationUtils.isPositiveDecimal(negativePrice), "Product price cannot be a negative value");
        assertTrue(ValidationUtils.isPositiveDecimal(positivePrice), "A positive decimal price should be valid");
    }

    @Test
    public void testStockNoNegatives() {
        String negativeStock = "-10";
        String zeroStock = "0";

        assertFalse(ValidationUtils.isNonNegativeInteger(negativeStock), "Stock levels cannot be negative");
        assertTrue(ValidationUtils.isNonNegativeInteger(zeroStock), "A stock level of zero should be valid");
    }

    @Test
    public void testStockOnlyIntegers() {
        String decimalStock = "10.5";
        String integerStock = "50";

        assertFalse(ValidationUtils.validate(decimalStock, ValidationUtils.REGEX_INTEGER_ONLY),
                "Stock values should not allow decimal points");
        assertTrue(ValidationUtils.validate(integerStock, ValidationUtils.REGEX_INTEGER_ONLY),
                "Stock must be a valid whole integer");
    }

    @Test
    public void testValidateProductFields_StockDependency() {
        String resultEdit = ValidationUtils.validateProductFields("P01", "Martillo", "5.50", "Proveedor A", "", false);
        assertNull(resultEdit, "Stock should not be required when editing basic product information");

        String resultAdd = ValidationUtils.validateProductFields("P01", "Martillo", "5.50", "Proveedor A", "", true);
        assertEquals("Error: El campo Stock Inicial es obligatorio al añadir a un inventario.", resultAdd, 
                     "The system must enforce initial stock requirements when creating new inventory entries");
    }

    @Test
    public void testPriceWithMultipleDots() {
        String invalidPrice = "10.50.20";
        assertFalse(ValidationUtils.isNumeric(invalidPrice), "A price with multiple decimal points must be rejected");
    }

    @Test
    public void testProductNameWithSpecialSymbols() {
        String invalidName = "Product#123!";
        assertFalse(ValidationUtils.isTextOnly(invalidName), "Product names should be restricted to letters and spaces for consistency");
    }

    @Test
    public void testStockExtremeValue_LargeInput() {
        String extremeStock = "9999999";
        assertTrue(ValidationUtils.isNonNegativeInteger(extremeStock), "The system should handle reasonably large integer stock values");
    }

    @Test
    public void testSupplierField_EmptyCheck() {
        String result = ValidationUtils.validateProductFields("P01", "Hammer", "10.00", "", "5", true);
        assertNotNull(result, "The supplier field should be mandatory when adding a product");
    }

    @Test
    public void testPriceWithLeadingZero() {
        String validPrice = "0.99";
        assertTrue(ValidationUtils.isPositiveDecimal(validPrice), "Prices starting with zero decimals should be valid");
    }

    @Test
    public void testBarcodeLengthBoundary_TooShort() {
        String shortBarcode = "123";
        assertTrue(ValidationUtils.validate(shortBarcode, ValidationUtils.REGEX_INTEGER_ONLY), 
                   "Integer validation should pass, though specific length logic might be handled separately");
    }

    @Test
    public void testProductFields_WhitespaceTrimming() {
        String result = ValidationUtils.validateProductFields("   ", "Name", "5.00", "Supplier", "10", true);
        assertNotNull(result, "An ID consisting only of spaces should be treated as empty and return an error");
    }

    @Test
    public void testPriceWithCurrencySymbol_ShouldFail() {
        String priceWithSymbol = "$10.50";
        assertFalse(ValidationUtils.isNumeric(priceWithSymbol), "Price fields should not contain currency symbols like $");
    }
}