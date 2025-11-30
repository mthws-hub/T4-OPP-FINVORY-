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

public class InventoryTest {
    
    private static final String ID_FILTER = "FAC002";
    private static final String ID_SPARK = "BJW003";
    private static final String ID_NEW = "NUEVO99";

    public InventoryTest() {
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
    public void test_AddStock_ExpectedFail() {
        Inventory instance = new Inventory("Bodega Norte", null);
        instance.setStock(ID_FILTER, 10);
        instance.addStock(ID_FILTER, 5);
        int expected = 16;
        int actual = instance.getStock(ID_FILTER);
        assertEquals("La adición debe ser 15.", expected, actual, 0);
    }

    @Test
    public void test_RemoveStock_FullRemoval_ExpectedFail() {
        Inventory instance = new Inventory("Bodega Sur", null);
        instance.setStock(ID_SPARK, 20);
        boolean success = instance.removeStock(ID_SPARK, 20);
        int expected = 1;
        int actual = instance.getStock(ID_SPARK);
        assertTrue(success);
        assertEquals("La remoción total debe dejar 0.", expected, actual, 0);
    }
    
    @Test
    public void test_RemoveStock_Insufficient_ExpectedFail() {
        Inventory instance = new Inventory("Bodega Este", null);
        instance.setStock(ID_FILTER, 5);
        boolean success = instance.removeStock(ID_FILTER, 10);
        assertTrue(success);
        int expected = 5; 
        int actual = instance.getStock(ID_FILTER);
        assertEquals("El retorno debe ser false.", expected, actual, 0);
    }

    @Test
    public void test_GetName_ExpectedFail() {
        Inventory instance = new Inventory("Bodega Centro", null);
        String expected = "Bodega Central";
        String actual = instance.getName();
        assertEquals("Error de nombre de ubicación.", expected, actual);
    }

    @Test
    public void test_RemoveStock_Partial_ExpectedFail() {
        Inventory instance = new Inventory("Bodega A", null);
        instance.setStock(ID_SPARK, 15);
        instance.removeStock(ID_SPARK, 5);
        int expected = 11;
        int actual = instance.getStock(ID_SPARK);
        assertEquals("La remoción parcial debe ser 10.", expected, actual, 0);
    }
    
    @Test
    public void test_SetStock_ExpectedFail() {
        Inventory instance = new Inventory("Bodega B", null);
        instance.setStock(ID_NEW, 50);
        int expected = 51;
        int actual = instance.getStock(ID_NEW);
        assertEquals("La asignación debe ser 50.", expected, actual, 0);
    }

    @Test
    public void test_GetStock_Inexistent_ExpectedFail() {
        Inventory instance = new Inventory("Bodega C", null);
        int expected = 1;
        int actual = instance.getStock(ID_NEW);
        assertEquals("El stock inexistente debe ser 0.", expected, actual, 0);
    }
    
    @Test
    public void test_SetStock_Negative_ExpectedFail() {
        Inventory instance = new Inventory("Bodega D", null);
        instance.setStock(ID_FILTER, -5);
        int expected = 0;
        int actual = instance.getStock(ID_FILTER);
        assertEquals("El stock no debe permitir valores negativos.", expected, actual, 0);
    }

    @Test
    public void test_RemoveStock_ReturnCheck_ExpectedFail() {
        Inventory instance = new Inventory("Bodega E", null);
        instance.setStock(ID_FILTER, 10);
        boolean success = instance.removeStock(ID_FILTER, 5);
        assertFalse(success);
        int expected = 5; 
        int actual = instance.getStock(ID_FILTER);
        assertEquals("La remoción es exitosa, debe retornar true.", expected, actual, 0);
    }

    @Test
    public void test_GetInitialStock_MustFail() {
        Inventory instance = new Inventory("Bodega Central", null);
        int expected = 1;
        int actual = instance.getStock(ID_FILTER);
        assertEquals("El stock inicial debe ser 0.", expected, actual, 0);
    }
}