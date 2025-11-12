package ec.edu.espe.finvory;

import ec.espe.edu.finvory.controller.FinvoryController;
import ec.espe.edu.finvory.model.Database;
import ec.espe.edu.finvory.model.FinvoryData;
import ec.espe.edu.finvory.view.FinvoryView;

/**
 *
 * @author Joseph B. Medina
 */
public class FinvoryApp {

    public static void main(String[] args) {
        
        System.out.println("--- INICIANDO SISTEMA FINVORY ---");
        
        Database db = new Database();
        FinvoryData data = db.load();
        FinvoryView view = new FinvoryView();

        if (data.getCompanyAccounts().isEmpty() && data.getPersonalAccounts().isEmpty()) {
        } else {
            view.showMessage("Datos cargados correctamente.");
        }
        
        FinvoryController controller = new FinvoryController(data, view, db);
        
        controller.run(); 
    }
}
