package ec.edu.espe.finvory.controller.report;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;


/**
 *
 * @author Joseph B. Medina
 */
public class PdfReportExportStrategy implements ReportExportStrategy {

    @Override
    public void export(String path, String reportTitle, String[] headers, List<Object[]> dataRows) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                float margin = 50;
                float y = page.getMediaBox().getHeight() - margin;

                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 14);
                cs.newLineAtOffset(margin, y);
                cs.showText(reportTitle);
                cs.endText();

                y -= 18;

                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 10);
                cs.newLineAtOffset(margin, y);
                cs.showText("Fecha de generación: " + LocalDate.now());
                cs.endText();

                y -= 25;

                cs.setFont(PDType1Font.HELVETICA_BOLD, 10);
                y = writeRow(cs, margin, y, headers);

                cs.setFont(PDType1Font.HELVETICA, 10);
                for (Object[] row : dataRows) {
                    String[] strRow = new String[row.length];
                    for (int i = 0; i < row.length; i++) {
                        strRow[i] = row[i] == null ? "" : row[i].toString();
                    }
                    y = writeRow(cs, margin, y, strRow);
                    if (y < margin) {
                        break;
                    }
                }
            }

            doc.save(path);
        }
    }

    private float writeRow(PDPageContentStream cs, float x, float y, String[] cols) throws IOException {
        float colWidth = 110;
        float leading = 14;

        for (int i = 0; i < cols.length; i++) {
            cs.beginText();
            cs.newLineAtOffset(x + (i * colWidth), y);
            cs.showText(trim(cols[i], 18));
            cs.endText();
        }
        return y - leading;
    }

    private String trim(String s, int max) {
        if (s == null) {
            return "";
        }
        if (s.length() <= max) {
            return s;
        }
        return s.substring(0, max - 3) + "...";
    }

}
