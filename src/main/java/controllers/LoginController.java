package main.java.controllers;

import main.java.models.User;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class LoginController {
    private UserController userController;
    private Scanner scanner;

    public LoginController(Connection connection, Scanner scanner) {
        this.userController = new UserController(connection);
        this.scanner = scanner;
    }

    public void login() {
        System.out.print("Inserisci il nome utente: ");
        String username = scanner.nextLine();
        System.out.print("Inserisci la password: ");
        String password = scanner.nextLine();

        try {
            String token = userController.loginUser(username, password);
            if (token != null) {
                System.out.println("Login effettuato con successo. Token: " + token);
            } else {
                System.out.println("Nome utente o password errati. Vuoi registrarti? (s/n)");
                String choice = scanner.nextLine();
                if (choice.equalsIgnoreCase("s")) {
                    registerUser(username, password);
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore durante il login: " + e.getMessage());
        }
    }

    private void registerUser(String username, String password) {
        System.out.print("Inserisci l'email: ");
        String email = scanner.nextLine();

        User newUser = new User(username, email, password);
        try {
            userController.addUser(newUser);
            System.out.println("Registrazione completata con successo. Puoi ora effettuare il login.");
            login();
        } catch (SQLException e) {
            System.out.println("Errore durante la registrazione: " + e.getMessage());
        }
    }

    public void logout() {
        userController.logoutUser();
        System.out.println("Logout effettuato.");
    }
}