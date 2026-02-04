package ec.edu.espe.finvory.controller.report;

import ec.edu.espe.finvory.controller.ExportController;
import java.util.List;
import javax.swing.JTable;

/**
 *
 * @author Joseph B. Medina
 */
public final class ReportFlow {

    private ReportFlow() {
    }

    public static void export(
            java.awt.Component parent,
            String dialogTitle,
            String reportTitle,
            JTable table,
            ReportType type,
            ExportController exportController
    ) throws Exception {

        ReportExporter exporter = new ReportExporter();
        exporter.setStrategy(ReportStrategyResolver.resolve(type, exportController));

        String path = ReportUiHelper.askSavePath(
                parent,
                dialogTitle,
                exporter.getStrategy().format()
        );
        if (path == null) {
            return;
        }

        String[] headers = ReportUiHelper.extractHeaders(table);
        List<Object[]> rows = ReportUiHelper.extractRows(table);

        exporter.export(path, reportTitle, headers, rows);
    }
}