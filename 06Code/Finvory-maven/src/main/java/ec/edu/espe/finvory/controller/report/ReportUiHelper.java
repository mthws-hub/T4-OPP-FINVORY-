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
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(title);

        if (fileChooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File file = fileChooser.getSelectedFile();
        String ext = format == ReportFormat.PDF ? ".pdf" : ".csv";
        String path = file.getAbsolutePath();

        if (!path.toLowerCase().endsWith(ext)) {
            path += ext;
        }
        return path;
    }

    public static String[] extractHeaders(JTable table) {
        TableModel model = table.getModel();
        String[] headers = new String[model.getColumnCount()];
        for (int i = 0; i < model.getColumnCount(); i++) {
            headers[i] = model.getColumnName(i);
        }
        return headers;
    }

    public static List<Object[]> extractRows(JTable table) {
        TableModel model = table.getModel();
        List<Object[]> rows = new ArrayList<>();
        for (int rowwCount = 0; rowwCount < model.getRowCount(); rowwCount++) {
            Object[] row = new Object[model.getColumnCount()];
            for (int columnCount = 0; columnCount < model.getColumnCount(); columnCount++) {
                row[columnCount] = model.getValueAt(rowwCount, columnCount);
            }
            rows.add(row);
        }
        return rows;
    }
}
