package ec.edu.espe.finvory.controller;

import ec.edu.espe.finvory.model.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Arelys Otavalo, the POOwer Rangers of Programming
 */
public class InventoryController {
    
    private final FinvoryController mainController;

    public InventoryController(FinvoryController mainController) {
        this.mainController = mainController;
    }


    public boolean handleCreateInventory(String name, Address address) {

        for (Inventory inventory : mainController.data.getInventories()) {
            if (inventory.getName().equalsIgnoreCase(name)) {
                return false;
            }
        }
        Inventory newInv = new Inventory(name, address);
        mainController.data.addInventory(newInv);

        mainController.saveData();
        return true;
    }

    public Inventory findInventoryByName(String nameInventory) {
        if (mainController.data == null || mainController.data.getInventories() == null) {
            return null;
        }
        for (Inventory inventory : mainController.data.getInventories()) {
            if (inventory.getName().equalsIgnoreCase(nameInventory)) {
                return inventory;
            }
        }
        return null;
    }

    public ArrayList<Inventory> findInventoriesByPartialName(String partialName) {
        ArrayList<Inventory> matches = new ArrayList<>();
        if (mainController.data == null) return matches;
        
        String q = partialName.trim().toLowerCase();
        for (Inventory inv : mainController.data.getInventories()) {
            if (inv.getName() != null && inv.getName().toLowerCase().contains(q)) {
                matches.add(inv);
            }
        }
        return matches;
    }

    public List<Object[]> getInventoryTableData(Inventory currentInventory) {
        List<Object[]> rows = new ArrayList<>();
        List<Product> globalProducts = mainController.getProducts();
        
        if (globalProducts == null || globalProducts.isEmpty() || mainController.data == null) {
            return rows;
        }

        float profit = mainController.data.getProfitPercentage() != null ? mainController.data.getProfitPercentage().floatValue() : 0.0f;
        float discountStandard = mainController.data.getDiscountStandard() != null ? mainController.data.getDiscountStandard().floatValue() : 0.0f;
        float discountPremium = mainController.data.getDiscountPremium() != null ? mainController.data.getDiscountPremium().floatValue() : 0.0f;
        float discountVip = mainController.data.getDiscountVip() != null ? mainController.data.getDiscountVip().floatValue() : 0.0f;

        for (Product product : globalProducts) {
            String productId = product.getId();
            int stock = currentInventory.getStock(productId);

            if (stock == 0 && currentInventory.getStock(productId.trim()) > 0) {
                stock = currentInventory.getStock(productId.trim());
            }

            int obsoleteStock = mainController.getObsoleteStock(productId);

            rows.add(new Object[]{
                product.getId(), 
                product.getName(), 
                product.getBarcode(),
                String.format("$%.2f", product.getBaseCostPrice()),
                String.format("$%.2f", product.getPrice("STANDARD", new BigDecimal(profit), new BigDecimal(discountStandard), new BigDecimal(discountPremium), new BigDecimal(discountVip))),
                String.format("$%.2f", product.getPrice("PREMIUM", new BigDecimal(profit), new BigDecimal(discountStandard), new BigDecimal(discountPremium), new BigDecimal(discountVip))),
                String.format("$%.2f", product.getPrice("VIP", new BigDecimal(profit), new BigDecimal(discountStandard), new BigDecimal(discountPremium), new BigDecimal(discountVip))),
                stock,
                obsoleteStock
            });
        }
        return rows;
    }

    public boolean handleZeroStock(Inventory inventory, String productId) {
        if (inventory == null || productId == null) {
            return false;
        }
        inventory.setStock(productId, 0);
        mainController.saveData();
        return true;
    }
}
