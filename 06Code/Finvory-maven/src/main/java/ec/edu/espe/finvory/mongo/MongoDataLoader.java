package ec.edu.espe.finvory.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import ec.edu.espe.finvory.model.Address;
import ec.edu.espe.finvory.model.CompanyAccount;
import ec.edu.espe.finvory.model.Customer;
import ec.edu.espe.finvory.model.FinvoryData;
import ec.edu.espe.finvory.model.Inventory;
import ec.edu.espe.finvory.model.InventoryOfObsolete;
import ec.edu.espe.finvory.model.InvoiceLineSim;
import ec.edu.espe.finvory.model.InvoiceSim;
import ec.edu.espe.finvory.model.Product;
import ec.edu.espe.finvory.model.ReturnedProduct;
import ec.edu.espe.finvory.model.Supplier;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.bson.Document;

/**
 *
 * @author Mathews Pastor, The POOwer Rangers Of Programming
 */
public class MongoDataLoader {

    public FinvoryData loadDataFromCloud(String username) {
        FinvoryData data = new FinvoryData();
        try {
            loadCompanyInfoFromCloud(data, username);
            loadConfigurationsFromCloud(data, username);
            loadProductsFromCloud(data, username);
            loadCustomersFromCloud(data, username);
            loadSuppliersFromCloud(data, username);
            loadInventoriesFromCloud(data, username);
            loadObsoleteInventoryFromCloud(data, username);
            loadInvoicesFromCloud(data, username);
            loadReturnsFromCloud(data, username);

            dedupeCustomers(data);
            dedupeSuppliers(data);
            dedupeProducts(data);
            dedupeInventories(data);

            return data;
        } catch (Exception e) {
            System.err.println("Error cargando desde cloud: " + e.getMessage());
            return null;
        }
    }

    private void loadCompanyInfoFromCloud(FinvoryData data, String username) {
        MongoCollection<Document> collection = MongoDBConnection.getCollection("companies");
        if (collection == null) {
            return;
        }
        Document doc = collection.find(Filters.eq("companyUsername", username)).first();
        if (doc != null) {
            CompanyAccount company = new CompanyAccount();
            company.setName(doc.getString("name"));
            company.setPhone(doc.getString("phone"));
            company.setEmail(doc.getString("email"));
            company.setLogoPath(doc.getString("logoPath"));
            Document addrDoc = (Document) doc.get("address");
            if (addrDoc != null) {
                company.setAddress(new Address(addrDoc.getString("country"), addrDoc.getString("city"), addrDoc.getString("street")));
            }
            data.setCompanyInfo(company);
        }
    }

    /*private void loadConfigurationsFromCloud(FinvoryData data, String username) {
        MongoCollection<Document> collection = MongoDBConnection.getCollection("configurations");
        if (collection == null) {
            return;
        }
        Document doc = collection.find(Filters.eq("companyUsername", username)).first();
        if (doc != null) {
            data.setTaxRate(BigDecimal.valueOf(getDoubleSafe(doc, "taxRate", 0.0)));
            data.setProfitPercentage(BigDecimal.valueOf(getDoubleSafe(doc, "profitPercentage", 0.0)));
            data.setDiscountStandard(BigDecimal.valueOf(getDoubleSafe(doc, "discountStandard", 0.0)));
            data.setDiscountPremium(BigDecimal.valueOf(getDoubleSafe(doc, "discountPremium", 0.0)));
            data.setDiscountVip(BigDecimal.valueOf(getDoubleSafe(doc, "discountVip", 0.0)));
        }
    }*/
    private void loadConfigurationsFromCloud(FinvoryData data, String username) {
        MongoCollection<Document> collection = MongoDBConnection.getCollection("configurations");
        if (collection == null) {
            return;
        }

        Document doc = collection.find(Filters.eq("companyUsername", username)).first();
        if (doc != null) {
            BigDecimal cloudTax = BigDecimal.valueOf(getDoubleSafe(doc, "taxRate", 0.15));
            ec.edu.espe.finvory.model.TaxManager.getInstance().setTaxRate(cloudTax);
            data.setTaxRate(ec.edu.espe.finvory.model.TaxManager.getInstance().getTaxRate());
            data.setProfitPercentage(BigDecimal.valueOf(getDoubleSafe(doc, "profitPercentage", 0.0)));
            data.setDiscountStandard(BigDecimal.valueOf(getDoubleSafe(doc, "discountStandard", 0.0)));
            data.setDiscountPremium(BigDecimal.valueOf(getDoubleSafe(doc, "discountPremium", 0.0)));
            data.setDiscountVip(BigDecimal.valueOf(getDoubleSafe(doc, "discountVip", 0.0)));
        }
    }

    private void loadProductsFromCloud(FinvoryData data, String username) {
        MongoCollection<Document> collection = MongoDBConnection.getCollection("products");
        if (collection == null) {
            return;
        }
        for (Document doc : collection.find(Filters.eq("companyUsername", username))) {
            BigDecimal cost = BigDecimal.valueOf(getDoubleSafe(doc, "baseCostPrice", 0.0));
            data.addProduct(new Product(
                    doc.getString("productId"), doc.getString("name"), doc.getString("description"),
                    doc.getString("barcode"), cost, doc.getString("supplierId")
            ));
        }
    }

    private void loadCustomersFromCloud(FinvoryData data, String username) {
        MongoCollection<Document> collection = MongoDBConnection.getCollection("customers");
        if (collection == null) {
            return;
        }
        for (Document doc : collection.find(Filters.eq("companyUsername", username))) {
            data.addCustomer(new Customer(
                    doc.getString("name"), doc.getString("identification"), doc.getString("phone"),
                    doc.getString("email"), doc.getString("clientType")
            ));
        }
    }

    private void loadSuppliersFromCloud(FinvoryData data, String username) {
        MongoCollection<Document> collection = MongoDBConnection.getCollection("suppliers");
        if (collection == null) {
            return;
        }
        for (Document doc : collection.find(Filters.eq("companyUsername", username))) {
            Supplier supplier = new Supplier(
                    doc.getString("fullName"), doc.getString("id1"), doc.getString("phone"),
                    doc.getString("email"), doc.getString("description")
            );
            supplier.setId2(doc.getString("id2"));
            data.addSupplier(supplier);
        }
    }

    private void loadInventoriesFromCloud(FinvoryData data, String username) {
        MongoCollection<Document> collection = MongoDBConnection.getCollection("inventories");
        if (collection == null) {
            return;
        }
        for (Document doc : collection.find(Filters.eq("companyUsername", username))) {
            String name = doc.getString("name");
            if (name == null || name.isBlank()) {
                continue;
            }

            Address address = toAddress((Document) doc.get("address"));
            Inventory inv = new Inventory(name, address);

            Object stockRaw = doc.get("productStock");
            if (stockRaw instanceof Document stockDoc) {
                for (Map.Entry<String, Object> entry : stockDoc.entrySet()) {
                    inv.setStock(entry.getKey(), toInt(entry.getValue(), 0));
                }
            }
            data.addInventory(inv);
        }
    }

    private void loadObsoleteInventoryFromCloud(FinvoryData data, String username) {
        MongoCollection<Document> collection = MongoDBConnection.getCollection("obsolete_inventory");
        if (collection == null || data.getObsoleteInventory() == null) {
            return;
        }

        Document doc = collection.find(Filters.eq("companyUsername", username)).first();
        if (doc != null) {
            InventoryOfObsolete obsolete = data.getObsoleteInventory();
            obsolete.setAddress(toAddress((Document) doc.get("address")));

            Object stockRaw = doc.get("productStock");
            if (stockRaw instanceof Document stockDoc) {
                obsolete.getProductStock().clear();
                for (Map.Entry<String, Object> entry : stockDoc.entrySet()) {
                    obsolete.setStock(entry.getKey(), toInt(entry.getValue(), 0));
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void loadInvoicesFromCloud(FinvoryData data, String username) {
        MongoCollection<Document> collection = MongoDBConnection.getCollection("invoices");
        if (collection == null) {
            return;
        }
        for (Document doc : collection.find(Filters.eq("companyUsername", username))) {
            String id = doc.getString("invoiceId");
            LocalDate date = toLocalDate(doc.getDate("date"));

            Document custDoc = (Document) doc.get("customer");
            Customer customer = (custDoc != null) ? new Customer(
                    custDoc.getString("name"), custDoc.getString("identification"),
                    custDoc.getString("phone"), custDoc.getString("email"), custDoc.getString("clientType"))
                    : new Customer("Consumidor Final", "9999999999", "", "", "Final");

            ArrayList<InvoiceLineSim> lines = new ArrayList<>();
            List<Document> linesDoc = (List<Document>) doc.get("lines");
            if (linesDoc != null) {
                for (Document l : linesDoc) {
                    double price = getDoubleSafe(l, "priceApplied", 0.0);
                    lines.add(new InvoiceLineSim(
                            l.getString("productId"), l.getString("productName"),
                            toInt(l.get("quantity"), 0), BigDecimal.valueOf(price)
                    ));
                }
            }
            BigDecimal tax = BigDecimal.valueOf(getDoubleSafe(doc, "tax", 0.0));
            InvoiceSim inv = new InvoiceSim(id, date, date, customer, lines, tax, BigDecimal.ZERO);
            inv.complete();
            data.addInvoice(inv);
        }
    }

    private void loadReturnsFromCloud(FinvoryData data, String username) {
        MongoCollection<Document> collection = MongoDBConnection.getCollection("returns");
        if (collection == null) {
            return;
        }
        for (Document doc : collection.find(Filters.eq("companyUsername", username))) {
            String pId = doc.getString("productId");
            Product product = findProductById(data, pId);
            if (product != null) {
                data.addReturn(new ReturnedProduct(product, toInt(doc.get("quantity"), 0), doc.getString("reason")));
            }
        }
    }

    private Product findProductById(FinvoryData data, String id) {
        if (data.getProducts() == null) {
            return null;
        }
        for (Product product : data.getProducts()) {
            if (product.getId().equals(id)) {
                return product;
            }
        }
        return null;
    }

    private double getDoubleSafe(Document doc, String key, double def) {
        if (doc == null || doc.get(key) == null) {
            return def;
        }
        Object v = doc.get(key);
        if (v instanceof Number) {
            return ((Number) v).doubleValue();
        }
        return def;
    }

    private int toInt(Object val, int def) {
        if (val instanceof Number) {
            return ((Number) val).intValue();
        }
        return def;
    }

    private Address toAddress(Document doc) {
        if (doc == null) {
            return null;
        }
        return new Address(doc.getString("country"), doc.getString("city"), doc.getString("street"));
    }

    private LocalDate toLocalDate(Date date) {
        return (date == null) ? LocalDate.now() : date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private void dedupeCustomers(FinvoryData data) {
        if (data == null || data.getCustomers() == null) {
            return;
        }
        Map<String, Customer> map = new LinkedHashMap<>();
        for (Customer customer : new ArrayList<>(data.getCustomers())) {
            if (customer != null) {
                map.putIfAbsent(customer.getIdentification(), customer);
            }
        }
        data.getCustomers().clear();
        data.getCustomers().addAll(map.values());
    }

    private void dedupeSuppliers(FinvoryData data) {
        if (data == null || data.getSuppliers() == null) {
            return;
        }
        Map<String, Supplier> map = new LinkedHashMap<>();
        for (Supplier supplier : new ArrayList<>(data.getSuppliers())) {
            if (supplier != null) {
                map.putIfAbsent(supplier.getId1(), supplier);
            }
        }
        data.getSuppliers().clear();
        data.getSuppliers().addAll(map.values());
    }

    private void dedupeProducts(FinvoryData data) {
        if (data == null || data.getProducts() == null) {
            return;
        }

        Map<String, Product> byId = new LinkedHashMap<>();
        for (Product product : new ArrayList<>(data.getProducts())) {
            if (product == null) {
                continue;
            }
            String id = norm(product.getId());
            byId.putIfAbsent(id.isEmpty() ? UUID.randomUUID().toString() : id, product);
        }

        data.getProducts().clear();
        data.getProducts().addAll(byId.values());
    }

    private void dedupeInvoices(FinvoryData data) {
        if (data == null || data.getInvoices() == null) {
            return;
        }

        Map<String, InvoiceSim> byId = new LinkedHashMap<>();
        for (InvoiceSim invoiceSim : new ArrayList<>(data.getInvoices())) {
            if (invoiceSim == null) {
                continue;
            }
            String key = norm(invoiceSim.getId());
            byId.putIfAbsent(key.isEmpty() ? UUID.randomUUID().toString() : key, invoiceSim);
        }

        data.getInvoices().clear();
        data.getInvoices().addAll(byId.values());
    }

    private static String norm(String string) {
        return string == null ? "" : string.trim();
    }

    private void dedupeInventories(FinvoryData data) {
        if (data == null || data.getInventories() == null) {
            return;
        }

        Map<String, Inventory> byName = new LinkedHashMap<>();
        for (Inventory inventory : new ArrayList<>(data.getInventories())) {
            if (inventory == null) {
                continue;
            }
            String key = norm(inventory.getName()).toLowerCase(Locale.ROOT);
            byName.putIfAbsent(key.isEmpty() ? UUID.randomUUID().toString() : key, inventory);
        }

        data.getInventories().clear();
        data.getInventories().addAll(byName.values());
    }

    public BigDecimal loadTaxRateFromCloud(String companyUsername) {
        MongoCollection<Document> collection = MongoDBConnection.getCollection("configurations");
        if (collection != null) {
            Document doc = collection.find(Filters.eq("companyUsername", companyUsername)).first();
            if (doc != null && doc.containsKey("taxRate")) {
                return new BigDecimal(doc.getDouble("taxRate").toString());
            }
        }
        return new BigDecimal("0.15"); 
    }
}
