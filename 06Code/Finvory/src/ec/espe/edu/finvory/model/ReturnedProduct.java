package ec.espe.edu.finvory.model;
/**
 *
 * @author @author Arelys Otavalo, The POOwer Rangers Of Programming
 */
public class ReturnedProduct {
    private String typeProduct;
    private double barCode;
    private String purchaseDate;
    private String returnDate;
    private float totalRefundPrice;
    private String reason;
    private boolean defectiveProduct;

    public ReturnedProduct(String typeProduct, double barCode, String purchaseDate, String returnDate, float totalRefundPrice, String reason, boolean defectiveProduct) {
        this.typeProduct = typeProduct;
        this.barCode = barCode;
        this.purchaseDate = purchaseDate;
        this.returnDate = returnDate;
        this.totalRefundPrice = totalRefundPrice;
        this.reason = reason;
        this.defectiveProduct = defectiveProduct;
    }

    public String getTypeProduct() {
        return typeProduct;
    }

    public void setTypeProduct(String typeProduct) {
        this.typeProduct = typeProduct;
    }

    public double getBarCode() {
        return barCode;
    }

    public void setBarCode(double barCode) {
        this.barCode = barCode;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public float getTotalRefundPrice() {
        return totalRefundPrice;
    }

    public void setTotalRefundPrice(float totalRefundPrice) {
        this.totalRefundPrice = totalRefundPrice;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isDefectiveProduct() {
        return defectiveProduct;
    }

    public void setDefectiveProduct(boolean defectiveProduct) {
        this.defectiveProduct = defectiveProduct;
    }
    
}
