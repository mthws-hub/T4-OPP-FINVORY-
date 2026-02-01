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

        CompanyAccount c = new CompanyAccount();
        c.setUsername(doc.getString("companyUsername"));
        c.setPassword(doc.getString("password"));
        c.setTwoFactorKey(doc.getString("twoFactorKey"));

        c.setName(doc.getString("name"));
        c.setRuc(doc.getString("ruc"));
        c.setPhone(doc.getString("phone"));
        c.setEmail(doc.getString("email"));
        c.setLogoPath(doc.getString("logoPath"));
        Document a = doc.get("address", Document.class);
        if (a != null) {
            Address address = new Address(
                    a.getString("country"),
                    a.getString("city"),
                    a.getString("street")
            );
            c.setAddress(address);
        }

        return c;
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

        PersonalAccount p = new PersonalAccount();
        p.setUsername(doc.getString("username"));
        p.setPassword(doc.getString("password"));
        p.setTwoFactorKey(doc.getString("twoFactorKey"));
        p.setFullName(doc.getString("fullName"));
        String photo = doc.getString("profilePhotoPath");
        if (photo == null) {
            photo = doc.getString("photoPath");
        }
        p.setProfilePhotoPath(photo);

        return p;
    }

    public boolean isUsernameTaken(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }
        String u = username.trim();

        MongoCollection<Document> cCol = companies();
        if (cCol != null && cCol.find(Filters.eq("companyUsername", u)).first() != null) {
            return true;
        }

        MongoCollection<Document> pCol = personalAccounts();
        return pCol != null && pCol.find(Filters.eq("username", u)).first() != null;
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

    public void upsertPersonalAccount(PersonalAccount p) {
        if (p == null || p.getUsername() == null || p.getUsername().isBlank()) {
            return;
        }
        MongoCollection<Document> col = personalAccounts();
        if (col == null) {
            return;
        }

        Document set = new Document()
                .append("username", p.getUsername())
                .append("password", p.getPassword())
                .append("twoFactorKey", p.getTwoFactorKey())
                .append("fullName", p.getFullName())
                .append("profilePhotoPath", p.getProfilePhotoPath());

        col.updateOne(
                Filters.eq("username", p.getUsername()),
                new Document("$set", set),
                new UpdateOptions().upsert(true)
        );
    }
}
