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

    /**
     * Asegura que exista un cliente y una base conectada.
     * No lanza excepción: si falla, deja database en null.
     */
    private static void ensureConnected() {
        if (database != null) {
            return;
        }
        try {
            String uri = System.getenv("MONGODB_URI");
            if (uri == null || uri.isEmpty()) {
                return;
            }
            if (client == null) {
                client = MongoClients.create(uri);
            }
            database = client.getDatabase(DEFAULT_DB_NAME);
        } catch (Exception ignored) {
            database = null;
        }
    }

    public MongoDatabase getDatabaseInstance() {
        return database;
    }

    /**
     * API esperada por el resto del proyecto: MongoDBConnection.getDatabase().
     * (No puede coexistir con un método de instancia del mismo nombre, por eso
     * el de instancia se llama getDatabaseInstance()).
     */
    public static MongoDatabase getDatabase() {
        return getDatabaseStatic();
    }

    /**
     * Acceso estático (útil para clases que no tienen referencia al objeto conexión).
     */
    public static MongoDatabase getDatabaseStatic() {
        ensureConnected();
        return database;
    }

    public static MongoCollection<Document> getCollection(String collectionName) {
        ensureConnected();
        if (database != null) {
            return database.getCollection(collectionName, Document.class);
        }
        return null;
    }

    /**
     * Ping real a MongoDB. Sirve para decidir si se puede trabajar online.
     */
    public static boolean ping() {
        ensureConnected();
        if (database == null) {
            return false;
        }
        try {
            database.runCommand(new Document("ping", 1));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Alias semántico.
     */
    public static boolean isOnline() {
        return ping();
    }

    public void close() {
        try {
            if (client != null) {
                client.close();
            }
        } catch (Exception ignored) {
        } finally {
            client = null;
            database = null;
        }
    }
}
