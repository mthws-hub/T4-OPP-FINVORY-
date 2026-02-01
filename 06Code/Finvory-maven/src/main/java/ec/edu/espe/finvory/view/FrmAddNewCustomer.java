package ec.edu.espe.finvory.view;

import ec.edu.espe.finvory.FinvoryApp;
import ec.edu.espe.finvory.controller.FinvoryController;
import ec.edu.espe.finvory.utils.ValidationUtils;
import ec.edu.espe.finvory.model.Customer;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Maryuri Quiña, The POOwer Rangers Of Programming
 */
public class FrmAddNewCustomer extends javax.swing.JFrame {

    private FinvoryController controller;
    private String customerIdToEdit = null;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmAddNewCustomer.class.getName());

    private final java.awt.Color ERROR_COLOR = java.awt.Color.RED;
    private final java.awt.Color DEFAULT_COLOR = java.awt.Color.BLACK;

    public FrmAddNewCustomer(FinvoryController controller) {
        initComponents();
        this.controller = controller;
        this.setLocationRelativeTo(null);
        this.toFront();
        this.requestFocus();
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        FinvoryApp.setIcon(this);
        ButtonStyles.applyPrimaryStyle(btnAdd);
        ButtonStyles.applyPrimaryStyle(btnCancel);
        ButtonStyles.applyPrimaryStyle(btnReturn);
    }

    public FrmAddNewCustomer(FinvoryController controller, String customerId) {
        this(controller);
        this.customerIdToEdit = customerId;
        loadCustomerData(customerId);
    }

    private void loadCustomerData(String customerId) {
        Customer customer = controller.customerController.findCustomerPublic(customerId);

        if (customer == null) {
            JOptionPane.showMessageDialog(this, "Cliente no encontrado para edición.", "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
            return;
        }
        txtID.setText(customer.getIdentification());
        txtID.setEnabled(false);
        txtName.setText(customer.getName());
        txtPhone.setText(customer.getPhone());
        txtEmail.setText(customer.getEmail());
        cmbTypeOfCustomer.setSelectedItem(customer.getClientType());
    }

    private void emptyFields() {
        txtName.setText("");
        txtID.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        cmbTypeOfCustomer.setSelectedIndex(0);
        txtID.setEnabled(true);
        this.customerIdToEdit = null;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        lblId = new javax.swing.JLabel();
        lblPhone = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        lblType = new javax.swing.JLabel();
        cmbTypeOfCustomer = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnReturn = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        txtName = new javax.swing.JTextField();
        txtID = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        txtPhone = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Perpetua Titling MT", 1, 20)); // NOI18N
        jLabel1.setText("Registro CLIENTE");

        lblName.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        lblName.setText("Nombre Completo:");

        lblId.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        lblId.setText("ID (RUC/CI):");

        lblPhone.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        lblPhone.setText("Teléfono:");

        lblEmail.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        lblEmail.setText("Email:");

        lblType.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        lblType.setText("Tipo:");

        cmbTypeOfCustomer.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 12)); // NOI18N
        cmbTypeOfCustomer.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "STANDARD", "PREMIUM", "VIP" }));

        btnAdd.setBackground(new java.awt.Color(0, 123, 0));
        btnAdd.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 12)); // NOI18N
        btnAdd.setForeground(new java.awt.Color(255, 255, 255));
        btnAdd.setText("Agregar");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnReturn.setBackground(new java.awt.Color(0, 123, 0));
        btnReturn.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 12)); // NOI18N
        btnReturn.setForeground(new java.awt.Color(255, 255, 255));
        btnReturn.setText("Volver");
        btnReturn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReturnActionPerformed(evt);
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
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(btnReturn))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(139, 139, 139)
                        .addComponent(btnAdd)
                        .addGap(44, 44, 44)
                        .addComponent(btnCancel)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAdd)
                    .addComponent(btnCancel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addComponent(btnReturn)
                .addContainerGap())
        );

        txtName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNameActionPerformed(evt);
            }
        });

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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblName)
                            .addComponent(lblId)
                            .addComponent(lblPhone)
                            .addComponent(lblEmail)
                            .addComponent(lblType))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbTypeOfCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(txtPhone, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
                                .addComponent(txtID, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtEmail, javax.swing.GroupLayout.Alignment.LEADING)))
                        .addGap(0, 44, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(144, 144, 144)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblName, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblId)
                        .addGap(18, 18, 18)
                        .addComponent(lblPhone))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(13, 13, 13)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEmail)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblType)
                    .addComponent(cmbTypeOfCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(50, Short.MAX_VALUE))
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
        onAdd();
    }//GEN-LAST:event_btnAddActionPerformed


    private void btnReturnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReturnActionPerformed
        onReturn();
    }//GEN-LAST:event_btnReturnActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        onCancel();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void txtIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIDActionPerformed

    }//GEN-LAST:event_txtIDActionPerformed

    private void txtNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNameActionPerformed

    }//GEN-LAST:event_txtNameActionPerformed

    private void onAdd() {
        resetFieldColors();
        CustomerFormData data = readForm();
        ValidationResult validation = validateForm(data);

        if (!validation.isValid) {
            showValidationErrors(validation);
            return;
        }

        if (saveCustomer(data)) {
            JOptionPane.showMessageDialog(this, "Cliente guardado exitosamente.");
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar: Verifique si el ID ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
        int option = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de cancelar el registro y borrar los datos?",
                "Confirmación",
                JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            emptyFields();
        }
    }

    private void onReturn() {
        this.dispose();
    }

    private void resetFieldColors() {
        txtID.setForeground(java.awt.Color.BLACK);
        txtName.setForeground(java.awt.Color.BLACK);
        txtEmail.setForeground(java.awt.Color.BLACK);
        txtPhone.setForeground(java.awt.Color.BLACK);
    }

    private CustomerFormData readForm() {
        CustomerFormData data = new CustomerFormData();
        data.id = txtID.getText().trim();
        data.name = txtName.getText().trim();
        data.email = txtEmail.getText().trim();
        data.phone = txtPhone.getText().trim();
        data.type = cmbTypeOfCustomer.getSelectedItem().toString();
        return data;
    }

    private ValidationResult validateForm(CustomerFormData data) {
        ValidationResult result = new ValidationResult();

        if (data.id.isEmpty() || !data.id.matches("\\d{10}|\\d{13}")) {
            result.add("- ID inválido (debe tener 10 o 13 dígitos).", Field.ID);
        }

        if (data.name.isEmpty() || !data.name.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            result.add("- Nombre inválido (solo letras).", Field.NAME);
        }

        if (data.email.isEmpty() || !data.email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            result.add("- Correo electrónico inválido.", Field.EMAIL);
        }

        if (data.phone.isEmpty() || !data.phone.matches("^\\d{7,15}$")) {
            result.add("- Teléfono inválido (7 a 15 dígitos).", Field.PHONE);
        }

        return result;
    }

    private void showValidationErrors(ValidationResult validation) {
        for (Field f : validation.fieldsWithError) {
            switch (f) {
                case ID ->
                    txtID.setForeground(java.awt.Color.RED);
                case NAME ->
                    txtName.setForeground(java.awt.Color.RED);
                case EMAIL ->
                    txtEmail.setForeground(java.awt.Color.RED);
                case PHONE ->
                    txtPhone.setForeground(java.awt.Color.RED);
            }
        }

        JOptionPane.showMessageDialog(
                this,
                "Por favor corrija los siguientes campos:\n" + validation.message.toString(),
                "Error de Validación",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private boolean saveCustomer(CustomerFormData data) {
        if (customerIdToEdit == null) {
            return controller.customerController.handleAddCustomer(
                    data.name, data.id, data.phone, data.email, data.type
            );
        }

        return controller.customerController.handleUpdateCustomerGUI(
                customerIdToEdit, data.name, data.phone, data.email, data.type
        );
    }

    private enum Field {
        ID, NAME, EMAIL, PHONE
    }

    private static class CustomerFormData {

        String id;
        String name;
        String email;
        String phone;
        String type;
    }

    private static class ValidationResult {

        boolean isValid = true;
        StringBuilder message = new StringBuilder();
        java.util.EnumSet<Field> fieldsWithError = java.util.EnumSet.noneOf(Field.class);

        void add(String msg, Field field) {
            isValid = false;
            message.append(msg).append("\n");
            fieldsWithError.add(field);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnReturn;
    private javax.swing.JComboBox<String> cmbTypeOfCustomer;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblId;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPhone;
    private javax.swing.JLabel lblType;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPhone;
    // End of variables declaration//GEN-END:variables
}
