package ec.edu.espe.finvory.mongo;

import ec.edu.espe.finvory.model.FinvoryData;
import ec.edu.espe.finvory.model.SystemUsers;

public class DataPersistenceManager {

    private final LocalFileService localService;
    private final MongoDataLoader mongoLoader;

    public DataPersistenceManager() {
        this.localService = new LocalFileService();
        this.mongoLoader = new MongoDataLoader();
    }

    public FinvoryData loadCompanyData(String companyUsername) {
        boolean online = MongoDBConnection.isOnline();

        if (online) {
            if (localService.hasPendingOffline(companyUsername)) {
                System.out.println("Subiendo datos locales pendientes a la nube...");
                FinvoryData pendingData = localService.loadCompanyDataLocal(companyUsername);
                try {
                    MongoDataExporter.exportCompanyData(companyUsername, pendingData, MongoDBConnection.getDatabase());
                    localService.clearPendingOffline(companyUsername);
                    System.out.println("Sincronización pendiente completada.");
                } catch (Exception e) {
                    System.err.println("Fallo al subir pendientes: " + e.getMessage());
                }
            }
            FinvoryData cloudData = mongoLoader.loadDataFromCloud(companyUsername);
            if (cloudData != null) {
                localService.saveCompanyDataLocal(cloudData, companyUsername);
                return cloudData;
            }
        }
        System.out.println("Usando modo OFFLINE (Datos locales).");
        return localService.loadCompanyDataLocal(companyUsername);
    }

    public void saveCompanyData(FinvoryData data, String companyUsername) {
        localService.saveCompanyDataLocal(data, companyUsername);

        if (MongoDBConnection.isOnline()) {
            try {
                MongoDataExporter.exportCompanyData(companyUsername, data, MongoDBConnection.getDatabase());
                localService.clearPendingOffline(companyUsername);
            } catch (Exception e) {
                System.err.println("Error guardando en nube: " + e.getMessage());
                localService.markPendingOffline(companyUsername);
            }
        } else {
            localService.markPendingOffline(companyUsername);
        }
    }

    public SystemUsers loadUsers() {
        return localService.loadUsers();
    }

    public void saveUsers(SystemUsers users) {
        localService.saveUsers(users);
        if (MongoDBConnection.isOnline()) {
            try {
                MongoDataExporter.exportUsers(users, MongoDBConnection.getDatabase());
            } catch (Exception e) {
                System.err.println("Error subiendo usuarios: " + e.getMessage());
            }
        }
    }
}
