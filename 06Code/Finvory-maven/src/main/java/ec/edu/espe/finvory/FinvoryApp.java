package ec.edu.espe.finvory;

import ec.edu.espe.finvory.controller.FinvoryController;
import ec.edu.espe.finvory.model.Database;
import ec.edu.espe.finvory.view.FinvoryView;
import ec.edu.espe.finvory.mongo.MongoDBConnection;
/**
 *
 * @author Joseph Medina, The POOwer Rangers of Programming
 */

public class FinvoryApp {
    
    private static final String CONNECTION_STRING = "mongodb+srv://Joseph:Joseph1751774793@cluster0.h8pi0ir.mongodb.net/?appName=Cluster0";
    private static final String DATABASE_NAME = "FinvoryDB";
    private static MongoDBConnection mongoDBConnection;
    
    public static void main(String[] args) {
        System.out.println("--- FINVORY MULTI-TENANT SYSTEM ---");
        
        mongoDBConnection = new MongoDBConnection(CONNECTION_STRING, DATABASE_NAME);
        Database db = new Database();
        FinvoryView view = new FinvoryView();
        
        FinvoryController controller = new FinvoryController(view, db);
        
        controller.run(); 
    }
    
    public static MongoDBConnection getMongoDBConnection() {
        return mongoDBConnection;
    }
}