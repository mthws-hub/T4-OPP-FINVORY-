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
}