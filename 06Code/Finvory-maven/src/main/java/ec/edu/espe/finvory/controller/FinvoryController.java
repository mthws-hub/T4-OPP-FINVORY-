package ec.edu.espe.finvory.controller;

import ec.edu.espe.finvory.mongo.DataPersistenceManager;
import ec.edu.espe.finvory.model.*;
import ec.edu.espe.finvory.view.FrmCustomers;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Arelys Otavalo, the POOwer Rangers of Programming
 */
public class FinvoryController {

    public FinvoryData data;
    public SystemUsers users;
    public DataPersistenceManager dataBase;
    public String currentCompanyUsername;
    public String userType = "";
    private static final BigDecimal DEFAULT_TAX_RATE = new BigDecimal("0.12");
    public UserController userController;
    public ProductController productController;
    public InventoryController inventoryController;
    public ObsoleteController obsoleteController;
    public PriceAndTaxController pricesController;
    public ExportController exportController;
    public SaleAndInvoiceController saleController;
    public CustomerController customerController;
    public SupplierController supplierController;
    public ReportController reportcontroller;

    public FinvoryController(DataPersistenceManager dataBase) {
        this.dataBase = dataBase;
        this.users = dataBase.loadUsers();
        this.userController = new UserController(this);
        this.productController = new ProductController(this);
        this.inventoryController = new InventoryController(this);
        this.obsoleteController = new ObsoleteController(this);
        this.pricesController = new PriceAndTaxController(this);
        this.exportController = new ExportController(this);
        this.saleController = new SaleAndInvoiceController(this);
        this.customerController = new CustomerController(this);
        this.supplierController = new SupplierController(this);
        this.reportcontroller = new ReportController(this);
    }

    public FinvoryData getData() {
        return data;
    }

    public void setData(FinvoryData data) {
        this.data = data;
    }

    public SystemUsers getUsers() {
        return users;
    }

    public DataPersistenceManager getDataBase() {
        return dataBase;
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

    public String getCurrentCompanyUsername() {
        return currentCompanyUsername;
    }

    public void setCurrentCompanyUsername(String username) {
        this.currentCompanyUsername = username;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void saveData() {
        if (currentCompanyUsername != null && data != null && data.getCompanyInfo() != null) {
            dataBase.saveCompanyData(data, currentCompanyUsername);
            dataBase.saveUsers(users);
            syncUsersToCloud();
        }
    }

    public void syncUsersToCloud() {

    }

    public String caesarCipher(String text, int shift) {
        StringBuilder result = new StringBuilder();
        for (char character : text.toCharArray()) {
            result.append((char) (character + shift));
        }
        return result.toString();
    }

    public int getObsoleteStock(String productId) {
        return obsoleteController.getObsoleteStock(productId);
    }

    public void openCustomerManagement() {
        new FrmCustomers(this.customerController, this).setVisible(true);
    }
}
