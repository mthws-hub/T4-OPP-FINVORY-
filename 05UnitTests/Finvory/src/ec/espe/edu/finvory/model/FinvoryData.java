package ec.espe.edu.finvory.model;
/**
 *
 * @author Mathews Pastor, POOwer Ranger of Programming
 */
import java.time.LocalDate;
import java.util.ArrayList;

public class FinvoryData {
    
    private CompanyAccount companyInfo;
    private AdminDetail adminInfo;
    private ArrayList<CompanyAccount> companyAccounts;
    private ArrayList<Customer> customers;
    private ArrayList<Product> products;
    private ArrayList<Supplier> suppliers; 
    private ArrayList<Inventory> inventories; 
    private ArrayList<PersonalAccount> personalAccounts;
    private InventoryOfObsolete obsoleteInventory; 
    
    private ArrayList<InvoiceSim> invoices;
    private ArrayList<ReturnedProduct> returns;
    
    private float taxRate;
    private float profitPercentage;
    private float discountStandard;
    private float discountPremium;
    private float discountVip;

    public FinvoryData() {
        companyAccounts = new ArrayList<>();
        customers = new ArrayList<>();
        suppliers = new ArrayList<>();
        products = new ArrayList<>();
        inventories = new ArrayList<>();
        personalAccounts = new ArrayList<>();
        invoices = new ArrayList<>();
        returns = new ArrayList<>();
        
        Address obsoleteInventoryAddress = new Address("Ecuador", "Quito", "Bodega Obsoletos");
        obsoleteInventory = new InventoryOfObsolete(obsoleteInventoryAddress);
        
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

    public ArrayList<Customer> getCustomers() { 
        return customers; 
    }
    
    public ArrayList<Product> getProducts() { 
        return products; 
    }
    
    public ArrayList<Supplier> getSuppliers() { 
        return suppliers; 
    }
    
    public ArrayList<Inventory> getInventories() { 
        return inventories; 
    }
    
    public ArrayList<PersonalAccount> getPersonalAccounts() { 
        return personalAccounts; 
    }
    
    public ArrayList<CompanyAccount> getCompanyAccounts() { 
        return companyAccounts; 
    }
    
    public InventoryOfObsolete getObsoleteInventory() { 
        return obsoleteInventory; 
    }
    
    public ArrayList<InvoiceSim> getInvoices() { 
        return invoices; 
    }
    
    public ArrayList<ReturnedProduct> getReturns() { 
        return returns; 
    }
    
    public float getTaxRate() { 
        return taxRate; 
    }
    
    public void setTaxRate(float taxRate) { 
        this.taxRate = taxRate; 
    }
    
    public AdminDetail getAdminInfo() { 
        return adminInfo; 
    }
    
    public void setAdminInfo(AdminDetail adminInfo) { 
        this.adminInfo = adminInfo; 
    }
    
    public CompanyAccount getCompanyInfo() { 
        return companyInfo; 
    }

    public void setCompanyInfo(CompanyAccount companyInfo) { 
        this.companyInfo = companyInfo; 
    }
    
    public float getProfitPercentage() { 
        return profitPercentage; 
    }
    
    public void setProfitPercentage(float profitPercentage) { 
        this.profitPercentage = profitPercentage; 
    }
    
    public float getDiscountStandard() { 
        return discountStandard; 
    }
    
    public void setDiscountStandard(float discountStandard) { 
        this.discountStandard = discountStandard; 
    }
    
    public float getDiscountPremium() { 
        return discountPremium; 
    }
    
    public void setDiscountPremium(float discountPremium) { 
        this.discountPremium = discountPremium; 
    }
    
    public float getDiscountVip() { 
        return discountVip; 
    }
    
    public void setDiscountVip(float discountVip) { 
        this.discountVip = discountVip; 
    }
}