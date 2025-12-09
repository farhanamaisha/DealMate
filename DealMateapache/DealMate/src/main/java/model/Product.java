package model;

public class Product {
    private int id;
    private String name;
    private double price;
    private int sellerId; // new field for seller ID

    public Product() {}

    public Product(int id, String name, double price, int sellerId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.sellerId = sellerId;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getSellerId() { return sellerId; }
    public void setSellerId(int sellerId) { this.sellerId = sellerId; }
}
