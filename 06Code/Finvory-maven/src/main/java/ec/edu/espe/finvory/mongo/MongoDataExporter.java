package ec.edu.espe.finvory.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import ec.edu.espe.finvory.model.Address;
import ec.edu.espe.finvory.model.CompanyAccount;
import ec.edu.espe.finvory.model.Customer;
import ec.edu.espe.finvory.model.Database;
import ec.edu.espe.finvory.model.FinvoryData;
import ec.edu.espe.finvory.model.Inventory;
import ec.edu.espe.finvory.model.InventoryOfObsolete;
import ec.edu.espe.finvory.model.InvoiceLineSim;
import ec.edu.espe.finvory.model.InvoiceSim;
import ec.edu.espe.finvory.model.PersonalAccount;
import ec.edu.espe.finvory.model.Product;
import ec.edu.espe.finvory.model.ReturnedProduct;
import ec.edu.espe.finvory.model.Supplier;
import ec.edu.espe.finvory.model.SystemUsers;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;

public class MongoDataExporter {

    private static final String ENV_URI_NAME = "MONGODB_URI";
    private static final String DATABASE_NAME = "FinvoryDB";

    public static void main(String[] args) {
        String connectionString = System.getenv(ENV_URI_NAME);
        if (connectionString == null || connectionString.isEmpty()) {
            System.err.println("La variable de entorno MONGODB_URI no está definida.");
            return;
        }

        Database localDatabase = new Database();
        SystemUsers systemUsers = localDatabase.loadUsers();

        MongoDBConnection connection = new MongoDBConnection(connectionString, DATABASE_NAME);
        MongoDatabase mongoDatabase = connection.getDatabase();

        exportUsers(systemUsers, mongoDatabase);

        for (CompanyAccount companyAccount : systemUsers.getCompanyAccounts()) {
            String companyUsername = companyAccount.getUsername();
            FinvoryData data = localDatabase.loadCompanyData(companyUsername);
            exportCompany(companyAccount, data, mongoDatabase);
        }

        connection.close();
    }

    private static void exportUsers(SystemUsers systemUsers, MongoDatabase mongoDatabase) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("users");

        List<Document> documents = new ArrayList<>();

        for (CompanyAccount companyAccount : systemUsers.getCompanyAccounts()) {
            Document document = new Document();
            document.append("username", companyAccount.getUsername());
            document.append("type", "COMPANY");
            document.append("companyUsername", companyAccount.getUsername());
            document.append("name", companyAccount.getName());
            documents.add(document);
        }

        for (PersonalAccount personalAccount : systemUsers.getPersonalAccounts()) {
            Document document = new Document();
            document.append("username", personalAccount.getUsername());
            document.append("type", "PERSONAL");
            document.append("fullName", personalAccount.getFullName());
            documents.add(document);
        }

        if (!documents.isEmpty()) {
            collection.deleteMany(new Document());
            collection.insertMany(documents);
        }
    }

    private static void exportCompany(CompanyAccount companyAccount, FinvoryData data, MongoDatabase mongoDatabase) {
        String companyUsername = companyAccount.getUsername();

        exportCompanyInfo(companyUsername, companyAccount, data, mongoDatabase);
        exportCustomers(companyUsername, data.getCustomers(), mongoDatabase);
        exportSuppliers(companyUsername, data.getSuppliers(), mongoDatabase);
        exportProducts(companyUsername, data.getProducts(), mongoDatabase);
        exportInventories(companyUsername, data.getInventories(), data.getProducts(), mongoDatabase);
        exportObsoleteInventory(companyUsername, data.getObsoleteInventory(), data.getProducts(), mongoDatabase);
        exportInvoices(companyUsername, data.getInvoices(), mongoDatabase);
        exportReturns(companyUsername, data.getReturns(), mongoDatabase);
    }

    private static void exportCompanyInfo(String companyUsername, CompanyAccount companyAccount, FinvoryData data, MongoDatabase mongoDatabase) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("companies");

        Document filter = new Document("companyUsername", companyUsername);
        collection.deleteMany(filter);

        Document addressDocument = toAddressDocument(companyAccount.getAddress());

        Document document = new Document();
        document.append("companyUsername", companyUsername);
        document.append("name", companyAccount.getName());
        document.append("ruc", companyAccount.getRuc());
        document.append("phone", companyAccount.getPhone());
        document.append("email", companyAccount.getEmail());
        document.append("address", addressDocument);
        document.append("taxRate", data.getTaxRate());
        document.append("profitPercentage", data.getProfitPercentage());
        document.append("discountStandard", data.getDiscountStandard());
        document.append("discountPremium", data.getDiscountPremium());
        document.append("discountVip", data.getDiscountVip());

        collection.insertOne(document);
    }

    private static void exportCustomers(String companyUsername, List<Customer> customers, MongoDatabase mongoDatabase) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("customers");

        Document filter = new Document("companyUsername", companyUsername);
        collection.deleteMany(filter);

        List<Document> documents = new ArrayList<>();

        for (Customer customer : customers) {
            Document document = new Document();
            document.append("companyUsername", companyUsername);
            document.append("name", customer.getName());
            document.append("identification", customer.getIdentification());
            document.append("phone", customer.getPhone());
            document.append("email", customer.getEmail());
            document.append("clientType", customer.getClientType());
            documents.add(document);
        }

        if (!documents.isEmpty()) {
            collection.insertMany(documents);
        }
    }

    private static void exportSuppliers(String companyUsername, List<Supplier> suppliers, MongoDatabase mongoDatabase) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("suppliers");

        Document filter = new Document("companyUsername", companyUsername);
        collection.deleteMany(filter);

        List<Document> documents = new ArrayList<>();

        for (Supplier supplier : suppliers) {
            Document document = new Document();
            document.append("companyUsername", companyUsername);
            document.append("fullName", supplier.getFullName());
            document.append("id1", supplier.getId1());
            document.append("id2", supplier.getId2());
            document.append("phone", supplier.getPhone());
            document.append("email", supplier.getEmail());
            document.append("description", supplier.getDescription());
            documents.add(document);
        }

        if (!documents.isEmpty()) {
            collection.insertMany(documents);
        }
    }

    private static void exportProducts(String companyUsername, List<Product> products, MongoDatabase mongoDatabase) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("products");

        Document filter = new Document("companyUsername", companyUsername);
        collection.deleteMany(filter);

        List<Document> documents = new ArrayList<>();

        for (Product product : products) {
            Document document = new Document();
            document.append("companyUsername", companyUsername);
            document.append("productId", product.getId());
            document.append("name", product.getName());
            document.append("description", product.getDescription());
            document.append("barcode", product.getBarcode());
            document.append("baseCostPrice", product.getBaseCostPrice());
            document.append("supplierId", product.getSupplierId());
            documents.add(document);
        }

        if (!documents.isEmpty()) {
            collection.insertMany(documents);
        }
    }

    private static void exportInventories(String companyUsername, List<Inventory> inventories, List<Product> products, MongoDatabase mongoDatabase) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("inventories");

        Document filter = new Document("companyUsername", companyUsername);
        collection.deleteMany(filter);

        List<Document> documents = new ArrayList<>();

        for (Inventory inventory : inventories) {
            Document document = new Document();
            document.append("companyUsername", companyUsername);
            document.append("name", inventory.getName());
            document.append("address", toAddressDocument(inventory.getAddress()));

            Document stockDocument = new Document();
            for (Product product : products) {
                int quantity = inventory.getStock(product.getId());
                if (quantity > 0) {
                    stockDocument.append(product.getId(), quantity);
                }
            }
            if (!stockDocument.isEmpty()) {
                document.append("productStock", stockDocument);
            }

            documents.add(document);
        }

        if (!documents.isEmpty()) {
            collection.insertMany(documents);
        }
    }

    private static void exportObsoleteInventory(String companyUsername, InventoryOfObsolete obsoleteInventory, List<Product> products, MongoDatabase mongoDatabase) {
        if (obsoleteInventory == null) {
            return;
        }

        MongoCollection<Document> collection = mongoDatabase.getCollection("obsolete_inventory");

        Document filter = new Document("companyUsername", companyUsername);
        collection.deleteMany(filter);

        Document document = new Document();
        document.append("companyUsername", companyUsername);
        document.append("address", toAddressDocument(obsoleteInventory.getAddress()));

        Document stockDocument = new Document();
        for (Product product : products) {
            int quantity = obsoleteInventory.getStock(product.getId());
            if (quantity > 0) {
                stockDocument.append(product.getId(), quantity);
            }
        }
        if (!stockDocument.isEmpty()) {
            document.append("productStock", stockDocument);
        }

        collection.insertOne(document);
    }

    private static void exportInvoices(String companyUsername, List<InvoiceSim> invoices, MongoDatabase mongoDatabase) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("invoices");

        Document filter = new Document("companyUsername", companyUsername);
        collection.deleteMany(filter);

        List<Document> documents = new ArrayList<>();

        for (InvoiceSim invoice : invoices) {
            Document document = new Document();
            document.append("companyUsername", companyUsername);
            document.append("invoiceId", invoice.getId());
            document.append("date", invoice.getDate());
            document.append("paymentDueDate", invoice.getPaymentDueDate());
            document.append("subtotal", invoice.getSubtotal());
            document.append("tax", invoice.getTax());
            document.append("total", invoice.getTotal());

            Document customerDocument = toCustomerDocument(invoice.getCustomer());
            if (customerDocument != null) {
                document.append("customer", customerDocument);
            }

            List<Document> lineDocuments = new ArrayList<>();
            for (InvoiceLineSim line : invoice.getLines()) {
                Document lineDocument = new Document();
                lineDocument.append("productId", line.getProductId());
                lineDocument.append("productName", line.getProductName());
                lineDocument.append("quantity", line.getQuantity());
                int quantity = line.getQuantity();
                float priceApplied = 0f;
                if (quantity > 0) {
                    priceApplied = line.getLineTotal() / quantity;
                }
                lineDocument.append("priceApplied", priceApplied);
                lineDocument.append("lineTotal", line.getLineTotal());
                lineDocuments.add(lineDocument);
            }
            document.append("lines", lineDocuments);

            documents.add(document);
        }

        if (!documents.isEmpty()) {
            collection.insertMany(documents);
        }
    }

    private static void exportReturns(String companyUsername, List<ReturnedProduct> returnedProducts, MongoDatabase mongoDatabase) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("returns");

        Document filter = new Document("companyUsername", companyUsername);
        collection.deleteMany(filter);

        List<Document> documents = new ArrayList<>();

        for (ReturnedProduct returnedProduct : returnedProducts) {
            Document document = new Document();
            document.append("companyUsername", companyUsername);
            document.append("quantity", returnedProduct.getQuantity());
            document.append("reason", returnedProduct.getReason());

            Product product = returnedProduct.getProduct();
            if (product != null) {
                Document productDocument = new Document();
                productDocument.append("productId", product.getId());
                productDocument.append("name", product.getName());
                productDocument.append("description", product.getDescription());
                productDocument.append("barcode", product.getBarcode());
                productDocument.append("baseCostPrice", product.getBaseCostPrice());
                productDocument.append("supplierId", product.getSupplierId());
                document.append("product", productDocument);
            }

            documents.add(document);
        }

        if (!documents.isEmpty()) {
            collection.insertMany(documents);
        }
    }

    private static Document toAddressDocument(Address address) {
        if (address == null) {
            return null;
        }

        Document document = new Document();
        document.append("country", address.getCountry());
        document.append("city", address.getCity());
        document.append("street", address.getStreet());
        return document;
    }

    private static Document toCustomerDocument(Customer customer) {
        if (customer == null) {
            return null;
        }

        Document document = new Document();
        document.append("name", customer.getName());
        document.append("identification", customer.getIdentification());
        document.append("phone", customer.getPhone());
        document.append("email", customer.getEmail());
        document.append("clientType", customer.getClientType());
        return document;
    }
}
