package ec.edu.espe.finvory.view;

import ec.edu.espe.finvory.FinvoryApp;
import ec.edu.espe.finvory.controller.FinvoryController;
import ec.edu.espe.finvory.model.Supplier;
import ec.edu.espe.finvory.model.Product;
import ec.edu.espe.finvory.model.Inventory;
import ec.edu.espe.finvory.utils.ValidationUtils;
import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.JOptionPane;

/**
 *
 * @author Maryuri Quiña, The POOwer Rangers Of Programming
 */
public class FrmAddNewProduct extends javax.swing.JFrame {

    private FinvoryController controller;
    private String productIdToEdit = null;
    private Inventory targetInventory = null;

    private final Color ERROR_COLOR = Color.RED;
    private final Color DEFAULT_COLOR = Color.BLACK;

    public FrmAddNewProduct(FinvoryController controller) {
        this(controller, null, null);
        FinvoryApp.setIcon(this);
    }

    public FrmAddNewProduct(FinvoryController controller, String productId) {
        this(controller, null, productId);
    }

    public FrmAddNewProduct(FinvoryController controller, Inventory targetInventory) {
        this(controller, targetInventory, null);
    }

    public FrmAddNewProduct(FinvoryController controller, Inventory inventory, String productId) {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        this.controller = controller;
        this.targetInventory = inventory;
        this.productIdToEdit = productId;

        initializeComboBoxes();
        setupFormMode();
    }

    private void emptyFields() {
        txtID.setText("");
        txtBarCode.setText("");
        txtProductName.setText("");
        txtDescription.setText("");
        txtPrice.setText("");
        txtInitialStock.setText("");
        cmbProductSupplier.setSelectedIndex(0);
        cmbInitialStock.setSelectedIndex(0);
        if (productIdToEdit == null) {
            txtID.setEnabled(true);
        }
    }

    private void resetColors() {
        lblProductId.setForeground(DEFAULT_COLOR);
        lblBarCode.setForeground(DEFAULT_COLOR);
        lblProductName.setForeground(DEFAULT_COLOR);
        lblDescription.setForeground(DEFAULT_COLOR);
        lblPrice.setForeground(DEFAULT_COLOR);
        lblStock.setForeground(DEFAULT_COLOR);
        lblSupplier.setForeground(DEFAULT_COLOR);
        lblInventory.setForeground(DEFAULT_COLOR);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblId = new javax.swing.JPanel();
        lblProductId = new javax.swing.JLabel();
        lblBarCode = new javax.swing.JLabel();
        lblProductName = new javax.swing.JLabel();
        lblDescription = new javax.swing.JLabel();
        lblPrice = new javax.swing.JLabel();
        lblStock = new javax.swing.JLabel();
        lblSupplier = new javax.swing.JLabel();
        lblInventory = new javax.swing.JLabel();
        cmbProductSupplier = new javax.swing.JComboBox<>();
        cmbInitialStock = new javax.swing.JComboBox<>();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextPane();
        txtPrice = new javax.swing.JFormattedTextField();
        txtInitialStock = new javax.swing.JFormattedTextField();
        txtID = new javax.swing.JTextField();
        txtBarCode = new javax.swing.JTextField();
        txtProductName = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lblProductId.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        lblProductId.setText("ID (SKU):");

        lblBarCode.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        lblBarCode.setText("Codigo de Barras:");

        lblProductName.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        lblProductName.setText("Nombre del Producto:");

        lblDescription.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        lblDescription.setText("Descripcion:");

        lblPrice.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        lblPrice.setText("Precio de Costo:");

        lblStock.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        lblStock.setText("Stock Inicial:");

        lblSupplier.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        lblSupplier.setText("Seleccione el proovedor del producto:");

        lblInventory.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        lblInventory.setText("Seleccione el inventario para el stock inicial:");

        jScrollPane5.setViewportView(txtDescription);

        txtID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIDActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout lblIdLayout = new javax.swing.GroupLayout(lblId);
        lblId.setLayout(lblIdLayout);
        lblIdLayout.setHorizontalGroup(
            lblIdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lblIdLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(lblIdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(lblIdLayout.createSequentialGroup()
                        .addComponent(lblDescription)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(lblIdLayout.createSequentialGroup()
                        .addGroup(lblIdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(lblIdLayout.createSequentialGroup()
                                .addComponent(lblProductName)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, lblIdLayout.createSequentialGroup()
                                .addComponent(lblBarCode)
                                .addGap(44, 44, 44)))
                        .addGroup(lblIdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtBarCode, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtProductName, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(lblIdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(lblIdLayout.createSequentialGroup()
                            .addComponent(lblInventory)
                            .addGap(18, 18, 18)
                            .addComponent(cmbInitialStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(lblIdLayout.createSequentialGroup()
                            .addComponent(lblSupplier)
                            .addGap(18, 18, 18)
                            .addComponent(cmbProductSupplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(lblIdLayout.createSequentialGroup()
                            .addGroup(lblIdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblPrice)
                                .addComponent(lblStock))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(lblIdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtInitialStock, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(lblIdLayout.createSequentialGroup()
                            .addComponent(lblProductId)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(209, 209, 209))))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        lblIdLayout.setVerticalGroup(
            lblIdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lblIdLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lblIdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProductId)
                    .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(lblIdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtBarCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBarCode))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(lblIdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProductName)
                    .addComponent(txtProductName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(lblIdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDescription)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(lblIdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPrice)
                    .addComponent(txtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(lblIdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStock)
                    .addComponent(txtInitialStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(lblIdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSupplier)
                    .addComponent(cmbProductSupplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(lblIdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblInventory)
                    .addComponent(cmbInitialStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        btnAdd.setBackground(new java.awt.Color(0, 123, 0));
        btnAdd.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 12)); // NOI18N
        btnAdd.setForeground(new java.awt.Color(255, 255, 255));
        btnAdd.setText("Agregar");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnCancel.setBackground(new java.awt.Color(0, 123, 0));
        btnCancel.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 12)); // NOI18N
        btnCancel.setForeground(new java.awt.Color(255, 255, 255));
        btnCancel.setText("Cancelar");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(108, 108, 108)
                .addComponent(btnAdd)
                .addGap(99, 99, 99)
                .addComponent(btnCancel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAdd)
                    .addComponent(btnCancel))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        lblTitle.setFont(new java.awt.Font("Perpetua Titling MT", 1, 20)); // NOI18N
        lblTitle.setText("Registro Producto");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(131, 131, 131)
                .addComponent(lblTitle)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblTitle)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblId, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(26, 26, 26))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        resetColors();
        String id = txtID.getText().trim();
        String barcode = txtBarCode.getText().trim();
        String name = txtProductName.getText().trim();
        String description = txtDescription.getText().trim();
        String priceStr = txtPrice.getText().trim();
        String stockStr = txtInitialStock.getText().trim();
        String supplierSelection = (String) cmbProductSupplier.getSelectedItem();

        StringBuilder errors = new StringBuilder();

        if (ValidationUtils.isEmpty(id)) {
            lblProductId.setForeground(ERROR_COLOR);
            errors.append("- El ID del producto es obligatorio.\n");
        } else if (productIdToEdit == null && controller.findProduct(id) != null) {
            lblProductId.setForeground(ERROR_COLOR);
            errors.append("- Ya existe un producto con ese ID.\n");
        }
        if (ValidationUtils.isEmpty(name)) {
            lblProductName.setForeground(ERROR_COLOR);
            errors.append("- El nombre del producto es obligatorio.\n");
        }
        if (ValidationUtils.isEmpty(priceStr)) {
            lblPrice.setForeground(ERROR_COLOR);
            errors.append("- El precio de costo es obligatorio.\n");
        } else if (!ValidationUtils.isPositiveDecimal(priceStr)) {
            lblPrice.setForeground(ERROR_COLOR);
            errors.append("- El precio debe ser un número positivo (ej: 10.50).\n");
        }
        if (ValidationUtils.isEmpty(stockStr)) {
            lblStock.setForeground(ERROR_COLOR);
            errors.append("- El stock es obligatorio.\n");
        } else if (!ValidationUtils.isNonNegativeInteger(stockStr)) {
            lblStock.setForeground(ERROR_COLOR);
            errors.append("- El stock debe ser un número entero positivo (ej: 10).\n");
        }
        if (!ValidationUtils.isNumeric(barcode)) {
            lblBarCode.setForeground(ERROR_COLOR);
            errors.append("- El código de barras solo puede contener numeros.\n");
        } 
        if (errors.length() > 0) {
            JOptionPane.showMessageDialog(this,
                    "Por favor corrija los siguientes errores:\n\n" + errors.toString(),
                    "Error de Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        String supplierId = extractIdFromCombo(supplierSelection);

        HashMap<String, String> productData = new HashMap<>();
        productData.put("id", id);
        productData.put("barcode", barcode);
        productData.put("name", name);
        productData.put("description", description);
        productData.put("costPrice", priceStr);
        productData.put("stock", stockStr);
        productData.put("supplierId", supplierId);

        Supplier supplierObj = controller.findSupplier(supplierId);
        boolean success;

        if (productIdToEdit == null) {
            success = controller.handleCreateProduct(productData, supplierObj, targetInventory);
            if (success) {
                JOptionPane.showMessageDialog(this, "Producto creado exitosamente.");
                emptyFields();
            }
        } else {
            success = controller.handleUpdateProductGUI(productIdToEdit, productData, targetInventory);
            if (success) {
                JOptionPane.showMessageDialog(this, "Producto actualizado correctamente.");
            }
        }
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        int option = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de cancelar el registro? Todos los datos serán borrados.",
                "Confirmación de Cancelación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            emptyFields();
            JOptionPane.showMessageDialog(
                    this,
                    "Registro cancelado. Los campos han sido limpiados.",
                    "Cancelado",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }//GEN-LAST:event_btnCancelActionPerformed

    private void txtIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIDActionPerformed
    private void setupFormMode() {
        if (productIdToEdit != null) {
            lblTitle.setText("EDITAR PRODUCTO");
            btnAdd.setText("ACTUALIZAR");
            loadProductData(productIdToEdit);
        } else {
            lblTitle.setText("NUEVO PRODUCTO");
            btnAdd.setText("GUARDAR");
            if (targetInventory != null) {
                cmbInitialStock.setSelectedItem(targetInventory.getName());
                cmbInitialStock.setEnabled(false);
            }
        }
    }

    private void initializeComboBoxes() {
        cmbProductSupplier.removeAllItems();
        cmbProductSupplier.addItem("Seleccione Proveedor...");
        if (controller.getData() != null) {
            HashSet<String> added = new HashSet<>();
            for (Supplier s : controller.getSuppliers()) {
                if (added.add(s.getId1())) {
                    cmbProductSupplier.addItem(s.getFullName() + " (ID: " + s.getId1() + ")");
                }
            }
        }

        cmbInitialStock.removeAllItems();
        cmbInitialStock.addItem("Seleccione Inventario...");
        if (controller.getData() != null) {
            for (Inventory i : controller.getInventories()) {
                cmbInitialStock.addItem(i.getName());
            }
        }
    }

    private void loadProductData(String pid) {
        Product product = controller.findProduct(pid);
        if (product == null) {
            this.dispose();
            return;
        }

        txtID.setText(product.getId());
        txtID.setEnabled(false);
        txtBarCode.setText(product.getBarcode());
        txtProductName.setText(product.getName());
        txtDescription.setText(product.getDescription());
        txtPrice.setText(String.valueOf(product.getBaseCostPrice()));

        if (targetInventory != null) {
            txtInitialStock.setText(String.valueOf(targetInventory.getStock(pid)));
            cmbInitialStock.setSelectedItem(targetInventory.getName());
            cmbInitialStock.setEnabled(false);
        } else {
            txtInitialStock.setText("0");
            txtInitialStock.setEnabled(false);
            cmbInitialStock.setEnabled(false);
        }
        if (product.getSupplierId() != null) {
            for (int i = 0; i < cmbProductSupplier.getItemCount(); i++) {
                if (cmbProductSupplier.getItemAt(i).contains(product.getSupplierId())) {
                    cmbProductSupplier.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private String extractIdFromCombo(String selection) {
        if (selection == null || !selection.contains("(ID: ")) {
            return null;
        }
        return selection.substring(selection.indexOf("(ID: ") + 5, selection.length() - 1);
    }

    private Inventory resolveInventory(String selection) {
        if (targetInventory != null) {
            return targetInventory;
        }
        if (selection == null || selection.startsWith("Seleccione")) {
            return null;
        }
        return controller.findInventoryByName(selection);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancel;
    private javax.swing.JComboBox<String> cmbInitialStock;
    private javax.swing.JComboBox<String> cmbProductSupplier;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JLabel lblBarCode;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JPanel lblId;
    private javax.swing.JLabel lblInventory;
    private javax.swing.JLabel lblPrice;
    private javax.swing.JLabel lblProductId;
    private javax.swing.JLabel lblProductName;
    private javax.swing.JLabel lblStock;
    private javax.swing.JLabel lblSupplier;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JTextField txtBarCode;
    private javax.swing.JTextPane txtDescription;
    private javax.swing.JTextField txtID;
    private javax.swing.JFormattedTextField txtInitialStock;
    private javax.swing.JFormattedTextField txtPrice;
    private javax.swing.JTextField txtProductName;
    // End of variables declaration//GEN-END:variables
}
