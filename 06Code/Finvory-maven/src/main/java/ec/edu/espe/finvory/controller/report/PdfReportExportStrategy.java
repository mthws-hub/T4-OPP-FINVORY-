package ec.edu.espe.finvory.controller.report;


import java.util.List;


/**
 *
 * @author Joseph B. Medina
 */
public class PdfReportExportStrategy implements ReportExportStrategy {

    @Override
    public void export(String path, String title, String[] headers, List<Object[]> rows) throws Exception {
        PdfReportGenerator.generate(path, title, headers, rows);
    }

    @Override
    public ReportFormat format() {
        return ReportFormat.PDF;
    }
}
