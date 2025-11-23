package ec.espe.edu.finvory.view;

import ec.espe.edu.finvory.model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.DateTimeException;
import java.util.regex.Pattern;
/**
 *
 * @author Arelys Otavalo, The POOwer Rangers of Programming
 */

public class FinvoryView {

    private Scanner scanner = new Scanner(System.in);

    private static final Pattern REGEX_EMAIL = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    private static final Pattern REGEX_PHONE = Pattern.compile("^\\+?[0-9\\s]{7,15}$");
    private static final Pattern REGEX_ID = Pattern.compile("^\\d{10}$|^\\d{13}$");
    private static final Pattern REGEX_NUMERIC = Pattern.compile("^-?[0-9]+(\\.[0-9]+)?$");

    public int showStartMenu() {
        System.out.println("\n--- BIENVENIDO A FINVORY ---");
        System.out.println("1. Iniciar Sesion");
        System.out.println("2. Registrar Nueva Cuenta");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opcion: ");
        return getIntInput();
    }
    
    public int showRegistrationMenu() {
        System.out.println("\n--- TIPO DE REGISTRO ---");
        System.out.println("1. Registrar Cuenta de Compania (Admin)");
        System.out.println("2. Registrar Cuenta Personal (Invitado)");
        System.out.println("0. Volver");
        System.out.print("Seleccione una opcion: ");
        return getIntInput();
    }

    public String showLogin() {
        System.out.println("--- INICIAR SESION ---");
        String user = getValidatedStringInput("Usuario: ", null, "", true);
        String pass = getValidatedStringInput("Contrasena: ", null, "", true);
        return user + ":" + pass;
    }

    public int showMainMenu() {
        System.out.println("\n--- MENU PRINCIPAL (Company Account) ---");
        System.out.println("1. Nueva Venta (Facturar)");
        System.out.println("2. Gestionar Inventario y Productos");
        System.out.println("3. Gestionar Clientes");
        System.out.println("4. Gestionar Proveedores");
        System.out.println("5. Administracion");
        System.out.println("6. Ver Reportes y Dashboard");
        System.out.println("7. Generar Cotizacion");
        System.out.println("0. Guardar y Salir");
        System.out.print("Seleccione una opcion: ");
        return getIntInput();
    }
    
    public int showPersonalAccountMenu() {
        System.out.println("\n--- MENU PERSONAL ---");
        System.out.println("1. Ver Catalogo de Productos");
        System.out.println("2. Ver Telefono de Companias");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opcion: ");
        return getIntInput();
    }
    
    public void showLimitedProductList(ArrayList<Product> products) {
        System.out.println("\n--- CATALOGO DE PRODUCTOS ---");
        if (products.isEmpty()) {
            System.out.println("No hay productos registrados.");
            return;
        }
        String format = "| %-25s | %-40s |";
        System.out.println(String.format(format, "Producto", "Descripcion"));
        System.out.println(new String(new char[70]).replace("\0", "-"));
        for (Product p : products) {
            System.out.println(String.format(format, p.getName(), p.getDescription()));
        }
    }
    
    public void showCompanyPhones(ArrayList<CompanyAccount> companies) {
        System.out.println("\n--- CONTACTO DE COMPANIAS ---");
        if (companies.isEmpty()) {
            System.out.println("No hay companias registradas en el sistema.");
            return;
        }
        String format = "| %-30s | %-15s |";
        System.out.println(String.format(format, "Compania", "Telefono"));
        System.out.println(new String(new char[50]).replace("\0", "-"));
        for (CompanyAccount c : companies) {
            System.out.println(String.format(format, c.getName(), c.getPhone()));
        }
    }

    public String askCustomerQuery() {
        return getMandatoryStringInput("-> Busque al cliente (Nombre o ID): ");
    }
    
    public boolean askToCreateNewCustomer(String query) {
        showError("Cliente '" + query + "' no encontrado.");
        return askConfirmation("¿Desea registrarlo como nuevo cliente ahora? (s/n)");
    }
    
    public String askProductId() {
        return getMandatoryStringInput("-> Ingrese ID del producto o el codigo de barras (o 'fin' para terminar): ");
    }
    
    public Inventory chooseInventoryToSellFrom(ArrayList<Inventory> inventories, String productId) {
        System.out.println("-> Seleccione el inventario de origen:");
        ArrayList<Inventory> available = new ArrayList<>();
        
        for (Inventory inventory : inventories) {
            if (inventory.getStock(productId) > 0) {
                available.add(inventory);
                System.out.println("   " + available.size() + ". " + inventory.getName() + " (Stock: " + inventory.getStock(productId) + ")");
            }
        }
        
        if (available.isEmpty()) {
            showError("No hay stock de este producto en ningun inventario.");
            return null;
        }
        
        int option = -1;
        while (option < 1 || option > available.size()) {
            option = getPositiveIntInput("Opcion: ");
        }
        return available.get(option - 1);
    }
    
    public int askQuantity(int maxStock) {
        int quantity = -1;
        while (quantity < 0 || quantity > maxStock) {
            quantity = getPositiveIntInput("-> Cantidad (Max: " + maxStock + "): ");
            if (quantity > maxStock) {
                showError("Stock insuficiente.");
                quantity = -1;
            }
        }
        return quantity;
    }
    
    public String askPaymentMethod() {
        System.out.println("-> Metodo de pago:");
        System.out.println("   1. CASH (Efectivo)");
        System.out.println("   2. TRANSFER (Transferencia)");
        System.out.println("   3. CHEQUE"); 
        System.out.print("Opcion: ");
        int option = getIntInput();
        if (option == 1) return "CASH";
        if (option == 2) return "TRANSFER";
        if (option == 3) return "CHEQUE";
        System.err.println("ERROR: Opcion de pago no valida. Por defecto se selecciono CASH.");
        return "CASH";
    }

    public String askPaymentDueDate() {
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();

        while (true) {
            String paymentDateString = getMandatoryStringInput("-> Ingrese la Fecha de Cobro (dd/mm/aaaa): ");
            String[] parts = paymentDateString.split("/");
            if (parts.length != 3) {
                showError("Formato incorrecto. Use dd/mm/aaaa.");
                continue;
            }
            try {
                int day = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int year = Integer.parseInt(parts[2]);
                if (year < currentYear) {
                    showError("El anio no puede ser menor al anio actual (" + currentYear + ").");
                    continue;
                }
                if (month < 1 || month > 12) {
                    showError("Mes invalido. Debe estar entre 01 y 12.");
                    continue;
                }
                LocalDate chosenDate;
                try {
                    chosenDate = LocalDate.of(year, month, day); 
                } catch (DateTimeException error) {
                    showError("Dia invalido para el mes y ano seleccionados.");
                    continue;
                }
                if (chosenDate.isBefore(today)) {
                    showError("La fecha de cobro no puede ser un dia que ya paso.");
                    continue;
                }
                return paymentDateString;
            } catch (NumberFormatException error) {
                showError("La fecha debe contener solo numeros (dd/mm/aaaa).");
            }
        }
    }

    public void showSaleSummary(InvoiceSim invoice) {
        System.out.println("\n--- RESUMEN DE VENTA (Factura: " + invoice.getId() + ") ---");
        System.out.println("Cliente: " + invoice.getCustomer().getName());
        String format = "| %-25s | %-8s | %-10s |";
        System.out.println(String.format(format, "Producto", "Cantidad", "Total Linea"));
        System.out.println(new String(new char[51]).replace("\0", "-"));
        for (InvoiceLineSim line : invoice.getLines()) {
            System.out.println(String.format(format,
                line.getProductName(),
                line.getQuantity(),
                "$" + String.format("%.2f", line.getLineTotal())
            ));
        }
        System.out.println(new String(new char[51]).replace("\0", "-"));
        System.out.println("Subtotal: $" + String.format("%.2f", invoice.getSubtotal()));
        System.out.println("Impuesto: $" + String.format("%.2f", invoice.getTax()));
        System.out.println("TOTAL VENTA: $" + String.format("%.2f", invoice.getTotal()));
        System.out.println("Estado: " + invoice.getStatus());
        if (invoice.getStatus().equals("PENDING")) {
            System.out.println("Fecha de Cobro: " + invoice.getPaymentDueDate());
        }
    }

    public int showInventoryMenu() {
        System.out.println("\n--- GESTION DE INVENTARIO Y PRODUCTOS ---");
        System.out.println("1. Ver Lista de Productos");
        System.out.println("2. Editar Producto");
        System.out.println("3. Eliminar Producto");
        System.out.println("4. Registrar Devolucion (a Obsoletos)");
        System.out.println("5. Gestionar Stock Obsoleto");
        System.out.println("0. Volver al Menu Principal");
        System.out.print("Seleccione una opcion: ");
        return getIntInput();
    }

    public void showProductList(ArrayList<Product> products, ArrayList<Inventory> inventories, InventoryOfObsolete obsoleteInventory, float profit, float discountStandard, float discountPremium, float discountVip) {
        System.out.println("\n--- LISTA DE PRODUCTOS REGISTRADOS ---");
        if (products.isEmpty()) {
            System.out.println("No hay productos registrados.");
            return;
        }
        String format = "| %-5s | %-20s | %-15s | %-7s | %-7s | %-7s | %-7s | %-6s | %-6s |";
        System.out.println(String.format(format, "ID", "Nombre", "Barcode", "Costo", "P.Std", "P.Prm", "P.Vip", "S(Main)", "S(Obs)"));
        System.out.println(new String(new char[98]).replace("\0", "-"));
        
        for (Product product : products) {
            float productStandard = product.getPrice("STANDARD", profit, discountStandard, discountPremium, discountVip);
            float productPremium = product.getPrice("PREMIUM", profit, discountStandard, discountPremium, discountVip);
            float productVip = product.getPrice("VIP", profit, discountStandard, discountPremium, discountVip);
            
            int totalMainStock = 0;
            for (Inventory inventory : inventories) {
                totalMainStock += inventory.getStock(product.getId());
            }
            int totalObsoleteStock = obsoleteInventory.getStock(product.getId());
            
            System.out.println(String.format(format,
                product.getId(), product.getName(), product.getBarcode(),
                "$" + product.getBaseCostPrice(),
                "$" + String.format("%.2f", productStandard),
                "$" + String.format("%.2f", productPremium),
                "$" + String.format("%.2f", productVip),
                totalMainStock, totalObsoleteStock
            ));
        }
    }
    
    public HashMap<String, String> askProductUpdateData(Product product) {
        System.out.println("\n--- EDITANDO PRODUCTO: " + product.getName() + " ---");
        System.out.println("Deje el campo vacio para no cambiar el valor.");
        HashMap<String, String> data = new HashMap<>();
        
        data.put("name", getValidatedStringInput("Nombre (" + product.getName() + "): ", null, "", false));
        data.put("description", getValidatedStringInput("Descripcion (" + product.getDescription() + "): ", null, "", false));
        data.put("costPrice", getValidatedStringInput("Costo (" + product.getBaseCostPrice() + "): ", REGEX_NUMERIC, "Debe ser un numero.", false));
        data.put("supplierId", getValidatedStringInput("ID Proveedor (" + product.getSupplierId() + "): ", REGEX_ID, "Debe ser un RUC (13 digitos).", false));
        
        return data;
    }

    public String askReturnReason() {
        System.out.println("-> Motivo de la devolucion:");
        System.out.println("   1. DEFECTIVE (Defectuoso)");
        System.out.println("   2. WRONG_PURCHASE (Compra incorrecta)");
        int option = getIntInput();
        return (option == 1) ? "DEFECTIVE" : "WRONG_PURCHASE";
    }

    public Inventory askObsoleteAction(String productName, int obsoleteStock, Address obsoleteLocation, ArrayList<Inventory> inventories) {
        System.out.println("-> El producto '" + productName + "' tiene " + obsoleteStock + " unidades obsoletas.");
        System.out.println("   Ubicacion Obsoletos: " + obsoleteLocation.toString());
        System.out.println("¿Que desea hacer?");
        System.out.println("   1. Desechar todo el stock obsoleto");
        
        int i = 2;
        for (Inventory inventory : inventories) {
            System.out.println("   " + i + ". Reasignar a " + inventory.getName());
            i++;
        }
        System.out.println("   0. Cancelar");
        
        int option = getIntInput();
        if (option == 1) return null; 
        if (option > 1 && option <= inventories.size() + 1) {
            return inventories.get(option - 2);
        }
        return null;
    }

    public int showCustomerMenu() {
        System.out.println("\n--- GESTION DE CLIENTES ---");
        System.out.println("1. Crear Nuevo Cliente");
        System.out.println("2. Ver Clientes Registrados");
        System.out.println("3. Editar Cliente");
        System.out.println("4. Eliminar Cliente");
        System.out.println("5. Registrar Pago de Factura");
        System.out.println("0. Volver al Menu Principal");
        System.out.print("Seleccione una opcion: ");
        return getIntInput();
    }
    
    public InvoiceSim choosePendingInvoice(ArrayList<InvoiceSim> pendingInvoices) {
        System.out.println("\n--- FACTURAS PENDIENTES DE PAGO ---");
        if (pendingInvoices.isEmpty()) {
            showMessage("No hay facturas pendientes.");
            return null;
        }
        
        String format = "| %-3s | %-10s | %-25s | %-10s | %-12s |";
        System.out.println(String.format(format, "N°", "Factura ID", "Cliente", "Total", "Fecha Cobro"));
        System.out.println(new String(new char[68]).replace("\0", "-"));
        
        int i = 1;
        for (InvoiceSim invoice : pendingInvoices) {
            System.out.println(String.format(format,
                i,
                invoice.getId(),
                invoice.getCustomer().getName(),
                "$" + String.format("%.2f", invoice.getTotal()),
                invoice.getPaymentDueDate()
            ));
            i++;
        }
        System.out.println("0. Cancelar");
        
        int option = -1;
        while (option < 0 || option > pendingInvoices.size()) {
            option = getIntInput();
            if (option < 0 || option > pendingInvoices.size()) {
                showError("Opcion no valida.");
            }
        }
        
        if (option == 0) return null;
        return pendingInvoices.get(option - 1);
    }
    
    public String askCustomerId() {
        return getValidatedStringInput("-> Ingrese el ID (RUC/Cedula) del cliente: ", REGEX_ID, "Debe ser una Cedula (10) o RUC (13 digitos).", true);
    }

    public void showCustomerList(ArrayList<Customer> customers) {
        System.out.println("\n--- LISTA DE CLIENTES REGISTRADOS ---");
        if (customers.isEmpty()) { System.out.println("No hay clientes registrados."); return; }
        
        String format = "| %-13s | %-25s | %-25s | %-10s |";
        System.out.println(String.format(format, "ID (RUC/CI)", "Nombre", "Email", "Tipo"));
        System.out.println(new String(new char[80]).replace("\0", "-"));
        for (Customer customer : customers) {
            System.out.println(String.format(format, customer.getIdentification(), customer.getName(), customer.getEmail(), customer.getClientType()));
        }
    }

    public HashMap<String, String> askNewCustomerData() {
        HashMap<String, String> data = new HashMap<>();
        System.out.println("\n--- REGISTRO DE NUEVO CLIENTE ---");
        data.put("name", getValidatedStringInput("-> Nombre Completo: ", null, "", true));
        data.put("id", getValidatedStringInput("-> Identificacion (RUC/Cedula): ", REGEX_ID, "Debe ser Cedula (10) o RUC (13 digitos).", true));
        data.put("phone", getValidatedStringInput("-> Telefono: ", REGEX_PHONE, "Formato invalido. Solo numeros y/o '+'.", true));
        data.put("email", getValidatedStringInput("-> Email: ", REGEX_EMAIL, "Email invalido. Use formato usuario@dominio.com.", true));
        data.put("clientType", askClientType("STANDARD"));
        return data;
    }
    
    public HashMap<String, String> askCustomerUpdateData(Customer customer) {
        System.out.println("\n--- EDITANDO CLIENTE: " + customer.getName() + " ---");
        System.out.println("Deje el campo vacio para no cambiar el valor.");
        HashMap<String, String> data = new HashMap<>();
        
        data.put("name", getValidatedStringInput("Nombre (" + customer.getName() + "): ", null, "", false));
        data.put("phone", getValidatedStringInput("Telefono (" + customer.getPhone() + "): ", REGEX_PHONE, "Formato invalido. Solo numeros y/o '+'.", false));
        data.put("email", getValidatedStringInput("Email (" + customer.getEmail() + "): ", REGEX_EMAIL, "Email invalido. Use formato usuario@dominio.com.", false));
        data.put("clientType", askClientType(customer.getClientType()));
        
        return data;
    }
    
    private String askClientType(String currentType) {
        System.out.println("-> Tipo de Cliente (Actual: " + currentType + "):");
        System.out.println("   1. STANDARD");
        System.out.println("   2. PREMIUM");
        System.out.println("   3. VIP");
        System.out.println("   (Presione Enter para mantener: " + currentType + ")");
        
        String input = getOptionalStringInput("Opcion: ");
        if (input.isEmpty()) return currentType;
        
        try {
            int option = Integer.parseInt(input);
            if (option == 1) return "STANDARD";
            if (option == 2) return "PREMIUM";
            if (option == 3) return "VIP";
            
            showError("Opcion no valida. Se mantendra el tipo actual: " + currentType);
            return currentType;
            
        }catch (NumberFormatException error){
            showError("Entrada invalida. Debe ingresar un numero o Enter.");
            return currentType;
        }
    }

    public int showSupplierMenu() {
        System.out.println("\n--- GESTION DE PROVEEDORES ---");
        System.out.println("1. Crear Nuevo Proveedor");
        System.out.println("2. Ver Proveedores Registrados");
        System.out.println("3. Editar Proveedor");
        System.out.println("4. Eliminar Proveedor");
        System.out.println("0. Volver al Menu Principal");
        System.out.print("Seleccione una opcion: ");
        return getIntInput();
    }
    
    public String askSupplierId() {
        return getValidatedStringInput("-> Ingrese el ID 1 (RUC) del proveedor: ", REGEX_ID, "Debe ser un RUC (13 digitos).", true);
    }
    
    public void showSupplierList(ArrayList<Supplier> suppliers) {
        System.out.println("\n--- LISTA DE PROVEEDORES ---");
        if (suppliers.isEmpty()) { System.out.println("No hay proveedores registrados."); return; }
        
        String format = "| %-13s | %-25s | %-15s | %-25s |";
        System.out.println(String.format(format, "ID 1 (RUC)", "Nombre Completo", "Telefono", "Email"));
        System.out.println(new String(new char[84]).replace("\0", "-"));
        for (Supplier supplier : suppliers) {
            System.out.println(String.format(format, supplier.getId1(), supplier.getFullName(), supplier.getPhone(), supplier.getEmail()));
        }
    }
    
    public HashMap<String, String> askNewSupplierData() {
        HashMap<String, String> data = new HashMap<>();
        System.out.println("\n--- REGISTRO DE NUEVO PROVEEDOR ---");
        data.put("id1", getValidatedStringInput("-> ID 1 (RUC) (Obligatorio): ", REGEX_ID, "Debe ser un RUC (13 digitos).", true));
        data.put("name", getValidatedStringInput("-> Nombre Completo: ", null, "", true));
        data.put("phone", getValidatedStringInput("-> Telefono: ", REGEX_PHONE, "Formato invalido.", true));
        data.put("email", getValidatedStringInput("-> Email: ", REGEX_EMAIL, "Email invalido.", true));
        data.put("description", getValidatedStringInput("-> Descripcion: ", null, "", true));
        data.put("id2", getValidatedStringInput("-> ID 2 (Opcional): ", null, "", false));
        return data;
    }
    
    public HashMap<String, String> askSupplierUpdateData(Supplier supplier) {
        System.out.println("\n--- EDITANDO PROVEEDOR: " + supplier.getFullName() + " ---");
        System.out.println("Deje el campo vacio para no cambiar el valor.");
        HashMap<String, String> data = new HashMap<>();
        
        data.put("name", getValidatedStringInput("Nombre (" + supplier.getFullName() + "): ", null, "", false));
        data.put("phone", getValidatedStringInput("Telefono (" + supplier.getPhone() + "): ", REGEX_PHONE, "Formato invalido.", false));
        data.put("email", getValidatedStringInput("Email (" + supplier.getEmail() + "): ", REGEX_EMAIL, "Email invalido.", false));
        data.put("description", getValidatedStringInput("Descripcion (" + supplier.getDescription() + "): ", null, "", false));
        data.put("id2", getValidatedStringInput("ID 2 (" + supplier.getId2() + "): ", null, "", false));
        
        return data;
    }
    
    public int showAdminMenu() {
        System.out.println("\n--- MENU DE ADMINISTRACION ---");
        System.out.println("1. Configurar Tasa de Impuesto");
        System.out.println("2. Configurar Algoritmo de Precios");
        System.out.println("3. Crear Nuevo Producto");
        System.out.println("4. Crear Nuevo Inventario");
        System.out.println("0. Volver al Menu Principal");
        System.out.print("Seleccione una opcion: ");
        return getIntInput();
    }
    
    public float askNewTaxRate(float currentRate) {
        return getPositiveFloatInput("-> Tasa de Impuesto Actual (" + currentRate + "): ");
    }
    
    public String askNewInventoryName() {
        return getMandatoryStringInput("-> Nombre del nuevo inventario (ej: Bodega Sur): ");
    }
    
    public Address askAddressData(String prompt) {
        System.out.println(prompt);
        String country = getMandatoryStringInput("   Pais (Obligatorio): ");
        String city = getMandatoryStringInput("   Ciudad (Obligatorio): ");
        String street = getMandatoryStringInput("   Calle (Obligatorio): ");
        
        Address address = new Address(country, city, street);
        
        address.setZipCode(getValidatedStringInput("   Codigo Postal (Opcional): ", null, "", false));
        address.setStreetNumber(getValidatedStringInput("   Numero de Calle (Opcional): ", null, "", false));
        address.setRegion(getValidatedStringInput("   Region (Opcional): ", null, "", false));
        
        return address;
    }
    
    public HashMap<String, String> askNewProductData() {
        HashMap<String, String> data = new HashMap<>();
        System.out.println("\n--- REGISTRO DE NUEVO PRODUCTO ---");
        data.put("id", getMandatoryStringInput("-> ID del Producto (SKU): "));
        data.put("barcode", getMandatoryStringInput("-> Codigo de Barras: "));
        data.put("name", getMandatoryStringInput("-> Nombre del Producto: "));
        data.put("description", getMandatoryStringInput("-> Descripcion corta: "));
        data.put("costPrice", String.valueOf(getPositiveFloatInput("-> Precio de Costo: ")));
        data.put("stock", String.valueOf(getPositiveIntInput("-> Stock Inicial: ")));
        return data;
    }
    
    public HashMap<String, String> askNewCompanyAccountData() {
        System.out.println("\n--- REGISTRO DE CUENTA DE COMPANIA ---");
        HashMap<String, String> accountData = new HashMap<>();
        accountData.put("username", getValidatedStringInput("-> Nombre de Usuario (para login): ", null, "", true));
        accountData.put("password", getValidatedStringInput("-> Contrasena: ", null, "", true));
        accountData.put("companyName", getValidatedStringInput("-> Nombre de la Compania: ", null, "", true));
        accountData.put("ruc", getValidatedStringInput("-> RUC (13 digitos): ", REGEX_ID, "Debe ser un RUC valido (13 digitos).", true));
        accountData.put("phone", getValidatedStringInput("-> Telefono de Contacto: ", REGEX_PHONE, "Formato invalido.", true));
        accountData.put("email", getValidatedStringInput("-> Email de Contacto: ", REGEX_EMAIL, "Email invalido.", true));
        return accountData;
    }
    
    public HashMap<String, String> askNewPersonalAccountData() {
        System.out.println("\n--- REGISTRO DE CUENTA PERSONAL ---");
        HashMap<String, String> accountData = new HashMap<>();
        accountData.put("fullName", getValidatedStringInput("-> Nombre Completo: ", null, "", true));
        accountData.put("username", getValidatedStringInput("-> Nombre de Usuario (para login): ", null, "", true));
        accountData.put("password", getValidatedStringInput("-> Contrasena: ", null, "", true));
        return accountData;
    }
    
    public Supplier chooseSupplier(ArrayList<Supplier> suppliers) {
        System.out.println("-> Seleccione el proveedor del producto:");
        if (suppliers.isEmpty()) {
            showError("No hay proveedores registrados.");
        }
        
        for (int i = 0; i < suppliers.size(); i++) {
            System.out.println("   " + (i + 1) + ". " + suppliers.get(i).getFullName() + " (ID: " + suppliers.get(i).getId1() + ")");
        }
        System.out.println("---------------------------------");
        System.out.println("   0. --- CREAR NUEVO PROVEEDOR ---");
        
        int option = -1;
        while (option < 0 || option > suppliers.size()) {
            option = getIntInput();
            if (option < 0 || option > suppliers.size()) {
                showError("Opcion no valida.");
            }
        }
        
        if (option == 0) {
            return null;
        }
        return suppliers.get(option - 1);
    }

    public Inventory chooseInventory(ArrayList<Inventory> inventories) {
        System.out.println("-> Seleccione el inventario para el stock inicial:");
        if (inventories.isEmpty()) {
            return null;
        }
        for (int i = 0; i < inventories.size(); i++) {
            System.out.println("   " + (i + 1) + ". " + inventories.get(i).getName());
        }
        
        int option = -1;
        while (option < 1 || option > inventories.size()) {
            option = getPositiveIntInput("Opcion: ");
            if (option < 1 || option > inventories.size()) {
                showError("Opcion no valida.");
            }
        }
        return inventories.get(option - 1);
    }
    
    public HashMap<String, Float> askPriceAlgorithmData(FinvoryData data) {
        HashMap<String, Float> percentages = new HashMap<>();
        System.out.println("\n--- CONFIGURAR ALGORITMO DE PRECIOS GLOBAL ---");
        System.out.println("Ingrese los porcentajes como decimales (ej: 30% = 0.30, 200% = 2.0)");
        
        float profit;
        float maximumProfit = 2.0f;

        do {
            profit = getPositiveFloatInput("Ganancia (" + data.getProfitPercentage() + "): ");
            if (profit > maximumProfit) {
                showError("Ha excedido la cantidad maxima de ganancia (200%, " + maximumProfit + " max).");
            }
        } while (profit > maximumProfit);

        percentages.put("profit", profit);

        float discountStandard = getValidatedDiscount("Desc. Standard", data.getDiscountStandard(), -1.0f, profit);
        percentages.put("discountStandard", discountStandard);

        float discountPremium = getValidatedDiscount("Desc. Premium", data.getDiscountPremium(), discountStandard, profit);
        percentages.put("discountPremium", discountPremium);

        float discountVip = getValidatedDiscount("Desc. Vip", data.getDiscountVip(), discountPremium, profit);
        percentages.put("discountVip", discountVip);
        
        return percentages;
    }

    public int showReportsMenu() {
        System.out.println("\n--- REPORTES Y DASHBOARD ---");
        System.out.println("1. Ver Dashboard de Ingresos");
        System.out.println("2. Reporte de Ventas por Producto (Mas vendidos)");
        System.out.println("3. Reporte de Actividad de Clientes (Mas frecuentes)");
        System.out.println("4. Reporte de Demanda de Proveedores (Mas solicitados)");
        System.out.println("0. Volver al Menu Principal");
        System.out.print("Seleccione una opcion: ");
        return getIntInput();
    }
    
    public void showReport(String title, HashMap<String, Integer> data) {
        System.out.println("\n--- " + title.toUpperCase() + " ---");
        if (data.isEmpty()) {
            System.out.println("No hay datos para mostrar.");
            return;
        }
        
        String format = "| %-30s | %-10s |";
        System.out.println(String.format(format, "Item", "Cantidad"));
        System.out.println(new String(new char[47]).replace("\0", "-"));
        
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            System.out.println(String.format(format, entry.getKey(), entry.getValue()));
        }
        System.out.println("-----------------------------------------------");
    }

    public void showDashboard(float totalDay, float totalProfile) {
        System.out.println("\n=== DASHBOARD DE INGRESOS ===");
        System.out.println("Ingresos Brutos (Hoy):       $" + totalDay);
        System.out.println("Ingresos Brutos (Historico): $" + totalProfile);
        System.out.println("==============================\n");
    }
    
    public void showQuoteEmailPlaceholder() {
        System.out.println("Explorando inventario graficamente...");
        System.out.println("Generando cotizacion...");
        showError("Funcionalidad de envio de correo AUN NO IMPLEMENTADA.");
    }
    
    public void showMessage(String message) { 
        System.out.println("INFO: " + message); 
    }
    
    public void showError(String error) { 
        System.err.println("ERROR: " + error); 
    }
    
    public boolean askConfirmation(String prompt) {
        String input = getMandatoryStringInput(prompt + " (s/n): ");
        return input.equalsIgnoreCase("s");
    }

    private String getMandatoryStringInput(String prompt) {
        return getValidatedStringInput(prompt, null, "", true);
    }
    
    private String getOptionalStringInput(String prompt) {
        return getValidatedStringInput(prompt, null, "", false);
    }
    
    private String getValidatedStringInput(String prompt, Pattern regexPattern, String errorMsg, boolean mandatory) {
        String input = "";
        while (true) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();

            if (mandatory && input.isEmpty()) {
                showError("Este campo es obligatorio. No puede estar vacio.");
                continue; 
            }
            
            if (!mandatory && input.isEmpty()) {
                return input; 
            }
            
            if (regexPattern != null) {
                if (!regexPattern.matcher(input).matches()) {
                    showError(errorMsg);
                    continue; 
                }
            }
            
            return input;
        }
    }
    
    private int getIntInput() {
        while (true) {
            try {
                String line = getValidatedStringInput("", REGEX_NUMERIC, "Debe ingresar un numero entero.", true);
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                showError("Debe ingresar un numero entero.");
                return -1;
            }
        }
    }
    
    private int getPositiveIntInput(String prompt) {
        int value = -1;
        while (value < 0) {
            System.out.print(prompt);
            try {
                String line = getValidatedStringInput("", REGEX_NUMERIC, "Debe ingresar un numero entero valido.", true);
                value = Integer.parseInt(line);
                if (value < 0) showError("El numero no puede ser negativo.");
            } catch (NumberFormatException e) {
                showError("Debe ingresar un numero entero valido.");
                value = -1;
            }
        }
        return value;
    }
    
    private float getPositiveFloatInput(String prompt) {
        float value = -1.0f;
        while (value < 0.0f) {
            System.out.print(prompt);
            try {
                String line = getValidatedStringInput("", REGEX_NUMERIC, "Debe ingresar un numero decimal valido.", true);
                value = Float.parseFloat(line);
                if (value < 0.0f) showError("El numero no puede ser negativo.");
            } catch (NumberFormatException e) {
                showError("Debe ingresar un numero decimal valido.");
                value = -1.0f;
            }
        }
        return value;
    }
    
    private float getValidatedDiscount(String label, float currentValue, float minimumLimit, float maximumLimit) {
        float discount;
        do {
            discount = getPositiveFloatInput(label + " (" + currentValue + "): ");

            if (discount >= maximumLimit) {
                showError("El descuento debe ser MENOR a la ganancia (" + maximumLimit + "). No pueden ser iguales.");
                continue;
            }

            if (minimumLimit >= 0 && discount <= minimumLimit) {
                showError("El descuento debe ser MAYOR al nivel anterior (" + minimumLimit + "). No pueden ser iguales.");
                continue;
            }

            break;

        } while (true);

        return discount;
    }
}