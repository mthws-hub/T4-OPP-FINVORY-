package ec.espe.edu.finvory.model;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.util.Scanner;
/**
 *
 * @author Arelys Otavalo, The POOwer Rangers Of Programming
 */

public class Address {
    private String country;
    private String city;
    private String street;
    private String zipCode;
    private String streetNumber;
    private String region;

    public Address() {}

    public Address(String country, String city, String street) {
        this.country = country;
        this.city = city;
        this.street = street;
        this.zipCode = "";
        this.streetNumber = "";
        this.region = "";
    }
    
    public void setZipCode(String zipCode) { 
        this.zipCode = zipCode; 
    }
    
    public void setStreetNumber(String streetNumber) { 
        this.streetNumber = streetNumber; 
    }
    
    public void setRegion(String region) { 
        this.region = region; 
    }
    
    public String getCountry() { 
        return country; 
    }
    
    public String getCity() { 
        return city; 
    }
    
    public String getStreet() { 
        return street; 
    }

    @Override
    public String toString() { 
        return street + ", " + city + ", " + country; 
    }
    
}

