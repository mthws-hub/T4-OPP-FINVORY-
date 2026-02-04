package ec.edu.espe.finvory.view;

import ec.edu.espe.finvory.controller.FinvoryController;
import ec.edu.espe.finvory.model.Inventory;
import ec.edu.espe.finvory.model.Product;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 *
 * @author Mathews Pastor, The POOwer Rangers Of Programming
 */
public class FrmEditProductListOnInvoice extends JDialog {

    private FinvoryController controller;
    private Product foundProduct = null;
    private FrmSaleInvoice parentInvoice;

    public FrmEditProductListOnInvoice(java.awt.Frame parent, boolean modal, FinvoryController controller) {
        super(parent, modal);
        this.controller = controller;
        if (parent instanceof FrmSaleInvoice) {
            this.parentInvoice = (FrmSaleInvoice) parent;
        }
        initComponents();
        ButtonStyles.applyPrimaryStyle(btnAdd);
        ButtonStyles.applyPrimaryStyle(btnDelete);
        this.setLocationRelativeTo(parent);
        loadInventories();
    }

    private void loadInventories() {
        cmbInventory.removeAllItems();
        if (controller != null && controller.getInventories() != null) {
            for (Inventory inv : controller.getInventories()) {
                cmbInventory.addItem(inv.getName());
            }
        }
    }

    private void searchProduct() {
        String id = txtId.getText().trim();
        String barcode = txtBarCode.getText().trim();

        foundProduct = null;

        if (!id.isEmpty()) {
            foundProduct = controller.productController.findProductPublic(id);
        } else if (!barcode.isEmpty()) {
            foundProduct = controller.productController.findProductByBarcodePublic(barcode);
        }

        if (foundProduct != null) {
            txtId.setText(foundProduct.getId());
            txtBarCode.setText(foundProduct.getBarcode());
            txtQuantity.requestFocus();
        } else {
            JOptionPane.showMessageDialog(this, "Producto no encontrado.", "Búsqueda", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void onAddProduct() {
    Product product = getOrSearchProduct();
    if (product == null) {
        return;
    }

    Integer quantity = getValidatedQuantity();
    if (quantity == null) {
        return;
    }

    Inventory selectedInv = getSelectedInventory();
    if (selectedInv == null) {
        JOptionPane.showMessageDialog(this, "Seleccione un inventario válido.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    if (!hasEnoughStock(selectedInv, product, quantity)) {
        return;
    }

    if (!confirmAddToInvoice(product, selectedInv, quantity)) {
        return;
    }

    addToInvoice(product, selectedInv, quantity);
    resetFormAfterAdd();
    }

    private void onClose() {
        this.dispose();
    }

    private Product getOrSearchProduct() {
        if (foundProduct == null) {
            searchProduct();
        }
        return foundProduct;
    }

    private Integer getValidatedQuantity() {
        String quantityStr = txtQuantity.getText();

        if (!ec.edu.espe.finvory.utils.ValidationUtils.isValidQuantity(quantityStr)) {
            JOptionPane.showMessageDialog(
                    this,
                    "La cantidad debe ser un número entero positivo mayor a 0.",
                    "Error de Validación",
                    JOptionPane.WARNING_MESSAGE
            );
            return null;
        }
        return Integer.parseInt(quantityStr.trim());
    }

    private Inventory getSelectedInventory() {
        String inventoryName = (String) cmbInventory.getSelectedItem();
        if (inventoryName == null || inventoryName.trim().isEmpty()) {
            return null;
        }
        return controller.inventoryController.findInventoryByName(inventoryName);
    }

    private boolean hasEnoughStock(Inventory inventory, Product product, int quantity) {
        int currentStock = inventory.getStock(product.getId());
        if (currentStock < quantity) {
            JOptionPane.showMessageDialog(
                    this,
                    "Stock insuficiente en " + inventory.getName() + ".\nDisponible: " + currentStock + "\nSolicitado: " + quantity,
                    "Stock Insuficiente",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
        return true;
    }

    private boolean confirmAddToInvoice(Product product, Inventory inventory, int quantity) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Agregar a la factura?\n\n"
                + "Producto: " + product.getName() + "\n"
                + "ID: " + product.getId() + "\n"
                + "Barcode: " + product.getBarcode() + "\n"
                + "Cantidad: " + quantity + "\n"
                + "Desde: " + inventory.getName(),
                "Confirmar Producto",
                JOptionPane.YES_NO_OPTION
        );
        return confirm == JOptionPane.YES_OPTION;
    }

    private void addToInvoice(Product product, Inventory inventory, int quantity) {
        if (parentInvoice != null) {
            parentInvoice.addProductToCart(product, inventory, quantity);
        }
    }

    private void resetFormAfterAdd() {
        emptyFields();
        foundProduct = null;
        txtId.requestFocus();
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblID = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        lblBarCode = new javax.swing.JLabel();
        txtBarCode = new javax.swing.JTextField();
        btnAdd = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        lblQuantity = new javax.swing.JLabel();
        txtQuantity = new javax.swing.JTextField();
        lblInventory = new javax.swing.JLabel();
        cmbInventory = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(224, 224, 224));

        lblTitle.setFont(new java.awt.Font("Perpetua Titling MT", 1, 20)); // NOI18N
        lblTitle.setText("AGREGAR/ELIMinAR PRODUCTO");

        lblID.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        lblID.setText("Id:");

        lblBarCode.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        lblBarCode.setText("Codigo de Barras:");

        btnAdd.setBackground(new java.awt.Color(0, 123, 0));
        btnAdd.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        btnAdd.setForeground(new java.awt.Color(242, 242, 242));
        btnAdd.setText("AGREGAR");
        btnAdd.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnDelete.setBackground(new java.awt.Color(0, 123, 0));
        btnDelete.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        btnDelete.setForeground(new java.awt.Color(242, 242, 242));
        btnDelete.setText("ELIMINAR");
        btnDelete.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        lblQuantity.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        lblQuantity.setText("Cantidad:");

        lblInventory.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        lblInventory.setText("Inventario:");

        cmbInventory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(btnAdd)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnDelete)
                .addGap(93, 93, 93))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblInventory, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblID, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblBarCode, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblQuantity, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(65, 65, 65)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtBarCode, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbInventory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(lblTitle)))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(lblTitle)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblID)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblBarCode)
                    .addComponent(txtBarCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblQuantity)
                    .addComponent(txtQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(lblInventory))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cmbInventory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 77, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDelete)
                    .addComponent(btnAdd))
                .addGap(110, 110, 110))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        onAddProduct();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        onClose();
    }//GEN-LAST:event_btnDeleteActionPerformed
    private void emptyFields() {
        txtId.setText("");
        txtBarCode.setText("");
        txtQuantity.setText("");
    }
    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JComboBox<String> cmbInventory;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblBarCode;
    private javax.swing.JLabel lblID;
    private javax.swing.JLabel lblInventory;
    private javax.swing.JLabel lblQuantity;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JTextField txtBarCode;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtQuantity;
    // End of variables declaration//GEN-END:variables
}
