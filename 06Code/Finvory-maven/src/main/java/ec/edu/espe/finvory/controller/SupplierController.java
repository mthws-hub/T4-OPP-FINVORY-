package ec.edu.espe.finvory.controller;

import ec.edu.espe.finvory.model.*;
import java.time.LocalDate;
import java.util.*;
/**
 *
 * @author Arelys Otavalo, the POOwer Rangers of Programming
 */
public class SupplierController  {
    
    private final FinvoryController mainController;

    public SupplierController(FinvoryController mainController) {
        this.mainController = mainController;
    }

    public Supplier findSupplier(String id) {
        if (id == null || mainController.getData() == null) {
            return null;
        }
        for (Supplier supplier : mainController.getData().getSuppliers()) {
            if (id.equals(supplier.getId1())) {
                return supplier;
            }
        }
        return null;
    }

    public Supplier findSupplierByName(String nameSupplier) {
        if (mainController.getData() == null || nameSupplier == null) {
            return null;
        }
        String q = nameSupplier.trim();
        for (Supplier supplier : mainController.getData().getSuppliers()) {
            if (supplier.getFullName() != null && supplier.getFullName().equalsIgnoreCase(q)) {
                return supplier;
            }
        }
        return null;
    }

    public boolean createSupplierGUI(String id1, String id2, String name, String phone, String email, String description) {
        if (findSupplier(id1) != null) {
            return false;
        }
        Supplier newSupplier = new Supplier(name, id1, phone, email, description);
        newSupplier.setId2(id2);
        mainController.getData().addSupplier(newSupplier);
        mainController.saveData();
        return true;
    }

    public boolean handleUpdateSupplierGUI(String originalId, String name, String phone, String email, String desc, String id2) {
        Supplier supplier = findSupplier(originalId);
        if (supplier == null) {
            return false;
        }
        try {
            supplier.setFullName(name);
            supplier.setPhone(phone);
            supplier.setEmail(email);
            supplier.setDescription(desc);
            supplier.setId2(id2);
            mainController.saveData();
            return true;
        } catch (Exception e) {
            System.err.println("Error actualizando proveedor: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteSupplierGUI(String id1) {
        Supplier supplier = findSupplier(id1);
        if (supplier == null) {
            return false;
        }
        for (Product product : mainController.getData().getProducts()) {
            if (product.getSupplierId() != null && product.getSupplierId().equals(supplier.getId1())) {
                return false;
            }
        }
        mainController.getData().removeSupplier(supplier);
        mainController.saveData();
        return true;
    }


    public HashMap<String, Integer> getSupplierDemandReport() {
        HashMap<String, Integer> map = new HashMap<>();
        if (mainController.getData() == null) return map;

        for (InvoiceSim invoiceSim : mainController.getData().getInvoices()) {
            if ("COMPLETED".equals(invoiceSim.getStatus())) {
                for (InvoiceLineSim line : invoiceSim.getLines()) {
                    Product product = mainController.productController.findById(line.getProductId());
                    if (product != null) {
                        Supplier supplier = findSupplier(product.getSupplierId());
                        if (supplier != null) {
                            map.put(supplier.getFullName(), map.getOrDefault(supplier.getFullName(), 0) + line.getQuantity());
                        }
                    }
                }
            }
        }
        return map;
    }

    public List<Object[]> getSupplierPerformanceFlexibleData(int year, int month) {
        List<Object[]> rows = new ArrayList<>();
        Map<String, Integer> supplyMap = new HashMap<>();
        FinvoryData data = mainController.getData();

        if (data == null) return rows;

        for (InvoiceSim invoice : data.getInvoices()) {
            LocalDate invoiceDate = invoice.getDate();
            boolean yearMatches = (invoiceDate.getYear() == year);
            boolean monthMatches = (month == 0 || invoiceDate.getMonthValue() == month);

            if ("COMPLETED".equals(invoice.getStatus()) && yearMatches && monthMatches) {
                for (InvoiceLineSim line : invoice.getLines()) {
                    Product product = mainController.productController.findById(line.getProductId());
                    if (product != null) {
                        Supplier supplier = findSupplier(product.getSupplierId());
                        if (supplier != null) {
                            String name = supplier.getFullName();
                            supplyMap.put(name, supplyMap.getOrDefault(name, 0) + line.getQuantity());
                        }
                    }
                }
            }
        }

        supplyMap.forEach((name, quantity) -> {
            rows.add(new Object[]{name, quantity});
        });

        rows.sort((rowA, rowB) -> ((Integer) rowB[1]).compareTo((Integer) rowA[1]));
        return rows;
    }
}
