package ec.edu.espe.finvory.controller;

import ec.edu.espe.finvory.view.FrmProducts;
import ec.edu.espe.finvory.view.FrmCustomers;
import ec.edu.espe.finvory.model.*;
import ec.edu.espe.finvory.view.FinvoryView;
import ec.edu.espe.finvory.mongo.MongoDataExporter;
import ec.edu.espe.finvory.view.FrmInventories;
import ec.edu.espe.finvory.view.FrmMainMenu;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;

/**
 *
 * @author Joseph Medina, The POOwer Rangers of Programming
 */
public class FinvoryController {

    private FinvoryData data;
    private SystemUsers users;
    private FinvoryView view;
    private Database dataBase;
    private String currentCompanyUsername;

    public FinvoryData getData() {
        return data;
    }

    public void saveData() {
        if (currentCompanyUsername != null && data != null && data.getCompanyInfo() != null) {
            dataBase.saveCompanyData(data, currentCompanyUsername);

            try {
                MongoDataExporter.exportCompanyData(
                        currentCompanyUsername,
                        data,
                        data.getCompanyInfo()
                );
            } catch (Exception e) {
                view.showError("ERROR: Fallo la exportación a MongoDB: " + e.getMessage());
            }
        }

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
            view.showError("Producto no encontrado.");
            return false;
        }

        for (InvoiceSim invoiceSim : data.getInvoices()) {
            for (InvoiceLineSim line : invoiceSim.getLines()) {
                if (line.getProductId().equals(product.getId())) {
                    view.showError("No se puede eliminar. El producto ya está en una factura.");
                    return false;
                }
            }
        }

        data.getProducts().remove(product);
        for (Inventory inventory : data.getInventories()) {
            inventory.removeStock(product.getId(), inventory.getStock(product.getId()));
        }
        data.getObsoleteInventory().removeStock(product.getId(), data.getObsoleteInventory().getStock(product.getId()));
        saveData();
        view.showMessage("Producto eliminado del sistema.");
        return true;
    }

    public FinvoryController(FinvoryView view, Database db) {
        this.view = view;
        this.dataBase = db;
        this.users = db.loadUsers();
    }

    public void run() {
        boolean running = true;
        while (running) {
            int opt = view.showStartMenu();
            switch (opt) {
                case 1 -> {
                    String role = handleLogin();
                    if (role.equals("COMPANY")) {
                        this.data = dataBase.loadCompanyData(currentCompanyUsername);

                        CompanyAccount currentAccount = findCompanyByUsername(currentCompanyUsername);
                        this.data.setCompanyInfo(currentAccount);

                        startMainMenu();

                        dataBase.saveCompanyData(data, currentCompanyUsername);
                    } else if (role.equals("PERSONAL")) {
                        startPersonalAccountMenu();
                    }
                }
                case 2 ->
                    handleRegistrationMenu();
                case 0 ->
                    running = false;
                default ->
                    view.showError("Opcion no valida.");
            }
        }
        view.showMessage("Saliendo del sistema...");
        System.exit(0);
    }

    private String handleLogin() {
        String input = view.showLogin();
        String[] parts = input.split(":");
        String username = parts[0];
        String password = parts[1];

        for (CompanyAccount company : users.getCompanyAccounts()) {
            if (company.getUsername().equals(username) && company.checkPassword(password)) {
                view.showMessage("Bienvenido (Compania) " + company.getName());
                this.currentCompanyUsername = username;
                return "COMPANY";
            }
        }

        for (PersonalAccount personal : users.getPersonalAccounts()) {
            if (personal.getUsername().equals(username) && personal.checkPassword(password)) {
                view.showMessage("Bienvenido (Personal) " + personal.getFullName());
                return "PERSONAL";
            }
        }

        view.showError("Credenciales incorrectas.");
        return "INVALID";
    }

    public boolean handleLoginGUI(String username, String password) {
        for (CompanyAccount company : users.getCompanyAccounts()) {
            if (company.getUsername().equals(username) && company.checkPassword(password)) {
                this.currentCompanyUsername = username;
                this.data = dataBase.loadCompanyData(currentCompanyUsername);
                this.data.setCompanyInfo(company);
                return true;
            }
        }
        return false;
    }

    public void startMainMenuPublic() {
        FrmMainMenu menu = new FrmMainMenu(this);
        menu.setVisible(true);
        //startMainMenu();
    }

    private void handleRegistrationMenu() {
        boolean running = true;
        while (running) {
            int opt = view.showRegistrationMenu();
            switch (opt) {
                case 1 ->
                    handleRegisterCompany();
                case 2 ->
                    handleRegisterPersonal();
                case 0 ->
                    running = false;
                default ->
                    view.showError("Opcion no valida.");
            }
        }
    }

    private void handleRegisterCompany() {
        HashMap<String, String> companyData = view.askNewCompanyAccountData();
        String username = companyData.get("username");

        if (findCompanyByUsername(username) != null || findPersonalByUsername(username) != null) {
            view.showError("Ese nombre de usuario ya esta en uso.");
            return;
        }

        Address address = view.askAddressData("-> Direccion de la Compania:");

        CompanyAccount newCompany = new CompanyAccount(
                companyData.get("companyName"), address, companyData.get("ruc"),
                companyData.get("phone"), companyData.get("email"),
                username, companyData.get("password")
        );

        users.getCompanyAccounts().add(newCompany);
        dataBase.saveUsers(users);

        FinvoryData initialData = new FinvoryData();
        initialData.setCompanyInfo(newCompany);
        dataBase.saveCompanyData(initialData, username);

        view.showMessage("Cuenta de Compania registrada con exito!");
        view.showMessage("Ahora puede iniciar sesion.");
    }

    private void handleRegisterPersonal() {
        HashMap<String, String> personalData = view.askNewPersonalAccountData();
        String username = personalData.get("username");

        if (findCompanyByUsername(username) != null || findPersonalByUsername(username) != null) {
            view.showError("Ese nombre de usuario ya esta en uso.");
            return;
        }

        PersonalAccount newPersonal = new PersonalAccount(
                personalData.get("fullName"), username, personalData.get("password")
        );

        users.getPersonalAccounts().add(newPersonal);
        dataBase.saveUsers(users);
        view.showMessage("Cuenta Personal registrada con exito!");
        view.showMessage("Ahora puede iniciar sesion.");
    }

    private void startMainMenu() {
        boolean running = true;
        while (running) {
            int option = view.showMainMenu();
            switch (option) {
                case 1 ->
                    handleNewSale();
                case 2 ->
                    handleInventoryMenu();
                case 3 ->
                    handleCustomerMenu();
                case 4 ->
                    handleSupplierMenu();
                case 5 ->
                    handleAdminMenu();
                case 6 ->
                    handleReportsMenu();
                case 7 ->
                    handleGenerateQuote();
                case 0 ->
                    running = false;
                default ->
                    view.showError("Opcion no valida.");
            }
        }
        dataBase.saveCompanyData(data, currentCompanyUsername);
    }

    private void startPersonalAccountMenu() {
        boolean running = true;
        while (running) {
            int opt = view.showPersonalAccountMenu();
            switch (opt) {
                case 1 ->
                    handleViewLimitedProducts();
                case 2 ->
                    handleViewCompanyPhones();
                case 0 ->
                    running = false;
                default ->
                    view.showError("Opcion no valida.");
            }
        }
    }

    private void handleViewLimitedProducts() {
        CompanyAccount target = view.chooseCompany(users.getCompanyAccounts());
        if (target != null) {
            FinvoryData tempData = dataBase.loadCompanyData(target.getUsername());
            view.showLimitedProductList(tempData.getProducts());
        }
    }

    private void handleViewCompanyPhones() {
        view.showCompanyPhones(users.getCompanyAccounts());
    }

    private void handleNewSale() {
        String query = view.askCustomerQuery();
        Customer customer = findCustomer(query);

        if (customer == null) {
            if (view.askToCreateNewCustomer(query)) {
                customer = handleCreateCustomer();
                if (customer == null) {
                    view.showError("No se pudo crear el cliente. Venta cancelada.");
                    return;
                }
            } else {
                view.showMessage("Venta cancelada.");
                return;
            }
        }

        view.showMessage("Cliente encontrado: " + customer.getName());

        HashMap<Product, HashMap<Inventory, Integer>> cart = new HashMap<>();

        while (true) {
            String id = view.askProductId();
            if (id.equalsIgnoreCase("fin")) {
                break;
            }

            Product product = findProduct(id);
            if (product == null) {
                continue;
            }

            Inventory inventory = view.chooseInventoryToSellFrom(data.getInventories(), product.getId());
            if (inventory == null) {
                continue;
            }

            int quantity = view.askQuantity(inventory.getStock(product.getId()));
            if (quantity == 0) {
                continue;
            }

            cart.putIfAbsent(product, new HashMap<>());
            cart.get(product).put(inventory, quantity);
            view.showMessage("Anadido: " + product.getName() + " (x" + quantity + ") desde " + inventory.getName());
        }

        if (cart.isEmpty()) {
            view.showMessage("Venta cancelada.");
            return;
        }

        String payment = view.askPaymentMethod();
        String dueDate = "";

        if (payment.equals("CHEQUE POSTFECHADO")) {
            dueDate = view.askPaymentDueDate();
        }

        String invoiceId = "F-" + (data.getInvoices().size() + 1);
        InvoiceSim invoice = new InvoiceSim(invoiceId, customer, payment, dueDate);

        float profitPercentage = data.getProfitPercentage();
        float discountStandard = data.getDiscountStandard();
        float discountPremium = data.getDiscountPremium();
        float discountVip = data.getDiscountVip();

        for (Map.Entry<Product, HashMap<Inventory, Integer>> entry : cart.entrySet()) {
            Product product = entry.getKey();
            for (Map.Entry<Inventory, Integer> stockEntry : entry.getValue().entrySet()) {
                Inventory inventory = stockEntry.getKey();
                int quantity = stockEntry.getValue();

                inventory.removeStock(product.getId(), quantity);
                float price = product.getPrice(customer.getClientType(), profitPercentage, discountStandard, discountPremium, discountVip);
                invoice.addLine(product, quantity, price);
            }
        }

        invoice.calculateTotals(data.getTaxRate());

        if (!payment.equals("CHEQUE POSTFECHADO")) {
            invoice.complete();
        } else {
            view.showMessage("Venta registrada como PENDIENTE DE PAGO (Cheque Postfechado).");
        }

        data.getInvoices().add(invoice);
        view.showSaleSummary(invoice);
    }

    private void handleInventoryMenu() {
        
    }

    private void handleViewProducts() {
        view.showProductList(
                data.getProducts(),
                data.getInventories(),
                data.getObsoleteInventory(),
                data.getProfitPercentage(),
                data.getDiscountStandard(),
                data.getDiscountPremium(),
                data.getDiscountVip()
        );
    }

    private void handleEditProduct() {
        Product product = findProduct(view.askProductId());
        if (product == null) {
            return;
        }

        HashMap<String, String> updates = view.askProductUpdateData(product);
        String newName = updates.get("name");
        if (!newName.isEmpty()) {
            product.setName(newName);
        }
        String newDesc = updates.get("description");
        if (!newDesc.isEmpty()) {
            product.setDescription(newDesc);
        }
        String newSupplierId = updates.get("supplierId");
        if (!newSupplierId.isEmpty()) {
            if (findSupplier(newSupplierId) != null) {
                product.setSupplierId(newSupplierId);
            } else {
                view.showError("ID de Proveedor no encontrado. No se actualizo.");
            }
        }
        try {
            String newCost = updates.get("costPrice");
            if (!newCost.isEmpty()) {
                float cost = Float.parseFloat(newCost);
                if (cost >= 0) {
                    product.setBaseCostPrice(cost);
                } else {
                    view.showError("El costo no puede ser negativo.");
                }
            }
            view.showMessage("Producto actualizado.");
        } catch (NumberFormatException e) {
            view.showError("El costo no es un numero valido.");
        }
    }

    private void handleProcessReturn() {
        Product product = findProduct(view.askProductId());
        if (product == null) {
            return;
        }
        int qty = view.askQuantity(9999);
        if (qty == 0) {
            return;
        }
        String reason = view.askReturnReason();
        data.getObsoleteInventory().addStock(product.getId(), qty);
        data.getReturns().add(new ReturnedProduct(product, qty, reason));
        view.showMessage("Devolucion registrada.");
    }

    private void handleManageObsolete() {
        Product product = findProduct(view.askProductId());
        if (product == null) {
            return;
        }
        int obsoleteStock = data.getObsoleteInventory().getStock(product.getId());
        if (obsoleteStock == 0) {
            view.showMessage("El producto no tiene stock obsoleto.");
            return;
        }
        Address obsoleteLocation = data.getObsoleteInventory().getAddress();
        Inventory targetInventory = view.askObsoleteAction(product.getName(), obsoleteStock, obsoleteLocation, data.getInventories());

        if (targetInventory != null) {
            data.getObsoleteInventory().removeStock(product.getId(), obsoleteStock);
            targetInventory.addStock(product.getId(), obsoleteStock);
            view.showMessage("Stock reasignado a " + targetInventory.getName());
        } else {
            data.getObsoleteInventory().removeStock(product.getId(), obsoleteStock);
            view.showMessage("Stock obsoleto desechado.");
        }
    }

    private void handleAddStockToInventory() {
        Product product = findProduct(view.askProductId());
        if (product == null) {
            view.showError("Producto no encontrado.");
            return;
        }

        if (data.getInventories().isEmpty()) {
            view.showError("No hay inventarios creados para agregar stock.");
            return;
        }

        Inventory targetInventory = view.chooseInventory(data.getInventories());
        if (targetInventory == null) {
            view.showError("Accion cancelada.");
            return;
        }

        int quantity = view.askQuantity(Integer.MAX_VALUE);
        if (quantity > 0) {
            targetInventory.addStock(product.getId(), quantity);
            view.showMessage("Se agregaron " + quantity + " unidades de '" + product.getName() + "' a " + targetInventory.getName());
        } else {
            view.showMessage("Stock no modificado.");
        }
    }

    private void handleCustomerMenu() {
        FrmCustomers customersWindow = new FrmCustomers(this);
        customersWindow.setVisible(true);
    }

    private void handleRegisterPayment() {
        view.showMessage("Cargando facturas pendientes...");
        ArrayList<InvoiceSim> pendingInvoices = new ArrayList<>();
        for (InvoiceSim inv : data.getInvoices()) {
            if (inv.getStatus().equals("PENDING")) {
                pendingInvoices.add(inv);
            }
        }
        InvoiceSim invoiceToPay = view.choosePendingInvoice(pendingInvoices);
        if (invoiceToPay == null) {
            view.showMessage("Accion cancelada.");
            return;
        }
        if (view.askConfirmation("Confirma el pago de la factura? (s/n)")) {
            invoiceToPay.complete();
            view.showMessage("Pago registrado.");
        }
    }

    private void handleViewCustomers() {
        view.showCustomerList(data.getCustomers());
    }

    private Customer handleCreateCustomer() {
        HashMap<String, String> customerData = view.askNewCustomerData();
        String id = customerData.get("id");
        if (findCustomer(id) != null) {
            view.showError("Un cliente con esa identificacion ya existe.");
            return null;
        }
        Customer newCustomer = new Customer(
                customerData.get("name"), id, customerData.get("phone"),
                customerData.get("email"), customerData.get("clientType")
        );
        this.data.getCustomers().add(newCustomer);
        view.showMessage("Cliente creado con exito!");
        return newCustomer;
    }

    private void handleEditCustomer() {
        Customer customer = findCustomer(view.askCustomerId());
        if (customer == null) {
            view.showError("Cliente no encontrado.");
            return;
        }
        HashMap<String, String> updates = view.askCustomerUpdateData(customer);
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
        view.showMessage("Cliente actualizado.");
    }

    public boolean handleDeleteCustomerGUI(String customerId) {
        Customer customer = findCustomer(customerId);
        if (customer == null) {
            view.showError("Cliente no encontrado.");
            return false;
        }
        for (InvoiceSim invoiceSim : data.getInvoices()) {
            if (invoiceSim.getCustomer().getIdentification().equals(customer.getIdentification())) {
                view.showError("No se puede eliminar. El cliente tiene facturas.");
                return false;
            }
        }
        data.getCustomers().remove(customer);
        saveData();
        return true;
    }

    private void handleSupplierMenu() {
        handleViewSuppliers();
    }

    private void handleViewSuppliers() {
        view.showSupplierList(data.getSuppliers());
    }

    private Supplier handleCreateSupplier() {
        HashMap<String, String> supplierData = view.askNewSupplierData();
        String id1 = supplierData.get("id1");
        if (findSupplier(id1) != null) {
            view.showError("Un proveedor con ese ID ya existe.");
            return null;
        }
        Supplier supplier = new Supplier(
                supplierData.get("name"), id1, supplierData.get("phone"),
                supplierData.get("email"), supplierData.get("description")
        );
        supplier.setId2(supplierData.get("id2"));
        data.getSuppliers().add(supplier);
        view.showMessage("Proveedor creado con exito!");
        return supplier;
    }

    private void handleEditSupplier() {
        Supplier supplier = findSupplier(view.askSupplierId());
        if (supplier == null) {
            view.showError("Proveedor no encontrado.");
            return;
        }
        HashMap<String, String> updates = view.askSupplierUpdateData(supplier);
        if (!updates.get("name").isEmpty()) {
            supplier.setFullName(updates.get("name"));
        }
        if (!updates.get("phone").isEmpty()) {
            supplier.setPhone(updates.get("phone"));
        }
        if (!updates.get("email").isEmpty()) {
            supplier.setEmail(updates.get("email"));
        }
        if (!updates.get("description").isEmpty()) {
            supplier.setDescription(updates.get("description"));
        }
        supplier.setId2(updates.get("id2"));
        view.showMessage("Proveedor actualizado.");
    }

    private void handleDeleteSupplier() {
        Supplier supplier = findSupplier(view.askSupplierId());
        if (supplier == null) {
            view.showError("Proveedor no encontrado.");
            return;
        }
        for (Product product : data.getProducts()) {
            if (product.getSupplierId() != null && product.getSupplierId().equals(supplier.getId1())) {
                view.showError("No se puede eliminar. El proveedor esta en uso.");
                return;
            }
        }
        if (view.askConfirmation("Seguro que desea eliminar? (s/n)")) {
            data.getSuppliers().remove(supplier);
            view.showMessage("Proveedor eliminado.");
        }
    }

    private void handleAdminMenu() {
        boolean running = true;
        while (running) {
            int opt = view.showAdminMenu();
            switch (opt) {
                case 1 ->
                    handleSetTaxRate();
                case 2 ->
                    handleSetPriceAlgorithm();
                case 3 ->
                    handleCreateProduct();
                case 4 ->
                    handleCreateInventory();
                case 0 ->
                    running = false;
                default ->
                    view.showError("Opcion no valida.");
            }
        }
    }

    private void handleCreateInventory() {
        String name = view.askNewInventoryName();
        for (Inventory inv : data.getInventories()) {
            if (inv.getName().equalsIgnoreCase(name)) {
                view.showError("Nombre de inventario duplicado.");
                return;
            }
        }
        Address address = view.askAddressData("-> Direccion del Nuevo Inventario:");
        Inventory newInv = new Inventory(name, address);
        data.getInventories().add(newInv);
        view.showMessage("Inventario creado.");
    }

    private void handleSetTaxRate() {
        float newRate = view.askNewTaxRate(data.getTaxRate());
        data.setTaxRate(newRate);

        saveData();
        view.showMessage("Impuesto actualizado.");
    }

    private void handleCreateProduct() {
        HashMap<String, String> productData = view.askNewProductData();
        String id = productData.get("id");
        String barcode = productData.get("barcode");

        if (findProduct(id) != null) {
            view.showError("ID de producto duplicado.");
            return;
        }
        if (findProductByBarcode(barcode) != null) {
            view.showError("Codigo de barras duplicado.");
            return;
        }

        Supplier supplier = view.chooseSupplier(data.getSuppliers());
        if (supplier == null) {
            supplier = handleCreateSupplier();
            if (supplier == null) {
                return;
            }
        }

        Inventory targetInventory = view.chooseInventory(data.getInventories());
        if (targetInventory == null) {
            view.showError("Cree un inventario primero.");
            return;
        }

        try {
            float costPrice = Float.parseFloat(productData.get("costPrice"));
            int stock = Integer.parseInt(productData.get("stock"));

            Product newProduct = new Product(
                    id, productData.get("name"), productData.get("description"),
                    barcode, costPrice, supplier.getId1()
            );
            data.getProducts().add(newProduct);
            targetInventory.setStock(id, stock);
            view.showMessage("Producto creado!");
        } catch (NumberFormatException e) {
            view.showError("Error en formato de numero.");
        }
    }

    private void handleSetPriceAlgorithm() {
        view.showMessage("Configurando algoritmo de precios...");
        HashMap<String, Float> percentages = view.askPriceAlgorithmData(data);

        data.setProfitPercentage(percentages.get("profit"));
        data.setDiscountStandard(percentages.get("discountStandard"));
        data.setDiscountPremium(percentages.get("discountPremium"));
        data.setDiscountVip(percentages.get("discountVip"));
        saveData();
    }

    private void handleReportsMenu() {
        boolean running = true;
        while (running) {
            int option = view.showReportsMenu();
            switch (option) {
                case 1 ->
                    view.showDashboard(data.getTotalGrossDay(), data.getTotalGrossProfile());
                case 2 -> {
                    HashMap<String, Integer> report = generateSalesOrDemandReport();
                    view.showReport("Reporte de Ventas por Producto (Unidades)", report);
                }
                case 3 -> {
                    HashMap<String, Integer> report = new HashMap<>();
                    for (InvoiceSim i : data.getInvoices()) {
                        if ("COMPLETED".equals(i.getStatus())) {
                            String name = i.getCustomer().getName();
                            report.put(name, report.getOrDefault(name, 0) + 1);
                        }
                    }
                    view.showReport("Reporte de Actividad de Clientes (N° Facturas)", report);
                }
                case 4 -> {
                    HashMap<String, Integer> report = generateSupplierDemandReport();
                    view.showReport("Reporte de Demanda de Proveedores", report);
                }
                case 5 ->
                    handleExportReports();
                case 0 ->
                    running = false;
                default ->
                    view.showError("Opcion no valida.");
            }
        }
    }

    private void handleExportReports() {
        int reportType = view.askReportToExport();
        HashMap<String, ? extends Object> reportData = new HashMap<>();
        String title = "";

        switch (reportType) {
            case 1:
                reportData = generateSalesOrDemandReport();
                title = "DemandReport";
                break;
            case 2:
                reportData = generateCustomerReportData();
                title = "CustomerPurchaseReport";
                break;
            case 3:
                reportData = generateSupplierDemandReport();
                title = "SupplierDemandReport";
                break;
            default:
                view.showError("Seleccion de reporte invalida.");
                return;
        }

        boolean success = dataBase.exportToCsv(title, reportData, currentCompanyUsername);
        if (success) {
            view.showMessage("Reporte '" + title + ".csv' exportado con exito.");
        } else {
            view.showError("Fallo la exportacion del reporte.");
        }
    }

    private HashMap<String, Integer> generateSalesOrDemandReport() {
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

    private HashMap<String, Float> generateCustomerReportData() {
        HashMap<String, Float> map = new HashMap<>();
        for (InvoiceSim i : data.getInvoices()) {
            if ("COMPLETED".equals(i.getStatus())) {
                map.put(i.getCustomer().getName(), map.getOrDefault(i.getCustomer().getName(), 0f) + i.getTotal());
            }
        }
        return map;
    }

    private HashMap<String, Integer> generateSupplierDemandReport() {
        HashMap<String, Integer> map = new HashMap<>();
        for (InvoiceSim i : data.getInvoices()) {
            if ("COMPLETED".equals(i.getStatus())) {
                for (InvoiceLineSim line : i.getLines()) {
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

    private void handleGenerateQuote() {
        view.showMessage("Generando Cotizacion...");
        String query = view.askCustomerQuery();
        Customer customer = findCustomer(query);
        if (customer == null) {
            if (view.askToCreateNewCustomer(query)) {
                customer = handleCreateCustomer();
                if (customer == null) {
                    return;
                }
            } else {
                return;
            }
        }

        InvoiceSim quote = new InvoiceSim("COT-" + System.currentTimeMillis(), customer, "COTIZACION", "");
        float profit = data.getProfitPercentage();
        float dStd = data.getDiscountStandard();
        float dPrm = data.getDiscountPremium();
        float dVip = data.getDiscountVip();

        while (true) {
            String id = view.askProductId();
            if (id.equalsIgnoreCase("fin")) {
                break;
            }
            Product product = findProduct(id);
            if (product == null) {
                continue;
            }
            view.chooseInventoryToSellFrom(data.getInventories(), product.getId());

            System.out.print("-> Cantidad a cotizar: ");
            int quantity = -1;
            try {
                quantity = view.askQuantity(999999);
            } catch (Exception e) {
                quantity = 0;
            }
            if (quantity <= 0) {
                continue;
            }

            float price = product.getPrice(customer.getClientType(), profit, dStd, dPrm, dVip);
            quote.addLine(product, quantity, price);
            view.showMessage("Agregado: " + product.getName());
        }

        if (quote.getLines().isEmpty()) {
            return;
        }
        quote.calculateTotals(data.getTaxRate());

        String filename = "data/" + currentCompanyUsername + "/cotizacion_" + customer.getName().replaceAll("\\s+", "_") + ".txt";
        try (PrintWriter out = new PrintWriter(new FileWriter(new File(filename)))) {
            out.println("COTIZACION - FINVORY");
            out.println("Cliente: " + customer.getName());
            out.println("----------------------");
            for (InvoiceLineSim line : quote.getLines()) {
                out.printf("%-20s %d $%.2f%n", line.getProductName(), line.getQuantity(), line.getLineTotal());
            }
            out.printf("TOTAL: $%.2f%n", quote.getTotal());
            view.showMessage("Archivo guardado: " + filename);
        } catch (IOException e) {
            view.showError("Error al guardar archivo.");
        }
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

    private Customer findCustomer(String format) {
        if (format == null) {
            return null;
        }
        format = format.toLowerCase();
        for (Customer c : data.getCustomers()) {
            if (format.equals(c.getIdentification()) || (c.getName() != null && c.getName().toLowerCase().contains(format))) {
                return c;
            }
        }
        return null;
    }

    private Supplier findSupplier(String id) {
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

    private CompanyAccount findCompanyByUsername(String usernameCompany) {
        if (usernameCompany == null) {
            return null;
        }
        for (CompanyAccount c : users.getCompanyAccounts()) {
            if (usernameCompany.equals(c.getUsername())) {
                return c;
            }
        }
        return null;
    }

    private PersonalAccount findPersonalByUsername(String username) {
        if (username == null) {
            return null;
        }
        for (PersonalAccount personalAccount : users.getPersonalAccounts()) {
            if (username.equals(personalAccount.getUsername())) {
                return personalAccount;
            }
        }
        return null;
    }

    public Inventory findInventoryByName(String name) {
        if (this.data == null || this.data.getInventories() == null) {
            return null;
        }

        for (Inventory inv : this.data.getInventories()) {
            if (inv.getName().equalsIgnoreCase(name)) {
                return inv;
            }
        }
        return null;
    }

    public ArrayList<Inventory> findInventoriesByPartialName(String query) {
        ArrayList<Inventory> matches = new ArrayList<>();

        String queryNormalized = query.trim().toLowerCase();

        for (Inventory inv : this.data.getInventories()) {
            if (inv.getName() != null && inv.getName().toLowerCase().contains(queryNormalized)) {
                matches.add(inv);
            }
        }
        return matches;
    }
}
