package ec.edu.espe.finvory.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Joseph B. Medina
 */
public class PriceValidationTest {
    
    public PriceValidationTest() {
    }

    @Test
    public void testPrices_ValidConfig_ReturnsNull() {
        String profit = "0.30";
        String std = "0.05";
        String prem = "0.10";
        String vip = "0.15";
        String tax = "0.12";
        
        String result = ValidationUtils.getPriceConfigError(profit, std, prem, vip, tax);
        assertNull(result, "Standard valid configuration should return null");
    }

    @Test
    public void testPrices_NegativeProfit_ReturnsError() {
        String result = ValidationUtils.getPriceConfigError("-0.10", "0.05", "0.10", "0.15", "0.15");
        assertNotNull(result);
        assertTrue(result.contains("positivos"));
    }

    @Test
    public void testPrices_ZeroDiscount_ReturnsError() {
        String result = ValidationUtils.getPriceConfigError("0.30", "0.00", "0.10", "0.15", "0.12");
        assertNotNull(result, "Currently 0.00 is not considered a positive decimal");
        assertTrue(result.contains("positivos"));
    }

    @Test
    public void testPrices_StdDiscountGreaterThenPremium_ReturnsError() {
        String std = "0.12";
        String prem = "0.10"; 
        
        String result = ValidationUtils.getPriceConfigError("0.30", std, prem, "0.15", "0.12");
        assertNotNull(result);
        assertTrue(result.contains("Standard") || result.contains("Premium"));
    }

    @Test
    public void testPrices_PremiumDiscountGreaterThenVip_ReturnsError() {
        String prem = "0.20";
        String vip = "0.15"; 
        
        String result = ValidationUtils.getPriceConfigError("0.30", "0.10", prem, vip, "0.12");
        assertNotNull(result);
        assertTrue(result.contains("Premium") || result.contains("VIP"));
    }

    @Test
    public void testPrices_VipDiscountGreaterThenProfit_ReturnsError() {
        String profit = "0.20";
        String vip = "0.25"; 
        
        String result = ValidationUtils.getPriceConfigError(profit, "0.05", "0.10", vip, "0.12");
        assertNotNull(result);
        assertTrue(result.contains("VIP") || result.contains("Ganancia"));
    }

    @Test
    public void testPrices_ProfitTooHigh_ReturnsError() {
        String profit = "2.50"; 
        
        String result = ValidationUtils.getPriceConfigError(profit, "0.05", "0.10", "0.15", "0.12");
        assertNotNull(result);
        assertTrue(result.contains("200%"));
    }

    @Test
    public void testPrices_TaxZero_ReturnsError() {
        String tax = "0.0";
        
        String result = ValidationUtils.getPriceConfigError("0.30", "0.05", "0.10", "0.15", tax);
        assertNotNull(result);
        assertTrue(result.contains("impuesto") || result.contains("positivos"));
    }
    
    @Test
    public void testPrices_NonNumericInput_ReturnsError() {
        String result = ValidationUtils.getPriceConfigError("abc", "0.05", "0.10", "0.15", "0.12");
        assertNotNull(result);
    }

    @Test
    public void testPrices_EmptyInput_ReturnsError() {
        String result = ValidationUtils.getPriceConfigError("", "0.05", "0.10", "0.15", "0.12");
        assertNotNull(result);
        assertTrue(result.contains("obligatorios"));
    }

    @Test
    public void testPrices_StandardNegative_ReturnsError() {
        String result = ValidationUtils.getPriceConfigError("0.30", "-0.05", "0.10", "0.15", "0.12");
        assertNotNull(result);
        assertTrue(result.contains("positivos"));
    }

    @Test
    public void testPrices_PremiumOverHundredPercent_ReturnsError() {
        String result = ValidationUtils.getPriceConfigError("0.30", "0.05", "1.10", "0.15", "0.12");
        assertNotNull(result);
        assertTrue(result.contains("porcentajes") || result.contains("0 y 1"));
    }

    @Test
    public void testPrices_VipOverHundredPercent_ReturnsError() {
        String result = ValidationUtils.getPriceConfigError("0.30", "0.05", "0.10", "1.50", "0.12");
        assertNotNull(result);
        assertTrue(result.contains("porcentajes") || result.contains("0 y 1"));
    }

    @Test
    public void testPrices_TaxOverHundredPercent_ReturnsError() {
        String result = ValidationUtils.getPriceConfigError("0.30", "0.05", "0.10", "0.15", "1.20");
        assertNotNull(result);
        assertTrue(result.contains("porcentajes") || result.contains("0 y 1"));
    }

    @Test
    public void testPrices_StandardEqualsPremium_ReturnsError() {
        String result = ValidationUtils.getPriceConfigError("0.30", "0.10", "0.10", "0.15", "0.12");
        assertNotNull(result);
        assertTrue(result.contains("Standard") || result.contains("Premium"));
    }

    @Test
    public void testPrices_PremiumEqualsVip_ReturnsError() {
        String result = ValidationUtils.getPriceConfigError("0.30", "0.05", "0.15", "0.15", "0.12");
        assertNotNull(result);
        assertTrue(result.contains("Premium") || result.contains("VIP"));
    }

    @Test
    public void testPrices_VipEqualsProfit_ReturnsError() {
        String result = ValidationUtils.getPriceConfigError("0.20", "0.05", "0.10", "0.20", "0.12");
        assertNotNull(result);
        assertTrue(result.contains("VIP") || result.contains("Ganancia"));
    }

    @Test
    public void testPrices_ProfitExactLimit_Valid() {
        String profit = "2.00";
        String result = ValidationUtils.getPriceConfigError(profit, "0.05", "0.10", "0.15", "0.12");
        assertNull(result, "Profit of exactly 2.0 should be allowed");
    }

    @Test
    public void testPrices_ProfitJustBelowLimit_Valid() {
        String profit = "1.99";
        String result = ValidationUtils.getPriceConfigError(profit, "0.05", "0.10", "0.15", "0.12");
        assertNull(result);
    }
}