package main.java;

import main.java.controllers.UserController;
import main.java.controllers.OrderController;
import main.java.controllers.ProductController;
import main.java.models.User;
import main.java.models.Order;
import main.java.models.Product;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

@SuppressWarnings("unused")
public class Master {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/Master";
        String user = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            UserController userController = new UserController(connection);
            OrderController orderController = new OrderController(connection);
            ProductController productController = new ProductController(connection);

            List<User> users = userController.getAllUsers();
            System.out.println("Users: " + users);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}