package ec.edu.espe.finvory;

import ec.espe.edu.finvory.controller.FinvoryController;
import ec.espe.edu.finvory.model.Database;
import ec.espe.edu.finvory.model.FinvoryData;
import ec.espe.edu.finvory.view.FinvoryView;

/**
 *
 * @author Joseph B. Medina, POOwer Ranger of Programming
 */
public class FinvoryApp {

    public static void main(String[] args) {
        
        System.out.println("--- INICIANDO SISTEMA FINVORY ---");
        
        Database database = new Database();
        FinvoryData data = database.load();
        FinvoryView view = new FinvoryView();

        if (data.getCompanyAccounts().isEmpty() && data.getPersonalAccounts().isEmpty()) {
        } else {
            view.showMessage("Datos cargados correctamente.");
        }
        
        FinvoryController controller = new FinvoryController(data, view, database);
        
        controller.run(); 
    }
}
