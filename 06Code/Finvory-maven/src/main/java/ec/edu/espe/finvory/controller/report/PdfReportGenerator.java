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

            PDPageContentStream contentStream = new PDPageContentStream(doc, page);

            float margin = 50;
            float yCoordenate = page.getMediaBox().getHeight() - margin;

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
            contentStream.newLineAtOffset(margin, yCoordenate);
            contentStream.showText(title);
            contentStream.endText();

            yCoordenate -= 30;

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
            yCoordenate = writeRow(contentStream, margin, yCoordenate, headers);

            contentStream.setFont(PDType1Font.HELVETICA, 10);
            for (Object[] row : rows) {
                String[] values = new String[row.length];
                for (int i = 0; i < row.length; i++) {
                    values[i] = row[i] == null ? "" : row[i].toString();
                }
                yCoordenate = writeRow(contentStream, margin, yCoordenate, values);
                if (yCoordenate < margin) {
                    contentStream.close();
                    page = new PDPage(PDRectangle.LETTER);
                    doc.addPage(page);
                    contentStream = new PDPageContentStream(doc, page);
                    yCoordenate = page.getMediaBox().getHeight() - margin;
                }
            }

            contentStream.close();
            doc.save(new File(path));
        }
    }

    private static float writeRow(
            PDPageContentStream content,
            float x,
            float y,
            String[] cells
    ) throws Exception {
        float colWidth = 500f / cells.length;
        float cursorX = x;

        for (String cell : cells) {
            content.beginText();
            content.newLineAtOffset(cursorX, y);
            content.showText(cell);
            content.endText();
            cursorX += colWidth;
        }
        return y - 15;
    }

}
