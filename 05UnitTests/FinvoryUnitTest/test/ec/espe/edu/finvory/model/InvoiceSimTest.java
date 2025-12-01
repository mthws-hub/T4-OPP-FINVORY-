package ec.espe.edu.finvory.model;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Joseph Medina
 */
public class InvoiceSimTest {
    
    private Customer customer;
    private Product product;

    public InvoiceSimTest() {
    }
    
    @Before
    public void setUp() {
        customer = new Customer("Test Client", "1700000000", "0999999999", "client@test.com", "Normal");
        product = new Product("P01", "Test Product", "Description", "111", 10.0f, "S01");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRejectNegativeQuantity() {
        InvoiceSim invoice = new InvoiceSim("F001", customer, "EFECTIVO", "");
        invoice.addLine(product, -5, 10.0f); 
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRejectNegativePrice() {
        InvoiceSim invoice = new InvoiceSim("F002", customer, "EFECTIVO", "");
        invoice.addLine(product, 1, -100.0f);
    }

    @Test(expected = IllegalStateException.class)
    public void testPreventCompletingEmptyInvoice() {
        InvoiceSim invoice = new InvoiceSim("F003", customer, "EFECTIVO", "");
        invoice.complete(); 
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRejectNullCustomerInConstructor() {
        new InvoiceSim("F004", null, "EFECTIVO", "");
    }

    @Test
    public void testEncapsulationOfInvoiceLines() {
        InvoiceSim invoice = new InvoiceSim("F005", customer, "EFECTIVO", "");
        invoice.addLine(product, 1, 10.0f);
        
        List<InvoiceLineSim> externalList = invoice.getLines();
        externalList.clear(); 
        
        assertFalse(invoice.getLines().isEmpty());
    }

    @Test(expected = IllegalStateException.class)
    public void testPreventCancelingCompletedInvoice() {
        InvoiceSim invoice = new InvoiceSim("F006", customer, "EFECTIVO", "");
        invoice.addLine(product, 1, 10.0f);
        invoice.complete();
        
        invoice.cancel();
    }

    @Test
    public void testPrecisionRoundingInTotals() {
        InvoiceSim invoice = new InvoiceSim("F007", customer, "EFECTIVO", "");
        invoice.addLine(product, 1, 10.123456f); 
        invoice.calculateTotals(0.12f);
        
        assertEquals(11.34f, invoice.getTotal(), 0.00f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRejectNegativeTaxRate() {
        InvoiceSim invoice = new InvoiceSim("F008", customer, "EFECTIVO", "");
        invoice.addLine(product, 1, 100.0f);
        invoice.calculateTotals(-0.12f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRejectInvalidDateFormatForCheque() {
        new InvoiceSim("F009", customer, "CHEQUE POSTFECHADO", "invalid-date-format");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRejectNullProductInLine() {
        InvoiceSim invoice = new InvoiceSim("F010", customer, "EFECTIVO", "");
        invoice.addLine(null, 1, 10.0f);
    }
    
    @Test
    public void testAutoFilledData_Visibility() {
        InvoiceSim invoice = new InvoiceSim("F-Auto", customer, "CASH", "");
        
        assertNotNull("El cliente no debe ser nulo", invoice.getCustomer());
        assertEquals("Nombre debe autocompletarse", "Test Client", invoice.getCustomer().getName());
        assertEquals("ID debe autocompletarse", "1700000000", invoice.getCustomer().getIdentification());
        assertEquals("Email debe autocompletarse", "client@test.com", invoice.getCustomer().getEmail());
    }
    
    @Test
    public void testAutoCalculate_LineTotal() {
        InvoiceSim invoice = new InvoiceSim("F-Calc", customer, "CASH", "");
        int qty = 5;
        float price = 10.0f;
        
        invoice.addLine(product, qty, price);
        
        float expectedLineTotal = 50.0f;
        assertEquals("El total de línea debe ser qty * price", 
                expectedLineTotal, invoice.getLines().get(0).getLineTotal(), 0.01f);
    }

    @Test
    public void testAutoCalculate_FinalTax() {
        InvoiceSim invoice = new InvoiceSim("F-Tax", customer, "CASH", "");
        invoice.addLine(product, 10, 10.0f);
        
        float taxRate = 0.15f;
        invoice.calculateTotals(taxRate);
        
        assertEquals("Impuesto calculado incorrectamente", 15.0f, invoice.getTax(), 0.01f);
        assertEquals("Total final incorrecto", 115.0f, invoice.getTotal(), 0.01f);
    }
    
    @Test
    public void testCalculation_MultipleLines_Accumulation() {
        InvoiceSim invoice = new InvoiceSim("F-Multi", customer, "CASH", "");
        invoice.addLine(product, 1, 10f); 
        invoice.addLine(product, 2, 20f);
        invoice.calculateTotals(0.0f);
        
        assertEquals("Debe sumar múltiples líneas correctamente", 50.0f, invoice.getTotal(), 0.01f);
    }
    
    @Test
    public void testCalculation_ZeroQuantity() {
        InvoiceSim invoice = new InvoiceSim("F-Zero", customer, "CASH", "");
        invoice.addLine(product, 0, 10f);
        invoice.calculateTotals(0.0f);
        
        assertEquals("Cantidad 0 no debe sumar valor", 0.0f, invoice.getTotal(), 0.01f);
    }
    
    @Test
    public void testCalculation_WithDiscountLogic_Validation() {
        InvoiceSim invoice = new InvoiceSim("F-Disc", customer, "CASH", "");
        float discountedPrice = 8.0f;
        invoice.addLine(product, 1, discountedPrice);
        invoice.calculateTotals(0.0f);
        
        assertEquals("El total debe basarse en el precio aplicado (descuento)", 
                8.0f, invoice.getTotal(), 0.01f);
    }
    
    @Test
    public void testInvoice_StatusPendingByDefault() {
        InvoiceSim invoice = new InvoiceSim("F-Status", customer, "CASH", "");
        assertEquals("El estado inicial debe ser PENDING para reportes correctos", "PENDING", invoice.getStatus());
    }
}