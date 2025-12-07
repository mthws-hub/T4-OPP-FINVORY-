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

    public MongoDBConnection(String connectionString, String databaseName) {
        client = MongoClients.create(connectionString);
        database = client.getDatabase(databaseName);
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
        if (client != null) {
            client.close();
        }
    }
    
}
