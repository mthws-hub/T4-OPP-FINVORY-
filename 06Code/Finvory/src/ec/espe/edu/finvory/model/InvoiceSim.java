package ec.espe.edu.finvory.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.util.ArrayList;
/**
 *
 * @author Maryuri Quiña, @ESPE
 */
public class InvoiceSim {
    public enum Status {ACTIVE,CANCELLED,REFUNDED}
    private String id;
    private Customer customer;
    private LocalDate date;
    private String paymentMethod;
    private List<InvoiceLineSim> lines;
    private Status status = Status.ACTIVE;
    private double totalNet;
    private double totalTax;
    private double totalGross;
    
    public InvoiceSim() {
        this.lines = new ArrayList<>(); 
    }

    public InvoiceSim(String id, Customer customer, LocalDate date, String paymentMethod, List<InvoiceLineSim> lines) {
        this.id = id;
        this.customer = customer;
        this.paymentMethod = paymentMethod;
        this.paymentDueDate = paymentDueDate;
        this.date = LocalDate.now().toString(); 
        this.status = "PENDING";
        this.lines = new ArrayList<>();
    }

public void addLine(Product p, int qty, float priceApplied) {
        lines.add(new InvoiceLineSim(p, qty, priceApplied));
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
    }
    
    public void cancel() { 
        this.status = "CANCELED"; 
    }

    public String getId() { 
        return id; 
    }
    
    public String getStatus() { 
        return status; }
    public String getDate() { return date; 
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
