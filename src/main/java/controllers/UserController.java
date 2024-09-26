package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import models.User;
import utils.JWTUtil;

public class UserController {
    private Connection connection;
    private JWTUtil jwtUtil;
    private int currentUserId;
    private BCryptPasswordEncoder passwordEncoder;

    public UserController(Connection connection) {
        this.connection = connection;
        this.jwtUtil = new JWTUtil();
        this.currentUserId = -1;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public void addUser(User user) throws SQLException {
        if (usernameExists(user.getUsername())) {
            throw new SQLException("Username already exists.");
        }
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, hashedPassword);
            stmt.executeUpdate();
        }
    }

    private boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User(rs.getString("username"), rs.getString("email"), rs.getString("password"));
                users.add(user);
            }
        }
        return users;
    }

    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET email = ? " +
                (user.getPassword() != null ? ", password = ?" : "") +
                " WHERE username = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getEmail());

            int index = 2;

            if (user.getPassword() != null) {
                String hashedPassword = passwordEncoder.encode(user.getPassword()); // Usa l'encoder
                stmt.setString(index++, hashedPassword);
            }

            stmt.setString(index, user.getUsername());

            stmt.executeUpdate();
        }
    }

    public void deleteUser(String username) throws SQLException {
        String sql = "DELETE FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }

    public String loginUser(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                if (passwordEncoder.matches(password, hashedPassword)) { // Usa l'encoder per la verifica
                    currentUserId = rs.getInt("id");
                    return jwtUtil.generateToken(username);
                }
            }
        }
        return null;
    }

    public void logoutUser() {
        currentUserId = -1;
    }

    public int getCurrentUserId() {
        return currentUserId;
    }
}