package ec.edu.espe.finvory.controller;

import ec.edu.espe.finvory.model.*;
import java.util.List;

/**
 *
 * @author Arelys Otavalo, the POOwer Rangers of Programming
 */
public class ObsoleteController {

    private final FinvoryController mainController;

    public ObsoleteController(FinvoryController mainController) {
        this.mainController = mainController;
    }

    public int getObsoleteStock(String productId) {
        int total = 0;
        if (mainController.data == null) {
            return 0;
        }

        for (ReturnedProduct ret : mainController.data.getReturns()) {
            if (ret.getProduct().getId().equals(productId)) {
                total += ret.getQuantity();
            }
        }
        return total;
    }

    private void updateReturnsList(String productId, int quantity, String reason) {
        List<ReturnedProduct> returnsList = mainController.data.getReturns();
        int remainingToDiscard = quantity;

        for (int i = 0; i < returnsList.size(); i++) {
            ReturnedProduct returnedProduct = returnsList.get(i);
            if (returnedProduct.getProduct().getId().equals(productId)
                    && returnedProduct.getReason().equalsIgnoreCase(reason)) {

                int currentQuantity = returnedProduct.getQuantity();
                if (currentQuantity <= remainingToDiscard) {
                    remainingToDiscard -= currentQuantity;
                    returnsList.remove(i);
                    i--;
                } else {
                    returnedProduct.setQuantity(currentQuantity - remainingToDiscard);
                    remainingToDiscard = 0;
                    break;
                }
            }
        }

        if (remainingToDiscard > 0) {
            for (int i = 0; i < returnsList.size(); i++) {
                ReturnedProduct returnedProduct = returnsList.get(i);
                if (returnedProduct.getProduct().getId().equals(productId)) {
                    int currentQty = returnedProduct.getQuantity();
                    if (currentQty <= remainingToDiscard) {
                        remainingToDiscard -= currentQty;
                        returnsList.remove(i);
                        i--;
                    } else {
                        returnedProduct.setQuantity(currentQty - remainingToDiscard);
                        remainingToDiscard = 0;
                        break;
                    }
                }
            }
        }
    }

    public boolean reassignObsoleteProduct(String productId, int quantity, String destinationInventoryName, String reasonForRelocation) {
        if (quantity <= 0) {
            return false;
        }

        InventoryOfObsolete obsolete = mainController.data.getObsoleteInventory();
        Inventory destination = mainController.data.getInventories().stream()
                .filter(inv -> inv.getName().equalsIgnoreCase(destinationInventoryName))
                .findFirst()
                .orElse(null);

        if (destination == null || obsolete.getStock(productId) < quantity) {
            return false;
        }

        obsolete.addStock(productId, -quantity);
        destination.addStock(productId, quantity);

        updateReturnsList(productId, quantity, reasonForRelocation);

        mainController.saveData();
        return true;
    }

    public boolean discardObsoleteProduct(String productId, int quantity, String reasonOfDiscard) {
        if (quantity <= 0) {
            return false;
        }

        InventoryOfObsolete obsolete = mainController.data.getObsoleteInventory();

        if (obsolete.getStock(productId) < quantity) {
            return false;
        }

        obsolete.addStock(productId, -quantity);

        updateReturnsList(productId, quantity, reasonOfDiscard);

        mainController.saveData();
        return true;
    }
}
