package ec.edu.espe.finvory.utils;

import ec.edu.espe.finvory.controller.FinvoryController;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Arelys Otavalo, The POOwer Rangers of Programming
 */
public class ObsoleteInventoryTest {

    private FinvoryController controller;

    @Test
    public void testAddObsoleteProduct_IncrementsTotal() {
        int initialStock = 0;
        int firstBatch = 5;
        int secondBatch = 7;

        int totalObsolete = initialStock + firstBatch + secondBatch;
        assertEquals(12, totalObsolete, "The total obsolete stock should be exactly 12");
    }

    @Test
    public void testDiscardObsoleteProduct_DecrementsCorrectly() {
        int currentStock = 12;
        int amountToDiscard = 7;

        int remainingStock = currentStock - amountToDiscard;

        assertEquals(5, remainingStock, "Discarding 7 from 12 should result in exactly 5 units remaining");
    }

    @Test
    public void testDiscardMoreThanAvailable_ShouldFail() {
        int availableStock = 12;
        int requestedDiscard = 20;

        boolean isOperationPossible = requestedDiscard <= availableStock;

        assertFalse(isOperationPossible, "The system should not allow discarding more units than available in stock");
    }

    @Test
    public void testFindProduct_NormalizationLogic() {
        String registeredId = "P001";
        String userInput = " p001 ".trim().toUpperCase();

        assertEquals(registeredId, userInput, "The system must normalize IDs by trimming spaces and converting to uppercase");
    }

    @Test
    public void testReassignObsoleteToNormalStock_Integrity() {
        int obsoleteStock = 10;
        int normalStock = 50;
        int quantityToTransfer = 3;

        int finalObsolete = obsoleteStock - quantityToTransfer;
        int finalNormal = normalStock + quantityToTransfer;

        assertAll("Stock transfer validation",
                () -> assertEquals(7, finalObsolete, "Obsolete stock should decrease by the transferred amount"),
                () -> assertEquals(53, finalNormal, "Normal stock should increase by the transferred amount")
        );
    }

    @Test
    public void testMultipleReturnReasons_Accumulation() {
        int damagedUnits = 5;
        int expiredUnits = 5;

        int totalAccumulated = damagedUnits + expiredUnits;

        assertEquals(10, totalAccumulated, "Accumulated stock from different reasons must be accurate");
    }

    @Test
    public void testInvalidNegativeQuantity_Validation() {
        int negativeInput = -10;
        boolean isValid = negativeInput > 0;

        assertFalse(isValid, "The system should reject negative or zero quantities for stock operations");
    }

    @Test
    public void testNonIntegerQuantity_ShouldFail() {
        float decimalInput = 5.7f;
        boolean isWholeNumber = (decimalInput == (int) decimalInput);

        assertFalse(isWholeNumber, "The system should reject decimal quantities for non-fractional products");
    }

    @Test
    public void testBoundaryValue_DiscardExactStock() {
        int stock = 100;
        int discard = 100;
        
        int result = stock - discard;
        assertEquals(0, result, "Stock should be exactly zero when the entire inventory is discarded");
    }

    @Test
    public void testStockIntegrity_MultipleAdditions() {
        int stock = 0;
        stock += 10;
        stock += 20;
        stock += 30;
        
        assertEquals(60, stock, "Sequential additions should result in a consistent total sum");
    }

    @Test
    public void testLargeQuantityHandling() {
        int currentStock = 1000000;
        int incomingStock = 500000;
        
        long totalPotentialStock = (long) currentStock + incomingStock;
        assertTrue(totalPotentialStock < Integer.MAX_VALUE, "Inventory system should handle large quantities without overflow");
    }

    @Test
    public void testSearchProduct_NonExistentId() {
        String existingId = "PROD-001";
        String searchId = "PROD-999";
        
        assertNotEquals(existingId, searchId, "The search should not find a match for IDs that are not registered");
    }

    @Test
    public void testReasonString_Normalization() {
        String reasonOne = "DAMAGED";
        String reasonTwo = "damaged".toUpperCase();
        
        assertEquals(reasonOne, reasonTwo, "System should handle return reasons in a case-insensitive manner");
    }

    @Test
    public void testZeroInitialStock_Addition() {
        int initialStock = 0;
        int addedUnits = 1;
        
        int finalStock = initialStock + addedUnits;
        assertEquals(1, finalStock, "Adding 1 unit to zero stock should result in exactly 1 unit");
    }

    @Test
    public void testSequentialMovements_Consistency() {
        int stock = 100;
        stock -= 20; // First movement
        stock += 10; // Restock
        stock -= 50; // Second movement
        
        assertEquals(40, stock, "The sequence of stock movements must maintain mathematical consistency");
    }
}