package model;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private int id;
    private User user;
    private List<OrderItem> items;

    public Order() {
        items = new ArrayList<>();
    }

    public Order(int id, User user) {
        this.id = id;
        this.user = user;
        this.items = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<OrderItem> getItems() { return items; }
    public void addItem(OrderItem item) { items.add(item); }
}
