package models;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private int id;
    private List<OrderProduct> products;
    private List<Product> tempCart;
    private double total;

    public Order(int id) {
        this.id = id;
        this.products = new ArrayList<>();
        this.tempCart = new ArrayList<>();
        this.total = 0.0;
    }

    public Order() {
        this.id = 0;
        this.products = new ArrayList<>();
        this.tempCart = new ArrayList<>();
        this.total = 0.0;
    }

    public void addProductToCart(Product product) {
        tempCart.add(product);
        System.out.println(product.getName() + " aggiunto al carrello.");
    }

    public void viewCart() {
        if (tempCart.isEmpty()) {
            System.out.println("Il carrello è vuoto.");
        } else {
            System.out.println("Prodotti nel carrello:");
            for (Product product : tempCart) {
                System.out.println("- " + product.getName() + ": €" + product.getPrice());
            }
        }
    }

    public void confirmOrder() {
        if (!tempCart.isEmpty()) {
            products.addAll(tempCart);
            total = calculateTotal();
            tempCart.clear();
            System.out.println("Ordine confermato con successo.");
        } else {
            System.out.println("Non ci sono prodotti nel carrello da confermare.");
        }
    }

    public void modifyOrder(Product oldProduct, Product newProduct) {
        if (tempCart.contains(oldProduct)) {
            tempCart.remove(oldProduct);
            tempCart.add(newProduct);
            System.out.println(oldProduct.getName() + " è stato sostituito con " + newProduct.getName() + ".");
        } else {
            System.out.println("Il prodotto " + oldProduct.getName() + " non è presente nel carrello.");
        }
    }

    public void cancelOrder() {
        tempCart.clear();
        System.out.println("Ordine cancellato. Il carrello è stato svuotato.");
    }

    private double calculateTotal() {
        return products.stream().mapToDouble(Product::getPrice).sum();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<OrderProduct> getProducts() {
        return products;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }    

    @Override
    public String toString() {
        StringBuilder productNames = new StringBuilder();
        for (Product product : products) {
            productNames.append(product.getName()).append(", ");
        }
        return "Order{" +
               "id=" + id +
               ", products=" + productNames.toString() +
               ", total=EUR " + String.format(java.util.Locale.US, "%.2f", total) +  // Formattazione con punto decimale
               '}';
    }   
}