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
    private Database db;      

    public FinvoryController(FinvoryData data, FinvoryView view, Database db) {
        this.data = data;
        this.view = view;
        this.db = db;
    }
    
    public void run() {
        boolean running = true;
        while (running) {
            int opt = view.showStartMenu();
            switch (opt) {
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
        
        db.save(data);
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
            int opt = view.showRegistrationMenu();
            switch(opt) {
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
        
        Address addr = view.askAddressData("-> Direccion de la Compania:");
        
        CompanyAccount newCompany = new CompanyAccount(
            companyData.get("companyName"),
            addr,
            ruc,
            companyData.get("phone"),
            companyData.get("email"),
            username,
            companyData.get("password")
        );
        
        data.getCompanyAccounts().add(newCompany);
        db.save(data);
        view.showMessage("¡Cuenta de Compania registrada con exito!");
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
        db.save(data);
        view.showMessage("¡Cuenta Personal registrada con exito!");
        view.showMessage("Ahora puede iniciar sesion.");
    }


    private void startMainMenu() {
        boolean running = true;
        while (running) {
            int opt = view.showMainMenu();
            switch (opt) {
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
            int opt = view.showPersonalAccountMenu();
            switch (opt) {
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
            if (id.equalsIgnoreCase("fin")) break;
            
            Product p = findProduct(id);
            if (p == null) continue;
            
            Inventory inv = view.chooseInventoryToSellFrom(data.getInventories(), p.getId());
            if (inv == null) continue;
            
            int qty = view.askQuantity(inv.getStock(p.getId()));
            if (qty == 0) continue;
            
            cart.putIfAbsent(p, new HashMap<>());
            cart.get(p).put(inv, qty);
            view.showMessage("Anadido: " + p.getName() + " (x" + qty + ") desde " + inv.getName());
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
        
        float profit = data.getProfitPercentage();
        float dStd = data.getDiscountStandard();
        float dPrm = data.getDiscountPremium();
        float dVip = data.getDiscountVip();
        
        for (Map.Entry<Product, HashMap<Inventory, Integer>> entry : cart.entrySet()) {
            Product p = entry.getKey();
            for (Map.Entry<Inventory, Integer> stockEntry : entry.getValue().entrySet()) {
                Inventory inv = stockEntry.getKey();
                int qty = stockEntry.getValue();
                
                inv.removeStock(p.getId(), qty);
                float price = p.getPrice(customer.getClientType(), profit, dStd, dPrm, dVip);
                invoice.addLine(p, qty, price);
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
        boolean running = true;
        while (running) {
            int opt = view.showInventoryMenu();
            switch (opt) {
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
        Product p = findProduct(view.askProductId());
        if (p == null) return;

        HashMap<String, String> updates = view.askProductUpdateData(p);
        
        String newName = updates.get("name");
        if (!newName.isEmpty()) p.setName(newName);
        
        String newDesc = updates.get("description");
        if (!newDesc.isEmpty()) p.setDescription(newDesc);
        
        String newSupplierId = updates.get("supplierId");
        if (!newSupplierId.isEmpty()) {
            if (findSupplier(newSupplierId) != null) {
                p.setSupplierId(newSupplierId);
            } else {
                view.showError("ID de Proveedor no encontrado. No se actualizo.");
            }
        }
        
        try {
            String newCost = updates.get("costPrice");
            if (!newCost.isEmpty()) {
                float cost = Float.parseFloat(newCost);
                if (cost >= 0) {
                    p.setBaseCostPrice(cost);
                } else {
                    view.showError("El costo no puede ser negativo.");
                }
            }
            view.showMessage("Producto actualizado.");
        } catch (NumberFormatException e) {
            view.showError("El costo no es un numero valido. No se actualizo el costo.");
        }
    }

    private void handleDeleteProduct() {
        Product p = findProduct(view.askProductId());
        if (p == null) return;
        
        for (InvoiceSim inv : data.getInvoices()) {
            for (InvoiceLineSim line : inv.getLines()) {
                if (line.getProductId().equals(p.getId())) {
                    view.showError("No se puede eliminar. El producto '" + p.getName() + "' ya esta en una factura (" + inv.getId() + ").");
                    return;
                }
            }
        }
        
        if (view.askConfirmation("¿Seguro que desea eliminar '" + p.getName() + "'? (s/n)")) {
            data.getProducts().remove(p);
            for (Inventory inv : data.getInventories()) {
                inv.removeStock(p.getId(), inv.getStock(p.getId()));
            }
            data.getObsoleteInventory().removeStock(p.getId(), data.getObsoleteInventory().getStock(p.getId()));
            
            view.showMessage("Producto eliminado.");
        }
    }

    private void handleProcessReturn() {
        Product p = findProduct(view.askProductId());
        if (p == null) return;
        
        int qty = view.askQuantity(9999);
        if (qty == 0) return;
        
        String reason = view.askReturnReason();

        data.getObsoleteInventory().addStock(p.getId(), qty);
        data.getReturns().add(new ReturnedProduct(p, qty, reason));

        view.showMessage("Devolucion registrada.");
        view.showMessage("Stock obsoleto de '" + p.getName() + "': " + data.getObsoleteInventory().getStock(p.getId()));
    }

    private void handleManageObsolete() {
        Product p = findProduct(view.askProductId());
        if (p == null) return;
        
        int obsoleteStock = data.getObsoleteInventory().getStock(p.getId());
        if (obsoleteStock == 0) {
            view.showMessage("El producto no tiene stock obsoleto.");
            return;
        }
        
        Address obsoleteLocation = data.getObsoleteInventory().getAddress();
        Inventory targetInventory = view.askObsoleteAction(p.getName(), obsoleteStock, obsoleteLocation, data.getInventories());
        
        if (targetInventory != null) { 
            data.getObsoleteInventory().removeStock(p.getId(), obsoleteStock);
            targetInventory.addStock(p.getId(), obsoleteStock);
            view.showMessage("Stock reasignado a " + targetInventory.getName());
        } else { 
            data.getObsoleteInventory().removeStock(p.getId(), obsoleteStock);
            view.showMessage("Stock obsoleto desechado.");
        }
    }

    private void handleCustomerMenu() {
        boolean running = true;
        while (running) {
            int opt = view.showCustomerMenu();
            switch (opt) {
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
        
        if (view.askConfirmation("¿Confirma el pago de la factura " + invoiceToPay.getId() + " por $" + invoiceToPay.getTotal() + "? (s/n)")) {
            invoiceToPay.complete();
            view.showMessage("¡Pago registrado! La factura esta ahora COMPLETED.");
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
        view.showMessage("¡Cliente '" + newCustomer.getName() + "' creado con exito!");
        return newCustomer;
    }
    
    private void handleEditCustomer() {
        Customer c = findCustomer(view.askCustomerId());
        if (c == null) {
            view.showError("Cliente no encontrado.");
            return;
        }
        
        HashMap<String, String> updates = view.askCustomerUpdateData(c);
        
        if (!updates.get("name").isEmpty()) c.setName(updates.get("name"));
        if (!updates.get("phone").isEmpty()) c.setPhone(updates.get("phone"));
        if (!updates.get("email").isEmpty()) c.setEmail(updates.get("email"));
        c.setClientType(updates.get("clientType")); 
        
        view.showMessage("Cliente actualizado.");
    }
    
    private void handleDeleteCustomer() {
        Customer c = findCustomer(view.askCustomerId());
        if (c == null) {
            view.showError("Cliente no encontrado.");
            return;
        }
        
        for (InvoiceSim inv : data.getInvoices()) {
            if (inv.getCustomer().getIdentification().equals(c.getIdentification())) {
                view.showError("No se puede eliminar. El cliente '" + c.getName() + "' tiene facturas asociadas (" + inv.getId() + ").");
                return;
            }
        }
        
        if (view.askConfirmation("¿Seguro que desea eliminar a '" + c.getName() + "'? (s/n)")) {
            data.getCustomers().remove(c);
            view.showMessage("Cliente eliminado.");
        }
    }

    private void handleSupplierMenu() {
        boolean running = true;
        while (running) {
            int opt = view.showSupplierMenu();
            switch (opt) {
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
        
        Supplier s = new Supplier(
            supplierData.get("name"), id1, supplierData.get("phone"),
            supplierData.get("email"), supplierData.get("description")
        );
        s.setId2(supplierData.get("id2"));
        
        data.getSuppliers().add(s);
        view.showMessage("¡Proveedor '" + s.getFullName() + "' creado con exito!");
        return s;
    }
    
    private void handleEditSupplier() {
        Supplier s = findSupplier(view.askSupplierId());
        if (s == null) {
            view.showError("Proveedor no encontrado.");
            return;
        }
        
        HashMap<String, String> updates = view.askSupplierUpdateData(s);
        
        if (!updates.get("name").isEmpty()) s.setFullName(updates.get("name"));
        if (!updates.get("phone").isEmpty()) s.setPhone(updates.get("phone"));
        if (!updates.get("email").isEmpty()) s.setEmail(updates.get("email"));
        if (!updates.get("description").isEmpty()) s.setDescription(updates.get("description"));
        s.setId2(updates.get("id2"));
        
        view.showMessage("Proveedor actualizado.");
    }
    
    private void handleDeleteSupplier() {
        Supplier s = findSupplier(view.askSupplierId());
        if (s == null) {
            view.showError("Proveedor no encontrado.");
            return;
        }
        
        for (Product p : data.getProducts()) {
            if (p.getSupplierId() != null && p.getSupplierId().equals(s.getId1())) {
                view.showError("No se puede eliminar. El proveedor '" + s.getFullName() + "' esta asignado al producto '" + p.getName() + "'.");
                return;
            }
        }
        
        if (view.askConfirmation("¿Seguro que desea eliminar a '" + s.getFullName() + "'? (s/n)")) {
            data.getSuppliers().remove(s);
            view.showMessage("Proveedor eliminado.");
        }
    }
    
    private void handleAdminMenu() {
        boolean running = true;
        while (running) {
            int opt = view.showAdminMenu(); 
            switch (opt) {
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
        Address addr = view.askAddressData("-> Direccion del Nuevo Inventario:");
        Inventory newInv = new Inventory(name, addr);
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
            
            view.showMessage("¡Producto '" + newProduct.getName() + "' creado con exito!");
            
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
        
        view.showMessage("¡Algoritmo de precios actualizado exitosamente!");
    }
    
    private void handleReportsMenu() {
        boolean running = true;
        while (running) {
            int opt = view.showReportsMenu();
            switch (opt) {
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
        HashMap<String, Integer> salesByProduct = new HashMap<>();
        for (InvoiceSim invoice : data.getInvoices()) {
            if (!invoice.getStatus().equals("COMPLETED")) continue;
            
            for (InvoiceLineSim line : invoice.getLines()) {
                String productName = line.getProductName();
                int qty = line.getQuantity();
                salesByProduct.put(productName, salesByProduct.getOrDefault(productName, 0) + qty);
            }
        }
        view.showReport("Reporte de Ventas por Producto (Unidades)", salesByProduct);
    }
    
    private void handleCustomerReport() {
        HashMap<String, Integer> salesByCustomer = new HashMap<>();
        for (InvoiceSim invoice : data.getInvoices()) {
            if (!invoice.getStatus().equals("COMPLETED")) continue;
            
            String customerName = invoice.getCustomer().getName();
            salesByCustomer.put(customerName, salesByCustomer.getOrDefault(customerName, 0) + 1);
        }
        view.showReport("Reporte de Actividad de Clientes (N° Facturas)", salesByCustomer);
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
        for (Product p : data.getProducts()) {
            if (id.equals(p.getId())) return p;
        }
        return null;
    }
    
    private Product findProductByBarcode(String barcode) {
        if (barcode == null || barcode.isEmpty()) return null;
        for (Product p : data.getProducts()) {
            if (p.getBarcode() != null && p.getBarcode().equals(barcode)) return p;
        }
        return null;
    }

    private Customer findCustomer(String query) {
        if (query == null || query.isEmpty()) return null;
        query = query.toLowerCase();
        for (Customer c : data.getCustomers()) {
            if (query.equals(c.getIdentification()) || 
                (c.getName() != null && c.getName().toLowerCase().contains(query))) {
                return c;
            }
        }
        return null;
    }
    
    private Supplier findSupplier(String id1) {
        if (id1 == null || id1.isEmpty()) return null;
        for (Supplier s : data.getSuppliers()) {
            if (id1.equals(s.getId1())) return s;
        }
        return null;
    }
    
    private CompanyAccount findCompanyByUsername(String username) {
        if (username == null || username.isEmpty()) return null;
        for (CompanyAccount c : data.getCompanyAccounts()) {
            if (username.equals(c.getUsername())) return c;
        }
        return null;
    }
    
    private PersonalAccount findPersonalByUsername(String username) {
        if (username == null || username.isEmpty()) return null;
        for (PersonalAccount p : data.getPersonalAccounts()) {
            if (username.equals(p.getUsername())) return p;
        }
        return null;
    }
}