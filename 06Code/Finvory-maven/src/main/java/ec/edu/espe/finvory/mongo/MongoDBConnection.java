package ec.edu.espe.finvory.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

/**
 *
 * @author Joseph B. Medina
 */
public class MongoDBConnection {
    
    private final MongoClient client;
    private final MongoDatabase database;

    public MongoDBConnection(String connectionString, String databaseName) {
        this.client = MongoClients.create(connectionString);
        this.database = client.getDatabase(databaseName);
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public void close() {
        client.close();
    }
    
}
