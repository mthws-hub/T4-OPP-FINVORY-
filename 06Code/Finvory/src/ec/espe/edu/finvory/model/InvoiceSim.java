package ec.espe.edu.finvory.model;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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

    public InvoiceSim(String id, Customer customer, LocalDate date, String paymentMethod, List<InvoiceLineSim> lines) {
        this.id = id;
        this.customer = customer;
        this.date = date;
        this.paymentMethod = paymentMethod;
        this.lines = lines;
    }

    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public List<InvoiceLineSim> getLines() {
        return lines;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public double getTotalNet() {
        return totalNet;
    }

    public double getTotalTax() {
        return totalTax;
    }

    public double getTotalGross() {
        return totalGross;
    }

    public void calculateTotals(Map<String, Double> taxes) {
        totalNet = lines.stream().mapToDouble(InvoiceLineSim::getLineTotal).sum();
        totalTax = taxes.values().stream().mapToDouble(rate -> totalNet * rate).sum();
        totalGross = totalNet + totalTax;
    }
}
