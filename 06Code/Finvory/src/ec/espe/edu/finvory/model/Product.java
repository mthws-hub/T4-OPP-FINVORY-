package ec.espe.edu.finvory.model;

/**
 *
 * @author Arelys Otavalo, The POOwer Rangers Of Programming
 */

public class Product {
    
    private float purchasePrice;
    private float price1;       
    private float price2;       
    private float price3;       
    private String description;
    private String productCode; 
    private double barCode;
    private int availabilityProduct; 

    public Product() {}

    public Product(float purchasePrice, float price1, float price2, float price3, 
                   String description, String productCode, double barCode, 
                   int availabilityProduct) {
        this.purchasePrice = purchasePrice;
        this.price1 = price1;
        this.price2 = price2;
        this.price3 = price3;
        this.description = description;
        this.productCode = productCode;
        this.barCode = barCode;
        this.availabilityProduct = availabilityProduct;
    }

    public float getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(float purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public float getPrice1() {
        return price1;
    }

    public void setPrice1(float price1) {
        this.price1 = price1;
    }

    public float getPrice2() {
        return price2;
    }

    public void setPrice2(float price2) {
        this.price2 = price2;
    }

    public float getPrice3() {
        return price3;
    }

    public void setPrice3(float price3) {
        this.price3 = price3;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public double getBarCode() {
        return barCode;
    }

    public void setBarCode(double barCode) {
        this.barCode = barCode;
    }

    public int getAvailabilityProduct() {
        return availabilityProduct;
    }

    public void setAvailabilityProduct(int availabilityProduct) {
        this.availabilityProduct = availabilityProduct;
    }
    
}
