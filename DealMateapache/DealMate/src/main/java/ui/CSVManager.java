
package ui;

import model.Product;
import model.Order;
import model.OrderItem;
import model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVManager {
private static final String USER_FILE = "users.csv";

    private static final String PRODUCT_FILE = "products.csv";
    private static final String ORDER_FILE = "orders.csv";
    
    // Save all users
public static void saveUsers(List<User> users) {
    try (PrintWriter pw = new PrintWriter(new FileWriter(USER_FILE))) {
        pw.println("id,name,role");
        for (User u : users) {
            pw.println(u.getId() + "," + u.getName() + "," + u.getRole());
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}

// Load all users
public static List<User> loadUsers() {
    List<User> users = new ArrayList<>();
    File file = new File(USER_FILE);
    if (!file.exists()) return users;
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line = br.readLine(); // skip header
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length < 3) continue;
            User u = new User();
            u.setId(Integer.parseInt(parts[0]));
            u.setName(parts[1]);
            u.setRole(parts[2]);
            users.add(u);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return users;
}


    // --------- PRODUCTS ---------
    public static void saveProducts(List<Product> products) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(PRODUCT_FILE))) {
         pw.println("id,name,price,sellerId");
            for (Product p : products) {
                pw.println(p.getId() + "," + p.getName() + "," + p.getPrice() + "," + p.getSellerId());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   public static List<Product> loadProducts() {
    List<Product> products = new ArrayList<>();
    File file = new File(PRODUCT_FILE);
    if (!file.exists()) return products;

    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line = br.readLine(); // skip header
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length < 4) continue; // now 4 columns: id,name,price,sellerId
            Product p = new Product();
            p.setId(Integer.parseInt(parts[0]));
            p.setName(parts[1]);
            p.setPrice(Double.parseDouble(parts[2]));
            p.setSellerId(Integer.parseInt(parts[3])); // important
            products.add(p);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return products;
}


    // --------- ORDERS ---------
    public static void saveOrders(List<Order> orders) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ORDER_FILE))) {
            pw.println("orderId,userId,productId,quantity"); // header
            for (Order o : orders) {
                for (OrderItem item : o.getItems()) {
                    pw.println(o.getId() + "," + o.getUser().getId() + "," +
                            item.getProduct().getId() + "," + item.getQuantity());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Order> loadOrders(List<Product> allProducts, List<User> allUsers) {
    List<Order> orders = new ArrayList<>();
    File file = new File(ORDER_FILE);
    if (!file.exists()) return orders;

    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line = br.readLine(); // skip header
        Order currentOrder = null;
        int lastOrderId = -1;

        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length < 4) continue;

            int orderId = Integer.parseInt(parts[0]);
            int userId = Integer.parseInt(parts[1]);
            int productId = Integer.parseInt(parts[2]);
            int qty = Integer.parseInt(parts[3]);

            // NEW ORDER
            if (orderId != lastOrderId) {
                User user = allUsers.stream()
                        .filter(u -> u.getId() == userId)
                        .findFirst()
                        .orElse(null);

                currentOrder = new Order();
                currentOrder.setId(orderId);
                currentOrder.setUser(user);
                currentOrder.setItems(new ArrayList<>()); // IMPORTANT
                orders.add(currentOrder);

                lastOrderId = orderId;
            }

            Product product = allProducts.stream()
                    .filter(p -> p.getId() == productId)
                    .findFirst()
                    .orElse(null);

            if (product != null) {
                OrderItem item = new OrderItem();
                item.setProduct(product);
                item.setQuantity(qty);
                currentOrder.getItems().add(item);
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    return orders;
}

}
