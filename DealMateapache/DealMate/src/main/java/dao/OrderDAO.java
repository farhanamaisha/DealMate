package dao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import model.Order;

public class OrderDAO {

    private static final String ORDER_FILE = "data/orders.dat";

    // Ensure data folder exists
    public OrderDAO() {
        File folder = new File("data");
        if (!folder.exists()) folder.mkdirs();
    }

    @SuppressWarnings("unchecked")
    public List<Order> getAllOrders() {
        File file = new File(ORDER_FILE);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Order>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Save a single order
    public void saveOrder(Order order) {
        List<Order> orders = getAllOrders();

        // Generate unique ID
        int maxId = orders.stream().mapToInt(Order::getId).max().orElse(0);
        order.setId(maxId + 1);

        orders.add(order);
        saveAllOrders(orders);
    }

    // Save the entire list
    private void saveAllOrders(List<Order> orders) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ORDER_FILE))) {
            oos.writeObject(orders);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
