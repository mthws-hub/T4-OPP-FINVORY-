package ec.espe.edu.finvory.model;
/**
 *
 * @author Arelys Otavalo, The POOwer Rangers Of Programming
 */
public class Address {
    private String country;
    private String city;
    private String zipCode;
    private String street;
    private String streetNumber;
    private String region;

    public Address(String country, String city, String zipCode, String street, String streetNumber, String region) {
        this.country = country;
        this.city = city;
        this.zipCode = zipCode;
        this.street = street;
        this.streetNumber = streetNumber;
        this.region = region;
    }
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
 
}
