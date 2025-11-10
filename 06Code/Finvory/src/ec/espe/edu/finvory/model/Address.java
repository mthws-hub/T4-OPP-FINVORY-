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
    private String zipCode;
    private String street;
    private String streetNumber;
    private String region;

    private static final String addressDataFilePath = "addressData.json";

    public Address() {}

    public Address(String country, String city, String zipCode, String street,
                   String streetNumber, String region) {
        this.country = country;
        this.city = city;
        this.zipCode = zipCode;
        this.street = street;
        this.streetNumber = streetNumber;
        this.region = region;
    }

    public void enterData() {
        Scanner scanner = new Scanner(System.in);

        load();

        if (country == null || country.isEmpty()) {
            System.out.println("No hay información de dirección registrada.");
            registerData(scanner);
            save();
        } else {
            System.out.println("Información de dirección encontrada.");
            System.out.println("1. Editar dirección");
            System.out.println("2. Mostrar dirección actual");
            System.out.print("Seleccione una opción: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            switch(option) {
                case 1 -> {
                    update(scanner);
                    save();
                }
                case 2 -> showData();
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private void registerData(Scanner scanner) {
        System.out.println("\n--- Registro de Dirección ---");
        System.out.print("País: ");
        country = scanner.nextLine();
        System.out.print("Ciudad: ");
        city = scanner.nextLine();
        System.out.print("Código Postal: ");
        zipCode = scanner.nextLine();
        System.out.print("Calle: ");
        street = scanner.nextLine();
        System.out.print("Número de calle: ");
        streetNumber = scanner.nextLine();
        System.out.print("Región: ");
        region = scanner.nextLine();

        System.out.println("\nRegistro de dirección completado con éxito.");
    }

    public boolean update(Scanner scanner) {
        System.out.println("\n--- Edición de Dirección ---");
        System.out.println("1. Cambiar país");
        System.out.println("2. Cambiar ciudad");
        System.out.println("3. Cambiar código postal");
        System.out.println("4. Cambiar calle");
        System.out.println("5. Cambiar número de calle");
        System.out.println("6. Cambiar región");
        System.out.print("Seleccione una opción: ");
        int option = scanner.nextInt();
        scanner.nextLine();

        switch(option) {
            case 1 -> {
                System.out.print("Nuevo país: ");
                country = scanner.nextLine();
                return true;
            }
            case 2 -> {
                System.out.print("Nueva ciudad: ");
                city = scanner.nextLine();
                return true;
            }
            case 3 -> {
                System.out.print("Nuevo código postal: ");
                zipCode = scanner.nextLine();
                return true;
            }
            case 4 -> {
                System.out.print("Nueva calle: ");
                street = scanner.nextLine();
                return true;
            }
            case 5 -> {
                System.out.print("Nuevo número de calle: ");
                streetNumber = scanner.nextLine();
                return true;
            }
            case 6 -> {
                System.out.print("Nueva región: ");
                region = scanner.nextLine();
                return true;
            }
            default -> {
                System.out.println("Opción inválida.");
                return false;
            }
        }
    }

    private void showData() {
        System.out.println("\n--- Información de Dirección ---");
        System.out.println("País: " + country);
        System.out.println("Ciudad: " + city);
        System.out.println("Código Postal: " + zipCode);
        System.out.println("Calle: " + street);
        System.out.println("Número de calle: " + streetNumber);
        System.out.println("Región: " + region);
    }

    public void save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = new FileWriter(addressDataFilePath)) {
            gson.toJson(this, writer);
            System.out.println("Datos de dirección guardados correctamente.");
        } catch (IOException e) {
            System.out.println("Error al guardar la dirección: " + e.getMessage());
        }
    }

    public void load() {
        File file = new File(addressDataFilePath);
        if (file.exists()) {
            Gson gson = new Gson();
            try (Reader reader = new FileReader(addressDataFilePath)) {
                Address loaded = gson.fromJson(reader, Address.class);
                this.country = loaded.country;
                this.city = loaded.city;
                this.zipCode = loaded.zipCode;
                this.street = loaded.street;
                this.streetNumber = loaded.streetNumber;
                this.region = loaded.region;
            } catch (IOException e) {
                System.out.println("Error al cargar la dirección: " + e.getMessage());
            }
        }
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

