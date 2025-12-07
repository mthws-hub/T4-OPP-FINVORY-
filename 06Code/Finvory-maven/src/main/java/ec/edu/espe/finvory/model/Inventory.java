package ec.edu.espe.finvory.model;

import java.util.HashMap;

/**
 *
 * @author Arelys Otavalo, The POOwer Rangers Of Programming
 */

public class Inventory {
    
    private String name;
    private Address address;
    private HashMap<String, Integer> productStock;

    public Inventory() {
        this.productStock = new HashMap<>();
    }
    
    public Inventory(String name, Address address) {
        this.name = name;
        this.address = address;
        this.productStock = new HashMap<>();
    }
    
    public String getName() { 
        return name; 
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
    
    public boolean removeStock(String productId, int quantity) {
        int currentStock = getStock(productId);
        if (currentStock >= quantity) {
            productStock.put(productId, currentStock - quantity);
            return true;
        }
        return false;
    }
}