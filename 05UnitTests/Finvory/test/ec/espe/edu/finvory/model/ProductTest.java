package ec.espe.edu.finvory.model;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Arelys Otavalo, The POOwer Rangers Of Programming
 */
public class ProductTest {

    @Test
    public void testProductCalculation_ShouldNotResultInNegativePrice_RED() {
        Product product = new Product("101", "Pastilla", "Descripcion", "000", 100.00f, "SUP01");
        float profit = 0.0f;
        float dStandard = 0.0f;
        float dPremium = 0.0f;
        float excessiveDiscountVip = 1.50f; 
        float expectedPrice = 0.0f;
        float actualPrice = product.getPrice("VIP", profit, dStandard, dPremium, excessiveDiscountVip);

        assertTrue("El precio final no debe ser negativo después de un descuento > 100%.", actualPrice >= 0.0f);
    }

    @Test
    public void testProductPrice_ShouldIncludeTaxLogic_RED() {
        
        Product product = new Product("102", "Filtro", "Filtro aire", "001", 23.00f, "SUP01");
        
        float profit = 0.80f; 
        float dStandard = 0.10f; 
        float dPremium = 0.0f; 
        float dVip = 0.0f;
        float expectedPriceWithTax = 47.61f; 
        float actualPriceWithoutTax = product.getPrice("STANDARD", profit, dStandard, dPremium, dVip);

        assertEquals("El precio de venta debe incluir el IVA (15%).", expectedPriceWithTax, actualPriceWithoutTax, 0.01f);
    }

 
    @Test(expected = IllegalArgumentException.class)
    public void testProductSetCost_ShouldRejectZero_RED() {
        Product product = new Product("103", "Bujía", "Desc", "002", 23.00f, "SUP02");
        System.out.println("TEST ROJO: Rechazando costo base de producto = 0.");
        product.setBaseCostPrice(0.0f); 
    }

    
    @Test(expected = IllegalArgumentException.class)
    public void testProductSetDescription_ShouldRejectEmpty_RED() {
        Product product = new Product("104", "Llanta", "Desc", "003", 23.00f, "SUP03");
        System.out.println("TEST ROJO: Rechazando descripción de producto vacía.");
        product.setDescription(""); 
    }

    
    @Test(expected = IllegalArgumentException.class)
    public void testProductSetCost_ShouldRejectNegative_RED() {
        Product product = new Product("105", "Aceite", "Desc", "004", 23.00f, "SUP04");
        System.out.println("TEST ROJO: Rechazando costo base negativo.");
        product.setBaseCostPrice(-1.0f); 
    }

    @Test
    public void testProductPriceCalculation_VIPDiscount_Success() {
        Product product = new Product("106", "TestName", "Desc", "005", 100.00f, "SUP05");
        float profit = 0.25f;
        float dStandard = 0.0f;
        float dPremium = 0.0f;
        float dVip = 0.10f;
        float expectedPrice = 112.50f;
        float actualPrice = product.getPrice("VIP", profit, dStandard, dPremium, dVip);

        assertEquals("El cálculo del precio VIP sin IVA debe ser correcto.", expectedPrice, actualPrice, 0.0001f);
    }
}