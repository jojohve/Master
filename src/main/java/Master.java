import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import controllers.LoginController;
import controllers.OrderController;
import controllers.ProductController;
import controllers.UserController;
import models.Order;
import models.Product;

public class Master {
    public static void main(String[] args) {
        List<Product> selectedProducts = new ArrayList<>();
        String url = "jdbc:mysql://localhost:3306/Master";
        String user = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, user, password);
                Scanner scanner = new Scanner(System.in)) { // Crea il Scanner qui

            OrderController orderController = new OrderController(connection);
            ProductController productController = new ProductController(connection);
            UserController userController = new UserController(connection);

            LoginController loginController = new LoginController(connection, scanner);
            loginController.login();

            // Menu principale
            while (true) {
                System.out.println("=== Menu ===");
                System.out.println("1. Visualizza tutti i prodotti");
                System.out.println("2. Aggiungi prodotto al tuo ordine");
                System.out.println("3. Visualizza il tuo ordine");
                System.out.println("4. Modifica il tuo ordine");
                System.out.println("5. Cancella l'ordine");
                System.out.println("6. Conferma l'ordine");
                System.out.println("7. Esci");
                System.out.print("Seleziona un'opzione: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        List<Product> products = productController.getAllProducts();
                        System.out.println("Prodotti: ");
                        for (Product product : products) {
                            System.out
                                    .println(product.getId() + ". " + product.getName() + " - €" + product.getPrice());
                        }
                        break;
                    case 2:
                        System.out.print("Inserisci l'ID del prodotto da aggiungere al tuo ordine: ");
                        int productId = scanner.nextInt();
                        Product product = productController.getProductById(productId);
                        if (product != null) {
                            System.out.print("Inserisci la quantità: ");
                            int quantity = scanner.nextInt();
                            for (int i = 0; i < quantity; i++) {
                                selectedProducts.add(product);
                            }
                            System.out.println("Prodotto " + product.getName() + " (quantità: " + quantity
                                    + ") aggiunto al tuo ordine.");
                        } else {
                            System.out.println("Prodotto non trovato.");
                        }
                        break;
                    case 3:
                        if (selectedProducts.isEmpty()) {
                            System.out.println("Nessun prodotto nel tuo ordine.");
                        } else {
                            System.out.println("Il tuo ordine:");
                            double total = 0;
                            for (Product p : selectedProducts) {
                                System.out.println(p.getId() + ". " + p.getName() + " - €" + p.getPrice());
                                total += p.getPrice();
                            }
                            System.out.println("Totale: €" + total);
                        }
                        break;
                    case 4:
                        if (selectedProducts.isEmpty()) {
                            System.out.println("Nessun prodotto nel tuo ordine.");
                            break;
                        }

                        System.out.println("Il tuo ordine attuale:");
                        List<Product> uniqueProducts = new ArrayList<>();
                        for (Product p : selectedProducts) {
                            if (!uniqueProducts.contains(p)) {
                                uniqueProducts.add(p);
                            }
                        }

                        for (Product p : uniqueProducts) {
                            long count = selectedProducts.stream().filter(prod -> prod.getId() == p.getId()).count();
                            System.out.println(p.getId() + ". " + p.getName() + " - €" + p.getPrice() + " (quantità: "
                                    + count + ")");
                        }

                        System.out.print("Inserisci l'ID del prodotto da modificare: ");
                        int productIdToModify = scanner.nextInt();
                        Product productToModify = productController.getProductById(productIdToModify);

                        if (productToModify != null && uniqueProducts.contains(productToModify)) {
                            System.out.print("Inserisci la nuova quantità: ");
                            int newQuantity = scanner.nextInt();

                            int currentQuantity = (int) selectedProducts.stream()
                                    .filter(prod -> prod.getId() == productIdToModify).count();
                            for (int i = 0; i < currentQuantity; i++) {
                                selectedProducts.remove(productToModify);
                            }

                            for (int i = 0; i < newQuantity; i++) {
                                selectedProducts.add(productToModify);
                            }

                            System.out.println(
                                    "Quantità aggiornata per " + productToModify.getName() + " a " + newQuantity + ".");
                        } else {
                            System.out.println("Prodotto da modificare non trovato nel carrello.");
                        }
                        break;
                    case 5:
                        if (selectedProducts.isEmpty()) {
                            System.out.println("Nessun prodotto nel tuo ordine da cancellare.");
                            break;
                        }

                        System.out.print("Inserisci l'ID dell'ordine da cancellare: ");
                        int orderIdToDelete = scanner.nextInt();

                        orderController.deleteOrder(orderIdToDelete);

                        selectedProducts.clear();
                        System.out.println("Ordine cancellato e carrello svuotato.");
                        break;
                    case 6:
                        Order newOrder = new Order();
                        newOrder.getProducts().addAll(selectedProducts);
                        newOrder.setTotal(selectedProducts.stream().mapToDouble(Product::getPrice).sum());

                        int userId = userController.getCurrentUserId();

                        orderController.addOrder(newOrder, userId);
                        orderController.addProductsToOrder(newOrder.getId(), selectedProducts);
                        System.out.println("Ordine confermato:\n" + newOrder);
                        selectedProducts.clear();
                        break;
                    case 7:
                        scanner.close();
                        System.out.println("Uscita dal programma...");
                        return;
                    default:
                        System.out.println("Scelta non valida. Riprova.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}