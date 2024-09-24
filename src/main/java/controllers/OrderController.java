package controllers;

import models.Order;
import models.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderController {
    private Connection connection;

    public OrderController(Connection connection) {
        this.connection = connection;
    }

    public void addOrder(Order order, int userId) throws SQLException {
        if (!userExists(userId)) {
            throw new SQLException("User ID " + userId + " does not exist in the database.");
        }
        
        String sql = "INSERT INTO orders (user_id, total) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setDouble(2, order.getTotal());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Failed to add order: " + e.getMessage());
        }
    }
    
    private boolean userExists(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        }
        return false;
    }    

    private List<String> getProductNames(List<Product> products) {
        List<String> names = new ArrayList<>();
        for (Product product : products) {
            names.add(product.getName());
        }
        return names;
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders";
        try (PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                double total = resultSet.getDouble("total");
                List<Product> products = getProductsForOrder(id);
                Order order = new Order(id);
                order.getProducts().addAll(products);
                order.setTotal(total);
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public void updateOrder(Order order) throws SQLException {
        String sql = "UPDATE orders SET products = ?, total = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, String.join(", ", getProductNames(order.getProducts())));
            stmt.setDouble(2, order.getTotal());
            stmt.setInt(3, order.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteOrder(int id) throws SQLException {
        String sql = "DELETE FROM orders WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private List<Product> getProductsForOrder(int orderId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.id, p.name, p.price FROM products p " +
                "JOIN order_products op ON p.id = op.product_id " +
                "WHERE op.order_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    double price = resultSet.getDouble("price");
                    Product product = new Product(id, name, price);
                    products.add(product);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public void addProductsToOrder(int orderId, List<Product> products) {
        String sql = "INSERT INTO order_products (order_id, product_id, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (Product product : products) {
                stmt.setInt(1, orderId);
                stmt.setInt(2, product.getId());
                stmt.setInt(3, 1);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}