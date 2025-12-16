package ec.edu.espe.finvory.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author Mathews Pastor, The POOwer Rangers Of Programming
 */
public class Product {

    private String id;
    private String name;
    private String description;
    private String barcode;
    private BigDecimal baseCostPrice;
    private String supplierId;

    private static final int FINAL_PRICE_SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final BigDecimal ONE = BigDecimal.ONE;

    public Product() {
        this.baseCostPrice = BigDecimal.ZERO;
    }
    
    public Product(String id, String name, String description, String barcode, BigDecimal baseCostPrice, String supplierId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.barcode = barcode;
        this.baseCostPrice = baseCostPrice.setScale(FINAL_PRICE_SCALE, ROUNDING_MODE);
        this.supplierId = supplierId;
    }

    public BigDecimal getPrice(String clientType, BigDecimal profitPercentage, BigDecimal discountStandard, BigDecimal discountPremium, BigDecimal discountVip) {
        BigDecimal cost = this.baseCostPrice; 
        BigDecimal priceBeforeDiscount = cost.multiply(BigDecimal.ONE.add(profitPercentage));
        BigDecimal customerDiscountRate;
        if ("VIP".equalsIgnoreCase(clientType)) {
            customerDiscountRate = discountVip;
        } else if ("PREMIUM".equalsIgnoreCase(clientType)) {
            customerDiscountRate = discountPremium;
        } else {
            customerDiscountRate = discountStandard;
        }
        BigDecimal discountFactor = BigDecimal.ONE.subtract(customerDiscountRate);
        BigDecimal finalPrice = priceBeforeDiscount.multiply(discountFactor);

        return finalPrice.setScale(2, RoundingMode.HALF_UP);
    }
    
    public BigDecimal getBaseCostPrice() {
        return baseCostPrice; 
    }
    
    public String getSupplierId() {
        return supplierId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public void setBaseCostPrice(BigDecimal baseCostPrice) {
        this.baseCostPrice = baseCostPrice.setScale(FINAL_PRICE_SCALE, ROUNDING_MODE);
    }
}
