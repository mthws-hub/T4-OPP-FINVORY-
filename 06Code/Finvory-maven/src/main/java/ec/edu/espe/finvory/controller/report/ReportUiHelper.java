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

    public static String askSavePath(java.awt.Component parent, String title, ReportFormat format) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(title);

        if (fc.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File f = fc.getSelectedFile();
        String ext = format == ReportFormat.PDF ? ".pdf" : ".csv";
        String path = f.getAbsolutePath();

        if (!path.toLowerCase().endsWith(ext)) {
            path += ext;
        }
        return path;
    }

    public static String[] extractHeaders(JTable table) {
        TableModel m = table.getModel();
        String[] headers = new String[m.getColumnCount()];
        for (int i = 0; i < m.getColumnCount(); i++) {
            headers[i] = m.getColumnName(i);
        }
        return headers;
    }

    public static List<Object[]> extractRows(JTable table) {
        TableModel m = table.getModel();
        List<Object[]> rows = new ArrayList<>();
        for (int r = 0; r < m.getRowCount(); r++) {
            Object[] row = new Object[m.getColumnCount()];
            for (int c = 0; c < m.getColumnCount(); c++) {
                row[c] = m.getValueAt(r, c);
            }
            rows.add(row);
        }
        return rows;
    }
}
