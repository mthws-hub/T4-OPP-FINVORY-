package ec.edu.espe.finvory.view;

import ec.edu.espe.finvory.controller.FinvoryController;
import ec.edu.espe.finvory.model.InvoiceSim;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Joseph B. Medina
 */
public class FrmGrossReport extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmGrossReport.class.getName());
    private final FinvoryController controller;
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Creates new form FrmGrossReport1
     * @param controller
     */
    public FrmGrossReport(FinvoryController controller) {
        this.controller = controller;
        initComponents();

        ButtonStyles.applyPrimaryStyle(btnFilter);
        ButtonStyles.applyPrimaryStyle(btnExportCsv);
        ButtonStyles.applyPrimaryStyle(btnRefresh);
        spnFromDate.setEditor(new javax.swing.JSpinner.DateEditor(spnFromDate, "dd/MM/yyyy"));
        spnToDate.setEditor(new javax.swing.JSpinner.DateEditor(spnToDate, "dd/MM/yyyy"));
        ((javax.swing.JSpinner.DefaultEditor) spnFromDate.getEditor()).getTextField().setColumns(10);
        ((javax.swing.JSpinner.DefaultEditor) spnToDate.getEditor()).getTextField().setColumns(10);

        Date now = new Date();
        spnFromDate.setValue(now);
        spnToDate.setValue(now);

        javax.swing.ButtonGroup grp = new javax.swing.ButtonGroup();
        grp.add(radioButtonPerDay);
        grp.add(radioButtonperInvoice);
        radioButtonPerDay.setSelected(true);

        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
        runReport();
    }
    
    private Date getFromDateStartOfDay() {
        Date date = (Date) spnFromDate.getValue();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private Date getToDateEndOfDay() {
        Date date = (Date) spnToDate.getValue();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    private boolean validateDates(Date from, Date to) {
        if (from.after(to)) {
            JOptionPane.showMessageDialog(
                    this,
                    "La fecha 'Desde' no puede ser mayor que 'Hasta'.",
                    "Fechas inválidas",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
        return true;
    }

    private LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private String money(BigDecimal value) {
        if (value == null) {
            value = BigDecimal.ZERO;
        }
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("es", "EC"));
        return numberFormat.format(value);
    }

    private void runReport() {
        if (controller == null || controller.getData() == null || controller.getData().getInvoices() == null) {

            lblGrossTodayValue1.setText(money(BigDecimal.ZERO));
            lblGrossHistoricValue1.setText(money(BigDecimal.ZERO));
            lblInvoiceCountValue1.setText("0");
            lblTotalGrossFooterValue.setText(money(BigDecimal.ZERO));

            scrGrossTable.setModel(new DefaultTableModel(
                    new Object[]{"Fecha", "Facturas", "Bruto", "Neto", "Total (con IVA)"}, 0
            ));
            return;
        }

        Date fromDate = getFromDateStartOfDay();
        Date toDate = getToDateEndOfDay();
        if (!validateDates(fromDate, toDate)) {
            return;
        }

        LocalDate from = toLocalDate(fromDate);
        LocalDate to = toLocalDate(toDate);

        boolean includePending = checkBoxIncludePendingInvoices.isSelected();
        boolean perDay = radioButtonPerDay.isSelected();

        List<InvoiceSim> invoices = controller.getData().getInvoices();

        List<InvoiceSim> filtered = new ArrayList<>();
        for (InvoiceSim invoice : invoices) {
            if (invoice == null || invoice.getDate() == null) {
                continue;
            }

            LocalDate d = invoice.getDate();
            boolean inRange = (!d.isBefore(from)) && (!d.isAfter(to));
            if (!inRange) {
                continue;
            }

            boolean isCompleted = "COMPLETED".equals(invoice.getStatus());
            if (!includePending && !isCompleted) {
                continue;
            }

            filtered.add(invoice);
        }

        BigDecimal grossRange = BigDecimal.ZERO;
        for (InvoiceSim invoice : filtered) {
            grossRange = grossRange.add(invoice.getSubtotal() != null ? invoice.getSubtotal() : BigDecimal.ZERO);
        }

        LocalDate today = LocalDate.now();
        BigDecimal grossToday = BigDecimal.ZERO;
        for (InvoiceSim invoice : invoices) {
            if (invoice == null || invoice.getDate() == null) {
                continue;
            }
            if (!invoice.getDate().equals(today)) {
                continue;
            }

            boolean isCompleted = "COMPLETED".equals(invoice.getStatus());
            if (!includePending && !isCompleted) {
                continue;
            }

            grossToday = grossToday.add(invoice.getSubtotal() != null ? invoice.getSubtotal() : BigDecimal.ZERO);
        }

        lblGrossTodayValue1.setText(money(grossToday));
        lblGrossHistoricValue1.setText(money(grossRange));
        lblInvoiceCountValue1.setText(String.valueOf(filtered.size()));
        lblTotalGrossFooterValue.setText(money(grossRange));

        if (perDay) {
            loadTablePerDay(filtered);
        } else {
            loadTablePerInvoice(filtered);
        }
    }

    private void loadTablePerDay(List<InvoiceSim> invoices) {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Fecha", "Facturas", "Bruto", "Neto", "Total (con IVA)"}, 0
        );

        Map<LocalDate, Integer> count = new HashMap<>();
        Map<LocalDate, BigDecimal> brutoSum = new HashMap<>();
        Map<LocalDate, BigDecimal> netoSum = new HashMap<>();
        Map<LocalDate, BigDecimal> totalSum = new HashMap<>();

        for (InvoiceSim invoice : invoices) {
            LocalDate date = invoice.getDate();
            count.put(date, count.getOrDefault(date, 0) + 1);

            BigDecimal bruto = invoice.getSubtotal() != null ? invoice.getSubtotal() : BigDecimal.ZERO;
            BigDecimal descuento = invoice.getDiscountAmount() != null ? invoice.getDiscountAmount() : BigDecimal.ZERO;
            BigDecimal neto = bruto.subtract(descuento);
            BigDecimal total = invoice.getTotal() != null ? invoice.getTotal() : BigDecimal.ZERO;

            brutoSum.put(date, brutoSum.getOrDefault(date, BigDecimal.ZERO).add(bruto));
            netoSum.put(date, netoSum.getOrDefault(date, BigDecimal.ZERO).add(neto));
            totalSum.put(date, totalSum.getOrDefault(date, BigDecimal.ZERO).add(total));
        }

        List<LocalDate> days = new ArrayList<>(count.keySet());
        days.sort(Comparator.naturalOrder());

        for (LocalDate date : days) {
            model.addRow(new Object[]{
                date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                count.get(date),
                money(brutoSum.get(date)),
                money(netoSum.get(date)),
                money(totalSum.get(date))
            });
        }

        scrGrossTable.setModel(model);
    }

    private void loadTablePerInvoice(List<InvoiceSim> invoices) {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Fecha", "Factura", "Bruto", "Neto", "Total (con IVA)"}, 0
        );

        invoices.sort(Comparator.comparing(InvoiceSim::getDate));

        for (InvoiceSim invoice : invoices) {
            BigDecimal bruto = invoice.getSubtotal() != null ? invoice.getSubtotal() : BigDecimal.ZERO;
            BigDecimal descuento = invoice.getDiscountAmount() != null ? invoice.getDiscountAmount() : BigDecimal.ZERO;
            BigDecimal neto = bruto.subtract(descuento);
            BigDecimal total = invoice.getTotal() != null ? invoice.getTotal() : BigDecimal.ZERO;

            model.addRow(new Object[]{
                invoice.getDate().format(DF),
                invoice.getId(),
                money(bruto),
                money(neto),
                money(total)
            });
        }

        scrGrossTable.setModel(model);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        spnFromDate = new javax.swing.JSpinner();
        jLabel9 = new javax.swing.JLabel();
        spnToDate = new javax.swing.JSpinner();
        checkBoxIncludePendingInvoices = new javax.swing.JCheckBox();
        btnFilter = new javax.swing.JButton();
        radioButtonPerDay = new javax.swing.JRadioButton();
        radioButtonperInvoice = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        scrGrossTable = new javax.swing.JTable();
        btnRefresh = new javax.swing.JButton();
        btnExportCsv = new javax.swing.JButton();
        lblTotalGrossFooterTitle = new javax.swing.JLabel();
        lblTotalGrossFooterValue = new javax.swing.JLabel();
        lblGrossTodayValue1 = new javax.swing.JLabel();
        lblGrossHistoricValue1 = new javax.swing.JLabel();
        lblInvoiceCountValue1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lblTitle.setFont(new java.awt.Font("Perpetua", 1, 36)); // NOI18N
        lblTitle.setText("Reporte de Ventas Brutas");

        jLabel5.setText("Total Ventas Brutas Hoy");

        jLabel6.setText("Total Ventas Brutas Histórico");

        jLabel7.setText("Número de Facturas");

        jLabel8.setText("Desde:");

        spnFromDate.setModel(new javax.swing.SpinnerDateModel());

        jLabel9.setText("Hasta:");

        spnToDate.setModel(new javax.swing.SpinnerDateModel());

        checkBoxIncludePendingInvoices.setText("Incluir Facturas Pendientes");
        checkBoxIncludePendingInvoices.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxIncludePendingInvoicesActionPerformed(evt);
            }
        });

        btnFilter.setBackground(new java.awt.Color(0, 123, 0));
        btnFilter.setForeground(new java.awt.Color(255, 255, 255));
        btnFilter.setText("Filtrar");
        btnFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterActionPerformed(evt);
            }
        });

        radioButtonPerDay.setText("Por Día");
        radioButtonPerDay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonPerDayActionPerformed(evt);
            }
        });

        radioButtonperInvoice.setText("Por Factura");
        radioButtonperInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonperInvoiceActionPerformed(evt);
            }
        });

        scrGrossTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Fecha", "Facturas", "Bruto", "Neto", "Total (con IVA)"
            }
        ));
        jScrollPane1.setViewportView(scrGrossTable);

        btnRefresh.setBackground(new java.awt.Color(0, 123, 0));
        btnRefresh.setForeground(new java.awt.Color(255, 255, 255));
        btnRefresh.setText("Refrescar");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        btnExportCsv.setBackground(new java.awt.Color(0, 123, 0));
        btnExportCsv.setForeground(new java.awt.Color(255, 255, 255));
        btnExportCsv.setText("Exportar");
        btnExportCsv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportCsvActionPerformed(evt);
            }
        });

        lblTotalGrossFooterTitle.setText("Total Bruto:");

        lblTotalGrossFooterValue.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblTotalGrossFooterValue.setText("$0.00");

        lblGrossTodayValue1.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lblGrossTodayValue1.setText("$0.00");

        lblGrossHistoricValue1.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lblGrossHistoricValue1.setText("$0.00");

        lblInvoiceCountValue1.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lblInvoiceCountValue1.setText("0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(spnFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(checkBoxIncludePendingInvoices))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(spnToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnFilter)
                                .addGap(77, 77, 77))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel6)))
                .addGap(136, 136, 136)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(radioButtonperInvoice)
                    .addComponent(radioButtonPerDay)
                    .addComponent(jLabel7))
                .addGap(55, 55, 55))
            .addGroup(layout.createSequentialGroup()
                .addGap(72, 72, 72)
                .addComponent(lblGrossTodayValue1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblGrossHistoricValue1)
                .addGap(249, 249, 249)
                .addComponent(lblInvoiceCountValue1)
                .addGap(108, 108, 108))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(143, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTotalGrossFooterTitle)
                        .addGap(18, 18, 18)
                        .addComponent(lblTotalGrossFooterValue))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 556, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(131, 131, 131))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(btnRefresh)
                        .addGap(18, 18, 18)
                        .addComponent(btnExportCsv))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(215, 215, 215)
                        .addComponent(lblTitle)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel6)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblGrossTodayValue1)
                    .addComponent(lblGrossHistoricValue1)
                    .addComponent(lblInvoiceCountValue1))
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(spnFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkBoxIncludePendingInvoices)
                    .addComponent(radioButtonPerDay))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9)
                        .addComponent(spnToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnFilter))
                    .addComponent(radioButtonperInvoice))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotalGrossFooterTitle)
                    .addComponent(lblTotalGrossFooterValue))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnExportCsv)
                    .addComponent(btnRefresh))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void checkBoxIncludePendingInvoicesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxIncludePendingInvoicesActionPerformed
        runReport();
    }//GEN-LAST:event_checkBoxIncludePendingInvoicesActionPerformed

    private void btnFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterActionPerformed
        runReport();
    }//GEN-LAST:event_btnFilterActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        runReport();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnExportCsvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportCsvActionPerformed
        onExportReport();
    }//GEN-LAST:event_btnExportCsvActionPerformed

    private void radioButtonPerDayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonPerDayActionPerformed
        runReport();
    }//GEN-LAST:event_radioButtonPerDayActionPerformed

    private void radioButtonperInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonperInvoiceActionPerformed
        runReport();
    }//GEN-LAST:event_radioButtonperInvoiceActionPerformed

    private void onExportReport() {

        ec.edu.espe.finvory.controller.report.ReportFormat format
                = ec.edu.espe.finvory.controller.report.ReportFormat.CSV;

        String path = ec.edu.espe.finvory.controller.report.ReportUiHelper.askSavePath(
                this,
                "Guardar Reporte de Ventas Brutas",
                format
        );
        if (path == null) {
            return;
        }

        String reportTitle = "REPORTE DE VENTAS BRUTAS";

        String[] headers = ec.edu.espe.finvory.controller.report.ReportUiHelper.extractHeaders(scrGrossTable);
        java.util.List<Object[]> rows = ec.edu.espe.finvory.controller.report.ReportUiHelper.extractRows(scrGrossTable);

        ec.edu.espe.finvory.controller.report.ReportExporter exporter
                = new ec.edu.espe.finvory.controller.report.ReportExporter();

        try {
            exporter.setStrategy(new ec.edu.espe.finvory.controller.report.CsvReportExportStrategy(controller.exportController));
            exporter.export(path, reportTitle, headers, rows);

            javax.swing.JOptionPane.showMessageDialog(this, "Reporte exportado con éxito.");
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage());
            logger.log(java.util.logging.Level.SEVERE, "Error exportando reporte", ex);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExportCsv;
    private javax.swing.JButton btnFilter;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JCheckBox checkBoxIncludePendingInvoices;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblGrossHistoricValue1;
    private javax.swing.JLabel lblGrossTodayValue1;
    private javax.swing.JLabel lblInvoiceCountValue1;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTotalGrossFooterTitle;
    private javax.swing.JLabel lblTotalGrossFooterValue;
    private javax.swing.JRadioButton radioButtonPerDay;
    private javax.swing.JRadioButton radioButtonperInvoice;
    private javax.swing.JTable scrGrossTable;
    private javax.swing.JSpinner spnFromDate;
    private javax.swing.JSpinner spnToDate;
    // End of variables declaration//GEN-END:variables
}
