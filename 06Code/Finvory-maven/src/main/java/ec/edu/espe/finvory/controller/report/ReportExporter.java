package ec.edu.espe.finvory.controller.report;

import java.util.List;

/**
 *
 * @author Joseph B. Medina
 */
public class ReportExporter {

    private ReportExportStrategy strategy;

    public void setStrategy(ReportExportStrategy strategy) {
        this.strategy = strategy;
    }

    public ReportExportStrategy getStrategy() {
        return strategy;
    }

    public void export(String path, String title, String[] headers, List<Object[]> rows) throws Exception {
        if (strategy == null) {
            throw new IllegalStateException("Export strategy not defined");
        }
        strategy.export(path, title, headers, rows);
    }
}
