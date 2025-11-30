package ec.espe.edu.finvory.model;
import java.util.HashMap;
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
    
    public int getStock(String productId) {
        return productStock.getOrDefault(productId, 0);
    }
    
    public void setStock(String productId, int quantity) {
        productStock.put(productId, quantity);
    }
    
    public void addStock(String productId, int quantity) {
        int currentStock = getStock(productId);
        productStock.put(productId, currentStock + quantity);
    }
    
    public void removeStock(String productId, int quantity) {
        int currentStock = getStock(productId);
        if (currentStock >= quantity) {
            productStock.put(productId, currentStock - quantity);
        }
    }
}