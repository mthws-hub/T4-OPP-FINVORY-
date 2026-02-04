package ec.edu.espe.finvory.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.StringJoiner;

/**
 *
 * @author Arelys Otavalo, the POOwer Rangers of Programming
 */
public class ExportController {

    private final FinvoryController mainController;

    public ExportController(FinvoryController mainController) {
        this.mainController = mainController;
    }

    public void exportTableToCSV(String fileName, String[] headers, List<Object[]> dataRows) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("sep=,");
            writer.println(String.join(",", headers));

            for (Object[] row : dataRows) {
                StringJoiner joiner = new StringJoiner(",");
                for (Object cell : row) {
                    joiner.add(cell.toString().replace(",", "."));
                }
                writer.println(joiner.toString());
            }
        } catch (IOException e) {
            System.err.println("Error al exportar a CSV: " + e.getMessage());
        }
    }

    public void exportTableWithDateToCSV(String fileName, String reportTitle, String[] headers, List<Object[]> dataRows) {
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(fileName); java.io.OutputStreamWriter osw = new java.io.OutputStreamWriter(fos, "Windows-1252"); java.io.PrintWriter writer = new java.io.PrintWriter(osw)) {

            writer.println("sep=,");
            writer.println(reportTitle);
            writer.println("");
            writer.println(String.join(",", headers));

            for (Object[] row : dataRows) {
                java.util.StringJoiner joiner = new java.util.StringJoiner(",");
                for (Object cell : row) {
                    String value = (cell == null) ? "" : cell.toString().replace(",", ".");
                    joiner.add(value);
                }
                writer.println(joiner.toString());
            }

        } catch (java.io.IOException e) {
            System.err.println("Error al exportar a CSV: " + e.getMessage());
            javax.swing.JOptionPane.showMessageDialog(null, "Error al guardar: " + e.getMessage());
        }
    }
}
