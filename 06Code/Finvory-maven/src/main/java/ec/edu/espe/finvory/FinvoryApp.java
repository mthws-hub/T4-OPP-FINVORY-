package ec.edu.espe.finvory;

import ec.edu.espe.finvory.controller.FinvoryController;
import ec.edu.espe.finvory.model.Database;
import ec.edu.espe.finvory.view.FinvoryView;
/**
 *
 * @author Joseph Medina, The POOwer Rangers of Programming
 */

public class FinvoryApp {
    public static void main(String[] args) {
        System.out.println("--- FINVORY MULTI-TENANT SYSTEM ---");
        
        Database db = new Database();
        FinvoryView view = new FinvoryView();
        
        FinvoryController controller = new FinvoryController(view, db);
        
        controller.run(); 
    }
}