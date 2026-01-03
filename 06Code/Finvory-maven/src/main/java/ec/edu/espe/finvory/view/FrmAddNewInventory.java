package ec.edu.espe.finvory.view;

import ec.edu.espe.finvory.controller.FinvoryController;
import ec.edu.espe.finvory.model.Address;
import ec.edu.espe.finvory.model.Inventory;
import ec.edu.espe.finvory.utils.ValidationUtils;
import java.awt.Color;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 *
 * @author Mathews Pastor, The POOwer Rangers Of Programming
 */
public class FrmAddNewInventory extends JDialog {

    private FinvoryController controller;

    public FrmAddNewInventory(java.awt.Frame parent, boolean modal, FinvoryController controller) {
        super(parent, modal);
        this.controller = controller;
        initComponents();
        this.setLocationRelativeTo(null);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        
    }

    public FrmAddNewInventory(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblCountry = new javax.swing.JLabel();
        txtCountry = new javax.swing.JTextField();
        lblCity = new javax.swing.JLabel();
        txtCity = new javax.swing.JTextField();
        lblStreet = new javax.swing.JLabel();
        txtStreet = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtZipCode = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtStreetNumber = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtRegion = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        itemInventories = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(224, 224, 224));

        jLabel1.setFont(new java.awt.Font("Perpetua Titling MT", 1, 20)); // NOI18N
        jLabel1.setText("DATOS GENERALES");

        lblName.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        lblName.setText("Nombre:");

        lblCountry.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        lblCountry.setText("Pais:");

        lblCity.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        lblCity.setText("Ciudad:");

        lblStreet.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        lblStreet.setText("Calle:");

        jLabel6.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        jLabel6.setText("Codigo Postal:");

        jLabel7.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        jLabel7.setText("Numero de Calle:");

        jLabel8.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        jLabel8.setText("Region:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblName)
                            .addComponent(lblCountry, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(lblStreet))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCountry, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtRegion, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(txtStreetNumber, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtStreet, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)))
                        .addGap(11, 11, 11)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(lblCity))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCity, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtZipCode, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel1)))
                .addContainerGap(63, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblName)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCountry)
                    .addComponent(txtCountry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCity)
                    .addComponent(txtCity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStreet)
                    .addComponent(txtStreet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtZipCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtStreetNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtRegion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(109, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(224, 224, 224));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnAdd.setBackground(new java.awt.Color(0, 123, 0));
        btnAdd.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        btnAdd.setForeground(new java.awt.Color(255, 255, 255));
        btnAdd.setText("AGREGAR");
        btnAdd.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        jPanel2.add(btnAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 20, 110, 30));

        btnCancel.setBackground(new java.awt.Color(0, 123, 0));
        btnCancel.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14)); // NOI18N
        btnCancel.setForeground(new java.awt.Color(255, 255, 255));
        btnCancel.setText("CANCELAR");
        btnCancel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        jPanel2.add(btnCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 20, 110, 30));

        jMenu1.setText("Finvory");
        jMenu1.addMenuKeyListener(new javax.swing.event.MenuKeyListener() {
            public void menuKeyPressed(javax.swing.event.MenuKeyEvent evt) {
                jMenu1MenuKeyPressed(evt);
            }
            public void menuKeyReleased(javax.swing.event.MenuKeyEvent evt) {
            }
            public void menuKeyTyped(javax.swing.event.MenuKeyEvent evt) {
            }
        });

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
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        resetColors();

        String name = txtName.getText().trim();
        String country = txtCountry.getText().trim();
        String city = txtCity.getText().trim();
        String street = txtStreet.getText().trim();
        String streetNumber = txtStreetNumber.getText().trim();
        String zipCode = txtZipCode.getText().trim();
        String region = txtRegion.getText().trim();

        boolean hasError = false;
        StringBuilder errorMsg = new StringBuilder();

        if (ValidationUtils.isEmpty(name)) {
            lblName.setForeground(Color.RED);
            hasError = true;
        }
        if (ValidationUtils.isEmpty(country)) {
            lblCountry.setForeground(Color.RED);
            hasError = true;
        } else if (!ValidationUtils.isTextOnly(country)) {
            lblCountry.setForeground(Color.RED);
            errorMsg.append("- País solo debe contener letras.\n");
            hasError = true;
        }
        if (ValidationUtils.isEmpty(city)) {
            lblCity.setForeground(Color.RED);
            hasError = true;
        } else if (!ValidationUtils.isTextOnly(city)) {
            lblCity.setForeground(Color.RED);
            errorMsg.append("- Ciudad solo debe contener letras.\n");
            hasError = true;
        }
        if (ValidationUtils.isEmpty(street)) {
            lblStreet.setForeground(Color.RED);
            hasError = true;
        } else if (!ValidationUtils.isTextOnly(street)) {
            lblStreet.setForeground(Color.RED);
            errorMsg.append("- Calle solo debe contener letras.\n");
            hasError = true;
        }
        if (ValidationUtils.isEmpty(streetNumber)) {
            jLabel7.setForeground(Color.RED);
            hasError = true;
        } else if (!ValidationUtils.isNumeric(streetNumber)) {
            jLabel7.setForeground(Color.RED);
            errorMsg.append("- Número de calle debe ser numérico.\n");
            hasError = true;
        }
        if (ValidationUtils.isEmpty(region)) {
            jLabel8.setForeground(Color.RED);
            hasError = true;
        } else if (!ValidationUtils.isTextOnly(region)) {
            jLabel8.setForeground(Color.RED);
            errorMsg.append("- Región solo debe contener letras.\n");
            hasError = true;
        }
        if (!ValidationUtils.isEmpty(zipCode) && !ValidationUtils.isNumeric(zipCode)) {
            jLabel6.setForeground(Color.RED);
            errorMsg.append("- Código Postal debe ser numérico.\n");
            hasError = true;
        }
        if (hasError) {
            String finalMsg = errorMsg.length() > 0 ? errorMsg.toString() : "Por favor llene correctamente los campos marcados en rojo.";
            JOptionPane.showMessageDialog(this, finalMsg, "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Address address = new Address(country, city, street);
        address.setStreetNumber(streetNumber);
        address.setZipCode(zipCode);
        address.setRegion(region);

        boolean success = controller.handleCreateInventory(name, address);

        if (success) {
            JOptionPane.showMessageDialog(this, "Inventario creado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error: Ya existe un inventario con ese nombre.", "Error al Guardar", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnAddActionPerformed

    private void itemInventoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemInventoriesActionPerformed
        this.dispose();
    }//GEN-LAST:event_itemInventoriesActionPerformed

    private void jMenu1MenuKeyPressed(javax.swing.event.MenuKeyEvent evt) {//GEN-FIRST:event_jMenu1MenuKeyPressed

    }//GEN-LAST:event_jMenu1MenuKeyPressed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        resetColors();
        int opt = JOptionPane.showConfirmDialog(this, "Sus datos ingresados se perderán ¿Está seguro?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            txtCity.setText("");
            txtCountry.setText("");
            txtName.setText("");
            txtRegion.setText("");
            txtStreet.setText("");
            txtStreetNumber.setText("");
            txtZipCode.setText("");
        }
    }//GEN-LAST:event_btnCancelActionPerformed
    private void resetColors() {
        lblCountry.setForeground(Color.black);
        lblCity.setForeground(Color.black);
        lblStreet.setForeground(Color.black);
        lblName.setForeground(Color.black);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancel;
    private javax.swing.JMenuItem itemInventories;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblCity;
    private javax.swing.JLabel lblCountry;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblStreet;
    private javax.swing.JTextField txtCity;
    private javax.swing.JTextField txtCountry;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtRegion;
    private javax.swing.JTextField txtStreet;
    private javax.swing.JTextField txtStreetNumber;
    private javax.swing.JTextField txtZipCode;
    // End of variables declaration//GEN-END:variables
}
