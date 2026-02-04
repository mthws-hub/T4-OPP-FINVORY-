package ec.edu.espe.finvory.view;

import ec.edu.espe.finvory.FinvoryApp;
import ec.edu.espe.finvory.controller.FinvoryController;
import ec.edu.espe.finvory.model.Supplier;
import ec.edu.espe.finvory.utils.ValidationUtils;
import java.awt.Color;
import javax.swing.JOptionPane;

/**
 *
 * @author Maryuri Quiña, The POOwer Rangers Of Programming
 */
public class FrmAddNewSupplier extends javax.swing.JFrame {

    private FinvoryController controller;
    private String supplierIdToEdit = null; 
    private final Color ERROR_COLOR = Color.RED;
    private final Color DEFAULT_COLOR = Color.BLACK;

    public FrmAddNewSupplier(FinvoryController controller) {
        this(controller, null);
        FinvoryApp.setIcon(this);
    }
    
    public FrmAddNewSupplier(FinvoryController controller, String supplierId) {
        this.controller = controller;
        this.supplierIdToEdit = supplierId;
        initComponents();
        this.setLocationRelativeTo(null);
        setupFormMode();
        FinvoryApp.setIcon(this);
        ButtonStyles.applyPrimaryStyle(btnAdd);
        ButtonStyles.applyPrimaryStyle(btnCancel);
    }

    private void setupFormMode() {
        if (supplierIdToEdit != null) {
            lblTitle.setText("EDITAR PROVEEDOR");
            btnAdd.setText("ACTUALIZAR");
            txtId1Supplier.setEnabled(false);
            loadSupplierData();
        } else {
            lblTitle.setText("REGISTRO PROVEEDOR");
            btnAdd.setText("AGREGAR");
            txtId1Supplier.setEnabled(true);
        }
    }

    private void loadSupplierData() {
        Supplier supplier = controller.supplierController.findSupplier(supplierIdToEdit);
        if (supplier != null) {
            txtId1Supplier.setText(supplier.getId1());
            txtId2Supplier.setText(supplier.getId2());
            txtName.setText(supplier.getFullName());
            txtPhone.setText(supplier.getPhone());
            txtEmail.setText(supplier.getEmail());
            txtpDescription.setText(supplier.getDescription());
        } else {
            JOptionPane.showMessageDialog(this, "Error: No se pudo cargar la información del proveedor.");
            this.dispose();
        }
    }

    private void emptyFields() {
        if (supplierIdToEdit == null) {
            txtId1Supplier.setText("");
        }
        txtId2Supplier.setText("");
        txtName.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtpDescription.setText("");
    }

    private boolean validateSupplierData() {
        resetColors();
        String id1 = txtId1Supplier.getText().trim();
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();

        StringBuilder errors = new StringBuilder();

        if (ValidationUtils.isEmpty(name)) {
            lblFullName.setForeground(ERROR_COLOR);
            errors.append("- El nombre del proveedor es obligatorio.\n");
        } else if (!ValidationUtils.isTextOnly(name)) {
            lblFullName.setForeground(ERROR_COLOR);
            errors.append("- El nombre del proveedor solo debe contener letras.\n");
        }

        if (supplierIdToEdit == null) {
            if (!ValidationUtils.isStrictRuc(id1)) {
                lblId1.setForeground(ERROR_COLOR);
                errors.append("- El RUC debe tener 13 dígitos numéricos y empezar en '001'.\n");
            }
        }

        if (ValidationUtils.isEmpty(phone)) {
            errors.append("- El celular del proveedor es obligatorio.\n");
            lblPhone.setForeground(ERROR_COLOR);
        } else if (!ValidationUtils.isValidPhone10Digits(phone)) {
            lblPhone.setForeground(ERROR_COLOR);
            errors.append("- El celular debe tener exactamente 10 dígitos numéricos.\n");
        }

        if (ValidationUtils.isEmpty(email)) {
            errors.append("- El correo del proveedor es obligatorio.\n");
            lblEmail.setForeground(ERROR_COLOR);
        } else if (!ValidationUtils.isValidEmail(email)) {
            lblEmail.setForeground(ERROR_COLOR);
            errors.append("- El correo electrónico no es válido.\n");
        }

        if (errors.length() > 0) {
            JOptionPane.showMessageDialog(this, "Por favor corrija los siguientes errores:\n\n" + errors.toString(), "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void resetColors() {
        lblFullName.setForeground(DEFAULT_COLOR);
        lblPhone.setForeground(DEFAULT_COLOR);
        lblId1.setForeground(DEFAULT_COLOR);
        lblEmail.setForeground(DEFAULT_COLOR);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblId1 = new javax.swing.JLabel();
        lblFullName = new javax.swing.JLabel();
        lblPhone = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        lblDescription = new javax.swing.JLabel();
        lblId2 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtpDescription = new javax.swing.JTextPane();
        txtId1Supplier = new javax.swing.JTextField();
        txtId2Supplier = new javax.swing.JTextField();
        txtName = new javax.swing.JTextField();
        txtPhone = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        itemSuppliers = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lblTitle.setFont(new java.awt.Font("Perpetua Titling MT", 1, 20)); // NOI18N
        lblTitle.setText("Registro Proveedor");

        lblId1.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        lblId1.setText("ID 1 (RUC):");

        lblFullName.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        lblFullName.setText("Nombre Completo:");

        lblPhone.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        lblPhone.setText("Telefono:");

        lblEmail.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        lblEmail.setText("Email:");

        lblDescription.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        lblDescription.setText("Descripcion:");

        lblId2.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        lblId2.setText("ID 2 (Opcional):");

        jScrollPane5.setViewportView(txtpDescription);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblId1)
                    .addComponent(lblFullName)
                    .addComponent(lblPhone)
                    .addComponent(lblEmail)
                    .addComponent(lblDescription)
                    .addComponent(lblId2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtId2Supplier, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                        .addComponent(txtId1Supplier, javax.swing.GroupLayout.Alignment.LEADING))
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblId1)
                    .addComponent(txtId1Supplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblId2)
                    .addComponent(txtId2Supplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFullName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblPhone)
                    .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblEmail)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDescription)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 74, Short.MAX_VALUE)
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

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(116, 116, 116)
                .addComponent(btnAdd)
                .addGap(94, 94, 94)
                .addComponent(btnCancel)
                .addContainerGap(103, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAdd)
                    .addComponent(btnCancel))
                .addContainerGap(36, Short.MAX_VALUE))
        );

        jMenu1.setText("Finvory");

        itemSuppliers.setText("Proveedores");
        itemSuppliers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemSuppliersActionPerformed(evt);
            }
        });
        jMenu1.add(itemSuppliers);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(147, 147, 147)
                .addComponent(lblTitle)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        onAddOrUpdate();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        onCancel();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void itemSuppliersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemSuppliersActionPerformed
        this.dispose();
    }//GEN-LAST:event_itemSuppliersActionPerformed
    
    private void onAddOrUpdate() {
        if (!validateSupplierData()) {
            return;
        }

        SupplierFormData data = readForm();

        boolean success;
        if (supplierIdToEdit == null) {
            success = controller.supplierController.createSupplierGUI(
                    data.id1, data.id2, data.name, data.phone, data.email, data.desc
            );

            if (success) {
                JOptionPane.showMessageDialog(this, "Proveedor creado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                emptyFields();
            } else {
                JOptionPane.showMessageDialog(this, "El proveedor ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }

        success = controller.supplierController.handleUpdateSupplierGUI(
                supplierIdToEdit, data.name, data.phone, data.email, data.desc, data.id2
        );

        if (success) {
            JOptionPane.showMessageDialog(this, "Proveedor actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
        resetColors();
        int option = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de cancelar?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            if (supplierIdToEdit != null) {
                this.dispose();
            } else {
                emptyFields();
            }
        }
    }

    private SupplierFormData readForm() {
        SupplierFormData data = new SupplierFormData();
        data.id1 = txtId1Supplier.getText().trim();
        data.id2 = txtId2Supplier.getText().trim();
        data.name = txtName.getText().trim();
        data.phone = txtPhone.getText().trim();
        data.email = txtEmail.getText().trim();
        data.desc = txtpDescription.getText().trim();
        return data;
    }

    private static class SupplierFormData {
        String id1;
        String id2;
        String name;
        String phone;
        String email;
        String desc;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancel;
    private javax.swing.JMenuItem itemSuppliers;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblFullName;
    private javax.swing.JLabel lblId1;
    private javax.swing.JLabel lblId2;
    private javax.swing.JLabel lblPhone;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtId1Supplier;
    private javax.swing.JTextField txtId2Supplier;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPhone;
    private javax.swing.JTextPane txtpDescription;
    // End of variables declaration//GEN-END:variables
}
