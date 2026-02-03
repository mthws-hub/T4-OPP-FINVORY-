package ec.edu.espe.finvory.controller.report;

/**
 *
 * @author Joseph B. Medina
 */
public class CsvReportExportStrategy implements ReportExportStrategy {

    private final ec.edu.espe.finvory.controller.ExportController exportController;

    public CsvReportExportStrategy(ec.edu.espe.finvory.controller.ExportController exportController) {
        this.exportController = exportController;
    }

    @Override
    public void export(String path, String title, String[] headers, java.util.List<Object[]> rows) throws Exception {
        exportController.exportTableWithDateToCSV(path, title, headers, rows);
    }

    @Override
    public ReportFormat format() {
        return ReportFormat.CSV;
    }

}
