package ec.espe.edu.finvory.model;
import ec.espe.edu.finvory.model.*;
import ec.espe.edu.finvory.view.FinvoryView;
import ec.espe.edu.finvory.controller.FinvoryController;
import java.lang.reflect.Method;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Mathews Pastor, The POOwer Rangers Of Programming
 */
public class FinvoryControllerTest {
    private FinvoryController controller;
    private FinvoryData data;

    @Before
    public void setUp() {
        // Preparamos datos simulados
        data = new FinvoryData();
        data.getCustomers().add(new Customer("Juan Perez", "0170000001", "0994521698", "j@gmail.com", "STANDARD"));
        data.getCustomers().add(new Customer("Empresa ABC", "1790011223001", "0972136548", "abc@corp.com", "PREMIUM"));
        data.getCustomers().add(new Customer("Maria One", "1711111111", "0989854123", "m1@gmail.com", "VIP"));
        data.getCustomers().add(new Customer("Maria Two", "1722222222", "0989854123", "m2@gmail.com", "VIP"));
        
        controller = new FinvoryController(data, new FinvoryView(), new Database());
    }
    
    private Customer invokeFindCustomer(String query) throws Exception {
        Method method = FinvoryController.class.getDeclaredMethod("findCustomer", String.class);
        method.setAccessible(true);
        return (Customer) method.invoke(controller, query);
    }

    @Test
    public void testSearch_ExactId_Found() throws Exception {
        Customer found = invokeFindCustomer("0170000001");
        assertNotNull("Debe encontrar por ID exacto", found);
        assertEquals("Juan Perez", found.getName());
    }

    @Test
    public void testSearch_PartialName_Found() throws Exception {
        Customer found = invokeFindCustomer("empresa");
        assertNotNull("Debe encontrar por nombre parcial", found);
        assertEquals("Empresa ABC", found.getName());
    }

    @Test
    public void testSearch_CaseInsensitiveId() throws Exception {
        data.getCustomers().add(new Customer("TestUpper", "PKR1", "0", "e", "S"));
        
        Customer found = invokeFindCustomer("pkr1");
        if(found == null) {
            System.out.println("La lógica actual no soporta Case Insensitive en ID.");
        }
        assertNotNull("Debe ignorar mayúsculas/minúsculas en ID", found);
    }

    @Test
    public void testSearch_AmbiguousResult() throws Exception {
        Customer found = invokeFindCustomer("maria");
        assertNotNull(found);
        assertTrue(found.getName().contains("Maria"));
    }

    @Test
    public void testSearch_NotFound_ReturnsNull() throws Exception {
        Customer found = invokeFindCustomer("XYZ999");
        assertNull("Debe retornar null si no existe", found);
    }
    
    @Test
    public void testSearch_EmptyQuery_ReturnsNull() throws Exception {
        Customer found = invokeFindCustomer("");
        assertNull("Query vacío no debe retornar resultados arbitrarios", found);
    }
}
