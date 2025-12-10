package dao;

import model.Product;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    private List<Product> products = new ArrayList<>();

    public List<Product> getAllProducts() {
        return products;
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public void deleteProduct(int id) {
        products.removeIf(p -> p.getId() == id);
    }

    // 🔍 Search products by name (case-insensitive)
    public List<Product> searchProducts(String name) {
        List<Product> list = new ArrayList<>();

        if (name == null || name.trim().isEmpty()) {
            return list; // return empty list if nothing to search
        }

        String searchLower = name.toLowerCase();

        for (Product p : products) {
            if (p.getName().toLowerCase().contains(searchLower)) {
                list.add(p);
            }
        }

        return list;
    }
}
