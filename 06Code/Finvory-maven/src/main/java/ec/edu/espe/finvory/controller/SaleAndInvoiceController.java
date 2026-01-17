package ec.edu.espe.finvory.controller;

import ec.edu.espe.finvory.model.*;
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

    public boolean handleNewSale(Customer customer, HashMap<Product, HashMap<Inventory, Integer>> cart, String paymentType) {
        if (cart == null || cart.isEmpty() || customer == null) {
            return false;
        }

        FinvoryData data = mainController.getData();

        BigDecimal taxRate = data.getTaxRate() != null ? data.getTaxRate() : DEFAULT_TAX_RATE;
        BigDecimal discountStandard = data.getDiscountStandard() != null ? data.getDiscountStandard() : BigDecimal.ZERO;
        BigDecimal discountPremium = data.getDiscountPremium() != null ? data.getDiscountPremium() : BigDecimal.ZERO;
        BigDecimal discountVip = data.getDiscountVip() != null ? data.getDiscountVip() : BigDecimal.ZERO;

        ArrayList<InvoiceLineSim> lines = new ArrayList<>();

        for (Map.Entry<Product, HashMap<Inventory, Integer>> entry : cart.entrySet()) {
            Product product = entry.getKey();
            HashMap<Inventory, Integer> stockDistribution = entry.getValue();

            int totalQuantityForProduct = 0;

            for (Map.Entry<Inventory, Integer> invEntry : stockDistribution.entrySet()) {
                Inventory inventory = invEntry.getKey();
                int qty = invEntry.getValue();

                if (qty > 0) {
                    inventory.removeStock(product.getId(), qty);
                    totalQuantityForProduct += qty;
                }
            }

            if (totalQuantityForProduct > 0) {
                BigDecimal finalPrice = product.getPrice(
                        customer.getClientType(),
                        data.getProfitPercentage(),
                        discountStandard,
                        discountPremium,
                        discountVip
                );

                lines.add(new InvoiceLineSim(product, totalQuantityForProduct, finalPrice));
            }
        }

        String invoiceId = generateInvoiceId();
        LocalDate now = LocalDate.now();

        BigDecimal discountRate = BigDecimal.ZERO;
        if ("VIP".equalsIgnoreCase(customer.getClientType())) {
            discountRate = discountVip;
        } else if ("PREMIUM".equalsIgnoreCase(customer.getClientType())) {
            discountRate = discountPremium;
        } else {
            discountRate = discountStandard;
        }

        InvoiceSim invoice = new InvoiceSim(invoiceId, now, now, customer, lines, taxRate, discountRate);
        invoice.complete();

        data.addInvoice(invoice);

        mainController.saveData();

        return true;
    }

    public List<Object[]> getSalesTableData() {
        List<Object[]> rows = new ArrayList<>();
        if (mainController.getData() != null && mainController.getData().getInvoices() != null) {
            for (InvoiceSim invoice : mainController.getData().getInvoices()) {
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
        if (mainController.getData() != null && mainController.getData().getInvoices() != null) {
            for (InvoiceSim invoice : mainController.getData().getInvoices()) {
                uniqueYears.add(invoice.getDate().getYear());
            }
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
        if (query == null || query.trim().isEmpty() || mainController.getData() == null) {
            return null;
        }

        String text = query.trim().toLowerCase();
        for (Customer customer : mainController.getData().getCustomers()) {
            if (customer.getIdentification().equals(query.trim())) {
                return customer;
            }
            if (customer.getName().toLowerCase().contains(text)) {
                return customer;
            }
        }
        return null;
    }

    public String generateInvoiceId() {
        return "FAC-" + System.currentTimeMillis();
    }

    public HashMap<String, Integer> getSalesOrDemandReport() {
        HashMap<String, Integer> demandMap = new HashMap<>();
        if (mainController.getData().getInvoices() == null) {
            return demandMap;
        }

        for (InvoiceSim inv : mainController.getData().getInvoices()) {
            if ("COMPLETED".equals(inv.getStatus())) {
                for (InvoiceLineSim line : inv.getLines()) {
                    demandMap.put(line.getProductName(),
                            demandMap.getOrDefault(line.getProductName(), 0) + line.getQuantity());
                }
            }
        }
        return demandMap;
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

    public InvoiceSim calculatePotentialInvoice(Customer customer, HashMap<Product, HashMap<Inventory, Integer>> cart) {
        if (cart == null || cart.isEmpty()) {
            return new InvoiceSim();
        }
        FinvoryData data = mainController.getData();
        BigDecimal taxRate = data.getTaxRate() != null ? data.getTaxRate() : new BigDecimal("0.15");
        BigDecimal discountStandard = data.getDiscountStandard();
        BigDecimal discountPremium = data.getDiscountPremium();
        BigDecimal discountVip = data.getDiscountVip();
        BigDecimal profit = data.getProfitPercentage();

        ArrayList<InvoiceLineSim> lines = new ArrayList<>();

        for (Map.Entry<Product, HashMap<Inventory, Integer>> entry : cart.entrySet()) {
            Product product = entry.getKey();
            int totalQty = entry.getValue().values().stream().mapToInt(Integer::intValue).sum();

            if (totalQty > 0) {
                BigDecimal price = product.getPrice(customer != null ? customer.getClientType() : "Final", profit, discountStandard, discountPremium, discountVip);
                lines.add(new InvoiceLineSim(product, totalQty, price));
            }
        }

        InvoiceSim tempInvoice = new InvoiceSim("TEMP", LocalDate.now(), LocalDate.now(), customer, lines, taxRate, BigDecimal.ZERO);
        return tempInvoice;
    }

    public boolean isSaleValid(Customer customer, Map<Product, HashMap<Inventory, Integer>> cart) {
        return customer != null && cart != null && !cart.isEmpty();
    }

    public boolean confirmSale(Customer customer, HashMap<Product, HashMap<Inventory, Integer>> cart, String paymentMethod) {
        return handleNewSale(customer, cart, paymentMethod);
    }

    public String resolvePaymentMethod(boolean cash, boolean transfer, boolean cheque) {
        if (transfer) {
            return "TRANSFERENCIA";
        }
        if (cheque) {
            return "CHEQUE POSTFECHADO";
        }
        return "EFECTIVO";
    }

    public LocalDate getCurrentInvoiceDate() {
        return LocalDate.now();
    }
}
