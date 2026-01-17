package ec.edu.espe.finvory;

import ec.edu.espe.finvory.controller.FinvoryController;
import ec.edu.espe.finvory.mongo.DataPersistenceManager;
import ec.edu.espe.finvory.mongo.MongoDBConnection;
import ec.edu.espe.finvory.view.FrmFinvorySplash;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author Joseph Medina, The POOwer Rangers of Programming
 */
public class FinvoryApp {

    private static MongoDBConnection mongoConnection;
    
    private static final Image APP_ICON = Toolkit.getDefaultToolkit().getImage(FinvoryApp.class.getResource("/FinvoryCorner.jpeg"));

    public static void main(String[] args) {
        setupTaskbarIcon();

        mongoConnection = new MongoDBConnection();
        DataPersistenceManager db = new DataPersistenceManager();
        FinvoryController controller = new FinvoryController(db);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                FrmFinvorySplash splash = new FrmFinvorySplash(controller);
                setIcon(splash); 
                splash.setVisible(true);
            }
        });
    }

    public static MongoDBConnection getMongoDBConnection() {
        return mongoConnection;
    }


    public static void setIcon(JFrame frame) {
        if (APP_ICON != null) {
            frame.setIconImage(APP_ICON);
        }
    }

    private static void setupTaskbarIcon() {
        try {
            if (java.awt.Taskbar.isTaskbarSupported()) {
                java.awt.Taskbar.getTaskbar().setIconImage(APP_ICON);
            }
        } catch (UnsupportedOperationException e) {
            System.out.println("El sistema operativo no soporta cambio de icono en Taskbar (Probablemente Windows antiguo o Linux sin soporte).");
        } catch (SecurityException e) {
            System.out.println("Permiso denegado para cambiar icono de Taskbar.");
        } catch (Throwable e) {
        }
    }
}