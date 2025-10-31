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
}
