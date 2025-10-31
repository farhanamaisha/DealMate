package dao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import model.User;
import model.Product;

public class DatabaseConnection {

    private static final String USER_FILE = "data/users.dat";
    private static final String PRODUCT_FILE = "data/products.dat";

    // Users
    public static List<User> loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_FILE))) {
            return (List<User>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void saveUsers(List<User> users) {
        try {
            new File("data").mkdirs();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_FILE))) {
                oos.writeObject(users);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Products
    public static List<Product> loadProducts() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PRODUCT_FILE))) {
            return (List<Product>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void saveProducts(List<Product> products) {
        try {
            new File("data").mkdirs();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PRODUCT_FILE))) {
                oos.writeObject(products);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}
