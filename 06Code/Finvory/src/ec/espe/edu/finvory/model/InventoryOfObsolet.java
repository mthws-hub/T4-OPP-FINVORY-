package ec.espe.edu.finvory.model;

/**
 *
 * @author Arelys Otavalo, The POOwer Rangers Of Programming
 */
public class InventoryOfObsolet {
    private Address inventoryLocation;
    private String inventoryId;

    public InventoryOfObsolet(Address inventoryLocation, String inventoryId) {
        this.inventoryLocation = inventoryLocation;
        this.inventoryId = inventoryId;
    }
    
    public Address getInventoryLocation() {
        return inventoryLocation;
    }

    public void setInventoryLocation(Address inventoryLocation) {
        this.inventoryLocation = inventoryLocation;
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }

    
}
