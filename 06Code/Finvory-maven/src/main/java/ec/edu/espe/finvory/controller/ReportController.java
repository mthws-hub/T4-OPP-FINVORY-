package ec.edu.espe.finvory.controller;

import ec.edu.espe.finvory.model.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author Arelys Otavalo, the POOwer Rangers of Programming
 */
public class ReportController {

    private final FinvoryController mainController;

    public ReportController(FinvoryController mainController) {
        this.mainController = mainController;
    }

    public void exportInventoryToCSV(String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("ID,Nombre,Descripcion,Codigo de Barras,Precio Costo,ID Proveedor");
            for (Product product : mainController.getData().getProducts()) {
                writer.printf("%s,%s,%s,%s,%.2f,%s%n",
                        product.getId(), product.getName(), product.getDescription(), product.getBarcode(),
                        product.getBaseCostPrice(), product.getSupplierId());
            }
        } catch (IOException e) {
            System.err.println("Error exportando inventario: " + e.getMessage());
        }
    }

    public Map<String, Integer> getProductDemandData() {
        Map<String, Integer> demand = new HashMap<>();
        for (InvoiceSim inv : mainController.getData().getInvoices()) {
            for (InvoiceLineSim line : inv.getLines()) {
                demand.put(line.getProductName(), demand.getOrDefault(line.getProductName(), 0) + line.getQuantity());
            }
        }
        return demand;
    }
}
