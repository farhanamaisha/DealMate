package dao;

import model.User;
import java.io.*;
import java.util.*;

public class UserDAO {
    private final String FILE_PATH = "users.dat";

    // Load users from file
    public List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            users = (List<User>) ois.readObject();
        } catch (FileNotFoundException e) {
            // first run → create default seller and buyer
            User seller = new User("Seller", "seller@deal.com", "1234", "seller");
            seller.setId(1);
            User buyer = new User("Buyer", "buyer@deal.com", "1234", "buyer");
            buyer.setId(2);
            users.add(seller);
            users.add(buyer);
            saveUsers(users);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return users;
    }

    // Save users to file
    public void saveUsers(List<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Register a user
    public boolean register(User user) {
        List<User> users = loadUsers();
        // Check if email already exists
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(user.getEmail())) {
                return false;
            }
        }
        user.setId(users.size() + 1); // simple auto-increment id
        user.setRole("buyer"); // default role
        users.add(user);
        saveUsers(users);
        return true;
    }

    // Login
    public User login(String email, String password) {
        List<User> users = loadUsers();
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email) && u.getPassword().equals(password)) {
                return u; // role is now set
            }
        }
        return null;
    }
}
