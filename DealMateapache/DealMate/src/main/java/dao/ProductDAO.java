package dao;
import ui.CSVManager; // wherever your CSVManager is


import java.util.List;
import java.util.ArrayList;
import model.Product;  // make sure this matches the actual package of your Product class


public class ProductDAO {

    private List<Product> products;

    public ProductDAO() {
        // Load products from CSV at startup
        products = CSVManager.loadProducts();
        if (products == null) products = new ArrayList<>();
    }

    public List<Product> getAllProducts() {
        return products;
    }

    public void addProduct(Product product) {
        // Give product an ID
        int maxId = products.stream().mapToInt(Product::getId).max().orElse(0);
        product.setId(maxId + 1);

        products.add(product);

        // Save immediately to CSV
        CSVManager.saveProducts(products);
    }

    public void deleteProduct(int id) {
        products.removeIf(p -> p.getId() == id);

        // Save updated list
        CSVManager.saveProducts(products);
    }

    public List<Product> searchProducts(String name) {
        List<Product> list = new ArrayList<>();
        if (name == null || name.trim().isEmpty()) return list;

        String searchLower = name.toLowerCase();
        for (Product p : products) {
            if (p.getName().toLowerCase().contains(searchLower)) {
                list.add(p);
            }
        }
        return list;
    }
}
