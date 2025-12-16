package ec.edu.espe.finvory.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

/**
 *
 * @author Joseph B. Medina
 */
public class MongoDBConnection {

    private static MongoClient client;
    private static MongoDatabase database;
    private static final String DEFAULT_DB_NAME = "FinvoryDB";

    public MongoDBConnection() {
        String uri = System.getenv("MONGODB_URI");
        if (uri == null || uri.isEmpty()) {
            System.err.println("ERROR: MONGODB_URI no está definida en las variables de entorno.");
        } else {
            connect(uri, DEFAULT_DB_NAME);
        }
    }

    public MongoDBConnection(String connectionString, String databaseName) {
        connect(connectionString, databaseName);
    }

    private void connect(String uri, String dbName) {
        try {
            if (client == null) {
                client = MongoClients.create(uri);
                database = client.getDatabase(dbName);
                System.out.println("Conexion a MongoDB establecida exitosamente.");
            }
        } catch (Exception e) {
            System.err.println("Error conectando a MongoDB: " + e.getMessage());
        }
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public static MongoCollection<Document> getCollection(String collectionName) {
        if (database != null) {
            return database.getCollection(collectionName, Document.class);
        }
        return null;
    }

    public void close() {
    }
}
