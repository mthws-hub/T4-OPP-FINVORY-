package ec.edu.espe.finvory.model;

import java.util.HashMap;
import java.util.Map;

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

    public Map<String, Integer> getProductStock() {
        return productStock;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setStock(String productId, int quantity) {
        if (quantity < 0) {

            System.err.println("Advertencia: El stock no puede ser negativo. Estableciendo a 0.");
            productStock.put(productId, 0);
        } else {
            productStock.put(productId, quantity);
        }
    }

    public void addStock(String productId, int quantity) {
        if (quantity < 0) {
            System.err.println("Advertencia: Usar removeStock() para restar. Se ignorará la operación.");
            return;
        }
        int currentStock = getStock(productId);
        productStock.put(productId, currentStock + quantity);
    }

    public boolean removeStock(String productId, int quantity) {
        if (quantity < 0) {
            System.err.println("Advertencia: La cantidad a remover no puede ser negativa.");
            return false;
        }
        int currentStock = getStock(productId);
        if (currentStock >= quantity) {
            productStock.put(productId, currentStock - quantity);
            return true;
        }
        return false;
    }
}
