package ec.edu.espe.finvory.controller;

import ec.edu.espe.finvory.view.FrmProducts;
import ec.edu.espe.finvory.view.FrmPrices;
import ec.edu.espe.finvory.view.FrmCustomers;
import ec.edu.espe.finvory.model.*;
import ec.edu.espe.finvory.mongo.MongoDBConnection;
import ec.edu.espe.finvory.mongo.MongoDataExporter;
import ec.edu.espe.finvory.view.FrmInventories;
import ec.edu.espe.finvory.view.FrmMainMenu;
import ec.edu.espe.finvory.view.FrmMainMenuPersonalAccount;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import javax.swing.JOptionPane;
import org.bson.Document;
import com.mongodb.client.model.Filters;

/**
 *
 * @author Joseph Medina, The POOwer Rangers of Programming
 */
public class FinvoryController {

    private FinvoryData data;
    private SystemUsers users;
    private Database dataBase;
    private String currentCompanyUsername;
    private String userType = "";

    private static final BigDecimal DEFAULT_TAX_RATE = new BigDecimal("0.12");

    public FinvoryController(Database dataBase) {
        this.dataBase = dataBase;
        this.users = dataBase.loadUsers();
    }

    public FinvoryData getData() {
        return data;
    }

    public List<Product> getProducts() {
        return data.getProducts();
    }

    public List<Inventory> getInventories() {
        return data.getInventories();
    }

    public List<Customer> getCustomers() {
        return data.getCustomers();
    }

    public List<Supplier> getSuppliers() {
        return data.getSuppliers();
    }

    public void saveData() {
        if (currentCompanyUsername != null && data != null && data.getCompanyInfo() != null) {
            dataBase.saveCompanyData(data, currentCompanyUsername);
            try {
                MongoDataExporter.exportCompanyData(currentCompanyUsername, data, data.getCompanyInfo());
            } catch (Exception e) {
            }
        }
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

    public Product findProductPublic(String id) {
        return findProduct(id);
    }

    public Product findProductByBarcodePublic(String barcode) {
        return findProductByBarcode(barcode);
    }

    public Customer findCustomerPublic(String format) {
        return findCustomer(format);
    }

    public boolean handleDeleteProduct(String productId) {
        Product product = findProduct(productId);
        if (product == null) {
            return false;
        }

        for (InvoiceSim invoiceSim : data.getInvoices()) {
            for (InvoiceLineSim line : invoiceSim.getLines()) {
                if (line.getProductId().equals(product.getId())) {
                    return false;
                }
            }
        }
        data.removeProduct(product);

        for (Inventory inventory : data.getInventories()) {
            inventory.removeStock(product.getId(), inventory.getStock(product.getId()));
        }
        data.getObsoleteInventory().removeStock(product.getId(), data.getObsoleteInventory().getStock(product.getId()));
        saveData();
        return true;
    }

    public boolean handleLoginGUI(String username, String password) {
        for (CompanyAccount company : users.getCompanyAccounts()) {
            if (company.getUsername().equals(username) && company.checkPassword(password)) {
                this.currentCompanyUsername = username;
                this.userType = "COMPANY";

                FinvoryData loadedData = dataBase.loadCompanyData(username);

                if (loadedData != null) {
                    this.data = loadedData;
                } else {
                    this.data = new FinvoryData();
                }

                this.data.setCompanyInfo(company);
                return true;
            }
        }

        for (PersonalAccount personal : users.getPersonalAccounts()) {
            if (personal.getUsername().equals(username) && personal.checkPassword(password)) {
                this.currentCompanyUsername = null;
                this.data = null;
                this.userType = "PERSONAL";
                return true;
            }
        }
        return false;
    }

    public void startMainMenuPublic() {
        if ("COMPANY".equals(this.userType)) {
            FrmMainMenu menu = new FrmMainMenu(this);
            menu.setVisible(true);
        } else if ("PERSONAL".equals(this.userType)) {
            FrmMainMenuPersonalAccount menu = new FrmMainMenuPersonalAccount(this);
            menu.setVisible(true);
        }
    }

    public boolean registerCompanyGUI(HashMap<String, String> data, Address address) {
        String username = data.get("username");
        if (findCompanyByUsername(username) != null || findPersonalByUsername(username) != null) {
            return false;
        }

        CompanyAccount newCompany = new CompanyAccount(data.get("companyName"), address, data.get("ruc"), data.get("phone"), data.get("email"), username, data.get("password"));
        users.getCompanyAccounts().add(newCompany);
        dataBase.saveUsers(users);

        FinvoryData initialData = new FinvoryData();
        initialData.setCompanyInfo(newCompany);
        dataBase.saveCompanyData(initialData, username);
        syncUsersToCloud();
        return true;
    }

    public boolean registerPersonalGUI(HashMap<String, String> data) {
        String username = data.get("username");
        if (findCompanyByUsername(username) != null || findPersonalByUsername(username) != null) {
            return false;
        }

        PersonalAccount newPersonal = new PersonalAccount(data.get("fullName"), username, data.get("password"));
        users.getPersonalAccounts().add(newPersonal);
        dataBase.saveUsers(users);
        syncUsersToCloud();
        return true;
    }

    private void syncUsersToCloud() {
        try {
            String uri = System.getenv("MONGODB_URI");
            if (uri != null && !uri.isEmpty()) {
                MongoDBConnection connection = new MongoDBConnection();
                MongoDataExporter.exportUsers(this.users, connection.getDatabaseInstance());
                connection.close();
            }
        } catch (Exception e) {
        }
    }

    public ProductDisplayData getProductDisplayData() {
        return new ProductDisplayData(
                new ArrayList<>(data.getProducts()),
                new ArrayList<>(data.getInventories()),
                data.getObsoleteInventory(),
                data.getProfitPercentage() != null ? data.getProfitPercentage().floatValue() : 0.0f,
                data.getDiscountStandard() != null ? data.getDiscountStandard().floatValue() : 0.0f,
                data.getDiscountPremium() != null ? data.getDiscountPremium().floatValue() : 0.0f,
                data.getDiscountVip() != null ? data.getDiscountVip().floatValue() : 0.0f
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

        public ProductDisplayData(List<Product> products, List<Inventory> inventories, InventoryOfObsolete inventoryObsolete, float profitPercetage, float discountStandard, float discountPremium, float discountVip) {
            this.products = products;
            this.inventories = inventories;
            this.obsoleteInventory = inventoryObsolete;
            this.profitPercentage = profitPercetage;
            this.discountStandard = discountStandard;
            this.discountPremium = discountPremium;
            this.discountVip = discountVip;
        }
    }

    public Customer handleCreateCustomer(HashMap<String, String> customerData) {
        String id = customerData.get("id");
        if (findCustomer(id) != null) {
            return null;
        }
        Customer newCustomer = new Customer(customerData.get("name"), id, customerData.get("phone"), customerData.get("email"), customerData.get("clientType"));
        this.data.addCustomer(newCustomer);
        saveData();
        return newCustomer;
    }

    public boolean handleEditCustomerGUI(String customerId, HashMap<String, String> updates) {
        Customer customer = findCustomer(customerId);
        if (customer == null) {
            return false;
        }
        if (!updates.get("name").isEmpty()) {
            customer.setName(updates.get("name"));
        }
        if (!updates.get("phone").isEmpty()) {
            customer.setPhone(updates.get("phone"));
        }
        if (!updates.get("email").isEmpty()) {
            customer.setEmail(updates.get("email"));
        }
        customer.setClientType(updates.get("clientType"));
        saveData();
        return true;
    }

    public boolean handleDeleteCustomerGUI(String customerId) {
        Customer customer = findCustomer(customerId);
        if (customer == null) {
            return false;
        }
        for (InvoiceSim invoiceSim : data.getInvoices()) {
            if (invoiceSim.getCustomer().getIdentification().equals(customer.getIdentification())) {
                return false;
            }
        }
        data.removeCustomer(customer);

        saveData();
        return true;
    }

    public boolean createSupplierGUI(String id1, String id2, String name, String phone, String email, String description) {
        if (findSupplier(id1) != null) {
            return false;
        }
        Supplier newSupplier = new Supplier(name, id1, phone, email, description);
        newSupplier.setId2(id2);
        data.addSupplier(newSupplier);

        saveData();
        return true;
    }

    public boolean deleteSupplierGUI(String id1) {
        Supplier supplier = findSupplier(id1);
        if (supplier == null) {
            return false;
        }
        for (Product product : data.getProducts()) {
            if (product.getSupplierId() != null && product.getSupplierId().equals(supplier.getId1())) {
                return false;
            }
        }
        data.removeSupplier(supplier);
        saveData();
        return true;
    }

    public boolean handleCreateInventory(String name, Address address) {
        for (Inventory inventory : data.getInventories()) {
            if (inventory.getName().equalsIgnoreCase(name)) {
                return false;
            }
        }
        Inventory newInv = new Inventory(name, address);
        data.addInventory(newInv);

        saveData();
        return true;
    }

    public boolean handleSetTaxRate(float newRateFloat) {
        try {
            BigDecimal newRate = new BigDecimal(Float.toString(newRateFloat)).setScale(4, RoundingMode.HALF_UP);
            data.setTaxRate(newRate);
            saveData();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean handleCreateProduct(HashMap<String, String> productData, Supplier supplier, Inventory targetInventory) {
        String id = productData.get("id");
        if (findProduct(id) != null) {
            return false;
        }

        try {
            BigDecimal costPrice = new BigDecimal(productData.get("costPrice")).setScale(2, RoundingMode.HALF_UP);
            int stock = Integer.parseInt(productData.get("stock"));
            Product newProduct = new Product(id, productData.get("name"), productData.get("description"), productData.get("barcode"), costPrice, supplier.getId1());
            data.addProduct(newProduct);
            targetInventory.setStock(id, stock);
            saveData();
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void handleSetPriceAlgorithm() {
        FrmPrices frmPrices = new FrmPrices(this);
        frmPrices.setVisible(true);
    }

    public boolean handleAddCustomer(String name, String id, String phone, String email, String type) {
        if (findCustomer(id) != null) {
            return false;
        }

        Customer newCustomer = new Customer(name, id, phone, email, type);

        List<Customer> currentList = data.getCustomers();

        try {
            currentList.add(newCustomer);
        } catch (UnsupportedOperationException e) {

            List<Customer> mutableList = new ArrayList<>(currentList);
            mutableList.add(newCustomer);

            try {
                currentList.clear();
                currentList.addAll(mutableList);
            } catch (Exception ex) {
                System.err.println("Error crítico: La lista en FinvoryData es inmutable y no tiene setter.");
                return false;
            }
        }

        saveData();
        return true;
    }

    public boolean handleUpdateCustomerGUI(String originalId, String name, String phone, String email, String type) {
        Customer customer = findCustomer(originalId);
        if (customer == null) {
            return false;
        }
        try {
            customer.setName(name);
            customer.setPhone(phone);
            customer.setEmail(email);
            customer.setClientType(type);
            saveData();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Customer findCustomer(String id) {
        for (Customer customer : data.getCustomers()) {
            if (customer.getIdentification().equals(id)) {
                return customer;
            }
        }
        return null;
    }

    public boolean handleNewSale(Customer customer, HashMap<Product, HashMap<Inventory, Integer>> cart, String payment) {
        if (cart.isEmpty() || customer == null) {
            return false;
        }

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
        String invoiceId = "F-" + (data.getInvoices().size() + 1);
        InvoiceSim invoice = new InvoiceSim(invoiceId, LocalDate.now(), LocalDate.now(), customer, lines, taxRate, discountRate);
        if (!payment.equals("CHEQUE POSTFECHADO")) {
            invoice.complete();
        }
        data.addInvoice(invoice);

        saveData();
        return true;
    }

    public float[] getDashboardData() {
        float totalGrossDay = data.getTotalGrossDay() != null ? data.getTotalGrossDay().floatValue() : 0.0f;
        float totalGrossProfile = data.getTotalGrossProfile() != null ? data.getTotalGrossProfile().floatValue() : 0.0f;
        return new float[]{totalGrossDay, totalGrossProfile};
    }

    public HashMap<String, Integer> getSalesOrDemandReport() {
        HashMap<String, Integer> map = new HashMap<>();
        for (InvoiceSim i : data.getInvoices()) {
            if ("COMPLETED".equals(i.getStatus())) {
                for (InvoiceLineSim line : i.getLines()) {
                    map.put(line.getProductName(), map.getOrDefault(line.getProductName(), 0) + line.getQuantity());
                }
            }
        }
        return map;
    }

    public HashMap<String, Float> getCustomerPurchaseReport() {
        HashMap<String, Float> map = new HashMap<>();
        for (InvoiceSim invoiceSim : data.getInvoices()) {
            if ("COMPLETED".equals(invoiceSim.getStatus())) {
                map.put(invoiceSim.getCustomer().getName(), map.getOrDefault(invoiceSim.getCustomer().getName(), 0f) + invoiceSim.getTotal().floatValue());
            }
        }
        return map;
    }

    public HashMap<String, Integer> getSupplierDemandReport() {
        HashMap<String, Integer> map = new HashMap<>();
        for (InvoiceSim invoiceSim : data.getInvoices()) {
            if ("COMPLETED".equals(invoiceSim.getStatus())) {
                for (InvoiceLineSim line : invoiceSim.getLines()) {
                    Product product = findProduct(line.getProductId());
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

    public Product findProduct(String id) {
        if (id == null) {
            return null;
        }
        for (Product product : data.getProducts()) {
            if (id.equals(product.getId())) {
                return product;
            }
        }
        return null;
    }

    public Product findProductByBarcode(String barcode) {
        if (barcode == null) {
            return null;
        }
        for (Product product : data.getProducts()) {
            if (barcode.equals(product.getBarcode())) {
                return product;
            }
        }
        return null;
    }

    public Supplier findSupplier(String id) {
        if (id == null) {
            return null;
        }
        for (Supplier supplier : data.getSuppliers()) {
            if (id.equals(supplier.getId1())) {
                return supplier;
            }
        }
        return null;
    }

    private CompanyAccount findCompanyByUsername(String u) {
        if (u == null) {
            return null;
        }
        for (CompanyAccount companyAccount : users.getCompanyAccounts()) {
            if (u.equals(companyAccount.getUsername())) {
                return companyAccount;
            }
        }
        return null;
    }

    private PersonalAccount findPersonalByUsername(String u) {
        if (u == null) {
            return null;
        }
        for (PersonalAccount personalAccount : users.getPersonalAccounts()) {
            if (u.equals(personalAccount.getUsername())) {
                return personalAccount;
            }
        }
        return null;
    }

    public Inventory findInventoryByName(String name) {
        if (data == null || data.getInventories() == null) {
            return null;
        }
        for (Inventory inventory : data.getInventories()) {
            if (inventory.getName().equalsIgnoreCase(name)) {
                return inventory;
            }
        }
        return null;
    }

    public ArrayList<Inventory> findInventoriesByPartialName(String query) {
        ArrayList<Inventory> matches = new ArrayList<>();
        String q = query.trim().toLowerCase();
        for (Inventory inv : data.getInventories()) {
            if (inv.getName() != null && inv.getName().toLowerCase().contains(q)) {
                matches.add(inv);
            }
        }
        return matches;
    }

    public Supplier findSupplierByName(String name) {
        if (data == null || data.getSuppliers() == null || name == null) {
            return null;
        }
        String q = name.trim();
        for (Supplier supplier : data.getSuppliers()) {
            if (supplier.getFullName() != null && supplier.getFullName().equalsIgnoreCase(q)) {
                return supplier;
            }
        }
        return null;
    }

    public List<Object[]> getInventoryTableData(Inventory currentInventory) {
        List<Object[]> rows = new ArrayList<>();
        List<Product> globalProducts = getProducts();
        if (globalProducts == null || globalProducts.isEmpty()) {
            return rows;
        }

        float profit = data.getProfitPercentage() != null ? data.getProfitPercentage().floatValue() : 0.0f;
        float discountStandard = data.getDiscountStandard() != null ? data.getDiscountStandard().floatValue() : 0.0f;
        float discountPremium = data.getDiscountPremium() != null ? data.getDiscountPremium().floatValue() : 0.0f;
        float discountVip = data.getDiscountVip() != null ? data.getDiscountVip().floatValue() : 0.0f;

        for (Product product : globalProducts) {
            String productId = product.getId();
            int stock = currentInventory.getStock(productId);
            if (stock == 0 && currentInventory.getStock(productId.trim()) > 0) {
                stock = currentInventory.getStock(productId.trim());
            }
            int obsoleteStock = data.getObsoleteInventory() != null ? data.getObsoleteInventory().getStock(productId) : 0;

            rows.add(new Object[]{
                product.getId(), product.getName(), product.getBarcode(),
                String.format("$%.2f", product.getBaseCostPrice()),
                String.format("$%.2f", product.getPrice("STANDARD", new BigDecimal(profit), new BigDecimal(discountStandard), new BigDecimal(discountPremium), new BigDecimal(discountVip))),
                String.format("$%.2f", product.getPrice("PREMIUM", new BigDecimal(profit), new BigDecimal(discountStandard), new BigDecimal(discountPremium), new BigDecimal(discountVip))),
                String.format("$%.2f", product.getPrice("VIP", new BigDecimal(profit), new BigDecimal(discountStandard), new BigDecimal(discountPremium), new BigDecimal(discountVip))),
                stock, (double) obsoleteStock
            });
        }
        return rows;
    }

    public List<Object[]> getProductTableData(Inventory specificInventory) {
        List<Object[]> rows = new ArrayList<>();
        List<Product> products = getProducts();
        if (products == null || products.isEmpty()) {
            return rows;
        }

        float profit = data.getProfitPercentage() != null ? data.getProfitPercentage().floatValue() : 0.0f;
        float dStd = data.getDiscountStandard() != null ? data.getDiscountStandard().floatValue() : 0.0f;
        float dPrm = data.getDiscountPremium() != null ? data.getDiscountPremium().floatValue() : 0.0f;
        float dVip = data.getDiscountVip() != null ? data.getDiscountVip().floatValue() : 0.0f;

        for (Product product : products) {
            int stockToShow = 0;
            boolean shouldAdd = false;
            if (specificInventory != null) {
                int stock = specificInventory.getStock(product.getId());
                if (stock >= 0) {
                    stockToShow = stock;
                    shouldAdd = true;
                }
            } else {
                shouldAdd = true;
            }

            if (shouldAdd) {
                rows.add(new Object[]{
                    product.getId(), product.getName(), product.getBarcode(),
                    String.format("$%.2f", product.getBaseCostPrice()),
                    String.format("$%.2f", product.getPrice("STANDARD", new BigDecimal(profit), new BigDecimal(dStd), new BigDecimal(dPrm), new BigDecimal(dVip))),
                    String.format("$%.2f", product.getPrice("PREMIUM", new BigDecimal(profit), new BigDecimal(dStd), new BigDecimal(dPrm), new BigDecimal(dVip))),
                    String.format("$%.2f", product.getPrice("VIP", new BigDecimal(profit), new BigDecimal(dStd), new BigDecimal(dPrm), new BigDecimal(dVip))),
                    stockToShow, 0
                });
            }
        }
        return rows;
    }

    public boolean handleUpdateProductGUI(String originalId, HashMap<String, String> data, Inventory targetInventory) {
        Product product = findProduct(originalId);
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
            saveData();
            return true;
        } catch (Exception e) {
            System.err.println("Error actualizando producto: " + e.getMessage());
            return false;
        }
    }

    public boolean handleZeroStock(Inventory inventory, String productId) {
        if (inventory == null || productId == null) {
            return false;
        }
        inventory.setStock(productId, 0);
        saveData();
        return true;
    }

    public boolean handleUpdatePricesGUI(String profit, String std, String prm, String vip, String tax) {
        try {
            BigDecimal profitVal = new BigDecimal(profit);
            BigDecimal standardValue = new BigDecimal(std);
            BigDecimal premiumValue = new BigDecimal(prm);
            BigDecimal vipValue = new BigDecimal(vip);
            BigDecimal taxValue = new BigDecimal(tax);
            data.setProfitPercentage(profitVal);
            data.setDiscountStandard(standardValue);
            data.setDiscountPremium(premiumValue);
            data.setDiscountVip(vipValue);
            data.setTaxRate(taxValue);
            saveData();
            return true;
        } catch (Exception e) {
            System.err.println("Error actualizando precios: " + e.getMessage());
            return false;
        }
    }

    public List<Object[]> searchProductsByCompany(String companyNameQuery) {
        List<Object[]> rows = new ArrayList<>();
        String query = companyNameQuery.toLowerCase().trim();
        String targetUsername = null;

        for (CompanyAccount companyAccount : users.getCompanyAccounts()) {
            if (companyAccount.getName() != null && companyAccount.getName().toLowerCase().contains(query)) {
                targetUsername = companyAccount.getUsername();
                break;
            }
        }

        if (targetUsername == null) {
            return rows;
        }

        FinvoryData targetData = dataBase.loadCompanyData(targetUsername);

        if (targetData == null || targetData.getProducts().isEmpty()) {
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

    public boolean isUsernameTaken(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        String user = username.trim();

        for (CompanyAccount companyAccount : users.getCompanyAccounts()) {
            if (companyAccount.getUsername().equalsIgnoreCase(user)) {
                return true;
            }
        }
        for (PersonalAccount personalAccount : users.getPersonalAccounts()) {
            if (personalAccount.getUsername().equalsIgnoreCase(user)) {
                return true;
            }
        }
        return false;
    }

    public boolean handleMoveProductStock(Inventory sourceInventory, Inventory targetInventory, String productId) {
        if (sourceInventory == null || targetInventory == null || productId == null) {
            return false;
        }
        Product product = findProduct(productId);
        if (product == null) {
            return false;
        }
        int sourceStock = sourceInventory.getStock(productId);
        if (sourceStock <= 0) {
            return false;
        }
        int currentTargetStock = targetInventory.getStock(productId);
        int newTargetStock = currentTargetStock + sourceStock;
        targetInventory.setStock(productId, newTargetStock);
        sourceInventory.setStock(productId, 0);
        saveData();

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
            saveData();
            return true;
        } catch (Exception e) {
            System.err.println("Error actualizando proveedor: " + e.getMessage());
            return false;
        }
    }

    public String generateNextInvoiceId() {
        if (data == null || data.getCompanyInfo() == null) {
            return "FUNK-001";
        }

        String companyName = data.getCompanyInfo().getName().toUpperCase();
        String prefix = companyName.length() >= 3 ? companyName.substring(0, 3) : companyName;
        prefix = prefix.replaceAll("[^A-Z0-9]", "");

        int nextSequence = data.getInvoices().size() + 1;
        return String.format("F%s-%03d", prefix, nextSequence);
    }

    public Customer searchCustomerForInvoice(String query) {
        if (query == null || query.trim().isEmpty()) {
            return null;
        }
        String que = query.trim().toLowerCase();
        for (Customer customer : data.getCustomers()) {
            if (customer.getIdentification().equals(query.trim())) {
                return customer;
            }
            if (customer.getName().toLowerCase().contains(que)) {
                return customer;
            }
        }
        return null;
    }

    public ArrayList<Customer> findCustomersByQuery(String query) {
        ArrayList<Customer> matches = new ArrayList<>();
        if (query == null || data == null) {
            return matches;
        }
        String que = query.trim().toLowerCase();
        for (Customer customer : data.getCustomers()) {
            boolean matchId = customer.getIdentification().startsWith(que);
            boolean matchName = customer.getName().toLowerCase().contains(que);

            if (matchId || matchName) {
                matches.add(customer);
            }
        }
        return matches;
    }

    public List<Object[]> getSalesTableData() {
        List<Object[]> rows = new ArrayList<>();
        if (data != null && data.getInvoices() != null) {
            for (ec.edu.espe.finvory.model.InvoiceSim inv : data.getInvoices()) {
                rows.add(new Object[]{
                    inv.getId(),
                    inv.getDate().toString(),
                    inv.getCustomer().getName(),
                    String.format("%.2f", inv.getSubtotal()),
                    String.format("%.2f", inv.getTotal())
                });
            }
        }
        return rows;
    }

    public void exportSalesToCSV(String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("sep=,");
            writer.println("ID Factura,Fecha,Cliente,Subtotal,Impuesto,Total");

            for (InvoiceSim inv : data.getInvoices()) {
                String subtotal = String.format("%.2f", inv.getSubtotal().doubleValue()).replace(",", ".");
                String tax = String.format("%.2f", inv.getTaxAmount().doubleValue()).replace(",", ".");
                String total = String.format("%.2f", inv.getTotal().doubleValue()).replace(",", ".");

                writer.printf("%s,%s,%s,%s,%s,%s%n",
                        inv.getId(),
                        inv.getDate(),
                        inv.getCustomer().getName(),
                        subtotal,
                        tax,
                        total);
            }
        } catch (IOException e) {
            System.err.println("Error al exportar CSV: " + e.getMessage());
        }
    }

    public boolean registerProductReturn(String productId, int quantity, String reason) {

        Product product = findProductById(productId);

        if (product == null) {
            return false;
        }

        ReturnedProduct returnRecord = new ReturnedProduct(product, quantity, reason);

        data.addReturn(returnRecord);

        data.getObsoleteInventory().addStock(productId, quantity);

        saveData();

        return true;
    }

    public Product findProductById(String id) {
        for (Product product : data.getProducts()) {
            if (product.getId().equalsIgnoreCase(id)) {
                return product;
            }
        }
        return null;
    }

    public boolean reassignObsoleteProduct(String productId, int quantity, String destinationInvName, String reason) {
        if (quantity <= 0) {
            return false;
        }

        InventoryOfObsolete obsolete = data.getObsoleteInventory();
        List<ReturnedProduct> returnsList = data.getReturns();

        Inventory destination = data.getInventories().stream()
                .filter(inv -> inv.getName().equalsIgnoreCase(destinationInvName))
                .findFirst()
                .orElse(null);

        if (destination == null || obsolete.getStock(productId) < quantity) {
            return false;
        }

        obsolete.addStock(productId, -quantity);
        destination.addStock(productId, quantity);

        updateReturnsList(productId, quantity, reason);

        saveData();
        return true;
    }

    public boolean discardObsoleteProduct(String productId, int quantity, String reason) {
        if (quantity <= 0) {
            return false;
        }

        InventoryOfObsolete obsolete = data.getObsoleteInventory();

        if (obsolete.getStock(productId) < quantity) {
            return false;
        }

        obsolete.addStock(productId, -quantity);

        updateReturnsList(productId, quantity, reason);

        saveData();
        return true;
    }

    private void updateReturnsList(String productId, int quantity, String reason) {
        List<ReturnedProduct> returnsList = data.getReturns();
        for (int i = 0; i < returnsList.size(); i++) {
            ReturnedProduct ret = returnsList.get(i);

            if (ret.getProduct().getId().equals(productId) && ret.getReason().equalsIgnoreCase(reason)) {
                int newQuantity = ret.getQuantity() - quantity;
                if (newQuantity <= 0) {
                    returnsList.remove(i);
                } else {
                    ret.setQuantity(newQuantity);
                }
                break;
            }
        }
    }
}
