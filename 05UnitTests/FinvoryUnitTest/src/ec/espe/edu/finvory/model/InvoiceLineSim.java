package ec.espe.edu.finvory.model;
/**
 *
 * @author Mathews Pastor, The POOwer Rangers Of Programming
 */
public class InvoiceLineSim {
    
    private String productId;
    private String productName;
    private int quantity;
    private float priceApplied;
    private float lineTotal;

    public InvoiceLineSim() {}

    public InvoiceLineSim(Product product, int quantity, float priceApplied) {
        this.productId = product.getId();
        this.productName = product.getName();
        this.quantity = quantity;
        this.priceApplied = priceApplied;
        this.lineTotal = quantity * priceApplied;
    }

    public String getProductId() { 
        return productId; 
    }
    
    public float getLineTotal() { 
        return lineTotal; 
    }
    
    public String getProductName() { 
        return productName; 
    }
    
    public int getQuantity() { 
        return quantity; 
    }
}