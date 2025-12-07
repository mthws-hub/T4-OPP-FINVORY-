package ec.edu.espe.finvory.model;

import java.time.LocalDate;
import java.util.ArrayList;
/**
 *
 * @author Joseph Medina, The POOwer Rangers of Programming
 */

public class FinvoryData {
    
    private CompanyAccount companyInfo; 
    
    private ArrayList<Customer> customers;
    private ArrayList<Product> products;
    private ArrayList<Supplier> suppliers; 
    private ArrayList<Inventory> inventories; 
    private InventoryOfObsolete obsoleteInventory; 
    
    private ArrayList<InvoiceSim> invoices;
    private ArrayList<ReturnedProduct> returns;
    
    private float taxRate;
    private float profitPercentage;
    private float discountStandard;
    private float discountPremium;
    private float discountVip;

    public FinvoryData() {
        customers = new ArrayList<>();
        suppliers = new ArrayList<>();
        products = new ArrayList<>();
        inventories = new ArrayList<>();
        invoices = new ArrayList<>();
        returns = new ArrayList<>();
        
        Address obsAddr = new Address("Ecuador", "Quito", "Bodega Obsoletos");
        obsoleteInventory = new InventoryOfObsolete(obsAddr);
        
        taxRate = 0.15f;
        profitPercentage = 0.0f;
        discountStandard = 0.0f;
        discountPremium = 0.0f;
        discountVip = 0.0f;
    }

    public float getTotalGrossProfile() {
        float total = 0;
        for (InvoiceSim invoice : invoices) {
            if ("COMPLETED".equals(invoice.getStatus())) {
                total += invoice.getTotal();
            }
        }
        return total;
    }

    public float getTotalGrossDay() {
        float total = 0;
        String today = LocalDate.now().toString(); 
        for (InvoiceSim invoice : invoices) {
            if ("COMPLETED".equals(invoice.getStatus()) && invoice.getDate().equals(today)) {
                total += invoice.getTotal();
            }
        }
        return total;
    }

    public CompanyAccount getCompanyInfo() { return companyInfo; }
    public void setCompanyInfo(CompanyAccount companyInfo) { this.companyInfo = companyInfo; }
    
    public ArrayList<Customer> getCustomers() { return customers; }
    public ArrayList<Product> getProducts() { return products; }
    public ArrayList<Supplier> getSuppliers() { return suppliers; }
    public ArrayList<Inventory> getInventories() { return inventories; }
    public InventoryOfObsolete getObsoleteInventory() { return obsoleteInventory; }
    public ArrayList<InvoiceSim> getInvoices() { return invoices; }
    public ArrayList<ReturnedProduct> getReturns() { return returns; }
    
    public float getTaxRate() { return taxRate; }
    public void setTaxRate(float taxRate) { this.taxRate = taxRate; }
    public float getProfitPercentage() { return profitPercentage; }
    public void setProfitPercentage(float p) { this.profitPercentage = p; }
    public float getDiscountStandard() { return discountStandard; }
    public void setDiscountStandard(float d) { this.discountStandard = d; }
    public float getDiscountPremium() { return discountPremium; }
    public void setDiscountPremium(float d) { this.discountPremium = d; }
    public float getDiscountVip() { return discountVip; }
    public void setDiscountVip(float d) { this.discountVip = d; }
}