package ec.edu.espe.finvory.controller.report;

import ec.edu.espe.finvory.controller.ExportController;

/**
 *
 * @author Joseph B. Medina
 */
public final class ReportStrategyResolver {

    private ReportStrategyResolver() {
    }

    public static ReportExportStrategy resolve(
            ReportType type,
            ExportController exportController
    ) {
        if (type == ReportType.POPULAR_PRODUCTS) {
            return new PdfReportExportStrategy();
        }
        return new CsvReportExportStrategy(exportController);
    }

}
