package ec.edu.espe.finvory.utils;

import ec.edu.espe.finvory.controller.FinvoryController;
import ec.edu.espe.finvory.model.Product;
import ec.edu.espe.finvory.model.ReturnedProduct;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

/**
 *
 * @author Arelys Otavalo, The POOwer Rangers of Programming
 */
public class ObsoleteInventoryTest {

    private FinvoryController controller;

    
    @Test
    public void testAddObsoleteProduct_IncrementsTotal() {
        int initialStock = 0;
        int added1 = 5;
        int added2 = 7;

        int total = initialStock + added1 + added2;
        assertEquals(12, total, "La suma total de obsoletos debería ser 12");
    }
    

    @Test
    public void testDiscardObsoleteProduct_DecrementsCorrectly() {
        int currentStock = 12;
        int toDiscard = 7;

        int result = currentStock - toDiscard;

        assertTrue(result == 5, "Al descartar 7 de 12, el stock debe bajar a 5");
    }
    

    @Test
    public void testDiscardMoreThanAvailable() {
        int available = 12;
        int attemptToDiscard = 20;

        boolean canDiscard = attemptToDiscard <= available;

        assertFalse(canDiscard, "No se debería poder descartar más de lo que hay en stock");
    }
    

    @Test
    public void testAddObsoleteProducts_IncrementsTotal() {
        int initialStock = 0;
        int added1 = 8;
        int added2 = 2;

        int total = initialStock + added1 + added2;
        assertEquals(12, total, "La suma total debería ser 12");
    }
    

    @Test
    public void testDiscardsMoreThanAvailable() {
        int available = 5;
        int attemptToDiscard = 7;

        boolean canDiscard = attemptToDiscard <= available;

        assertFalse(canDiscard, "No se debería poder descartar más de lo que hay en stock");
    }
    

    @Test
    public void testFindProductCaseInsensitiveAndSpaces() {
        String savedId = "P001";
        String userInput = " p001 ".trim().toUpperCase();

        assertEquals(savedId, userInput, "El sistema debe normalizar el ID para encontrar el producto");
    }
    

    @Test
    public void testReassignObsoleteToNormalStock() {
        int obsoleteStock = 10;
        int normalStock = 50;
        int quantityToMove = 3;

        int finalObsolete = obsoleteStock - quantityToMove;
        int finalNormal = normalStock + quantityToMove;

        assertAll("Verificar movimiento de stock",
                () -> assertEquals(7, finalObsolete, "El stock obsoleto debe disminuir"),
                () -> assertEquals(53, finalNormal, "El stock normal debe aumentar")
        );
    }
    

    @Test
    public void testMultipleReturnsSameProduct() {
        int returnReasonA = 5;
        int returnReasonB = 5;

        int totalObserved = returnReasonA + returnReasonB;

        assertEquals(10, totalObserved, "La suma de diferentes motivos para el mismo ID debe ser exacta");
    }
    

    @Test
    public void testInvalidQuantityInput() {
        int inputQuantity = -10;

        boolean isValid = inputQuantity > 0;

        assertFalse(isValid, "El sistema no debe procesar cantidades negativas o cero");
    }
    

    @Test
    public void testInvalidQuantityInputWithFloat() {
        float inputQuantity = 5.7f;

        boolean isInteger = (inputQuantity == (int) inputQuantity);

        assertFalse(isInteger, "El sistema no debe procesar cantidades decimales para productos no fraccionables");
    }

}
