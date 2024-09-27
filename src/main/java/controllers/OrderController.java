package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import models.Order;
import models.OrderProduct;

public class OrderController {
    private Connection connection;

    public OrderController(Connection connection) {
        this.connection = connection;
    }

    public int addOrder(Order order, int userId) throws SQLException {
        String sql = "INSERT INTO orders (user_id, total) VALUES (?, ?)";
        double totalAmount = 0.0;  

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, userId);
            pstmt.setDouble(2, totalAmount);  
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creazione ordine fallita, nessun ID ottenuto.");
                }
            }
        }
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders";
    
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
    
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int userId = resultSet.getInt("user_id");
                double total = resultSet.getDouble("total");
    
                Order order = new Order(id, userId, total);
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }    

    public void updateOrder(Order order) throws SQLException {
        String sql = "UPDATE orders SET total = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, order.getTotal());  
            stmt.setInt(2, order.getId());   
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

    @SuppressWarnings("unused")
    private List<OrderProduct> getProductsForOrder(int orderId) {
        List<OrderProduct> orderProducts = new ArrayList<>();
        String sql = "SELECT op.order_id, op.product_id, op.quantity FROM order_products op WHERE op.order_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderId);

            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    int productId = resultSet.getInt("product_id");
                    int quantity = resultSet.getInt("quantity");

                    OrderProduct orderProduct = new OrderProduct(orderId, productId, quantity);
                    orderProducts.add(orderProduct);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orderProducts;
    }

    public void addProductsToOrder(int orderId, List<OrderProduct> orderProducts) throws SQLException {
        String sql = "INSERT INTO order_products (order_id, product_id, quantity) VALUES (?, ?, ?)";
        double totalAmount = 0.0;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (OrderProduct orderProduct : orderProducts) {
                stmt.setInt(1, orderId);
                stmt.setInt(2, orderProduct.getProductId());
                stmt.setInt(3, orderProduct.getQuantity());
                stmt.executeUpdate();

                double productPrice = getProductPrice(orderProduct.getProductId());
                totalAmount += productPrice * orderProduct.getQuantity();
            }

            updateOrderTotal(orderId, totalAmount);
        }
    }

    public void confirmOrder(int userId, List<OrderProduct> orderProducts) throws SQLException {
        int orderId = addOrder(null, userId); 
        addProductsToOrder(orderId, orderProducts); 
    }

    private double getProductPrice(int productId) throws SQLException {
        String sql = "SELECT price FROM products WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("price");
                } else {
                    throw new SQLException("Prodotto non trovato");
                }
            }
        }
    }

    private void updateOrderTotal(int orderId, double totalAmount) throws SQLException {
        String sql = "UPDATE orders SET total = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, totalAmount);
            stmt.setInt(2, orderId);
            stmt.executeUpdate();
        }
    }
}