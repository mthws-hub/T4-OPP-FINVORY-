package ec.edu.espe.finvory.model;
/**
 *
 * @author @author Arelys Otavalo, The POOwer Rangers Of Programming
 */

public class ReturnedProduct {
    private Product product;
    private int quantity;
    private String reason;

    public ReturnedProduct() {}

    public ReturnedProduct(Product product, int quantity, String reason) {
        this.product = product;
        this.quantity = quantity;
        this.reason = reason;
    }

    public Product getProduct() { 
        return product; 
    }
    
    public int getQuantity() { 
        return quantity; 
    }
    
    public String getReason() { 
        return reason; 
    }
}
