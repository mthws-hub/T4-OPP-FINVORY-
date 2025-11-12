package ec.edu.espe.finvory.controller;

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
                case 1 -> {
                    String userRole = handleLogin(); 
                    if (userRole.equals("COMPANY")) {
                        startMainMenu(); 
                    } else if (userRole.equals("PERSONAL")) {
                        startPersonalAccountMenu(); 
                    }
                }
                case 2 -> handleRegistrationMenu(); 
                case 0 -> running = false;
                default -> view.showError("Opcion no valida.");
            }
        }
        view.showMessage("Saliendo del sistema...");
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
                case 1 -> handleRegisterCompany();
                case 2 -> handleRegisterPersonal();
                case 0 -> running = false;
                default -> view.showError("Opcion no valida.");
            }
        }
    }
    
    private void handleRegisterCompany() {
        HashMap<String, String> companyData = view.askNewCompanyAccountData();
        Address addr = view.askAddressData("-> Direccion de la Compania:");
        CompanyAccount newCompany = new CompanyAccount(
            companyData.get("companyName"), addr, companyData.get("ruc"),
            companyData.get("phone"), companyData.get("email"),
            companyData.get("username"), companyData.get("password")
        );
        data.getCompanyAccounts().add(newCompany);
        db.save(data); 
        view.showMessage("¡Cuenta de Compania registrada con exito!");
    }

    private void handleRegisterPersonal() {
        HashMap<String, String> personalData = view.askNewPersonalAccountData();
        PersonalAccount newPersonal = new PersonalAccount(
            personalData.get("fullName"), personalData.get("username"),
            personalData.get("password")
        );
        data.getPersonalAccounts().add(newPersonal);
        db.save(data);
        view.showMessage("¡Cuenta Personal registrada con exito!");
    }

    private void startMainMenu() {
        boolean running = true;
        while (running) {
            int opt = view.showMainMenu();
            switch (opt) {
                case 1 -> handleNewSale();
                case 2 -> handleInventoryMenu();
                case 3 -> handleCustomerMenu();
                case 4 -> handleSupplierMenu();
                case 5 -> handleAdminMenu();
                case 6 -> view.showError("Funcionalidad AUN NO IMPLEMENTADA.");
                case 7 -> view.showQuoteEmailPlaceholder();
                case 0 -> running = false;
                default -> view.showError("Opcion no valida.");
            }
        }
        db.save(data);
    }
    
    private void startPersonalAccountMenu() {
        boolean running = true;
        while (running) {
            int opt = view.showPersonalAccountMenu();
            switch (opt) {
                case 1 -> handleViewLimitedProducts();
                case 2 -> handleViewCompanyPhones();
                case 0 -> running = false;
                default -> view.showError("Opcion no valida.");
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
        
        float profit = 0.0f; // (Logica de precios aun no implementada)
        float dStd = 0.0f;
        float dPrm = 0.0f;
        float dVip = 0.0f;
        
        for (Map.Entry<Product, HashMap<Inventory, Integer>> entry : cart.entrySet()) {
            Product p = entry.getKey();
            for (Map.Entry<Inventory, Integer> stockEntry : entry.getValue().entrySet()) {
                Inventory inv = stockEntry.getKey();
                int qty = stockEntry.getValue();
                
                inv.removeStock(p.getId(), qty);
                
                // Usa el precio de costo, ya que el algoritmo de precios es de otro commit
                float price = p.getBaseCostPrice(); 
                invoice.addLine(p, qty, price);
            }
        }
        
        invoice.calculateTotals(0.15f); // (Tasa de impuesto aun no implementada)
        
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
                case 1 -> handleViewProducts();
                case 2 -> handleEditProduct();
                case 3 -> handleDeleteProduct();
                case 4 -> view.showError("Funcionalidad AUN NO IMPLEMENTADA.");
                case 5 -> view.showError("Funcionalidad AUN NO IMPLEMENTADA.");
                case 0 -> running = false;
                default -> view.showError("Opcion no valida.");
            }
        }
    }

    private void handleViewProducts() {
        view.showProductList(data.getProducts());
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
        if (!newSupplierId.isEmpty()) p.setSupplierId(newSupplierId);
        
        try {
            String newCost = updates.get("costPrice");
            if (!newCost.isEmpty()) {
                p.setBaseCostPrice(Float.parseFloat(newCost));
            }
            view.showMessage("Producto actualizado.");
        } catch (NumberFormatException e) {
            view.showError("El costo no es un numero valido. No se actualizo el costo.");
        }
    }

    private void handleDeleteProduct() {
        Product p = findProduct(view.askProductId());
        if (p == null) return;
        
        if (view.askConfirmation("¿Seguro que desea eliminar '" + p.getName() + "'? (s/n)")) {
            data.getProducts().remove(p);
            view.showMessage("Producto eliminado.");
        }
    }

    private void handleCustomerMenu() {
        boolean running = true;
        while (running) {
            int opt = view.showCustomerMenu();
            switch (opt) {
                case 1 -> handleCreateCustomer();
                case 2 -> handleViewCustomers();
                case 3 -> handleEditCustomer();
                case 4 -> handleDeleteCustomer();
                case 5 -> handleRegisterPayment();
                case 0 -> running = false;
                default -> view.showError("Opcion no valida.");
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
                case 1 -> handleCreateSupplier();
                case 2 -> handleViewSuppliers();
                case 3 -> handleEditSupplier();
                case 4 -> handleDeleteSupplier();
                case 0 -> running = false;
                default -> view.showError("Opcion no valida.");
            }
        }
    }
    
    private void handleViewSuppliers() {
        view.showSupplierList(data.getSuppliers());
    }
    
    private Supplier handleCreateSupplier() {
        HashMap<String, String> supplierData = view.askNewSupplierData();
        String id1 = supplierData.get("id1");
        
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
                case 1 -> view.showError("Funcionalidad AUN NO IMPLEMENTADA.");
                case 2 -> view.showError("Funcionalidad AUN NO IMPLEMENTADA.");
                case 3 -> handleCreateProduct();
                case 4 -> handleCreateInventory();
                case 0 -> running = false;
                default -> view.showError("Opcion no valida.");
            }
        }
    }
    
    private void handleCreateInventory() {
        String name = view.askNewInventoryName();
        Address addr = view.askAddressData("-> Direccion del Nuevo Inventario:");
        Inventory newInv = new Inventory(name, addr);
        data.getInventories().add(newInv);
        view.showMessage("Inventario '" + name + "' creado con exito.");
    }
    
    private void handleCreateProduct() {
        HashMap<String, String> productData = view.askNewProductData();
        String id = productData.get("id");
        String barcode = productData.get("barcode");
        
        Supplier supplier = view.chooseSupplier(data.getSuppliers());
        if (supplier == null) {
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
    
    private Product findProduct(String id) {
        for (Product p : data.getProducts()) {
            if (p.getId().equals(id)) return p;
        }
        return null;
    }
    
    private Product findProductByBarcode(String barcode) {
        for (Product p : data.getProducts()) {
            if (p.getBarcode() != null && p.getBarcode().equals(barcode)) return p;
        }
        return null;
    }

    private Customer findCustomer(String query) {
        for (Customer c : data.getCustomers()) {
            if (c.getIdentification().equals(query) || c.getName().toLowerCase().contains(query.toLowerCase())) {
                return c;
            }
        }
        return null;
    }
    
    private Supplier findSupplier(String id1) {
        for (Supplier s : data.getSuppliers()) {
            if (s.getId1().equals(id1)) return s;
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