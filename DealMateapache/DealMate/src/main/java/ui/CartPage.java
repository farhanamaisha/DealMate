package ui;

import model.Product;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.List;

public class CartPage extends JPanel {

    private final List<Product> cart;
    private final JPanel contentCards;
    private final String backCard;

    private JTable cartTable;
    private CartTableModel cartModel;

    public CartPage(List<Product> cart, JPanel contentCards, String backCard) {
        this.cart = cart;
        this.contentCards = contentCards;
        this.backCard = backCard;

        setLayout(new BorderLayout());
        initUI();
    }

    private void initUI() {
        // Title
        JLabel title = new JLabel("My Cart");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(new EmptyBorder(10, 12, 10, 12));
        add(title, BorderLayout.NORTH);

        // Table
        cartModel = new CartTableModel(cart);
        cartTable = new JTable(cartModel);
        cartTable.setFillsViewportHeight(true);
        cartTable.setRowHeight(28);
        JScrollPane scroll = new JScrollPane(cartTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));

        JButton backBtn = new JButton("← Back");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backBtn.setBackground(new Color(220, 210, 215));
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> {
            CardLayout cl = (CardLayout) contentCards.getLayout();
            cl.show(contentCards, backCard); // go back to previous card
        });

        JButton placeOrderBtn = new JButton("Place Order");
        placeOrderBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        placeOrderBtn.setBackground(new Color(255, 120, 150));
        placeOrderBtn.setForeground(Color.WHITE);
        placeOrderBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        placeOrderBtn.addActionListener(e -> placeOrder());

        bottom.add(backBtn);
        bottom.add(placeOrderBtn);

        add(bottom, BorderLayout.SOUTH);
    }

    private void placeOrder() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty!");
            return;
        }

        StringBuilder summary = new StringBuilder("Order placed:\n");
        double total = 0;
        for (Product p : cart) {
            summary.append(p.getName()).append(" - ৳").append(p.getPrice()).append("\n");
            total += p.getPrice();
        }
        summary.append("Total: ৳").append(total);

        JOptionPane.showMessageDialog(this, summary.toString());
        cart.clear();
        cartModel.fireTableDataChanged();
    }

    // Table Model
    private static class CartTableModel extends AbstractTableModel {
        private final String[] columns = {"ID", "Name", "Price"};
        private final List<Product> cart;

        public CartTableModel(List<Product> cart) { this.cart = cart; }

        @Override public int getRowCount() { return cart.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int col) { return columns[col]; }
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Product p = cart.get(rowIndex);
            switch (columnIndex) {
                case 0: return p.getId();
                case 1: return p.getName();
                case 2: return p.getPrice();
                default: return null;
            }
        }
    }
}
