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
    public void testPrices_ValidConfig() {
        String profit = "0.30";
        String std = "0.05";
        String prem = "0.10";
        String vip = "0.15";
        String tax = "0.12";
        
        String result = ValidationUtils.getPriceConfigError(profit, std, prem, vip, tax);
        assertNull(result, "Standard valid configuration should return null");
    }

    @Test
    public void testPrices_NegativeProfit() {
        String result = ValidationUtils.getPriceConfigError("-0.10", "0.05", "0.10", "0.15", "0.15");
        assertNotNull(result);
        assertTrue(result.contains("positivos"));
    }

    @Test
    public void testPrices_ZeroDiscount() {
        String result = ValidationUtils.getPriceConfigError("0.30", "0.00", "0.10", "0.15", "0.12");
        assertNotNull(result, "Currently 0.00 is not considered a positive decimal");
        assertTrue(result.contains("positivos"));
    }

    @Test
    public void testPrices_StdDiscountGreaterThenPremium() {
        String std = "0.12";
        String prem = "0.10"; 
        
        String result = ValidationUtils.getPriceConfigError("0.30", std, prem, "0.15", "0.12");
        assertNotNull(result);
        assertTrue(result.contains("Standard") || result.contains("Premium"));
    }

    @Test
    public void testPrices_PremiumDiscountGreaterThenVip() {
        String prem = "0.20";
        String vip = "0.15"; 
        
        String result = ValidationUtils.getPriceConfigError("0.30", "0.10", prem, vip, "0.12");
        assertNotNull(result);
        assertTrue(result.contains("Premium") || result.contains("VIP"));
    }

    @Test
    public void testPrices_VipDiscountGreaterThenProfit() {
        String profit = "0.20";
        String vip = "0.25"; 
        
        String result = ValidationUtils.getPriceConfigError(profit, "0.05", "0.10", vip, "0.12");
        assertNotNull(result);
        assertTrue(result.contains("VIP") || result.contains("Ganancia"));
    }

    @Test
    public void testPrices_ProfitTooHigh() {
        String profit = "2.50"; 
        
        String result = ValidationUtils.getPriceConfigError(profit, "0.05", "0.10", "0.15", "0.12");
        assertNotNull(result);
        assertTrue(result.contains("200%"));
    }

    @Test
    public void testPrices_TaxZero() {
        String tax = "0.0";
        
        String result = ValidationUtils.getPriceConfigError("0.30", "0.05", "0.10", "0.15", tax);
        assertNotNull(result);
        assertTrue(result.contains("impuesto") || result.contains("positivos"));
    }
    
    @Test
    public void testPrices_NonNumericInput() {
        String result = ValidationUtils.getPriceConfigError("abc", "0.05", "0.10", "0.15", "0.12");
        assertNotNull(result);
    }

    @Test
    public void testPrices_EmptyInput() {
        String result = ValidationUtils.getPriceConfigError("", "0.05", "0.10", "0.15", "0.12");
        assertNotNull(result);
        assertTrue(result.contains("obligatorios"));
    }

    @Test
    public void testPrices_StandardNegative() {
        String result = ValidationUtils.getPriceConfigError("0.30", "-0.05", "0.10", "0.15", "0.12");
        assertNotNull(result);
        assertTrue(result.contains("positivos"));
    }

    @Test
    public void testPrices_PremiumOverHundredPercent() {
        String result = ValidationUtils.getPriceConfigError("0.30", "0.05", "1.10", "0.15", "0.12");
        assertNotNull(result);
        assertTrue(result.contains("porcentajes") || result.contains("0 y 1"));
    }

    @Test
    public void testPrices_VipOverHundredPercent() {
        String result = ValidationUtils.getPriceConfigError("0.30", "0.05", "0.10", "1.50", "0.12");
        assertNotNull(result);
        assertTrue(result.contains("porcentajes") || result.contains("0 y 1"));
    }

    @Test
    public void testPrices_TaxOverHundredPercent() {
        String result = ValidationUtils.getPriceConfigError("0.30", "0.05", "0.10", "0.15", "1.20");
        assertNotNull(result);
        assertTrue(result.contains("porcentajes") || result.contains("0 y 1"));
    }

    @Test
    public void testPrices_StandardEqualsPremium() {
        String result = ValidationUtils.getPriceConfigError("0.30", "0.10", "0.10", "0.15", "0.12");
        assertNotNull(result);
        assertTrue(result.contains("Standard") || result.contains("Premium"));
    }

    @Test
    public void testPrices_PremiumEqualsVip() {
        String result = ValidationUtils.getPriceConfigError("0.30", "0.05", "0.15", "0.15", "0.12");
        assertNotNull(result);
        assertTrue(result.contains("Premium") || result.contains("VIP"));
    }

    @Test
    public void testPrices_VipEqualsProfit() {
        String result = ValidationUtils.getPriceConfigError("0.20", "0.05", "0.10", "0.20", "0.12");
        assertNotNull(result);
        assertTrue(result.contains("VIP") || result.contains("Ganancia"));
    }

    @Test
    public void testPrices_ProfitExactLimit() {
        String profit = "2.00";
        String result = ValidationUtils.getPriceConfigError(profit, "0.05", "0.10", "0.15", "0.12");
        assertNull(result, "Profit of exactly 2.0 should be allowed");
    }

    @Test
    public void testPrices_ProfitJustBelowLimit() {
        String profit = "1.99";
        String result = ValidationUtils.getPriceConfigError(profit, "0.05", "0.10", "0.15", "0.12");
        assertNull(result);
    }
    
    @Test
    public void testPriceConfig_AllZeroValues() {
        String profit = "0.00";
        String std = "0.00";
        String prem = "0.00";
        String vip = "0.00";
        String tax = "0.00";
        
        String result = ValidationUtils.getPriceConfigError(profit, std, prem, vip, tax);
        assertNotNull(result, "Zero values configuration must trigger a validation error");
    }


    @Test
    public void testPriceConfig_HighProfitMargin() {
        String highProfit = "1.50"; 
        String result = ValidationUtils.getPriceConfigError(highProfit, "0.10", "0.20", "0.30", "0.15");
        assertNull(result, "A high profit margin like 150% should be acceptable if it's a valid number");
    }

    @Test
    public void testPriceConfig_DiscountGreaterThanProfit() {
        String result = ValidationUtils.getPriceConfigError("0.10", "0.05", "0.15", "0.20", "0.12");
        assertNotNull(result, "Discounts exceeding the profit margin should be flagged as errors");
    }

    @Test
    public void testPriceConfig_MixedDecimalFormats() {
        String profit = "0.50";
        String std = "0.1";
        String prem = "0.2";
        String vip = "0.3";
        String tax = "0.15";

        String result = ValidationUtils.getPriceConfigError(profit, std, prem, vip, tax);
        assertNull(result, "The validator should handle single-digit decimals correctly");
    }

    @Test
    public void testPriceConfig_VipHigherThanPremium() {
        String result = ValidationUtils.getPriceConfigError("0.50", "0.05", "0.15", "0.25", "0.12");
        assertNull(result, "Standard hierarchical discount structure should be valid");
    }

    @Test
    public void testPriceConfig_ExtremeTaxValue() {
        String extremeTax = "5.00";
        String result = ValidationUtils.getPriceConfigError("0.30", "0.05", "0.10", "0.15", extremeTax);
        assertNotNull(result, "Extreme tax percentages should be validated against reasonable limits");
    }
}