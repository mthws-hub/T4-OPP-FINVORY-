package ec.edu.espe.finvory.controller.report;

import java.io.File;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 *
 * @author Joseph B. Medina
 */
public class PdfReportGenerator {

    public static void generate(
            String path,
            String title,
            String[] headers,
            List<Object[]> rows
    ) throws Exception {

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            doc.addPage(page);

            PDPageContentStream cs = new PDPageContentStream(doc, page);

            float margin = 50;
            float y = page.getMediaBox().getHeight() - margin;

            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
            cs.newLineAtOffset(margin, y);
            cs.showText(title);
            cs.endText();

            y -= 30;

            cs.setFont(PDType1Font.HELVETICA_BOLD, 10);
            y = writeRow(cs, margin, y, headers);

            cs.setFont(PDType1Font.HELVETICA, 10);
            for (Object[] row : rows) {
                String[] values = new String[row.length];
                for (int i = 0; i < row.length; i++) {
                    values[i] = row[i] == null ? "" : row[i].toString();
                }
                y = writeRow(cs, margin, y, values);
                if (y < margin) {
                    cs.close();
                    page = new PDPage(PDRectangle.LETTER);
                    doc.addPage(page);
                    cs = new PDPageContentStream(doc, page);
                    y = page.getMediaBox().getHeight() - margin;
                }
            }

            cs.close();
            doc.save(new File(path));
        }
    }

    private static float writeRow(
            PDPageContentStream cs,
            float x,
            float y,
            String[] cells
    ) throws Exception {
        float colWidth = 500f / cells.length;
        float cursorX = x;

        for (String cell : cells) {
            cs.beginText();
            cs.newLineAtOffset(cursorX, y);
            cs.showText(cell);
            cs.endText();
            cursorX += colWidth;
        }
        return y - 15;
    }

}
