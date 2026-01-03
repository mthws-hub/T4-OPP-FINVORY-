package ec.edu.espe.finvory.model;

import java.time.LocalDate;

/**
 *
 * @author @author Arelys Otavalo, The POOwer Rangers Of Programming
 */

public class ReturnedProduct {

    private Product product;
    private int quantity;
    private String reason;
    private LocalDate returnDate;

    public ReturnedProduct() {
    }

    public ReturnedProduct(Product product, int quantity, String reason) {
        this.product = product;
        this.quantity = quantity;
        this.reason = reason;
        this.returnDate = LocalDate.now();
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
    

}
