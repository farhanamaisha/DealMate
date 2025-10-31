package dao;

import java.util.List;
import model.Product;

public class ProductDAO {

    public boolean addProduct(Product p) {
        List<Product> products = DatabaseConnection.loadProducts();
        products.add(p);
        DatabaseConnection.saveProducts(products);
        return true;
    }

    public List<Product> getAllProducts() {
        return DatabaseConnection.loadProducts();
    }
}
