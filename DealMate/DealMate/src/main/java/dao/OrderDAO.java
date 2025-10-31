package dao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import model.Order;

public class OrderDAO {

    private static final String ORDER_FILE = "data/orders.dat";

    public List<Order> getAllOrders() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ORDER_FILE))) {
            return (List<Order>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void saveOrder(Order order) {
        List<Order> orders = getAllOrders();
        orders.add(order);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ORDER_FILE))) {
            oos.writeObject(orders);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
