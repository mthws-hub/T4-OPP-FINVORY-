package ec.edu.espe.finvory;

import ec.edu.espe.finvory.controller.FinvoryController;
import ec.edu.espe.finvory.model.Database;
import ec.edu.espe.finvory.mongo.MongoDBConnection;
import ec.edu.espe.finvory.view.FrmFinvorySplash;
import javax.swing.SwingUtilities;

/**
 *
 * @author Joseph Medina, The POOwer Rangers of Programming
 */
public class FinvoryApp {

    private static MongoDBConnection mongoConnection;

    public static void main(String[] args) {
        mongoConnection = new MongoDBConnection();

        Database db = new Database();
        FinvoryController controller = new FinvoryController(db);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FrmFinvorySplash(controller).setVisible(true);
            }
        });
    }

    public static MongoDBConnection getMongoDBConnection() {
        return mongoConnection;
    }
}
