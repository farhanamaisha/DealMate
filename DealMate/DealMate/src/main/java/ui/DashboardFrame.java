package ui;

import dao.ProductDAO;
import dao.UserDAO;
import model.Product;
import model.User;
import model.Order;
import model.OrderItem;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DashboardFrame extends JFrame {

    private JTable productTable;
    private ProductTableModel productTableModel;

    private JTable orderTable;
    private OrderTableModel orderTableModel;

    private JButton addProductButton;
    private JButton removeProductButton;
    private JButton placeOrderButton;
    private JButton logoutButton;

    private ProductDAO productDAO;
    private UserDAO userDAO;

    private List<Product> products;
    private List<Order> orders;

    private User currentUser;

    private Color bgColor = new Color(34, 45, 65);
    private Color textColor = Color.WHITE;
    private Color buttonColor = new Color(70, 130, 180);

    public DashboardFrame(User user) {
        this.currentUser = user;
        setTitle("DealMate Dashboard - " + user.getName() + " (" + user.getRole() + ")");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        productDAO = new ProductDAO();
        userDAO = new UserDAO();

        products = productDAO.getAllProducts(); // load fresh products
        orders = new ArrayList<>();

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(bgColor);

        // --- Top panel with logout ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setBackground(bgColor);
        logoutButton = new JButton("Logout");
        styleButton(logoutButton);
        topPanel.add(logoutButton);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // --- Product Table ---
        productTableModel = new ProductTableModel(products);
        productTable = new JTable(productTableModel);
        productTable.setFillsViewportHeight(true);
        JScrollPane productScroll = new JScrollPane(productTable);
        productScroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(buttonColor, 2), "Products", 0, 0, new Font("Arial", Font.BOLD, 14), textColor));

        // --- Buttons ---
        addProductButton = new JButton("Add Product");
        removeProductButton = new JButton("Remove Product");
        placeOrderButton = new JButton("Place Order");

        styleButton(addProductButton);
        styleButton(removeProductButton);
        styleButton(placeOrderButton);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(bgColor);

        if ("seller".equalsIgnoreCase(currentUser.getRole())) {
            buttonPanel.add(addProductButton);
            buttonPanel.add(removeProductButton);
        }
        if ("buyer".equalsIgnoreCase(currentUser.getRole()) || "seller".equalsIgnoreCase(currentUser.getRole())) {
            buttonPanel.add(placeOrderButton);
        }

        JPanel productPanel = new JPanel(new BorderLayout());
        productPanel.setBackground(bgColor);
        productPanel.add(productScroll, BorderLayout.CENTER);
        productPanel.add(buttonPanel, BorderLayout.SOUTH);

        // --- Order Table ---
        orderTableModel = new OrderTableModel(orders);
        orderTable = new JTable(orderTableModel);
        orderTable.setFillsViewportHeight(true);
        JScrollPane orderScroll = new JScrollPane(orderTable);
        orderScroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(buttonColor, 2), "Orders", 0, 0, new Font("Arial", Font.BOLD, 14), textColor));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, productPanel, orderScroll);
        splitPane.setDividerLocation(300);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        add(mainPanel);

        // --- Button Actions ---
        addProductButton.addActionListener(e -> addProduct());
        removeProductButton.addActionListener(e -> removeSelectedProduct());
        placeOrderButton.addActionListener(e -> placeOrder());
        logoutButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
    }

    private void styleButton(JButton button) {
        button.setBackground(buttonColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
    }

    private void addProduct() {
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.add(new JLabel("Product Name:"));
        JTextField nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Price:"));
        JTextField priceField = new JTextField();
        inputPanel.add(priceField);

        inputPanel.add(new JLabel("Seller ID:"));
        JTextField sellerField = new JTextField(String.valueOf(currentUser.getId()));
        sellerField.setEditable(false); // prefill with current seller
        inputPanel.add(sellerField);

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "Add Product", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        String name = nameField.getText().trim();
        String priceStr = priceField.getText().trim();

        if (name.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid price!");
            return;
        }

        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setSellerId(currentUser.getId()); // assign seller ID

        productDAO.addProduct(product);

        // --- FIX: refresh products to avoid duplicates ---
        products = productDAO.getAllProducts();
        productTableModel.productList = products;
        productTableModel.fireTableDataChanged();
    }

    private void removeSelectedProduct() {
        int row = productTable.getSelectedRow();
        if (row >= 0 && row < products.size()) {
            Product removed = products.get(row);
            productDAO.deleteProduct(removed.getId());
            products = productDAO.getAllProducts();
            productTableModel.productList = products;
            productTableModel.fireTableDataChanged();
        } else {
            JOptionPane.showMessageDialog(this, "Select a product to remove!");
        }
    }

    private void placeOrder() {
        int row = productTable.getSelectedRow();
        if (row < 0 || row >= products.size()) {
            JOptionPane.showMessageDialog(this, "Select a product to order!");
            return;
        }

        String qtyStr = JOptionPane.showInputDialog(this, "Enter quantity:");
        if (qtyStr == null || qtyStr.trim().isEmpty()) return;

        int qty;
        try {
            qty = Integer.parseInt(qtyStr);
            if (qty <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity!");
            return;
        }

        Product selectedProduct = products.get(row);

        OrderItem item = new OrderItem();
        item.setProduct(selectedProduct);
        item.setQuantity(qty);

        Order order = new Order();
        order.setUser(currentUser);
        order.getItems().add(item);
        order.setId(orders.size() + 1);

        orders.add(order);
        orderTableModel.fireTableDataChanged();

        JOptionPane.showMessageDialog(this, "Order placed successfully!");
    }

    // --- Table Models ---
    private class ProductTableModel extends AbstractTableModel {
        private final String[] columnNames = {"ID", "Name", "Price", "Seller ID"};
        private List<Product> productList;

        public ProductTableModel(List<Product> productList) { this.productList = productList; }

        @Override public int getRowCount() { return productList.size(); }
        @Override public int getColumnCount() { return columnNames.length; }
        @Override public String getColumnName(int column) { return columnNames[column]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Product p = productList.get(rowIndex);
            switch (columnIndex) {
                case 0: return p.getId();
                case 1: return p.getName();
                case 2: return p.getPrice();
                case 3: return p.getSellerId();
                default: return null;
            }
        }
    }

    private class OrderTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Order ID", "User", "Product", "Quantity", "Total"};
        private List<Order> orderList;

        public OrderTableModel(List<Order> orderList) { this.orderList = orderList; }

        @Override
        public int getRowCount() {
            int count = 0;
            for (Order o : orderList) count += o.getItems().size();
            return count;
        }

        @Override public int getColumnCount() { return columnNames.length; }
        @Override public String getColumnName(int column) { return columnNames[column]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            int currentIndex = 0;
            for (Order o : orderList) {
                for (OrderItem item : o.getItems()) {
                    if (currentIndex == rowIndex) {
                        switch (columnIndex) {
                            case 0: return o.getId();
                            case 1: return o.getUser().getName();
                            case 2: return item.getProduct().getName();
                            case 3: return item.getQuantity();
                            case 4: return item.getQuantity() * item.getProduct().getPrice();
                        }
                    }
                    currentIndex++;
                }
            }
            return null;
        }
    }
}
