package ec.espe.edu.finvory.model;
/**
 *
 * @author @author Arelys Otavalo, The POOwer Rangers Of Programming
 */
public class ProductSold {
    private String customer;
    private  float totalAmount;
    private boolean paymentOption;
    private String typeProduct;
    private String saleDate;

    public ProductSold(String customer, float totalAmount, boolean paymentOption, String typeProduct, String saleDate) {
        this.customer = customer;
        this.totalAmount = totalAmount;
        this.paymentOption = paymentOption;
        this.typeProduct = typeProduct;
        this.saleDate = saleDate;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public boolean isPaymentOption() {
        return paymentOption;
    }

    public void setPaymentOption(boolean paymentOption) {
        this.paymentOption = paymentOption;
    }

    public String getTypeProduct() {
        return typeProduct;
    }

    public void setTypeProduct(String typeProduct) {
        this.typeProduct = typeProduct;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(String saleDate) {
        this.saleDate = saleDate;
    }
    
    
}
