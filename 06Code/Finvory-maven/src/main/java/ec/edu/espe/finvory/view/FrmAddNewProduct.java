package ec.edu.espe.finvory.view;

import ec.edu.espe.finvory.controller.FinvoryController;
import ec.edu.espe.finvory.model.Supplier;
import ec.edu.espe.finvory.model.Product;
import ec.edu.espe.finvory.model.Inventory;
import java.util.HashSet;
import javax.swing.JOptionPane;

/**
 *
 * @author Maryuri Quiña, @ESPE
 */
public class FrmAddNewProduct extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmAddNewProduct.class.getName());
    private FinvoryController controller;
    private String productIdToEdit = null;
    private Inventory targetInventory = null;

    public FrmAddNewProduct(FinvoryController controller) {
        this(controller, null, null);
    }

    public FrmAddNewProduct(FinvoryController controller, String productId) {
        this(controller, null, productId);
    }

    public FrmAddNewProduct(FinvoryController controller, Inventory targetInventory) {
        this(controller, targetInventory, null);
    }

    /**
     * Creates new form FrmAddNewProduct
     *
     * @param controller
     */
    public FrmAddNewProduct(FinvoryController controller, Inventory inventory, String productId) {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        this.controller = controller;
        this.targetInventory = inventory;
        this.productIdToEdit = productId;

        initializeComboBoxes();

        if (productId != null) {
            jLabel1.setText("EDITAR PRODUCTO");
            btnAdd.setText("ACTUALIZAR");
            btnCancel.setEnabled(false);
            btnCancel.setVisible(false);
            loadProductData(productId);
        } else {
            jLabel1.setText("NUEVO PRODUCTO");
            btnAdd.setText("GUARDAR");
            btnCancel.setEnabled(true);
            btnCancel.setVisible(true);

            if (targetInventory != null) {
                cmbInitialStock.setSelectedItem(targetInventory.getName());
                cmbInitialStock.setEnabled(false);
            }
        }
    }

    public FrmAddNewProduct() {
        initComponents();
    }

    private void initializeComboBoxes() {
        cmbProductSupplier.removeAllItems();
        cmbProductSupplier.addItem("Seleccione Proveedor...");

        if (controller.getData() != null && controller.getData().getSuppliers() != null) {
            HashSet<String> addedSuppliers = new HashSet<>();

            for (Supplier s : controller.getData().getSuppliers()) {
                if (!addedSuppliers.contains(s.getId1())) {
                    cmbProductSupplier.addItem(s.getFullName() + " (ID: " + s.getId1() + ")");
                    addedSuppliers.add(s.getId1());
                }
            }
        }

        cmbInitialStock.removeAllItems();
        cmbInitialStock.addItem("Seleccione Inventario...");
        if (controller.getData() != null && controller.getData().getInventories() != null) {
            for (Inventory i : controller.getData().getInventories()) {
                cmbInitialStock.addItem(i.getName());
            }
        }
    }

    private void loadProductData(String productId) {
        Product product = controller.findProduct(productId);

        if (product == null) {
            JOptionPane.showMessageDialog(this, "Error: Producto no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
            return;
        }

        txtID.setText(product.getId());
        txtID.setEnabled(false);
        txtBarCode.setText(product.getBarcode());
        txtProductName.setText(product.getName());
        txtDescription.setText(product.getDescription());
        ftfPrice.setText(String.valueOf(product.getBaseCostPrice()));

        if (targetInventory != null) {
            int currentStock = targetInventory.getStock(product.getId());
            ftfInitialStock.setText(String.valueOf(currentStock));
            ftfInitialStock.setEnabled(true);
            cmbInitialStock.setSelectedItem(targetInventory.getName());
            cmbInitialStock.setEnabled(false);
        } else {
            ftfInitialStock.setText("0");
            ftfInitialStock.setEnabled(false);
            cmbInitialStock.setEnabled(false);
        }
        
        if (product.getSupplierId() != null) {
            for (int i = 0; i < cmbProductSupplier.getItemCount(); i++) {
                String item = cmbProductSupplier.getItemAt(i);
                if (item.contains("(ID: " + product.getSupplierId() + ")")) {
                    cmbProductSupplier.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void emptyFields() {
        txtID.setText("");
        txtBarCode.setText("");
        txtProductName.setText("");
        txtDescription.setText("");
        ftfPrice.setText("");
        ftfInitialStock.setText("");
        cmbProductSupplier.setSelectedIndex(0);
        cmbInitialStock.setSelectedIndex(0);
        if (productIdToEdit == null) {
            txtID.setEnabled(true);
        }
    }

    private Object[] validateFieldsAndConvert() {
        String id = txtID.getText().trim();
        String barcode = txtBarCode.getText().trim();
        String name = txtProductName.getText().trim();
        String description = txtDescription.getText().trim();
        String supplierName = (String) cmbProductSupplier.getSelectedItem();
        String inventoryName = (String) cmbInitialStock.getSelectedItem();
        float costPrice = 0.0f;
        int stock = 0;

        if (id.isEmpty() || barcode.isEmpty() || name.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Error: Todos los campos de texto son obligatorios.", "Validación", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        if (supplierName.startsWith("Item") || supplierName.equals("Seleccione...")) {
            JOptionPane.showMessageDialog(this, "Error: Debe seleccionar un Proveedor válido.", "Validación", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        if (inventoryName.startsWith("Item") || inventoryName.equals("Seleccione...")) {
            JOptionPane.showMessageDialog(this, "Error: Debe seleccionar un Inventario válido.", "Validación", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        try {
            String priceText = ftfPrice.getText().trim();
            if (!priceText.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
                JOptionPane.showMessageDialog(this, "Error: El Precio debe usar punto decimal (ej. 32.50) y tener máximo 2 decimales.", "Validación de Formato", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            costPrice = Float.parseFloat(priceText);
            if (costPrice < 0) {
                JOptionPane.showMessageDialog(this, "Error: El Precio de Costo no puede ser negativo.", "Validación Numérica", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: Ingrese un número decimal válido para el Precio de Costo.", "Dato Incorrecto", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        try {
            String stockText = ftfInitialStock.getText().trim();

            if (!stockText.matches("^-?\\d+$")) {
                JOptionPane.showMessageDialog(this, "Error: El Stock Inicial debe ser un número entero (sin decimales).", "Dato Incorrecto", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            stock = Integer.parseInt(stockText);
            if (stock < 0) {
                JOptionPane.showMessageDialog(this, "Error: El Stock Inicial no puede ser negativo.", "Validación Numérica", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: Ingrese un número entero válido para el Stock Inicial.", "Dato Incorrecto", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        return new Object[]{id, barcode, name, description, costPrice, stock, supplierName, inventoryName};
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        cmbProductSupplier = new javax.swing.JComboBox<>();
        cmbInitialStock = new javax.swing.JComboBox<>();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextPane();
        ftfPrice = new javax.swing.JFormattedTextField();
        ftfInitialStock = new javax.swing.JFormattedTextField();
        txtID = new javax.swing.JTextField();
        txtBarCode = new javax.swing.JTextField();
        txtProductName = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        itemInventories = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel2.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        jLabel2.setText("ID (SKU):");

        jLabel3.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        jLabel3.setText("Codigo de Barras:");

        jLabel4.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        jLabel4.setText("Nombre del Producto:");

        jLabel5.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        jLabel5.setText("Descripcion:");

        jLabel6.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        jLabel6.setText("Precio de Costo:");

        jLabel7.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        jLabel7.setText("Stock Inicial:");

        jLabel8.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        jLabel8.setText("Seleccione el proovedor del producto:");

        jLabel9.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        jLabel9.setText("Seleccione el inventario para el stock inicial:");

        jScrollPane5.setViewportView(txtDescription);

        txtID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIDActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(44, 44, 44)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtBarCode, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtProductName, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel9)
                            .addGap(18, 18, 18)
                            .addComponent(cmbInitialStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel8)
                            .addGap(18, 18, 18)
                            .addComponent(cmbProductSupplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel6)
                                .addComponent(jLabel7))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(ftfInitialStock, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(ftfPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(209, 209, 209))))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtBarCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtProductName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(ftfPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(ftfInitialStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(cmbProductSupplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
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

        jLabel1.setFont(new java.awt.Font("Perpetua Titling MT", 1, 20)); // NOI18N
        jLabel1.setText("Registro Producto");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(131, 131, 131)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel1)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jMenu1.setText("Finvory");

        itemInventories.setText("Inventarios");
        itemInventories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemInventoriesActionPerformed(evt);
            }
        });
        jMenu1.add(itemInventories);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        String id = txtID.getText().trim();
        String barcode = txtBarCode.getText().trim();
        String name = txtProductName.getText().trim();
        String description = txtDescription.getText().trim();
        String supplierItem = (String) cmbProductSupplier.getSelectedItem();
        String inventoryName = (String) cmbInitialStock.getSelectedItem();

        if (id.isEmpty() || name.isEmpty() || barcode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete los campos obligatorios (*).");
            return;
        }

        if (cmbProductSupplier.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un proveedor válido.");
            return;
        }

        float costPrice = 0;
        int stock = 0;
        try {
            costPrice = Float.parseFloat(ftfPrice.getText().trim());
            stock = Integer.parseInt(ftfInitialStock.getText().trim());
            if (costPrice < 0 || stock < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Precio y Stock deben ser números positivos válidos.");
            return;
        }

        String supplierId = supplierItem.substring(supplierItem.indexOf("(ID: ") + 5, supplierItem.length() - 1);

        if (productIdToEdit == null) {

            if (controller.findProduct(id) != null) {
                JOptionPane.showMessageDialog(this, "El ID '" + id + "' ya existe.");
                return;
            }

            Inventory destInventory = null;
            if (targetInventory != null) {
                destInventory = targetInventory;
            } else {
                for (Inventory inv : controller.getData().getInventories()) {
                    if (inv.getName().equals(inventoryName)) {
                        destInventory = inv;
                        break;
                    }
                }
            }

            if (destInventory == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un inventario para el stock inicial.");
                return;
            }

            Product newProduct = new Product(id, name, description, barcode, costPrice, supplierId);
            controller.getData().getProducts().add(newProduct);

            if (stock > 0) {
                destInventory.addStock(id, stock);
            }

            controller.saveData();
            JOptionPane.showMessageDialog(this, "Producto creado exitosamente.");

        } else {
            Product existingProduct = controller.findProduct(productIdToEdit);
            if (existingProduct != null) {
                existingProduct.setName(name);
                existingProduct.setBarcode(barcode);
                existingProduct.setDescription(description);
                existingProduct.setBaseCostPrice(costPrice);
                existingProduct.setSupplierId(supplierId);
                
                if (targetInventory != null) {
                    targetInventory.setStock(existingProduct.getId(), stock);
                }

                controller.saveData();
                JOptionPane.showMessageDialog(this, "Producto actualizado.");
            }
        }

        this.dispose();
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

    private void itemInventoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemInventoriesActionPerformed
        FrmInventories frmInventories = new FrmInventories(this.controller);
        this.setVisible(false);
        frmInventories.setVisible(true);
    }//GEN-LAST:event_itemInventoriesActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancel;
    private javax.swing.JComboBox<String> cmbInitialStock;
    private javax.swing.JComboBox<String> cmbProductSupplier;
    private javax.swing.JFormattedTextField ftfInitialStock;
    private javax.swing.JFormattedTextField ftfPrice;
    private javax.swing.JMenuItem itemInventories;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTextField txtBarCode;
    private javax.swing.JTextPane txtDescription;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtProductName;
    // End of variables declaration//GEN-END:variables
}
