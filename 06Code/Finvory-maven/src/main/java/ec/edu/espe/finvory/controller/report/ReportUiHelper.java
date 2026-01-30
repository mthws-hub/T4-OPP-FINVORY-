package ec.edu.espe.finvory.controller.report;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

/**
 *
 * @author Joseph B. Medina
 */
public final class ReportUiHelper {

    private ReportUiHelper() {
    }

    public enum Format {
        CSV, PDF
    }

    public static Format askFormat(java.awt.Component parent) {
        Object[] options = {"CSV", "PDF"};
        int choice = JOptionPane.showOptionDialog(
                parent,
                "¿En qué formato deseas guardar el reporte?",
                "Exportar reporte",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) {
            return Format.CSV;
        }
        if (choice == 1) {
            return Format.PDF;
        }
        return null;
    }

    public static String askSavePath(java.awt.Component parent, String dialogTitle, Format format) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(dialogTitle);

        if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File file = chooser.getSelectedFile();
        String path = file.getAbsolutePath();

        String ext = (format == Format.CSV) ? ".csv" : ".pdf";
        if (!path.toLowerCase().endsWith(ext)) {
            path += ext;
        }
        return path;
    }

    public static String[] extractHeaders(JTable table) {
        TableModel model = table.getModel();
        String[] headers = new String[model.getColumnCount()];
        for (int c = 0; c < model.getColumnCount(); c++) {
            headers[c] = model.getColumnName(c);
        }
        return headers;
    }

    public static List<Object[]> extractRows(JTable table) {
        TableModel model = table.getModel();
        List<Object[]> rows = new ArrayList<>();

        for (int r = 0; r < model.getRowCount(); r++) {
            Object[] row = new Object[model.getColumnCount()];
            for (int c = 0; c < model.getColumnCount(); c++) {
                row[c] = model.getValueAt(r, c);
            }
            rows.add(row);
        }
        return rows;
    }

}
