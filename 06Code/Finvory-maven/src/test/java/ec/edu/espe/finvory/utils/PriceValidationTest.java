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
}