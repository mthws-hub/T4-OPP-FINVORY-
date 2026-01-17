package ec.edu.espe.finvory.controller;

import ec.edu.espe.finvory.controller.FinvoryController;
import ec.edu.espe.finvory.model.*;
import ec.edu.espe.finvory.view.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 *
 * @author Mathews Pastor, The POOwer Rangers Of Programming
 */
public class SaleInvoiceController {

    private FinvoryController app;

    private Customer selectedCustomer = null;
    private HashMap<Product, HashMap<Inventory, Integer>> currentCart = new HashMap<>();
    private FrmSaleInvoice view;

    public SaleInvoiceController(FinvoryController app, FrmSaleInvoice view) {
        this.app = app;
        this.view = view;
    }

    public void onFindCustomer(String id, String name) {
        List<Customer> matches = app.customerController.findCustomers(id, name);

        if (matches == null || matches.isEmpty()) {
            view.showNoCustomerFound();
            reset();
            return;
        }

        if (matches.size() == 1) {
            loadCustomer(matches.get(0));
            return;
        }

        openCustomerSelector(matches);
    }

    private void openCustomerSelector(List<Customer> matches) {
        FrmCustomerSelector selector = new FrmCustomerSelector(view, true, matches);
        selector.setVisible(true);

        Customer selected = selector.getSelectedCustomer();
        if (selected != null) {
            loadCustomer(selected);
        }
    }

    private void loadCustomer(Customer customer) {
        this.selectedCustomer = customer;
        view.showCustomer(customer);
    }

    public void addProduct(Product product, Inventory sourceInventory, int quantity) {
        if (selectedCustomer == null) {
            view.showSelectCustomerWarning();
            return;
        }

        currentCart.putIfAbsent(product, new HashMap<>());
        Map<Inventory, Integer> inventoryMap = currentCart.get(product);
        inventoryMap.put(sourceInventory, inventoryMap.getOrDefault(sourceInventory, 0) + quantity);

        InvoiceSim sim = app.saleController.calculatePotentialInvoice(selectedCustomer, currentCart);

        view.refreshProductTable(product, quantity, sim);
        view.updateTotals(sim);
    }

    public void onConfirmSale(boolean cash, boolean transfer, boolean cheque) {

        if (!app.saleController.isSaleValid(selectedCustomer, currentCart)) {
            view.showMissingDataError();
            return;
        }

        String payment = app.saleController.resolvePaymentMethod(cash, transfer, cheque);

        boolean ok = app.saleController.confirmSale(selectedCustomer, currentCart, payment);

        if (ok) {
            view.showSaleSuccess();
            reset();
            view.close();
        } else {
            view.showSaleError();
        }
    }

    public void onCancel() {
        reset();
        view.close();
    }

    private void reset() {
        selectedCustomer = null;
        currentCart.clear();
        view.clearForm();
    }

    public void onManageProductList() {
        if (!isCustomerSelected()) {
            showSelectCustomerWarning();
            return;
        }
        openEditProductListDialog();
    }

    private boolean isCustomerSelected() {
        return selectedCustomer != null;
    }

    private void showSelectCustomerWarning() {
        JOptionPane.showMessageDialog(view, "Por favor seleccione un cliente antes de agregar productos.", "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    private void openEditProductListDialog() {
        FrmEditProductListOnInvoice dialog = new FrmEditProductListOnInvoice(view, true, app);
        dialog.setVisible(true);
    }

}
