package ec.edu.espe.finvory.controller;

import ec.edu.espe.finvory.model.*;
import org.bson.Document;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 *
 * @author Arelys Otavalo, the POOwer Rangers of Programming
 */
public class SaleAndInvoiceController {
    private final FinvoryController mainController;
    private static final BigDecimal DEFAULT_TAX_RATE = new BigDecimal("0.15");

    public SaleAndInvoiceController(FinvoryController mainController) {
        this.mainController = mainController;
    }

    public boolean handleNewSale(Customer customer, HashMap<Product, HashMap<Inventory, Integer>> cart, String payment) {
        if (cart.isEmpty() || customer == null) {
            return false;
        }

        FinvoryData data = mainController.data;
        BigDecimal profitPercentage = data.getProfitPercentage() != null ? data.getProfitPercentage() : BigDecimal.ZERO;
        BigDecimal discountStandard = data.getDiscountStandard() != null ? data.getDiscountStandard() : BigDecimal.ZERO;
        BigDecimal discountPremium = data.getDiscountPremium() != null ? data.getDiscountPremium() : BigDecimal.ZERO;
        BigDecimal discountVip = data.getDiscountVip() != null ? data.getDiscountVip() : BigDecimal.ZERO;
        BigDecimal taxRate = data.getTaxRate() != null ? data.getTaxRate() : DEFAULT_TAX_RATE;
        BigDecimal discountRate = customer.getDiscountRate(discountStandard, discountPremium, discountVip);

        ArrayList<InvoiceLineSim> lines = new ArrayList<>();

        for (Map.Entry<Product, HashMap<Inventory, Integer>> entry : cart.entrySet()) {
            Product product = entry.getKey();
            for (Map.Entry<Inventory, Integer> stockEntry : entry.getValue().entrySet()) {
                Inventory inventory = stockEntry.getKey();
                int quantity = stockEntry.getValue();
                inventory.removeStock(product.getId(), quantity);
                
                BigDecimal price = product.getPrice(customer.getClientType(), profitPercentage, discountStandard, discountPremium, discountVip);
                lines.add(new InvoiceLineSim(product.getId(), product.getName(), quantity, price));
            }
        }

        String invoiceId = generateNextInvoiceId();
        InvoiceSim invoice = new InvoiceSim(invoiceId, LocalDate.now(), LocalDate.now(), customer, lines, taxRate, discountRate);
        
        if (!payment.equals("CHEQUE POSTFECHADO")) {
            invoice.complete();
        }
        
        data.addInvoice(invoice);
        mainController.saveData();
        return true;
    }

    public String generateNextInvoiceId() {
        if (mainController.data == null || mainController.data.getCompanyInfo() == null) {
            return "FUNK-001";
        }

        String companyName = mainController.data.getCompanyInfo().getName().toUpperCase();
        String prefix = companyName.length() >= 3 ? companyName.substring(0, 3) : companyName;
        prefix = prefix.replaceAll("[^A-Z0-9]", "");

        int nextSequence = mainController.data.getInvoices().size() + 1;
        return String.format("F%s-%03d", prefix, nextSequence);
    }

    public Document buildInvoiceDocument(InvoiceSim invoice, String companyUsername) {
        Document document = new Document();
        document.append("companyUsername", companyUsername);
        document.append("invoiceId", invoice.getId());
        document.append("date", invoice.getDate());
        document.append("paymentDueDate", invoice.getPaymentDueDate());
        document.append("subtotal", invoice.getSubtotal().doubleValue());
        document.append("tax", invoice.getTaxAmount().doubleValue());
        document.append("total", invoice.getTotal().doubleValue());
        return document;
    }

    public float[] getDashboardData() {
        FinvoryData data = mainController.data;
        float totalGrossDay = data.getTotalGrossDay() != null ? data.getTotalGrossDay().floatValue() : 0.0f;
        float totalGrossProfile = data.getTotalGrossProfile() != null ? data.getTotalGrossProfile().floatValue() : 0.0f;
        return new float[]{totalGrossDay, totalGrossProfile};
    }

    public HashMap<String, Integer> getSalesOrDemandReport() {
        HashMap<String, Integer> map = new HashMap<>();
        for (InvoiceSim invoiceSim : mainController.data.getInvoices()) {
            if ("COMPLETED".equals(invoiceSim.getStatus())) {
                for (InvoiceLineSim line : invoiceSim.getLines()) {
                    map.put(line.getProductName(), map.getOrDefault(line.getProductName(), 0) + line.getQuantity());
                }
            }
        }
        return map;
    }

    public List<Object[]> getSalesTableData() {
        List<Object[]> rows = new ArrayList<>();
        if (mainController.data != null && mainController.data.getInvoices() != null) {
            for (InvoiceSim invoice : mainController.data.getInvoices()) {
                rows.add(new Object[]{
                    invoice.getId(),
                    invoice.getDate().toString(),
                    invoice.getCustomer().getName(),
                    String.format("%.2f", invoice.getSubtotal()),
                    String.format("%.2f", invoice.getTotal())
                });
            }
        }
        return rows;
    }

    public List<String> getAvailableInvoiceYears() {
        Set<Integer> uniqueYears = new TreeSet<>(Collections.reverseOrder());
        for (InvoiceSim invoice : mainController.data.getInvoices()) {
            uniqueYears.add(invoice.getDate().getYear());
        }

        if (uniqueYears.isEmpty()) {
            uniqueYears.add(LocalDate.now().getYear());
        }

        List<String> years = new ArrayList<>();
        for (Integer year : uniqueYears) {
            years.add(String.valueOf(year));
        }
        return years;
    }

    public Customer searchCustomerForInvoice(String query) {
        if (query == null || query.trim().isEmpty()) return null;
        
        String text = query.trim().toLowerCase();
        for (Customer customer : mainController.data.getCustomers()) {
            if (customer.getIdentification().equals(query.trim())) return customer;
            if (customer.getName().toLowerCase().contains(text)) return customer;
        }
        return null;
    }
}
