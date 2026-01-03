package ec.edu.espe.finvory.model;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Mathews Pastor, The POOwer Rangers Of Programming
 */
public class InventoryOfObsolete {

    private Address address;
    private HashMap<String, Integer> productStock;

    public InventoryOfObsolete() {
        this.productStock = new HashMap<>();
    }

    public InventoryOfObsolete(Address address) {
        this.address = address;
        this.productStock = new HashMap<>();
    }

    public Address getAddress() {
        return address;
    }

    public Map<String, Integer> getProductStock() {
        return productStock;
    }

    public int getStock(String productId) {
        return productStock.getOrDefault(productId, 0);
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setStock(String productId, int quantity) {
        if (quantity < 0) {
            System.err.println("Advertencia: No se puede establecer stock obsoleto negativo.");
            productStock.put(productId, 0);
        } else {
            productStock.put(productId, quantity);
        }
    }

    public void addStock(String productId, int quantity) {
        if (quantity <= 0) {
            System.err.println("Advertencia: La cantidad a añadir debe ser positiva.");
            return;
        }
        int currentStock = getStock(productId);
        productStock.put(productId, currentStock + quantity);
    }

    public boolean removeStock(String productId, int quantity) {
        if (quantity <= 0) {
            System.err.println("Advertencia: La cantidad a remover debe ser positiva.");
            return false;
        }

        int currentStock = getStock(productId);
        if (currentStock >= quantity) {
            productStock.put(productId, currentStock - quantity);
            return true;
        } else {
            System.err.printf("Error: No hay suficiente stock obsoleto de %s. Stock actual: %d, Intento remover: %d%n", productId, currentStock, quantity);
            return false;
        }
    }

}
