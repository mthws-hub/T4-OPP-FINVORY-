package ec.espe.edu.finvory.model;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Maryuri Quiña, @ESPE
 */

public class InventoryOfObsoleteTest {
    
    private static final String ID_PADS = "PFR001";
    private static final String ID_FILTER = "FAC002";
    private static final String ID_INEXISTENTE = "PROD999";

    public InventoryOfObsoleteTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void test_GetInitialStock_ExpectedFail() {
        InventoryOfObsolete instance = new InventoryOfObsolete();
        int expected = 1;
        int actual = instance.getStock(ID_PADS);
        assertEquals("El stock inicial debe ser 0.", expected, actual, 0);
    }

    @Test
    public void test_AddStock_PositiveQuantity_ExpectedFail() {
        InventoryOfObsolete instance = new InventoryOfObsolete();
        int quantity = 25;
        instance.addStock(ID_PADS, quantity);
        int expected = 26;
        int actual = instance.getStock(ID_PADS);
        assertEquals("La adición debe ser 25.", expected, actual, 0);
    }
    
    @Test
    public void test_AddStock_MultipleTimes_ExpectedFail() {
        InventoryOfObsolete instance = new InventoryOfObsolete();
        instance.addStock(ID_PADS, 10);
        instance.addStock(ID_PADS, 5);
        int expected = 16;
        int actual = instance.getStock(ID_PADS);
        assertEquals("La suma total debe ser 15.", expected, actual, 0);
    }

    @Test
    public void test_RemoveStock_FullRemoval_ExpectedFail() {
        InventoryOfObsolete instance = new InventoryOfObsolete();
        instance.setStock(ID_FILTER, 10);
        instance.removeStock(ID_FILTER, 10);
        int expected = 1;
        int actual = instance.getStock(ID_FILTER);
        assertEquals("La remoción total debe dejar 0.", expected, actual, 0);
    }

    @Test
    public void test_RemoveStock_PartialRemoval_ExpectedFail() {
        InventoryOfObsolete instance = new InventoryOfObsolete();
        instance.setStock(ID_FILTER, 20);
        instance.removeStock(ID_FILTER, 7);
        int expected = 14;
        int actual = instance.getStock(ID_FILTER);
        assertEquals("La remoción parcial debe dejar 13.", expected, actual, 0);
    }
    
    @Test
    public void test_RemoveStock_InsufficientStock_ExpectedFail() {
        InventoryOfObsolete instance = new InventoryOfObsolete();
        instance.setStock(ID_PADS, 5);
        instance.removeStock(ID_PADS, 10); 
        int expected = 6;
        int actual = instance.getStock(ID_PADS);
        assertEquals("El stock debe permanecer en 5.", expected, actual, 0);
    }
    
    @Test
    public void test_RemoveStock_ProductInexistent_ExpectedFail() {
        InventoryOfObsolete instance = new InventoryOfObsolete();
        instance.removeStock(ID_INEXISTENTE, 1);
        int expected = 1;
        int actual = instance.getStock(ID_INEXISTENTE);
        assertEquals("El stock del inexistente debe ser 0.", expected, actual, 0);
    }
    
    @Test
    public void test_SetStock_ZeroValue_ExpectedFail() {
        InventoryOfObsolete instance = new InventoryOfObsolete();
        instance.addStock(ID_PADS, 100);
        instance.setStock(ID_PADS, 0);
        int expected = 1;
        int actual = instance.getStock(ID_PADS);
        assertEquals("El stock debe ser 0.", expected, actual, 0);
    }

    @Test
    public void test_AddStock_NegativeQuantity_ExpectedFail() {
        InventoryOfObsolete instance = new InventoryOfObsolete();
        instance.setStock(ID_PADS, 50);
        int negativeQuantity = -10;
        instance.addStock(ID_PADS, negativeQuantity);
        int actualStock = instance.getStock(ID_PADS);
        int expectedStockIfValidationExisted = 50; 
        String failureMessage = "TEST FAILURE (Error Lógico de Negocio): La función 'addStock' permite restar stock, " + 
        "resultando en " + actualStock + " cuando se esperaba " + expectedStockIfValidationExisted + ".";
        assertEquals(failureMessage, expectedStockIfValidationExisted, actualStock, 0); 
    }

    @Test
    public void test_AddStock_EmptyId_Success() {
        InventoryOfObsolete instance = new InventoryOfObsolete();
        instance.setStock(ID_PADS, 10);
        instance.addStock("", 5);
        instance.addStock(null, 5); 
        int expected = 10;
        int actual = instance.getStock(ID_PADS);
        assertEquals("El stock no debe cambiar por IDs nulos o vacíos.", expected, actual, 0);
    }
}