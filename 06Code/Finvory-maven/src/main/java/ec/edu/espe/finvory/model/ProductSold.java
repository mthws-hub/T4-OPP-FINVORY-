package ec.edu.espe.finvory.model;
/**
 *
 * @author @author Arelys Otavalo, The POOwer Rangers Of Programming
 */

public class ProductSold {
    private String productId;
    private String productName;
    private int quantity;
    private float totalSold;

    public ProductSold(String productId, String productName, int quantity, float totalSold) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.totalSold = totalSold;
    }
    
    public String getProductId() {
        return productId;
    }

    public String getProductName() { 
        return productName; 
    }
    
    public int getQuantity() { 
        return quantity; 
    }
    
    public float getTotalSold() { 
        return totalSold; 
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setTotalSold(float totalSold) {
        this.totalSold = totalSold;
    }
    
}
