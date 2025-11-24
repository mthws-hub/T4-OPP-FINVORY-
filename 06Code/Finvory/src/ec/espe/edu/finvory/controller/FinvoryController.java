package ec.espe.edu.finvory.controller;

import ec.espe.edu.finvory.model.*;
import ec.espe.edu.finvory.view.FinvoryView;
import java.util.ArrayList; 
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author Arelys Otavalo, The POOwer Rangers of Programming
 */

public class FinvoryController {

    private FinvoryData data; 
    private FinvoryView view; 
    private Database database;      

    public FinvoryController(FinvoryData data, FinvoryView view, Database database) {
        this.data = data;
        this.view = view;
        this.database = database;
    }
    
    public void run() {
        boolean running = true;
        while (running) {
            int optionion = view.showStartMenu();
            switch (optionion) {
                case 1:
                    String userRole = handleLogin();
                    if (userRole.equals("COMPANY")) {
                        startMainMenu();
                    } else if (userRole.equals("PERSONAL")) {
                        startPersonalAccountMenu();
                    }
                    break;
                case 2:
                    handleRegistrationMenu();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    view.showError("Opcion no valida.");
                    break;
            }
        }
        
        database.save(data);
        view.showMessage("Datos guardados. Saliendo del sistema...");
    }

    private String handleLogin() {
        String input = view.showLogin();
        String[] parts = input.split(":");
        String username = parts[0];
        String password = parts[1];
        
        for (CompanyAccount company : data.getCompanyAccounts()) {
            if (company.getUsername().equals(username) && company.checkPassword(password)) {
                view.showMessage("Bienvenido (Compania) " + company.getName());
                return "COMPANY";
            }
        }
        
        for (PersonalAccount personal : data.getPersonalAccounts()) {
            if (personal.getUsername().equals(username) && personal.checkPassword(password)) {
                view.showMessage("Bienvenido (Personal) " + personal.getFullName());
                return "PERSONAL";
            }
        }
        
        view.showError("Credenciales incorrectas.");
        return "INVALID";
    }
    
    private void handleRegistrationMenu() {
        boolean running = true;
        while (running) {
            int option = view.showRegistrationMenu();
            switch(option) {
                case 1:
                    handleRegisterCompany();
                    break;
                case 2:
                    handleRegisterPersonal();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    view.showError("Opcion no valida.");
                    break;
            }
        }
    }
    
    private void handleRegisterCompany() {
        HashMap<String, String> companyData = view.askNewCompanyAccountData();
        String username = companyData.get("username");
        String ruc = companyData.get("ruc");
        
        if (findCompanyByUsername(username) != null || findPersonalByUsername(username) != null) {
            view.showError("Ese nombre de usuario ya esta en uso. Intente con otro.");
            return;
        }
        for (CompanyAccount c : data.getCompanyAccounts()) {
            if (c.getRuc().equals(ruc)) {
                view.showError("Una compania con ese RUC ya esta registrada.");
                return;
            }
        }
        
        Address address = view.askAddressData("-> Direccion de la Compania:");
        
        CompanyAccount newCompany = new CompanyAccount(
            companyData.get("companyName"),
            address,
            ruc,
            companyData.get("phone"),
            companyData.get("email"),
            username,
            companyData.get("password")
        );
        
        data.getCompanyAccounts().add(newCompany);
        database.save(data);
        view.showMessage("Cuenta de Compania registrada con exito");
        view.showMessage("Ahora puede iniciar sesion.");
    }

    private void handleRegisterPersonal() {
        HashMap<String, String> personalData = view.askNewPersonalAccountData();
        String username = personalData.get("username");
        
        if (findCompanyByUsername(username) != null || findPersonalByUsername(username) != null) {
            view.showError("Ese nombre de usuario ya esta en uso. Intente con otro.");
            return;
        }
        
        PersonalAccount newPersonal = new PersonalAccount(
            personalData.get("fullName"),
            username,
            personalData.get("password")
        );
        
        data.getPersonalAccounts().add(newPersonal);
        database.save(data);
        view.showMessage("¡Cuenta Personal registrada con exito!");
        view.showMessage("Ahora puede iniciar sesion.");
    }


    private void startMainMenu() {
        boolean running = true;
        while (running) {
            int option = view.showMainMenu();
            switch (option) {
                case 1:
                    handleNewSale();
                    break;
                case 2:
                    handleInventoryMenu();
                    break;
                case 3:
                    handleCustomerMenu();
                    break;
                case 4:
                    handleSupplierMenu();
                    break;
                case 5:
                    handleAdminMenu();
                    break;
                case 6:
                    handleReportsMenu();
                    break;
                case 7:
                    view.showQuoteEmailPlaceholder();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    view.showError("Opcion no valida.");
                    break;
            }
        }
    }
    
    private void startPersonalAccountMenu() {
        boolean running = true;
        while (running) {
            int option = view.showPersonalAccountMenu();
            switch (option) {
                case 1:
                    handleViewLimitedProducts();
                    break;
                case 2:
                    handleViewCompanyPhones();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    view.showError("Opcion no valida.");
                    break;
            }
        }
    }
    
    private void handleViewLimitedProducts() {
        view.showLimitedProductList(data.getProducts());
    }
    
    private void handleViewCompanyPhones() {
        view.showCompanyPhones(data.getCompanyAccounts());
    }

    private void handleNewSale() {
        String query = view.askCustomerQuery();
        ArrayList<Customer> matches = findAllMatchingCustomers(query);
        Customer customer = null;
        
        if (matches.isEmpty()) {
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
        } else if(matches.size() == 1){
            customer = matches.get(0);
        } else {
            customer = view.ambiguousCustomerSearch(matches);
            if (customer == null) {
                view.showMessage("Selección cancelada. Venta cancelada.");
                return;
            }
        }
        view.showMessage("Cliente encontrado: " + customer.getName());
        
        HashMap<Product, HashMap<Inventory, Integer>> cart = new HashMap<>();
        
        while (true) {
            String id = view.askProductId();
            if (id.equalsIgnoreCase("fin")) break;
            
            Product product = findProduct(id);
            if (product == null) continue;
            
            Inventory inventory = view.chooseInventoryToSellFrom(data.getInventories(), product.getId());
            if (inventory == null) continue;
            
            int quantity = view.askQuantity(inventory.getStock(product.getId()));
            if (quantity == 0) continue;
            
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
        
        String finalInvoiceDate = view.askTestDate();
        String invoiceId = "F-" + (data.getInvoices().size() + 1);
        InvoiceSim invoice = new InvoiceSim(invoiceId, customer, payment, dueDate);
        invoice.setDate(finalInvoiceDate);
       
        
        float profit = data.getProfitPercentage();
        float discountStandard = data.getDiscountStandard();
        float discountPremium = data.getDiscountPremium();
        float discountVip = data.getDiscountVip();
        
        for (Map.Entry<Product, HashMap<Inventory, Integer>> entry : cart.entrySet()) {
            Product product = entry.getKey();
            for (Map.Entry<Inventory, Integer> stockEntry : entry.getValue().entrySet()) {
                Inventory inv = stockEntry.getKey();
                int qty = stockEntry.getValue();
                
                inv.removeStock(product.getId(), qty);
                float price = product.getPrice(customer.getClientType(), profit, discountStandard, discountPremium, discountVip);
                invoice.addLine(product, qty, price);
            }
        }
        
        invoice.calculateTotals(data.getTaxRate());
        
        if (!payment.equals("CHEQUE POSTFECHADO")) {
            invoice.complete(); 
        } else {
            view.showMessage("Venta registrada como PENDIENTE DE PAGO.");
        }
        
        data.getInvoices().add(invoice);
        view.showSaleSummary(invoice);
    }

    private void handleInventoryMenu() {
        boolean running = true;
        while (running) {
            int option = view.showInventoryMenu();
            switch (option) {
                case 1:
                    handleViewProducts();
                    break;
                case 2:
                    handleEditProduct();
                    break;
                case 3:
                    handleDeleteProduct();
                    break;
                case 4:
                    handleProcessReturn();
                    break;
                case 5:
                    handleManageObsolete();
                    break;
                case 6:
                    handleAddStockToInventory(); 
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    view.showError("Opcion no valida.");
                    break;
            }
        }
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
        if (product == null) return;

        HashMap<String, String> updates = view.askProductUpdateData(product);
        
        String newName = updates.get("name");
        if (!newName.isEmpty()) product.setName(newName);
        
        String newDesc = updates.get("description");
        if (!newDesc.isEmpty()) product.setDescription(newDesc);
        
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
        } catch (NumberFormatException error) {
            view.showError("El costo no es un numero valido. No se actualizo el costo.");
        }
    }

    private void handleDeleteProduct() {
        Product product = findProduct(view.askProductId());
        if (product == null) return;
        
        for (InvoiceSim inv : data.getInvoices()) {
            for (InvoiceLineSim line : inv.getLines()) {
                if (line.getProductId().equals(product.getId())) {
                    view.showError("No se puede eliminar. El producto '" + product.getName() + "' ya esta en una factura (" + inv.getId() + ").");
                    return;
                }
            }
        }
        
        if (view.askConfirmation("Seguro que desea eliminar ? '" + product.getName() + "'? (s/n)")) {
            data.getProducts().remove(product);
            for (Inventory inv : data.getInventories()) {
                inv.removeStock(product.getId(), inv.getStock(product.getId()));
            }
            data.getObsoleteInventory().removeStock(product.getId(), data.getObsoleteInventory().getStock(product.getId()));
            
            view.showMessage("Producto eliminado.");
        }
    }

    private void handleProcessReturn() {
        Product product = findProduct(view.askProductId());
        if (product == null){
            view.showError("Producto no encontrado. No se puede registrar la devolucion.");
            return;
        }
        
        int qty = view.askQuantity(9999);
        if (qty == 0) return;
        
        String reason = view.askReturnReason();

        data.getObsoleteInventory().addStock(product.getId(), qty);
        data.getReturns().add(new ReturnedProduct(product, qty, reason));

        view.showMessage("Devolucion registrada.");
        view.showMessage("Stock obsoleto de '" + product.getName() + "': " + data.getObsoleteInventory().getStock(product.getId()));
    }

    private void handleManageObsolete() {
        Product product = findProduct(view.askProductId());
        if (product == null) return;
        
        int obsoleteStock = data.getObsoleteInventory().getStock(product.getId());
        if (obsoleteStock == 0) {
            view.showMessage("El producto no tiene stock obsoleto.");
            return;
        }
        
        Address obsoleteLocation = data.getObsoleteInventory().getAddress();
        Inventory targetInventory = view.askObsoleteAction(product.getName(), obsoleteStock, obsoleteLocation, data.getInventories());
        
        if (targetInventory != null && targetInventory.getName().equals("CANCEL")) {
        view.showMessage("Accion cancelada.");
        return;
        }
        if (targetInventory != null) { 
            data.getObsoleteInventory().removeStock(product.getId(), obsoleteStock);
            targetInventory.addStock(product.getId(), obsoleteStock);
            view.showMessage("Stock reasignado a " + targetInventory.getName());
        } else { 
            data.getObsoleteInventory().removeStock(product.getId(), obsoleteStock);
            view.showMessage("Stock obsoleto desechado.");
        }
    }

    private void handleCustomerMenu() {
        boolean running = true;
        while (running) {
            int option = view.showCustomerMenu();
            switch (option) {
                case 1:
                    handleCreateCustomer();
                    break;
                case 2:
                    handleViewCustomers();
                    break;
                case 3:
                    handleEditCustomer();
                    break;
                case 4:
                    handleDeleteCustomer();
                    break;
                case 5:
                    handleRegisterPayment();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    view.showError("Opcion no valida.");
                    break;
            }
        }
    }
    
    private void handleRegisterPayment() {
        view.showMessage("Cargando facturas pendientes (Cheques Postfechados)...");
        
        ArrayList<InvoiceSim> pendingInvoices = new ArrayList<>();
        for (InvoiceSim invoiceSim : data.getInvoices()) {
            if (invoiceSim.getStatus().equals("PENDING")) {
                pendingInvoices.add(invoiceSim);
            }
        }
        
        InvoiceSim invoiceToPay = view.choosePendingInvoice(pendingInvoices);
        
        if (invoiceToPay == null) {
            view.showMessage("Accion cancelada.");
            return;
        }
        
        if (view.askConfirmation("Confirma el pago de la factura? " + invoiceToPay.getId() + " por $" + invoiceToPay.getTotal() + "? (s/n)")) {
            invoiceToPay.complete();
            view.showMessage("Pago registrado. La factura esta ahora COMPLETED.");
        } else {
            view.showMessage("Accion cancelada.");
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
        view.showMessage("Cliente '" + newCustomer.getName() + "' creado con exito!");
        return newCustomer;
    }
    
    private void handleEditCustomer() {
        Customer customer = findCustomer(view.askCustomerId());
        if (customer == null) {
            view.showError("Cliente no encontrado.");
            return;
        }
        
        HashMap<String, String> updates = view.askCustomerUpdateData(customer);
        
        if (!updates.get("name").isEmpty()) customer.setName(updates.get("name"));
        if (!updates.get("phone").isEmpty()) customer.setPhone(updates.get("phone"));
        if (!updates.get("email").isEmpty()) customer.setEmail(updates.get("email"));
        customer.setClientType(updates.get("clientType")); 
        
        view.showMessage("Cliente actualizado.");
    }
    
    private void handleDeleteCustomer() {
        Customer customer = findCustomer(view.askCustomerId());
        if (customer == null) {
            view.showError("Cliente no encontrado.");
            return;
        }
        
        for (InvoiceSim inv : data.getInvoices()) {
            if (inv.getCustomer().getIdentification().equals(customer.getIdentification())) {
                view.showError("No se puede eliminar. El cliente '" + customer.getName() + "' tiene facturas asociadas (" + inv.getId() + ").");
                return;
            }
        }
        
        if (view.askConfirmation("Seguro que desea eliminar a '" + customer.getName() + "'? (s/n)")) {
            data.getCustomers().remove(customer);
            view.showMessage("Cliente eliminado.");
        }
    }

    private void handleSupplierMenu() {
        boolean running = true;
        while (running) {
            int option = view.showSupplierMenu();
            switch (option) {
                case 1:
                    handleCreateSupplier();
                    break;
                case 2:
                    handleViewSuppliers();
                    break;
                case 3:
                    handleEditSupplier();
                    break;
                case 4:
                    handleDeleteSupplier();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    view.showError("Opcion no valida.");
                    break;
            }
        }
    }
    
    private void handleViewSuppliers() {
        view.showSupplierList(data.getSuppliers());
    }
    
    private Supplier handleCreateSupplier() {
        HashMap<String, String> supplierData = view.askNewSupplierData();
        String id1 = supplierData.get("id1");
        
        if (findSupplier(id1) != null) {
            view.showError("Un proveedor con ese ID 1 (RUC) ya existe.");
            return null;
        }
        
        Supplier supplier = new Supplier(
            supplierData.get("name"), id1, supplierData.get("phone"),
            supplierData.get("email"), supplierData.get("description")
        );
        supplier.setId2(supplierData.get("id2"));
        
        data.getSuppliers().add(supplier);
        view.showMessage("Proveedor '" + supplier.getFullName() + "' creado con exito!");
        return supplier;
    }
    
    private void handleEditSupplier() {
        Supplier supplier = findSupplier(view.askSupplierId());
        if (supplier == null) {
            view.showError("Proveedor no encontrado.");
            return;
        }
        
        HashMap<String, String> updates = view.askSupplierUpdateData(supplier);
        
        if (!updates.get("name").isEmpty()) supplier.setFullName(updates.get("name"));
        if (!updates.get("phone").isEmpty()) supplier.setPhone(updates.get("phone"));
        if (!updates.get("email").isEmpty()) supplier.setEmail(updates.get("email"));
        if (!updates.get("description").isEmpty()) supplier.setDescription(updates.get("description"));
        supplier.setId2(updates.get("id2"));
        
        view.showMessage("Proveedor actualizado.");
    }
    
    private void handleDeleteSupplier() {
        Supplier supplier = findSupplier(view.askSupplierId());
        if (supplier == null) {
            view.showError("Proveedor no encontrado.");
            return;
        }
        
        for (Product p : data.getProducts()) {
            if (p.getSupplierId() != null && p.getSupplierId().equals(supplier.getId1())) {
                view.showError("No se puede eliminar. El proveedor '" + supplier.getFullName() + "' esta asignado al producto '" + p.getName() + "'.");
                return;
            }
        }
        
        if (view.askConfirmation("Seguro que desea eliminar a '" + supplier.getFullName() + "'? (s/n)")) {
            data.getSuppliers().remove(supplier);
            view.showMessage("Proveedor eliminado.");
        }
    }
    
    private void handleAdminMenu() {
        boolean running = true;
        while (running) {
            int option = view.showAdminMenu(); 
            switch (option) {
                case 1:
                    handleSetTaxRate();
                    break;
                case 2:
                    handleSetPriceAlgorithm();
                    break;
                case 3:
                    handleCreateProduct();
                    break;
                case 4:
                    handleCreateInventory();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    view.showError("Opcion no valida.");
                    break;
            }
        }
    }
    
    private void handleCreateInventory() {
        String name = view.askNewInventoryName();
        for (Inventory inv : data.getInventories()) {
            if (inv.getName().equalsIgnoreCase(name)) {
                view.showError("Un inventario con ese nombre ya existe.");
                return;
            }
        }
        Address address = view.askAddressData("-> Direccion del Nuevo Inventario:");
        Inventory newInv = new Inventory(name, address);
        data.getInventories().add(newInv);
        view.showMessage("Inventario '" + name + "' creado con exito.");
    }
    
    private void handleSetTaxRate() {
        float newRate = view.askNewTaxRate(data.getTaxRate());
        data.setTaxRate(newRate);
        view.showMessage("Impuesto actualizado a " + (newRate * 100) + "%");
    }
    
    private void handleCreateProduct() {
        HashMap<String, String> productData = view.askNewProductData();
        String id = productData.get("id");
        String barcode = productData.get("barcode");
        
        if (findProduct(id) != null) {
            view.showError("Un producto con ese ID ya existe.");
            return;
        }
        if (findProductByBarcode(barcode) != null) {
            view.showError("Un producto con ese Codigo de Barras ya existe.");
            return;
        }
        
        Supplier supplier = view.chooseSupplier(data.getSuppliers());
        if (supplier == null) {
            view.showMessage("El producto necesita un proveedor. Por favor, cree uno.");
            supplier = handleCreateSupplier();
            if (supplier == null) {
                view.showError("Creacion de producto cancelada.");
                return;
            }
        }
        
        Inventory targetInventory = view.chooseInventory(data.getInventories());
        if (targetInventory == null) {
            view.showError("No hay inventarios creados. Cancele y cree un inventario primero.");
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
            
            view.showMessage("Producto '" + newProduct.getName() + "' creado con exito!");
            
        } catch (NumberFormatException e) {
            view.showError("Error en el formato del precio o stock.");
        }
    }
    
    private void handleSetPriceAlgorithm() {
        view.showMessage("Configurando porcentajes globales (aplicara a todos los productos).");
        HashMap<String, Float> percentages = view.askPriceAlgorithmData(data);
        
        data.setProfitPercentage(percentages.get("profit"));
        data.setDiscountStandard(percentages.get("discountStandard"));
        data.setDiscountPremium(percentages.get("discountPremium"));
        data.setDiscountVip(percentages.get("discountVip"));
        
        view.showMessage("Algoritmo de precios actualizado exitosamente");
    }
    
    private void handleReportsMenu() {
        boolean running = true;
        while (running) {
            int option = view.showReportsMenu();
            switch (option) {
                case 1:
                    handleReports(); 
                    break;
                case 2:
                    handleSalesReport();
                    break;
                case 3:
                    handleCustomerReport();
                    break;
                case 4:
                    handleSupplierReport();
                    break;
                case 5:
                    handleExportReports(); 
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    view.showError("Opcion no valida.");
                    break;
            }
        }
    }

    private void handleReports() {
        view.showDashboard(data.getTotalGrossDay(), data.getTotalGrossProfile());
    }
    
    private void handleSalesReport() {
        HashMap<String, Integer> salesByMonth = new HashMap<>();
        for (InvoiceSim invoice : data.getInvoices()) {
            if (!invoice.getStatus().equals("COMPLETED")) continue;
            
            String monthYear = invoice.getDate().substring(0, 7);
            int totalUnits = 0;
            
            for (InvoiceLineSim line : invoice.getLines()) {
                totalUnits += line.getQuantity();
            }
            salesByMonth.put(monthYear, salesByMonth.getOrDefault(monthYear, 0) + totalUnits);
        }
        view.showReport("Reporte de Demanda (Unidades Vendidas por Mes)", salesByMonth);
    }
    
    private void handleCustomerReport() {
        HashMap<String, Float> salesByCustomer = new HashMap<>();
        for (InvoiceSim invoice : data.getInvoices()) {
            if (!invoice.getStatus().equals("COMPLETED")) continue;
            
            String customerName = invoice.getCustomer().getName();
            float currentTotal = salesByCustomer.getOrDefault(customerName, 0.0f);
            salesByCustomer.put(customerName, currentTotal + invoice.getTotal());
        }
        view.showFloatReport("Reporte de Compra de Clientes (Volumen Total)", salesByCustomer);
    }
    
    private void handleSupplierReport() {
        HashMap<String, Integer> salesBySupplier = new HashMap<>();
        for (InvoiceSim invoice : data.getInvoices()) {
            if (!invoice.getStatus().equals("COMPLETED")) continue;
            
            for (InvoiceLineSim line : invoice.getLines()) {
                Product p = findProduct(line.getProductId());
                if (p == null) continue;
                
                Supplier s = findSupplier(p.getSupplierId());
                if (s == null) continue;

                String supplierName = s.getFullName();
                int qty = line.getQuantity();
                salesBySupplier.put(supplierName, salesBySupplier.getOrDefault(supplierName, 0) + qty);
            }
        }
        view.showReport("Reporte de Demanda de Proveedores (Unidades Vendidas)", salesBySupplier);
    }

    
    private Product findProduct(String id) {
        if (id == null || id.isEmpty()) return null;
        for (Product product : data.getProducts()) {
            if (id.equalsIgnoreCase(product.getId())) {
                return product;
            }
        }
        return null;
    }
    
    private Product findProductByBarcode(String barcode) {
        if (barcode == null || barcode.isEmpty()) return null;
        for (Product product : data.getProducts()) {
            if (product.getBarcode() != null && product.getBarcode().equalsIgnoreCase(barcode)) {
                return product;
            }
        }
        return null;
    }

    private Customer findCustomer(String query) {
        if (query == null || query.isEmpty()) return null;
        for (Customer customer : data.getCustomers()) {
            if (query.equalsIgnoreCase(customer.getIdentification())) {
                return customer;
            }

            if (customer.getName() != null && 
                customer.getName().toLowerCase().contains(query.toLowerCase())) {
                return customer;
            }
        }
        return null;
    }
    
    private Supplier findSupplier(String id1) {
        if (id1 == null || id1.isEmpty()) return null;
        for (Supplier supplier : data.getSuppliers()) {
            if (id1.equals(supplier.getId1())) return supplier;
        }
        return null;
    }
    
    private CompanyAccount findCompanyByUsername(String username) {
        if (username == null || username.isEmpty()) return null;
        for (CompanyAccount companyAccount : data.getCompanyAccounts()) {
            if (username.equals(companyAccount.getUsername())) return companyAccount;
        }
        return null;
    }
    
    private PersonalAccount findPersonalByUsername(String username) {
        if (username == null || username.isEmpty()) return null;
        for (PersonalAccount personalAccount : data.getPersonalAccounts()) {
            if (username.equals(personalAccount.getUsername())) return personalAccount;
        }
        return null;
    }
    
    private ArrayList<Customer> findAllMatchingCustomers(String query) {
        ArrayList<Customer> matches = new ArrayList<>();
        if (query == null || query.isEmpty()) return matches;
        
        for (Customer customer : data.getCustomers()) {
            if (query.equalsIgnoreCase(customer.getIdentification())) {
                matches.add(customer);
            }
            if (customer.getName() != null && 
                customer.getName().toLowerCase().contains(query.toLowerCase())) {
                matches.add(customer);
            }
        }
        return matches;
    }
    private HashMap<String, Integer> generateSalesOrDemandReport() {
        HashMap<String, Integer> salesByMonth = new HashMap<>();
        for (InvoiceSim invoice : data.getInvoices()) {
            if (!invoice.getStatus().equals("COMPLETED")) continue;
        
            String monthYear = invoice.getDate().substring(0, 7); 
        
                int totalUnits = 0;
        for (InvoiceLineSim line : invoice.getLines()) {
            totalUnits += line.getQuantity();
        }
        salesByMonth.put(monthYear, salesByMonth.getOrDefault(monthYear, 0) + totalUnits);
        }
    return salesByMonth;
    }

    private HashMap<String, Float> generateCustomerReportData() {
        HashMap<String, Float> salesByCustomer = new HashMap<>();
        for (InvoiceSim invoice : data.getInvoices()) {
            if (!invoice.getStatus().equals("COMPLETED")) continue;
        
            String customerName = invoice.getCustomer().getName();
            float currentTotal = salesByCustomer.getOrDefault(customerName, 0.0f);
            salesByCustomer.put(customerName, currentTotal + invoice.getTotal());
        }
        return salesByCustomer;
    }

    private HashMap<String, Integer> generateSupplierDemandReport() {
        HashMap<String, Integer> salesBySupplier = new HashMap<>();
        for (InvoiceSim invoice : data.getInvoices()) {
            if (!invoice.getStatus().equals("COMPLETED")) continue;
        
            for (InvoiceLineSim line : invoice.getLines()) {
                Product p = findProduct(line.getProductId());
                if (p == null) continue;
            
                Supplier s = findSupplier(p.getSupplierId());
                if (s == null) continue;

                String supplierName = s.getFullName();
                int qty = line.getQuantity();
                salesBySupplier.put(supplierName, salesBySupplier.getOrDefault(supplierName, 0) + qty);
            }
        }
        return salesBySupplier;
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
            view.showError("Acción cancelada.");
            return;
        }

        int quantity = view.askQuantity(Integer.MAX_VALUE); // No hay límite superior al comprar
        if (quantity > 0) {
            targetInventory.addStock(product.getId(), quantity);
            view.showMessage("Se agregaron " + quantity + " unidades de '" + product.getName() + "' a " + targetInventory.getName());
        } else {
            view.showMessage("Stock no modificado.");
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
        boolean success = database.exportToCsv(title, reportData);
        if (success) {
            view.showMessage("Reporte '" + title + ".csv' exportado con exito en la carpeta 'data'.");
        } else {
            view.showError("Fallo la exportacion del reporte.");
        }
    }
    
        
}