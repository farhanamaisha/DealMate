package ui;

import javax.swing.*;
import java.awt.*;
import dao.ProductDAO;
import model.Product;
import java.util.List;

public class SearchPage extends JFrame {

    public SearchPage(String query) {
        setTitle("Search Results for: " + query);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));

        ProductDAO dao = new ProductDAO();
        List<Product> products = dao.searchProducts(query);

        if (products.isEmpty()) {
            resultPanel.add(new JLabel("No products found!"));
        } else {
            for (Product p : products) {
                resultPanel.add(new JLabel("▶ " + p.getName() + " - $" + p.getPrice()));
            }
        }

        add(new JScrollPane(resultPanel));
    }
}

