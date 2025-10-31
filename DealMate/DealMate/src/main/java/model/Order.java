package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable {
    private String userEmail;
    private List<OrderItem> items = new ArrayList<>();

    public Order(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserEmail() { return userEmail; }

    public List<OrderItem> getItems() { return items; }
    public void addItem(OrderItem item) { items.add(item); }
}
