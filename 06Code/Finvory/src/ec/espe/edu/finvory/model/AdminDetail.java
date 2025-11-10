package ec.espe.edu.finvory.model;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.Scanner;
/**
 *
 * @author Arelys Otavalo, The POOwer Rangers Of Programming
 */

public class AdminDetail {
    private String fullName;
    private String birthDate;
    private String nickname;
    private String email; 

    private static final String adminDataFilePath = "adminData.json";

    public AdminDetail() {}
    
    public AdminDetail(String fullName, String birthDate, String nickname, String email) {
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.nickname = nickname;
        this.email = email;
    }
    
    public void enterData() {
        Scanner scanner = new Scanner(System.in);

        loadData(); 

        if (fullName == null || fullName.isEmpty()) {
            System.out.println("No hay información del administrador registrada.");
            registerData(scanner);
            save();
        } else {
            System.out.println("Información del administrador encontrada.");
            System.out.println("1. Editar información");
            System.out.println("2. Mostrar información actual");
            System.out.print("Seleccione una opción: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
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
        System.out.println("\n--- Registro de Administrador ---");
        System.out.print("Nombre completo: ");
        fullName = scanner.nextLine();
        System.out.print("Fecha de nacimiento (dd/mm/aaaa): ");
        birthDate = scanner.nextLine();
        System.out.print("Apodo (nickname): ");
        nickname = scanner.nextLine();
        System.out.print("Correo electrónico: ");
        email = scanner.nextLine();

        System.out.println("\nRegistro completado con éxito.");
    }

    public boolean update(Scanner scanner) {
        System.out.println("\n--- Edición de Información ---");
        System.out.println("1. Cambiar nombre");
        System.out.println("2. Cambiar fecha de nacimiento");
        System.out.println("3. Cambiar apodo");
        System.out.println("4. Cambiar correo electrónico");
        System.out.print("Seleccione una opción: ");
        int option = scanner.nextInt();
        scanner.nextLine();

        switch (option) {
            case 1 -> {
                System.out.print("Nuevo nombre completo: ");
                fullName = scanner.nextLine();
                System.out.println("Nombre actualizado correctamente.");
                return true;
            }
            case 2 -> {
                System.out.print("Nueva fecha de nacimiento: ");
                birthDate = scanner.nextLine();
                System.out.println("Fecha actualizada correctamente.");
                return true;
            }
            case 3 -> {
                System.out.print("Nuevo apodo: ");
                nickname = scanner.nextLine();
                System.out.println("Apodo actualizado correctamente.");
                return true;
            }
            case 4 -> {
                System.out.print("Nuevo correo electrónico: ");
                email = scanner.nextLine();
                System.out.println("Correo electrónico actualizado correctamente.");
                return true;
            }
            default -> {
                System.out.println("Opción inválida.");
                return false;
            }
        }
    }

    private void showData() {
        System.out.println("\n--- Información del Administrador ---");
        System.out.println("Nombre completo: " + fullName);
        System.out.println("Fecha de nacimiento: " + birthDate);
        System.out.println("Apodo: " + nickname);
        System.out.println("Correo electrónico: " + email);
    }

    public void save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = new FileWriter(adminDataFilePath)) {
            gson.toJson(this, writer);
            System.out.println("Datos del administrador guardados correctamente.");
        } catch (IOException e) {
            System.out.println("Error al guardar los datos: " + e.getMessage());
        }
    }

    public void loadData() {
        File file = new File(adminDataFilePath);
        if (file.exists()) {
            Gson gson = new Gson();
            try (Reader reader = new FileReader(adminDataFilePath)) {
                AdminDetail loaded = gson.fromJson(reader, AdminDetail.class);
                this.fullName = loaded.fullName;
                this.birthDate = loaded.birthDate;
                this.nickname = loaded.nickname;
                this.email = loaded.email;
            } catch (IOException e) {
                System.out.println("Error al cargar los datos: " + e.getMessage());
            }
        }
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

