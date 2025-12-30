package ec.edu.espe.finvory.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import ec.edu.espe.finvory.model.*;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import java.math.BigDecimal;
import java.util.Map;

public class MongoDataExporter {

    public static void exportCompanyData(String companyUsername, FinvoryData data, MongoDatabase mongoDatabase) {
        if (mongoDatabase == null) {
            throw new IllegalStateException("MongoDatabase es null");
        }
        if (!isDatabaseOnline(mongoDatabase)) {
            throw new IllegalStateException("Sin conexión a MongoDB");
        }

        CompanyAccount account;
        if (data != null) {
            account = data.getCompanyInfo();
        } else {
            account = null;
        }
        
        exportCompanyInternal(companyUsername, account, data, mongoDatabase);
    }

    private static boolean isDatabaseOnline(MongoDatabase dataBase) {
        try {
            dataBase.runCommand(new Document("ping", 1));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static final String ENV_URI_NAME = "MONGODB_URI";
    private static final String DATABASE_NAME = "FinvoryDB";

    public static void main(String[] args) {
        String connectionString = System.getenv(ENV_URI_NAME);
        if (connectionString == null || connectionString.isEmpty()) {
            System.err.println("La variable de entorno MONGODB_URI no está definida.");
            return;
        }

        MongoDBConnection connection = new MongoDBConnection(connectionString, DATABASE_NAME);
        MongoDatabase mongoDatabase = connection.getDatabaseInstance();

        if (mongoDatabase == null) {
            System.err.println("Error: No se pudo obtener la base de datos después de la conexión.");
            connection.close();
            return;
        }

        Database localDatabase = new Database();
        SystemUsers systemUsers = localDatabase.loadUsers();

        exportUsers(systemUsers, mongoDatabase);

        for (CompanyAccount companyAccount : systemUsers.getCompanyAccounts()) {
            String companyUsername = companyAccount.getUsername();
            FinvoryData data = localDatabase.loadCompanyData(companyUsername);
            exportCompanyInternal(companyUsername, companyAccount, data, mongoDatabase);
        }

        connection.close();
    }

    public static void exportUsers(SystemUsers systemUsers, MongoDatabase mongoDatabase) {
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

    public static void exportCompanyData(String companyUsername, FinvoryData data, CompanyAccount companyAccount) {
        ec.edu.espe.finvory.mongo.MongoDBConnection conn = ec.edu.espe.finvory.FinvoryApp.getMongoDBConnection();

        if (conn == null || conn.getDatabaseInstance() == null) {
            System.err.println("ERROR: La conexión a MongoDB no está activa.");
            return;
        }

        MongoDatabase mongoDatabase = conn.getDatabaseInstance();

        exportCompanyInfo(companyUsername, companyAccount, data, mongoDatabase);
        exportCustomers(companyUsername, data.getCustomers(), mongoDatabase);
        exportSuppliers(companyUsername, data.getSuppliers(), mongoDatabase);
        exportProducts(companyUsername, data.getProducts(), mongoDatabase);
        exportInventories(companyUsername, data.getInventories(), data.getProducts(), mongoDatabase);
        exportObsoleteInventory(companyUsername, data.getObsoleteInventory(), mongoDatabase);

        exportInvoices(companyUsername, data.getInvoices(), mongoDatabase);
        exportReturns(companyUsername, data.getReturns(), mongoDatabase);

        try {
            MongoCollection<Document> configurateCollection = mongoDatabase.getCollection("configurations");
            if (configurateCollection != null) {
                configurateCollection.deleteMany(Filters.eq("companyUsername", companyUsername));

                Document configDoc = new Document("companyUsername", companyUsername)
                        .append("taxRate", toDouble(data.getTaxRate()))
                        .append("profitPercentage", toDouble(data.getProfitPercentage()))
                        .append("discountStandard", toDouble(data.getDiscountStandard()))
                        .append("discountPremium", toDouble(data.getDiscountPremium()))
                        .append("discountVip", toDouble(data.getDiscountVip()));
                configurateCollection.insertOne(configDoc);
            }
        } catch (Exception e) {
            System.err.println("Error subiendo configuración: " + e.getMessage());
        }
    }

    private static void exportCompanyInternal(String companyUsername, CompanyAccount companyAccount, FinvoryData data, MongoDatabase mongoDatabase) {
        if (companyAccount != null) {
            exportCompanyInfo(companyUsername, companyAccount, data, mongoDatabase);
        }
        exportCustomers(companyUsername, data.getCustomers(), mongoDatabase);
        exportSuppliers(companyUsername, data.getSuppliers(), mongoDatabase);
        exportProducts(companyUsername, data.getProducts(), mongoDatabase);
        exportInventories(companyUsername, data.getInventories(), data.getProducts(), mongoDatabase);
        exportObsoleteInventory(companyUsername, data.getObsoleteInventory(), mongoDatabase);
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
        document.append("taxRate", toDouble(data.getTaxRate()));
        document.append("profitPercentage", toDouble(data.getProfitPercentage()));
        document.append("discountStandard", toDouble(data.getDiscountStandard()));
        document.append("discountPremium", toDouble(data.getDiscountPremium()));
        document.append("discountVip", toDouble(data.getDiscountVip()));

        collection.insertOne(document);

    }

    private static void exportCustomers(String companyUsername, List<Customer> customers, MongoDatabase mongoDatabase) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("customers");
        collection.deleteMany(Filters.eq("companyUsername", companyUsername));

        List<Document> documents = new ArrayList<>();
        for (Customer customer : customers) {
            Document document = toCustomerDocument(customer);
            if (document != null) {
                document.append("companyUsername", companyUsername);
                documents.add(document);
            }
        }
        if (!documents.isEmpty()) {
            collection.insertMany(documents);
        }
    }

    private static void exportSuppliers(String companyUsername, List<Supplier> suppliers, MongoDatabase mongoDatabase) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("suppliers");
        collection.deleteMany(Filters.eq("companyUsername", companyUsername));

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
        collection.deleteMany(Filters.eq("companyUsername", companyUsername));

        List<Document> documents = new ArrayList<>();
        for (Product product : products) {
            Document document = new Document();
            document.append("companyUsername", companyUsername);
            document.append("productId", product.getId());
            document.append("name", product.getName());
            document.append("description", product.getDescription());
            document.append("barcode", product.getBarcode());
            document.append("baseCostPrice", toDouble(product.getBaseCostPrice()));
            document.append("supplierId", product.getSupplierId());
            documents.add(document);
        }
        if (!documents.isEmpty()) {
            collection.insertMany(documents);
        }
    }

    private static void exportInventories(String companyUsername, List<Inventory> inventories, List<Product> products, MongoDatabase mongoDatabase) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("inventories");
        collection.deleteMany(Filters.eq("companyUsername", companyUsername));

        List<Document> documents = new ArrayList<>();
        for (Inventory inventory : inventories) {
            Document document = new Document();
            document.append("companyUsername", companyUsername);
            document.append("name", inventory.getName());
            document.append("address", toAddressDocument(inventory.getAddress()));

            Document stockDocument = new Document();
            for (Map.Entry<String, Integer> entry : inventory.getProductStock().entrySet()) {
                int quantity = entry.getValue();
                if (quantity > 0) {
                    stockDocument.append(entry.getKey(), quantity);
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

    private static void exportObsoleteInventory(String companyUsername, InventoryOfObsolete obsoleteInventory, MongoDatabase mongoDatabase) {
        if (obsoleteInventory == null) {
            return;
        }

        MongoCollection<Document> collection = mongoDatabase.getCollection("obsolete_inventory");
        collection.deleteMany(Filters.eq("companyUsername", companyUsername));

        Document document = new Document();
        document.append("companyUsername", companyUsername);
        document.append("address", toAddressDocument(obsoleteInventory.getAddress()));

        Document stockDocument = new Document();
        for (Map.Entry<String, Integer> entry : obsoleteInventory.getProductStock().entrySet()) {
            int quantity = entry.getValue();
            if (quantity > 0) {
                stockDocument.append(entry.getKey(), quantity);
            }
        }

        if (!stockDocument.isEmpty()) {
            document.append("productStock", stockDocument);
        }

        collection.insertOne(document);
    }

    private static void exportInvoices(String companyUsername, List<InvoiceSim> invoices, MongoDatabase mongoDatabase) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("invoices");
        collection.deleteMany(Filters.eq("companyUsername", companyUsername));

        List<Document> documents = new ArrayList<>();
        for (InvoiceSim invoice : invoices) {
            Document document = new Document();
            document.append("companyUsername", companyUsername);
            document.append("invoiceId", invoice.getId());
            document.append("date", invoice.getDate());
            document.append("paymentDueDate", invoice.getPaymentDueDate());

            document.append("subtotal", toDouble(invoice.getSubtotal()));
            document.append("tax", toDouble(invoice.getTaxRate()));
            document.append("total", toDouble(invoice.getTotal()));

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

                double lineTotal = toDouble(line.getLineTotal());
                double priceApplied = 0.0;
                int quantity = line.getQuantity();

                if (quantity > 0) {
                    priceApplied = lineTotal / quantity;
                }

                lineDocument.append("priceApplied", priceApplied);
                lineDocument.append("lineTotal", lineTotal);
                lineDocuments.add(lineDocument);
            }
            document.append("lines", lineDocuments);
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

    private static Double toDouble(BigDecimal decimal) {
        return (decimal != null) ? decimal.doubleValue() : null;
    }

    private static void exportReturns(String companyUsername, List<ReturnedProduct> returnsList, MongoDatabase mongoDatabase) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("returns");

        collection.deleteMany(Filters.eq("companyUsername", companyUsername));

        List<Document> documents = new ArrayList<>();

        for (ReturnedProduct ret : returnsList) {
            if (ret.getProduct() != null) {
                Document doc = new Document()
                        .append("companyUsername", companyUsername)
                        .append("productId", ret.getProduct().getId())
                        .append("quantity", ret.getQuantity())
                        .append("reason", ret.getReason())
                        .append("returnDate", ret.getReturnDate().toString());
                documents.add(doc);
            }
        }

        if (!documents.isEmpty()) {
            collection.insertMany(documents);
        }
    }
}
