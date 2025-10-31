package ui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import dao.ProductDAO;
import model.Product;
import java.util.List;

public class DashboardFrame extends JFrame {
    public DashboardFrame() {
        setTitle("DealMate - Dashboard");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton backBtn = new JButton("Logout");
        backBtn.addActionListener(e -> {
            new FrontPage().setVisible(true);
            dispose();
        });

        ProductDAO dao = new ProductDAO();
        List<Product> products = dao.getAllProducts();
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for(Product p : products) {
            listModel.addElement(p.getName() + " - $" + p.getPrice() + " (" + p.getQuantity() + ")");
        }

        JList<String> productList = new JList<>(listModel);

        add(new JScrollPane(productList), BorderLayout.CENTER);
        add(backBtn, BorderLayout.SOUTH);
        setVisible(true);
    }
}
