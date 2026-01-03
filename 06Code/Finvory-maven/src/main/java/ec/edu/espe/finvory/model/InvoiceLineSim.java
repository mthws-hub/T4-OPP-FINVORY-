package ec.edu.espe.finvory.model;
import java.math.BigDecimal;
import java.math.RoundingMode;
/**
 *
 * @author Mathews Pastor, The POOwer Rangers Of Programming
 */
public class InvoiceLineSim {
    
    private String productId;
    private String productName;
    private int quantity;
    private BigDecimal priceApplied; 
    private BigDecimal lineTotal;   
    
    private static final int FINAL_PRICE_SCALE = 2; 

    public InvoiceLineSim() {
        this.priceApplied = BigDecimal.ZERO;
        this.lineTotal = BigDecimal.ZERO;
    }
    
    public InvoiceLineSim(String productId, String productName, int quantity, BigDecimal priceApplied) { 
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.priceApplied = priceApplied;
        BigDecimal quantityBD = BigDecimal.valueOf(quantity);
        this.lineTotal = quantityBD.multiply(priceApplied);
        this.lineTotal = this.lineTotal.setScale(FINAL_PRICE_SCALE, RoundingMode.HALF_UP);
    }

    public InvoiceLineSim(Product product, int quantity, BigDecimal priceApplied) { 
        this.productId = product.getId();
        this.productName = product.getName();
        this.quantity = quantity;
        this.priceApplied = priceApplied;
        BigDecimal quantityBD = BigDecimal.valueOf(quantity);
        this.lineTotal = quantityBD.multiply(priceApplied);
        this.lineTotal = this.lineTotal.setScale(FINAL_PRICE_SCALE, RoundingMode.HALF_UP);
    }

    public String getProductId() { 
        return productId; 
    }
    
    public BigDecimal getLineTotal() { 
        return lineTotal; 
    }
    
    public String getProductName() { 
        return productName; 
    }
    
    public int getQuantity() { 
        return quantity; 
    }
    
    public BigDecimal getPriceApplied() { 
        return priceApplied;
    }

    public static int getFINAL_PRICE_SCALE() {
        return FINAL_PRICE_SCALE;
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

    public void setPriceApplied(BigDecimal priceApplied) {
        this.priceApplied = priceApplied;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }
    
    
}