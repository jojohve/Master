package models;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private List<Product> products; 
    private int id;
    private int userId;
    private double total;

    public Order() {
        this.products = new ArrayList<>();
    }

    public Order(int id, int userId, double total) {
        this.id = id;
        this.userId = userId;
        this.total = total;
        this.products = new ArrayList<>();
    }

    public List<Product> getProducts() {
        return products;
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Order ID: " + id + ", User ID: " + userId + ", Total: " + total + ", Products: " + products;
    }
}