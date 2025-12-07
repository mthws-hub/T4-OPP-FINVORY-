package ec.edu.espe.finvory.model;

import java.time.LocalDate;
import java.util.ArrayList;
/**
 *
 * @author Joseph Medina, The POOwer Rangers of Programming
 */

public class InvoiceSim {
    
    private String id;
    private String date; 
    private Customer customer;
    private ArrayList<InvoiceLineSim> lines;
    private float subtotal;
    private float tax;
    private float total;
    private String paymentMethod; 
    private String paymentDueDate;
    private String status; 

    public InvoiceSim() {
        this.lines = new ArrayList<>(); 
    }

    public InvoiceSim(String id, Customer customer, String paymentMethod, String paymentDueDate) {
        this.id = id;
        this.customer = customer;
        this.paymentMethod = paymentMethod;
        this.paymentDueDate = paymentDueDate;
        this.date = LocalDate.now().toString(); 
        this.status = "PENDING";
        this.lines = new ArrayList<>();
    }
    
    public void setDate(String date) {
        this.date = date;
    }

    public void addLine(Product product, int qty, float price) {
        lines.add(new InvoiceLineSim(product, qty, price));
    }

    public void calculateTotals(float taxRate) {
        this.subtotal = 0;
        for (InvoiceLineSim line : lines) { 
            this.subtotal += line.getLineTotal();
        }
        this.tax = this.subtotal * taxRate;
        this.total = this.subtotal + this.tax;
    }
    
    public void complete() { 
        this.status = "COMPLETED"; 
        if (this.date == null || this.date.isEmpty()) {
            this.date = LocalDate.now().toString();
        }
    }

    public String getId() { 
        return id; 
    }
    
    public String getStatus() { 
        return status; 
    }
    
    public String getDate() { 
        return date; 
    }
    
    public float getTotal() { 
        return total; 
    }
    
    public float getSubtotal() { 
        return subtotal; 
    }
    
    public float getTax() { 
        return tax; 
    }
    
    public Customer getCustomer() { 
        return customer; 
    }
    public ArrayList<InvoiceLineSim> getLines() { 
        return lines; 
    }
    
    public String getPaymentDueDate() {
        return paymentDueDate; 
    }
}