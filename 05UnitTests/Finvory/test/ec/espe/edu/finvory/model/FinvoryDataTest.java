package ec.espe.edu.finvory.model; 

import org.junit.Test; 
import org.junit.BeforeClass; 
import static org.junit.Assert.*; 
import java.io.ByteArrayInputStream; 
import java.io.InputStream;

/**
 *
 * @author Arelys Otavalo, The POOwer Rangers Of Programming
 */

 public class FinvoryDataTest {

    @BeforeClass
    public static void setUpClass() {
        String input = "0\n"; 
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
    }

    @Test(expected = IllegalArgumentException.class) 
    public void testSetProfit_ShouldRejectOutOfRangeValue_ExpectedFail() {
        FinvoryData data = new FinvoryData();
        float illegalProfit = 5.0f; 
        data.setProfitPercentage(illegalProfit); 

    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetStandardDiscount_ShouldRejectNegativeValue_ExpectedFail() {
        FinvoryData data = new FinvoryData();
        float illegalDiscount = -0.1f; 
        data.setDiscountStandard(illegalDiscount); 
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetPremiumDiscount_ShouldRejectNegativeValue_ExpectedFail() {
        FinvoryData data = new FinvoryData();
        float illegalDiscount = -0.5f; 
        data.setDiscountPremium(illegalDiscount); 
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetVipDiscount_ShouldRejectNegativeValue_ExpectedFail() {
        FinvoryData data = new FinvoryData();
        float illegalDiscount = -1.0f; 
        data.setDiscountVip(illegalDiscount); 
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDiscount_ShouldCheckAgainstProfit_ExpectedFail() {
        FinvoryData data = new FinvoryData();
        data.setProfitPercentage(0.1f); 
        float highDiscount = 0.5f; 
        data.setDiscountStandard(highDiscount); 
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDiscounts_ShouldCheckStandardGreaterThanVip_ExpectedFail() {
        FinvoryData data = new FinvoryData();
        data.setDiscountVip(0.3f); 
        float highStandard = 0.5f; 
        data.setDiscountStandard(highStandard); 
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDiscounts_ShouldRejectEqualDiscounts_ExpectedFail() {
        FinvoryData data = new FinvoryData();
        float equalDiscount = 0.2f;
        data.setDiscountPremium(equalDiscount); 
        data.setDiscountStandard(equalDiscount); 
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDiscounts_ShouldRejectProfitEqualsDiscountVip_ExpectedFail() {
        FinvoryData data = new FinvoryData();
        float equalValue = 0.5f;
        data.setProfitPercentage(equalValue); 
        data.setDiscountVip(equalValue); 
    }

    @Test
    public void testSetProfit_AcceptsValidValue_Success() {
        FinvoryData data = new FinvoryData();
        float validProfit = 0.15f; 

        assertEquals("P9: El valor válido debe ser guardado correctamente.", validProfit, data.getProfitPercentage(), 0.0001f);
    }

    @Test
    public void testSetStandardDiscount_AcceptsValidValue_Success() {
        FinvoryData data = new FinvoryData();
        float validDiscount = 0.05f; 
        data.setDiscountStandard(validDiscount); 
        assertEquals("P10: El valor válido debe ser guardado correctamente.", validDiscount, data.getDiscountStandard(), 0.0001f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetTaxRate_ShouldRejectZero_RED() {
        FinvoryData data = new FinvoryData();
        System.out.println("TEST ROJO: Rechazando Tasa de IVA = 0.");
        data.setTaxRate(0.0f); 
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetTaxRate_ShouldRejectAboveOneHundredPercent_RED() {
        FinvoryData data = new FinvoryData();
        System.out.println("TEST ROJO: Rechazando Tasa de IVA > 1.0.");
        data.setTaxRate(1.0001f); 
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDiscountPremium_ShouldRejectNegativeValue_NewRed() {
        FinvoryData data = new FinvoryData();
        System.out.println("TEST ROJO: Rechazando descuento Premium negativo.");
        data.setDiscountPremium(-0.00f); 
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetDiscountPremium2_ShouldRejectNegativeValue_NewRed() {
        FinvoryData data = new FinvoryData();
        System.out.println("TEST ROJO: Rechazando descuento VIP negativo.");
        data.setDiscountVip(-0.024f); 
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetDiscountPremium3_ShouldRejectNegativeValue_NewRed() {
        FinvoryData data = new FinvoryData();
        System.out.println("TEST ROJO: Rechazando descuento Standard negativo.");
        data.setDiscountStandard(-0.01f); 
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDiscountStandard_ShouldRejectAboveOneHundredPercent_RED() {
        FinvoryData data = new FinvoryData();
        System.out.println("TEST ROJO: Rechazando descuento Standard > 100%.");
        data.setDiscountStandard(1.5f); 
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetDiscountStandard2_ShouldRejectAboveOneHundredPercent_RED() {
        FinvoryData data = new FinvoryData();
        System.out.println("TEST ROJO: Rechazando descuento VIP > 100%.");
        data.setDiscountVip(1.5f); 
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetProfitPercentage_ShouldRejectNegativeValue_RED() {
        FinvoryData data = new FinvoryData();
        System.out.println("TEST ROJO: Rechazando porcentaje de beneficio negativo.");
        data.setProfitPercentage(-0.1f);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetProfitPercentage2_ShouldRejectNegativeValue_RED() {
        FinvoryData data = new FinvoryData();
        System.out.println("TEST ROJO: Rechazando porcentaje de beneficio nulo.");
        data.setProfitPercentage(0f);
    }
}




