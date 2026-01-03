package ec.edu.espe.finvory.model;

import java.util.ArrayList;
/**
 *
 * @author Joseph Medina, The POOwer Rangers of Programming
 */
public class SystemUsers {
    
    private ArrayList<CompanyAccount> companyAccounts;
    private ArrayList<PersonalAccount> personalAccounts;

    public SystemUsers() {
        this.companyAccounts = new ArrayList<>();
        this.personalAccounts = new ArrayList<>();
    }

    public void addCompanyAccount(CompanyAccount account) {
        if (account != null) {
            this.companyAccounts.add(account);
        }
    }
    
    public void addPersonalAccount(PersonalAccount account) {
        if (account != null) {
            this.personalAccounts.add(account);
        }
    }

    public ArrayList<CompanyAccount> getCompanyAccounts() { 
        return companyAccounts; 
    }
    
    public ArrayList<PersonalAccount> getPersonalAccounts() { 
        return personalAccounts; 
    }

    public void setCompanyAccounts(ArrayList<CompanyAccount> companyAccounts) {
        this.companyAccounts = companyAccounts;
    }

    public void setPersonalAccounts(ArrayList<PersonalAccount> personalAccounts) {
        this.personalAccounts = personalAccounts;
    }
    
    
}