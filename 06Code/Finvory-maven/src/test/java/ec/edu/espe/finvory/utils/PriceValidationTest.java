package ec.edu.espe.finvory.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Mathews Pastor, The POOwer Rangers Of Programming
 */
public class PriceValidationTest {

    public PriceValidationTest() {
    }

    @Test
    public void testPrices_StandardConfig_Valid() {
        String result = ValidationUtils.getPriceConfigError("0.30", "0.05", "0.10", "0.15", "0.15");
        assertNull(result);
    }

    @Test
    public void testPrices_ZeroDiscounts_Valid() {
        String result = ValidationUtils.getPriceConfigError("0.30", "0.0", "0.0", "0.0", "0.12");
        assertNull(result);
    }

    @Test
    public void testPrices_NegativeProfit_ReturnsError() {
        String result = ValidationUtils.getPriceConfigError("-0.10", "0.05", "0.10", "0.15", "0.12");
        assertNotNull(result);
    }

    @Test
    public void testPrices_NegativeTax_ReturnsError() {
        String result = ValidationUtils.getPriceConfigError("0.30", "0.05", "0.10", "0.15", "-0.12");
        assertNotNull(result);
    }

    @Test
    public void testPrices_EmptyFields_ReturnsError() {
        String result = ValidationUtils.getPriceConfigError("", "0.05", "0.10", "0.15", "0.12");
        assertNotNull(result);
    }

    @Test
    public void testPrices_NonNumericInput_ReturnsError() {
        String result = ValidationUtils.getPriceConfigError("abc", "0.05", "0.10", "0.15", "0.12");
        assertNotNull(result);
    }

    @Test
    public void testPrices_StandardGreaterThanPremium_ReturnsError() {
        String result = ValidationUtils.getPriceConfigError("0.40", "0.12", "0.10", "0.15", "0.12");
        assertNotNull(result);
        assertTrue(result.contains("Standard") || result.contains("Premium"));
    }

    @Test
    public void testPrices_PremiumGreaterThanVip_ReturnsError() {
        String result = ValidationUtils.getPriceConfigError("0.40", "0.05", "0.20", "0.15", "0.12");
        assertNotNull(result);
        assertTrue(result.contains("Premium") || result.contains("VIP"));
    }

    @Test
    public void testPrices_VipGreaterThanProfit_ReturnsError() {
        String result = ValidationUtils.getPriceConfigError("0.20", "0.05", "0.10", "0.25", "0.12");
        assertNotNull(result);
        assertTrue(result.contains("VIP") || result.contains("Profit") || result.contains("Ganancia"));
    }

    @Test
    public void testPrices_TaxGreaterThan100Percent_ReturnsError() {
        String result = ValidationUtils.getPriceConfigError("0.30", "0.05", "0.10", "0.15", "1.50");
        assertNotNull(result);
    }

    @Test
    public void testPrices_HighProfitMargin_Valid() {
        String result = ValidationUtils.getPriceConfigError("2.50", "0.05", "0.10", "0.15", "0.12");
        assertNull(result, "Should allow profit margins greater than 200%");
    }

    @Test
    public void testPrices_ZeroTax_Valid() {
        String result = ValidationUtils.getPriceConfigError("0.30", "0.05", "0.10", "0.15", "0.0");
        assertNull(result, "Should allow 0% tax for duty-free products");
    }

    @Test
    public void testProfitIsGreaterThanZero() {
        String invalidProfit = "0";
        String negativeProfit = "-0.10";
        String validProfit = "0.25";

        assertFalse(ValidationUtils.isPositiveDecimal(invalidProfit), "La ganancia no debería ser cero");
        assertFalse(ValidationUtils.isPositiveDecimal(negativeProfit), "La ganancia no puede ser negativa");
        assertTrue(ValidationUtils.isPositiveDecimal(validProfit), "La ganancia positiva debe ser válida");
    }

    @Test
    public void testProfitNotEqualToVip() {
        String profit = "0.20";
        String sameVip = "0.20";
        String lowerVip = "0.15";

        String error = ValidationUtils.getPriceConfigError(profit, "0.05", "0.10", sameVip, "0.15");
        assertEquals("El descuento VIP no puede ser mayor o igual a la Ganancia.", error,
                "Debería retornar error si VIP es igual a la ganancia");

        String noError = ValidationUtils.getPriceConfigError(profit, "0.05", "0.10", lowerVip, "0.15");
        assertNull(noError, "No debería haber error si VIP es menor a la ganancia");
    }

    @Test
    public void testTaxIsPositive() {
        String negativeTax = "-0.12";

        assertFalse(ValidationUtils.isPositiveDecimal(negativeTax), "El IVA no puede ser negativo");
    }

    @Test
    public void testTaxRangeZeroToOne() {
        String outOfRangeTax = "1.5";
        String validTax = "0.15";

        String error = ValidationUtils.getPriceConfigError("0.5", "0.1", "0.2", "0.3", outOfRangeTax);
        assertEquals("Los porcentajes deben estar entre 0 y 1.", error,
                "Debería fallar si el IVA es mayor a 1.0");

        String noError = ValidationUtils.getPriceConfigError("0.5", "0.1", "0.2", "0.3", validTax);
        assertNull(noError, "El IVA de 0.15 debería ser aceptado");
    }

    @Test
    public void testGetPriceConfigError_MaxProfitLimit() {
        String error = ValidationUtils.getPriceConfigError("2.1", "0.05", "0.10", "0.15", "0.15");
        assertEquals("La ganancia no debería exceder el 200% (2.0).", error);
    }

    @Test
    public void testGetPriceConfigError_TaxMustBePositive() {
        String error = ValidationUtils.getPriceConfigError("0.3", "0.05", "0.10", "0.15", "0");
        assertEquals("La tasa de impuesto debe ser mayor a 0.", error);
    }
}
