package ec.edu.espe.finvory.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Joseph Medina, The POOwer Rangers of Programming
 */
public class FinvoryData {

    private CompanyAccount companyInfo;

    private final ArrayList<Customer> customers;
    private final ArrayList<Product> products;
    private final ArrayList<Supplier> suppliers;
    private final ArrayList<Inventory> inventories;
    private InventoryOfObsolete obsoleteInventory;
    private final ArrayList<InvoiceSim> invoices;
    private final ArrayList<ReturnedProduct> returns;
    private BigDecimal taxRate;
    private BigDecimal profitPercentage;
    private BigDecimal discountStandard;
    private BigDecimal discountPremium;
    private BigDecimal discountVip;

    public FinvoryData() {
        customers = new ArrayList<>();
        suppliers = new ArrayList<>();
        products = new ArrayList<>();
        inventories = new ArrayList<>();
        invoices = new ArrayList<>();
        returns = new ArrayList<>();

        Address obsAddr = new Address("Ecuador", "Quito", "Bodega Obsoletos");
        obsoleteInventory = new InventoryOfObsolete(obsAddr);
        taxRate = new BigDecimal("0.15");
        profitPercentage = BigDecimal.ZERO;
        discountStandard = BigDecimal.ZERO;
        discountPremium = BigDecimal.ZERO;
        discountVip = BigDecimal.ZERO;
    }

    public CompanyAccount getCompanyInfo() {
        return companyInfo;
    }

    public void setCompanyInfo(CompanyAccount companyInfo) {
        this.companyInfo = companyInfo;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public BigDecimal getProfitPercentage() {
        return profitPercentage;
    }

    public void setProfitPercentage(BigDecimal p) {
        this.profitPercentage = p;
    }

    public BigDecimal getDiscountStandard() {
        return discountStandard;
    }

    public void setDiscountStandard(BigDecimal d) {
        this.discountStandard = d;
    }

    public BigDecimal getDiscountPremium() {
        return discountPremium;
    }

    public void setDiscountPremium(BigDecimal d) {
        this.discountPremium = d;
    }

    public BigDecimal getDiscountVip() {
        return discountVip;
    }

    public void setDiscountVip(BigDecimal d) {
        this.discountVip = d;
    }

    public InventoryOfObsolete getObsoleteInventory() {
        return obsoleteInventory;
    }

    public List<Customer> getCustomers() {
        return Collections.unmodifiableList(customers);
    }

    public List<Product> getProducts() {
        return Collections.unmodifiableList(products);
    }

    public List<Supplier> getSuppliers() {
        return Collections.unmodifiableList(suppliers);
    }

    public List<Inventory> getInventories() {
        return Collections.unmodifiableList(inventories);
    }

    public List<InvoiceSim> getInvoices() {
        return Collections.unmodifiableList(invoices);
    }

    public List<ReturnedProduct> getReturns() {
        return Collections.unmodifiableList(returns);
    }

    public void addCustomer(Customer customer) {
        if (customer != null && !this.customers.contains(customer)) {
            this.customers.add(customer);
        }
    }

    public void addProduct(Product product) {
        if (product != null && !this.products.contains(product)) {
            this.products.add(product);
        }
    }

    public void addSupplier(Supplier supplier) {
        if (supplier != null && !this.suppliers.contains(supplier)) {
            this.suppliers.add(supplier);
        }
    }

    public void addInventory(Inventory inventory) {
        if (inventory != null && !this.inventories.contains(inventory)) {
            this.inventories.add(inventory);
        }
    }

    public void addInvoice(InvoiceSim invoice) {
        if (invoice != null) {
            this.invoices.add(invoice);
        }
    }

    public void addReturn(ReturnedProduct returnedProduct) {
        if (returnedProduct != null) {
            this.returns.add(returnedProduct);
        }
    }

    public void removeProduct(Product p) {
        this.products.remove(p);
    }

    public void removeCustomer(Customer c) {
        this.customers.remove(c);
    }

    public void removeSupplier(Supplier s) {
        this.suppliers.remove(s);
    }
    
    public BigDecimal getTodaySales() {
        BigDecimal total = BigDecimal.ZERO;
        LocalDate today = LocalDate.now();
        for (InvoiceSim invoice : invoices) {
            if (invoice != null && invoice.getDate().equals(today)) {
                total = total.add(invoice.getTotal());
            }
        }
        return total;
    }

    public BigDecimal getTotalGrossDay() {
        return getTodaySales();
    }

    public BigDecimal getTotalGrossProfile() {
        return getTodaySales();
    }
}
