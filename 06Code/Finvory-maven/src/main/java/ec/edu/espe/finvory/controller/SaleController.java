package ec.edu.espe.finvory.controller;

import ec.edu.espe.finvory.model.*;
import org.bson.Document;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import ec.edu.espe.finvory.mongo.MongoDBConnection;

/**
 *
 * @author Arelys Otavalo, the POOwer Rangers of Programming
 */
public class SaleController {

    private final FinvoryController mainController;

    public SaleController(FinvoryController mainController) {
        this.mainController = mainController;
    }

    public Document buildInvoiceDocument(InvoiceSim invoice, String companyUsername) {
        Document document = new Document();
        document.append("companyUsername", companyUsername);
        document.append("invoiceId", invoice.getId());
        document.append("date", invoice.getDate().toString());
        document.append("paymentDueDate", invoice.getPaymentDueDate().toString());
        document.append("customer", invoice.getCustomer().getName());
        document.append("subtotal", invoice.getSubtotal().doubleValue());
        document.append("tax", invoice.getTaxAmount().doubleValue());
        document.append("total", invoice.getTotal().doubleValue());
        document.append("status", invoice.getStatus());
        return document;
    }

    public boolean handleNewSale(String invoiceId, Customer customer, ArrayList<InvoiceLineSim> lines,
            BigDecimal taxRate, BigDecimal discountRate, Inventory targetInventory) {

        for (InvoiceLineSim line : lines) {
            int currentStock = targetInventory.getStock(line.getProductId());
            if (currentStock < line.getQuantity()) {
                return false;
            }
        }

        InvoiceSim invoice = new InvoiceSim(invoiceId, LocalDate.now(), LocalDate.now().plusDays(30),
                customer, lines, taxRate, discountRate);
        invoice.complete();

        for (InvoiceLineSim line : lines) {
            int currentStock = targetInventory.getStock(line.getProductId());
            targetInventory.setStock(line.getProductId(), currentStock - line.getQuantity());
        }

        mainController.getData().addInvoice(invoice);
        mainController.saveData();

        try {
            com.mongodb.client.MongoDatabase database = MongoDBConnection.getDatabaseStatic();
            if (database != null) {
                Document doc = buildInvoiceDocument(invoice, mainController.getCurrentCompanyUsername());
                database.getCollection("invoices").insertOne(doc);
            }
        } catch (Exception e) {
            System.err.println("Error al sincronizar venta con la nube: " + e.getMessage());
        }

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
}
