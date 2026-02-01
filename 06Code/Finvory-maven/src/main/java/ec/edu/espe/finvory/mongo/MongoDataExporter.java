package ec.edu.espe.finvory.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import ec.edu.espe.finvory.model.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bson.Document;

public class MongoDataExporter {

    private static final String ENV_URI_NAME = "MONGODB_URI";
    private static final String DATABASE_NAME = "FinvoryDB";

    private MongoDataExporter() {
    }

    public static void exportCompanyData(String companyUsername, FinvoryData data, MongoDatabase mongoDatabase) {
        if (mongoDatabase == null) {
            throw new IllegalStateException("MongoDatabase es null");
        }
        if (!isDatabaseOnline(mongoDatabase)) {
            throw new IllegalStateException("Sin conexión a MongoDB");
        }
        if (companyUsername == null || companyUsername.isBlank()) {
            throw new IllegalArgumentException("companyUsername vacío");
        }

        CompanyAccount account = (data != null) ? data.getCompanyInfo() : null;

        exportCompanyInfo(companyUsername, account, data, mongoDatabase);
        exportCustomers(companyUsername, safeList(data != null ? data.getCustomers() : null), mongoDatabase);
        exportSuppliers(companyUsername, safeList(data != null ? data.getSuppliers() : null), mongoDatabase);
        exportProducts(companyUsername, safeList(data != null ? data.getProducts() : null), mongoDatabase);
        exportInventories(companyUsername,
                safeList(data != null ? data.getInventories() : null),
                safeList(data != null ? data.getProducts() : null),
                mongoDatabase
        );
        exportObsoleteInventory(companyUsername, data != null ? data.getObsoleteInventory() : null, mongoDatabase);
        exportInvoices(companyUsername, safeList(data != null ? data.getInvoices() : null), mongoDatabase);
        exportReturns(companyUsername, safeList(data != null ? data.getReturns() : null), mongoDatabase);

        exportConfigurations(companyUsername, data, mongoDatabase);
    }

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
        
        connection.close();
    }

    public static void exportUsers(SystemUsers users, MongoDatabase database) {
        if (users == null || database == null) {
            return;
        }

        MongoCollection<Document> companyCol = database.getCollection("companies");
        for (CompanyAccount companyAccount : safeList(users.getCompanyAccounts())) {
            if (companyAccount == null || companyAccount.getUsername() == null) {
                continue;
            }

            Document doc = new Document()
                    .append("companyUsername", companyAccount.getUsername())
                    .append("password", companyAccount.getPassword())
                    .append("twoFactorKey", companyAccount.getTwoFactorKey())
                    .append("name", companyAccount.getName())
                    .append("ruc", companyAccount.getRuc())
                    .append("phone", companyAccount.getPhone())
                    .append("email", companyAccount.getEmail())
                    .append("logoPath", companyAccount.getLogoPath());

            if (companyAccount.getAddress() != null) {
                doc.append("address", toAddressDocument(companyAccount.getAddress()));
            }

            companyCol.replaceOne(
                    Filters.eq("companyUsername", companyAccount.getUsername()),
                    doc,
                    new ReplaceOptions().upsert(true)
            );
        }

        MongoCollection<Document> personalCol = database.getCollection("personal_accounts");
        for (PersonalAccount personalAccount : safeList(users.getPersonalAccounts())) {
            if (personalAccount == null || personalAccount.getUsername() == null) {
                continue;
            }

            Document doc = new Document()
                    .append("username", personalAccount.getUsername())
                    .append("password", personalAccount.getPassword())
                    .append("twoFactorKey", personalAccount.getTwoFactorKey())
                    .append("fullName", personalAccount.getFullName())
                    .append("photoPath", personalAccount.getProfilePhotoPath());

            personalCol.replaceOne(
                    Filters.eq("username", personalAccount.getUsername()),
                    doc,
                    new ReplaceOptions().upsert(true)
            );
        }

        System.out.println("Usuarios sincronizados con éxito.");
    }

    private static boolean isDatabaseOnline(MongoDatabase dataBase) {
        try {
            dataBase.runCommand(new Document("ping", 1));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void exportCompanyInfo(String companyUsername, CompanyAccount companyAccount, FinvoryData data, MongoDatabase mongoDatabase) {
        if (mongoDatabase == null) {
            return;
        }
        MongoCollection<Document> collection = mongoDatabase.getCollection("companies");

        Document doc = new Document()
                .append("companyUsername", companyUsername);

        if (companyAccount != null) {
            doc.append("name", companyAccount.getName());
            doc.append("ruc", companyAccount.getRuc());
            doc.append("phone", companyAccount.getPhone());
            doc.append("email", companyAccount.getEmail());
            doc.append("address", toAddressDocument(companyAccount.getAddress()));

            doc.append("password", companyAccount.getPassword());
            doc.append("twoFactorKey", companyAccount.getTwoFactorKey());
            doc.append("logoPath", companyAccount.getLogoPath());
        }

        if (data != null) {
            doc.append("taxRate", toDouble(data.getTaxRate()));
            doc.append("profitPercentage", toDouble(data.getProfitPercentage()));
            doc.append("discountStandard", toDouble(data.getDiscountStandard()));
            doc.append("discountPremium", toDouble(data.getDiscountPremium()));
            doc.append("discountVip", toDouble(data.getDiscountVip()));
        }

        collection.replaceOne(
                Filters.eq("companyUsername", companyUsername),
                doc,
                new ReplaceOptions().upsert(true)
        );
    }


    private static void exportConfigurations(String companyUsername, FinvoryData data, MongoDatabase mongoDatabase) {
        if (mongoDatabase == null || data == null) {
            return;
        }

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

    private static void exportCustomers(String companyUsername, List<Customer> customers, MongoDatabase mongoDatabase) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("customers");
        collection.deleteMany(Filters.eq("companyUsername", companyUsername));

        List<Document> documents = new ArrayList<>();
        for (Customer customer : safeList(customers)) {
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
        for (Supplier supplier : safeList(suppliers)) {
            if (supplier == null) {
                continue;
            }
            Document document = new Document()
                    .append("companyUsername", companyUsername)
                    .append("fullName", supplier.getFullName())
                    .append("id1", supplier.getId1())
                    .append("id2", supplier.getId2())
                    .append("phone", supplier.getPhone())
                    .append("email", supplier.getEmail())
                    .append("description", supplier.getDescription());
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
        for (Product product : safeList(products)) {
            if (product == null) {
                continue;
            }
            Document document = new Document()
                    .append("companyUsername", companyUsername)
                    .append("productId", product.getId())
                    .append("name", product.getName())
                    .append("description", product.getDescription())
                    .append("barcode", product.getBarcode())
                    .append("baseCostPrice", toDouble(product.getBaseCostPrice()))
                    .append("supplierId", product.getSupplierId());
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
        for (Inventory inventory : safeList(inventories)) {
            if (inventory == null) {
                continue;
            }

            Document document = new Document()
                    .append("companyUsername", companyUsername)
                    .append("name", inventory.getName())
                    .append("address", toAddressDocument(inventory.getAddress()));

            Document stockDocument = new Document();
            if (inventory.getProductStock() != null) {
                for (Map.Entry<String, Integer> entry : inventory.getProductStock().entrySet()) {
                    int quantity = entry.getValue() != null ? entry.getValue() : 0;
                    if (quantity > 0) {
                        stockDocument.append(entry.getKey(), quantity);
                    }
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

        Document document = new Document()
                .append("companyUsername", companyUsername)
                .append("address", toAddressDocument(obsoleteInventory.getAddress()));

        Document stockDocument = new Document();
        if (obsoleteInventory.getProductStock() != null) {
            for (Map.Entry<String, Integer> entry : obsoleteInventory.getProductStock().entrySet()) {
                int quantity = entry.getValue() != null ? entry.getValue() : 0;
                if (quantity > 0) {
                    stockDocument.append(entry.getKey(), quantity);
                }
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
        for (InvoiceSim invoice : safeList(invoices)) {
            if (invoice == null) {
                continue;
            }

            Document document = new Document()
                    .append("companyUsername", companyUsername)
                    .append("invoiceId", invoice.getId())
                    .append("date", invoice.getDate())
                    .append("paymentDueDate", invoice.getPaymentDueDate())
                    .append("subtotal", toDouble(invoice.getSubtotal()))
                    .append("tax", toDouble(invoice.getTaxRate()))
                    .append("total", toDouble(invoice.getTotal()));

            Document customerDocument = toCustomerDocument(invoice.getCustomer());
            if (customerDocument != null) {
                document.append("customer", customerDocument);
            }

            List<Document> lineDocuments = new ArrayList<>();
            if (invoice.getLines() != null) {
                for (InvoiceLineSim line : invoice.getLines()) {
                    if (line == null) {
                        continue;
                    }
                    Document lineDocument = new Document()
                            .append("productId", line.getProductId())
                            .append("productName", line.getProductName())
                            .append("quantity", line.getQuantity());

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
            }

            document.append("lines", lineDocuments);
            documents.add(document);
        }

        if (!documents.isEmpty()) {
            collection.insertMany(documents);
        }
    }

    private static void exportReturns(String companyUsername, List<ReturnedProduct> returnsList, MongoDatabase mongoDatabase) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("returns");
        collection.deleteMany(Filters.eq("companyUsername", companyUsername));

        List<Document> documents = new ArrayList<>();
        for (ReturnedProduct returnedProduct : safeList(returnsList)) {
            if (returnedProduct == null || returnedProduct.getProduct() == null) {
                continue;
            }

            Document document = new Document()
                    .append("companyUsername", companyUsername)
                    .append("productId", returnedProduct.getProduct().getId())
                    .append("quantity", returnedProduct.getQuantity())
                    .append("reason", returnedProduct.getReason())
                    .append("returnDate", returnedProduct.getReturnDate() != null ? returnedProduct.getReturnDate().toString() : null);

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
        return new Document()
                .append("country", address.getCountry())
                .append("city", address.getCity())
                .append("street", address.getStreet());
    }

    private static Document toCustomerDocument(Customer customer) {
        if (customer == null) {
            return null;
        }
        return new Document()
                .append("name", customer.getName())
                .append("identification", customer.getIdentification())
                .append("phone", customer.getPhone())
                .append("email", customer.getEmail())
                .append("clientType", customer.getClientType());
    }

    private static Double toDouble(BigDecimal decimal) {
        return (decimal != null) ? decimal.doubleValue() : null;
    }

    private static double toDouble(BigDecimal decimal, double fallback) {
        return (decimal != null) ? decimal.doubleValue() : fallback;
    }

    private static <T> List<T> safeList(List<T> list) {
        return (list != null) ? list : new ArrayList<>();
    }
}
