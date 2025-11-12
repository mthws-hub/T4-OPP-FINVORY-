package ec.espe.edu.finvory.model;
/**
 *
 * @author Maryuri Quiña, @ESPE
 */
public class InvoiceLineSim {
    private String productId;
    private String productName;
    private int quantity;
    private float priceApplied;
    private float lineTotal;

    public InvoiceLineSim(String productId, String productName, int quantity, double unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.priceApplied = priceApplied;
        this.lineTotal = quantity * priceApplied;
    }

    public double getLineTotal() {
        return lineTotal;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
