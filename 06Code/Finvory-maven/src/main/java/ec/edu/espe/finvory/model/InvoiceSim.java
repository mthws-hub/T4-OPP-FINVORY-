package ec.edu.espe.finvory.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 *
 * @author Mathews Pastor, The POOwer Rangers Of Programming
 */
public class InvoiceSim {

    private String invoiceNumber;
    private LocalDate date;
    private LocalDate paymentDueDate; 
    private Customer customer;
    private ArrayList<InvoiceLineSim> lines;
    private String status; 
    private BigDecimal subtotal;
    private BigDecimal discountRate; 
    private BigDecimal discountAmount;
    private BigDecimal taxRate; 
    private BigDecimal taxAmount;
    private BigDecimal total;
    private static final int FINAL_PRICE_SCALE = 2;

    public InvoiceSim() {
        this.lines = new ArrayList<>();
        this.subtotal = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
        this.taxRate = BigDecimal.ZERO;
        this.taxAmount = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
        this.date = LocalDate.now();
        this.paymentDueDate = LocalDate.now();
        this.status = "PENDING";
    }

    public InvoiceSim(String id, LocalDate date, LocalDate paymentDueDate, Customer customer, ArrayList<InvoiceLineSim> lines, BigDecimal taxRate, BigDecimal discountRate) {
        this.invoiceNumber = id;
        this.date = date;
        this.paymentDueDate = paymentDueDate;
        this.customer = customer;
        this.lines = lines;
        this.taxRate = taxRate; 
        this.discountRate = discountRate; 
        this.status = "PENDING";
        calculateTotals(discountRate);
    }

    public final void calculateTotals(BigDecimal discountRate) {
        BigDecimal calculatedSubtotal = BigDecimal.ZERO;
        for (InvoiceLineSim line : lines) {
            calculatedSubtotal = calculatedSubtotal.add(line.getLineTotal());
        }
        
        this.subtotal = calculatedSubtotal.setScale(FINAL_PRICE_SCALE, RoundingMode.HALF_UP);
        BigDecimal calculatedDiscountAmount = this.subtotal.multiply(discountRate); 
        this.discountAmount = calculatedDiscountAmount.setScale(FINAL_PRICE_SCALE, RoundingMode.HALF_UP);
        BigDecimal taxableBase = this.subtotal.subtract(this.discountAmount);
        BigDecimal calculatedTaxAmount = taxableBase.multiply(this.taxRate);
        this.taxAmount = calculatedTaxAmount.setScale(FINAL_PRICE_SCALE, RoundingMode.HALF_UP);
        this.total = taxableBase.add(this.taxAmount).setScale(FINAL_PRICE_SCALE, RoundingMode.HALF_UP);
    }

    public void complete() {
        this.status = "COMPLETED";
    }

    public String getId() {
        return invoiceNumber;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalDate getPaymentDueDate() { 
        return paymentDueDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public ArrayList<InvoiceLineSim> getLines() {
        return lines;
    }
    
    public String getStatus() { 
        return status;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public BigDecimal getTotal() {
        return total;
    }
    
    public BigDecimal getDiscountRate() {
        return discountRate;
    }
}