package ec.edu.espe.finvory.controller.report;

import ec.edu.espe.finvory.controller.ExportController;
import java.util.List;

/**
 *
 * @author Joseph B. Medina
 */
public class CsvReportExportStrategy implements ReportExportStrategy {

    private final ExportController exportController;

    public CsvReportExportStrategy(ExportController exportController) {
        this.exportController = exportController;
    }

    @Override
    public void export(String path, String reportTitle, String[] headers, List<Object[]> dataRows) {
        exportController.exportTableWithDateToCSV(path, reportTitle, headers, dataRows);
    }

}
