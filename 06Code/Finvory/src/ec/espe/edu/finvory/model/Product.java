package ec.espe.edu.finvory.model;
/**
 *
 * @author Mathews Pastor, The POOwer Rangers Of Programming
 */
public class Product {
    
    private String id;
    private String name;
    private String description;
    private String barcode;
    private float baseCostPrice; 
    private String supplierId;

    public Product() {}

    public Product(String id, String name, String description, String barcode, float baseCostPrice, String supplierId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.barcode = barcode;
        this.baseCostPrice = baseCostPrice;
        this.supplierId = supplierId;
    }
    
    public float getPrice(String clientType, float profit, float dStandard, float dPremium, float dVip) {
        float priceWithProfit = this.baseCostPrice * (1 + profit);
        
        if ("VIP".equals(clientType)) {
            return priceWithProfit * (1 - dVip);
        }
        if ("PREMIUM".equals(clientType)) {
            return priceWithProfit * (1 - dPremium);
        }
        return priceWithProfit * (1 - dStandard);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getBarcode() { return barcode; }
    public float getBaseCostPrice() { return baseCostPrice; }
    public String getSupplierId() { return supplierId; }
    
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setBaseCostPrice(float baseCostPrice) { this.baseCostPrice = baseCostPrice; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }
}