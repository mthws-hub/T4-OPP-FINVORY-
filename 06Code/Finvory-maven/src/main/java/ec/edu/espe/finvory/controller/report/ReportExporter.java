package ec.edu.espe.finvory.controller.report;

import java.util.List;

/**
 *
 * @author Joseph B. Medina
 */
public class ReportExporter {

    private ReportExportStrategy strategy;

    public ReportExporter(ReportExportStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(ReportExportStrategy strategy) {
        this.strategy = strategy;
    }

    public void export(String path, String reportTitle, String[] headers, List<Object[]> dataRows) throws Exception {
        if (strategy == null) {
            throw new IllegalStateException("No hay estrategia de exportación definida.");
        }
        strategy.export(path, reportTitle, headers, dataRows);
    }

}
