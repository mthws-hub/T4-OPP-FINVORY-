package ec.edu.espe.finvory.controller.report;

import java.util.List;

/**
 *
 * @author Joseph B. Medina
 */
public interface ReportExportStrategy {
    
    void export(String path, String reportTitle, String[] headers, List<Object[]> dataRows) throws Exception;
    
}
