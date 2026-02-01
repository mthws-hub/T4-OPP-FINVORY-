package ec.edu.espe.finvory.mongo;

import ec.edu.espe.finvory.model.FinvoryData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class DataPersistenceManager {

    private final LocalFileService localService;
    private final MongoDataLoader mongoLoader;
    private final MongoAuthService authService;

    private static final String PENDING_FILE = ".pending_offline_upload";

    public DataPersistenceManager() {
        this.localService = new LocalFileService();
        this.mongoLoader = new MongoDataLoader();
        this.authService = new MongoAuthService();
    }

    public MongoAuthService auth() {
        return authService;
    }

    public FinvoryData loadCompanyData(String companyUsername) {
        // 1) intentar nube
        FinvoryData cloud = mongoLoader.loadDataFromCloud(companyUsername);
        if (cloud != null) {
            // si había pendiente offline, lo intentamos subir luego (opcional)
            return cloud;
        }

        // 2) fallback local
        return localService.loadCompanyDataLocal(companyUsername);
    }

    /**
     * Guarda local SIEMPRE. Si hay internet, exporta a Mongo. Si falla la
     * exportación o no hay internet, marca "pending" para subir luego.
     */
    public void saveCompanyData(FinvoryData data, String companyUsername) {
        localService.saveCompanyDataLocal(data, companyUsername);

        if (!MongoDBConnection.isOnline()) {
            markPendingOffline(companyUsername);
            return;
        }

        try {
            MongoDataExporter.exportCompanyData(companyUsername, data, MongoDBConnection.getDatabase());
            clearPendingOffline(companyUsername);
        } catch (Exception e) {
            System.err.println("Error exportando company data a Mongo: " + e.getMessage());
            markPendingOffline(companyUsername);
        }
    }

    /**
     * Si hay flag pending, intenta subir la data local a Mongo. Útil para
     * llamar después de login, o al abrir el menú.
     */
    public void syncPendingCompanyData(String companyUsername) {
        if (!hasPendingOffline(companyUsername)) {
            return;
        }
        if (!MongoDBConnection.isOnline()) {
            return;
        }
        try {
            FinvoryData local = localService.loadCompanyDataLocal(companyUsername);
            MongoDataExporter.exportCompanyData(companyUsername, local, MongoDBConnection.getDatabase());
            clearPendingOffline(companyUsername);
        } catch (Exception e) {
            System.err.println("No se pudo sincronizar pendiente offline: " + e.getMessage());
        }
    }

    private void markPendingOffline(String companyUsername) {
        try {
            String folder = localService.getCompanyFolderPath(companyUsername); // necesitas este método (abajo te digo)
            new File(folder).mkdirs();
            File flag = new File(folder + File.separator + PENDING_FILE);
            try (Writer writer = new FileWriter(flag)) {
                writer.write("pending");
            }
        } catch (IOException ignored) {
        }
    }

    private boolean hasPendingOffline(String companyUsername) {
        String folder = localService.getCompanyFolderPath(companyUsername);
        File flag = new File(folder + File.separator + PENDING_FILE);
        return flag.exists();
    }

    private void clearPendingOffline(String companyUsername) {
        String folder = localService.getCompanyFolderPath(companyUsername);
        File flag = new File(folder + File.separator + PENDING_FILE);
        if (flag.exists()) {
            flag.delete();
        }
    }
}
