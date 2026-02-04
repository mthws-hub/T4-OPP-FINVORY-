package ec.edu.espe.finvory.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import ec.edu.espe.finvory.model.Address;
import ec.edu.espe.finvory.model.CompanyAccount;
import ec.edu.espe.finvory.model.PersonalAccount;
import org.bson.Document;

/**
 *
 * @author Mathews Pastor, The POOwer Rangers Of Programming
 */
public class MongoAuthService {

    private MongoCollection<Document> companies() {
        return MongoDBConnection.getCollection("companies");
    }

    private MongoCollection<Document> personalAccounts() {
        return MongoDBConnection.getCollection("personal_accounts");
    }

    public CompanyAccount findCompanyByUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        MongoCollection<Document> col = companies();
        if (col == null) {
            return null;
        }

        Document doc = col.find(Filters.eq("companyUsername", username.trim())).first();
        if (doc == null) {
            return null;
        }

        CompanyAccount company = new CompanyAccount();
        company.setUsername(doc.getString("companyUsername"));
        company.setPassword(doc.getString("password"));
        company.setTwoFactorKey(doc.getString("twoFactorKey"));

        company.setName(doc.getString("name"));
        company.setRuc(doc.getString("ruc"));
        company.setPhone(doc.getString("phone"));
        company.setEmail(doc.getString("email"));
        company.setLogoPath(doc.getString("logoPath"));
        Document addressDoc = doc.get("address", Document.class);
        if (addressDoc != null) {
            Address address = new Address(
                    addressDoc.getString("country"),
                    addressDoc.getString("city"),
                    addressDoc.getString("street")
            );
            company.setAddress(address);
        }

        return company;
    }

    public PersonalAccount findPersonalByUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        MongoCollection<Document> col = personalAccounts();
        if (col == null) {
            return null;
        }

        Document doc = col.find(Filters.eq("username", username.trim())).first();
        if (doc == null) {
            return null;
        }

        PersonalAccount personal = new PersonalAccount();
        personal.setUsername(doc.getString("username"));
        personal.setPassword(doc.getString("password"));
        personal.setTwoFactorKey(doc.getString("twoFactorKey"));
        personal.setFullName(doc.getString("fullName"));
        String photo = doc.getString("profilePhotoPath");
        if (photo == null) {
            photo = doc.getString("photoPath");
        }
        personal.setProfilePhotoPath(photo);

        return personal;
    }

    public boolean isUsernameTaken(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }
        String user = username.trim();

        MongoCollection<Document> cCol = companies();
        if (cCol != null && cCol.find(Filters.eq("companyUsername", user)).first() != null) {
            return true;
        }

        MongoCollection<Document> pCol = personalAccounts();
        return pCol != null && pCol.find(Filters.eq("username", user)).first() != null;
    }

    public void upsertCompanyAccount(CompanyAccount company) {
        if (company == null || company.getUsername() == null || company.getUsername().isBlank()) {
            return;
        }
        MongoCollection<Document> col = companies();
        if (col == null) {
            return;
        }

        Document set = new Document()
                .append("companyUsername", company.getUsername())
                .append("password", company.getPassword())
                .append("twoFactorKey", company.getTwoFactorKey())
                .append("name", company.getName())
                .append("ruc", company.getRuc())
                .append("phone", company.getPhone())
                .append("email", company.getEmail())
                .append("logoPath", company.getLogoPath());

        if (company.getAddress() != null) {
            set.append("address", new Document()
                    .append("country", company.getAddress().getCountry())
                    .append("city", company.getAddress().getCity())
                    .append("street", company.getAddress().getStreet()));
        }

        col.updateOne(
                Filters.eq("companyUsername", company.getUsername()),
                new Document("$set", set),
                new UpdateOptions().upsert(true)
        );
    }

    public void upsertPersonalAccount(PersonalAccount personal) {
        if (personal == null || personal.getUsername() == null || personal.getUsername().isBlank()) {
            return;
        }
        MongoCollection<Document> col = personalAccounts();
        if (col == null) {
            return;
        }

        Document set = new Document()
                .append("username", personal.getUsername())
                .append("password", personal.getPassword())
                .append("twoFactorKey", personal.getTwoFactorKey())
                .append("fullName", personal.getFullName())
                .append("profilePhotoPath", personal.getProfilePhotoPath());

        col.updateOne(Filters.eq("username", personal.getUsername()),
                new Document("$set", set),
                new UpdateOptions().upsert(true)
        );
    }

    public java.util.List<ec.edu.espe.finvory.model.CompanyAccount> findAllCompanies() {
        java.util.List<ec.edu.espe.finvory.model.CompanyAccount> companiesList = new java.util.ArrayList<>();
        com.mongodb.client.MongoCollection<org.bson.Document> col = companies();

        if (col != null) {
            for (org.bson.Document doc : col.find()) {
                ec.edu.espe.finvory.model.CompanyAccount company = new ec.edu.espe.finvory.model.CompanyAccount();
                company.setUsername(doc.getString("companyUsername"));
                company.setName(doc.getString("name"));
                company.setPhone(doc.getString("phone"));
                company.setEmail(doc.getString("email"));
                companiesList.add(company);
            }
        }
        return companiesList;
    }
}
