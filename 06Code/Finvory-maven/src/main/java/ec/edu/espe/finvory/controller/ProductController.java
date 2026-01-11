package ec.edu.espe.finvory.controller;

import ec.edu.espe.finvory.model.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 *
 * @author Arelys Otavalo, the POOwer Rangers of Programming
 */
public class ProductController {

    private final FinvoryController mainController;

    public ProductController(FinvoryController mainController) {
        this.mainController = mainController;
    }

    public Product findById(String id) {
        if (id == null || mainController.data == null) {
            return null;
        }
        for (Product product : mainController.data.getProducts()) {
            if (id.equalsIgnoreCase(product.getId())) {
                return product;
            }
        }
        return null;
    }

    public Product findByBarcode(String barcode) {
        if (barcode == null || mainController.data == null) {
            return null;
        }
        for (Product product : mainController.data.getProducts()) {
            if (barcode.equals(product.getBarcode())) {
                return product;
            }
        }
        return null;
    }

    public Product findProductPublic(String id) {
        return findById(id);
    }

    public Product findProductByBarcodePublic(String barcode) {
        return findByBarcode(barcode);
    }

    public Product findProduct(String id) {
        return findById(id);
    }

    public Product findProductById(String id) {
        return findById(id);
    }

    public boolean handleCreateProduct(HashMap<String, String> productData, Supplier supplier, Inventory targetInventory) {
        String id = productData.get("id");
        if (findById(id) != null) {
            return false;
        }

        try {
            BigDecimal costPrice = new BigDecimal(productData.get("costPrice")).setScale(2, RoundingMode.HALF_UP);
            int stock = Integer.parseInt(productData.get("stock"));

            Product newProduct = new Product(
                    id,
                    productData.get("name"),
                    productData.get("description"),
                    productData.get("barcode"),
                    costPrice,
                    supplier.getId1()
            );

            mainController.data.addProduct(newProduct);
            if (targetInventory != null) {
                targetInventory.setStock(id, stock);
            }

            mainController.saveData();
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean handleUpdateProductGUI(String originalId, HashMap<String, String> data, Inventory targetInventory) {
        Product product = findById(originalId);
        if (product == null) {
            return false;
        }

        try {
            product.setName(data.get("name"));
            product.setBarcode(data.get("barcode"));
            product.setDescription(data.get("description"));
            product.setSupplierId(data.get("supplierId"));

            BigDecimal newCost = new BigDecimal(data.get("costPrice")).setScale(2, RoundingMode.HALF_UP);
            product.setBaseCostPrice(newCost);

            if (targetInventory != null) {
                int newStock = Integer.parseInt(data.get("stock"));
                targetInventory.setStock(originalId, newStock);
            }

            mainController.saveData();
            return true;
        } catch (Exception e) {
            System.err.println("Error actualizando producto: " + e.getMessage());
            return false;
        }
    }

    public boolean handleDeleteProduct(String productId) {
        Product product = findById(productId);
        if (product == null) {
            return false;
        }

        for (InvoiceSim invoice : mainController.data.getInvoices()) {
            for (InvoiceLineSim line : invoice.getLines()) {
                if (line.getProductId().equals(product.getId())) {
                    return false;
                }
            }
        }

        mainController.data.removeProduct(product);

        for (Inventory inventory : mainController.data.getInventories()) {
            inventory.removeStock(product.getId(), inventory.getStock(product.getId()));
        }

        InventoryOfObsolete obs = mainController.data.getObsoleteInventory();
        obs.removeStock(product.getId(), obs.getStock(product.getId()));

        mainController.saveData();
        return true;
    }

    public boolean handleMoveProductStock(Inventory source, Inventory target, String productId) {
        if (source == null || target == null || productId == null) {
            return false;
        }

        int sourceStock = source.getStock(productId);
        if (sourceStock <= 0) {
            return false;
        }

        target.setStock(productId, target.getStock(productId) + sourceStock);
        source.setStock(productId, 0);

        mainController.saveData();
        return true;
    }

    public boolean registerProductReturn(String productId, int quantity, String reason) {
        Product product = findById(productId);
        if (product == null) {
            return false;
        }

        ReturnedProduct returnRecord = new ReturnedProduct(product, quantity, reason);
        mainController.data.addReturn(returnRecord);
        mainController.data.getObsoleteInventory().addStock(productId, quantity);

        mainController.saveData();
        return true;
    }

    public List<Object[]> searchProductsByCompany(String companyNameQuery) {
        List<Object[]> rows = new ArrayList<>();
        String query = companyNameQuery.toLowerCase().trim();
        String targetUsername = null;

        for (CompanyAccount company : mainController.users.getCompanyAccounts()) {
            if (company.getName() != null && company.getName().toLowerCase().contains(query)) {
                targetUsername = company.getUsername();
                break;
            }
        }

        if (targetUsername == null) {
            return rows;
        }

        FinvoryData targetData = mainController.dataBase.loadCompanyData(targetUsername);
        if (targetData == null) {
            return rows;
        }

        for (Product product : targetData.getProducts()) {
            int totalStock = 0;
            for (Inventory inv : targetData.getInventories()) {
                totalStock += inv.getStock(product.getId());
            }

            rows.add(new Object[]{
                product.getId(),
                product.getName(),
                product.getBarcode(),
                totalStock
            });
        }
        return rows;
    }

    public List<Object[]> getPopularProductsReportData() {
        List<Object[]> rows = new ArrayList<>();
        HashMap<String, Integer> popularityMap = mainController.saleController.getSalesOrDemandReport();

        popularityMap.forEach((name, qty) -> {
            rows.add(new Object[]{name, qty});
        });

        rows.sort((rowA, rowB) -> ((Integer) rowB[1]).compareTo((Integer) rowA[1]));
        return rows;
    }

    public List<Object[]> getProductTableData(Inventory specificInventory) {
        List<Object[]> rows = new ArrayList<>();
        FinvoryData data = mainController.data;
        if (data == null) {
            return rows;
        }

        BigDecimal profit = data.getProfitPercentage() != null ? data.getProfitPercentage() : BigDecimal.ZERO;
        BigDecimal dStd = data.getDiscountStandard() != null ? data.getDiscountStandard() : BigDecimal.ZERO;
        BigDecimal dPrm = data.getDiscountPremium() != null ? data.getDiscountPremium() : BigDecimal.ZERO;
        BigDecimal dVip = data.getDiscountVip() != null ? data.getDiscountVip() : BigDecimal.ZERO;

        for (Product product : data.getProducts()) {
            int stockToShow = (specificInventory != null) ? specificInventory.getStock(product.getId()) : 0;
            int obsoleteStock = mainController.obsoleteController.getObsoleteStock(product.getId());

            rows.add(new Object[]{
                product.getId(),
                product.getName(),
                product.getBarcode(),
                String.format("$%.2f", product.getBaseCostPrice()),
                String.format("$%.2f", product.getPrice("STANDARD", profit, dStd, dPrm, dVip)),
                String.format("$%.2f", product.getPrice("PREMIUM", profit, dStd, dPrm, dVip)),
                String.format("$%.2f", product.getPrice("VIP", profit, dStd, dPrm, dVip)),
                stockToShow,
                obsoleteStock
            });
        }
        return rows;
    }

    public ProductDisplayData getProductDisplayData() {
        FinvoryData data = mainController.data;
        return new ProductDisplayData(
                new ArrayList<>(data.getProducts()),
                new ArrayList<>(data.getInventories()),
                data.getObsoleteInventory(),
                data.getProfitPercentage().floatValue(),
                data.getDiscountStandard().floatValue(),
                data.getDiscountPremium().floatValue(),
                data.getDiscountVip().floatValue()
        );
    }

    public static class ProductDisplayData {

        public final List<Product> products;
        public final List<Inventory> inventories;
        public final InventoryOfObsolete obsoleteInventory;
        public final float profitPercentage;
        public final float discountStandard;
        public final float discountPremium;
        public final float discountVip;

        public ProductDisplayData(List<Product> products, List<Inventory> inventory, InventoryOfObsolete obsoleteInventory,
                                  float profitPercentage, float discountStandard, float discountPremium, float discountVip) {
            this.products = products;
            this.inventories = inventory;
            this.obsoleteInventory = obsoleteInventory;
            this.profitPercentage = profitPercentage;
            this.discountStandard = discountStandard;
            this.discountPremium = discountPremium;
            this.discountVip = discountVip;
        }
    }

}
