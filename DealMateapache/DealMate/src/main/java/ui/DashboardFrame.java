package ui;
import dao.OrderDAO;
import dao.ProductDAO;
import dao.UserDAO;
import model.Product;
import model.User;
import model.Order;
import model.OrderItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * DashboardFrame - Full polished modern pastel dashboard with:
 * - Sidebar navigation that switches views (Home, Products, Orders, Account)
 * - Pure-Swing custom charts (Bar, Line, Pie) implemented with paintComponent
 * - Product CRUD wired to ProductDAO (getAllProducts(), addProduct(), deleteProduct())
 * - Orders remain in-memory (can be replaced by OrderDAO later)
 * - UI polish: rounded panels, hover effects, avatar menu, search, bell, toast
 *
 * Notes:
 * - Keep your DAO methods consistent; adjust names if needed.
 * - This file aims to be self-contained and readable.
 */
public class DashboardFrame extends JFrame {
private final List<Product> cartProducts = new ArrayList<>();

    private final User currentUser;
    private final ProductDAO productDAO;
    private final UserDAO userDAO;
    private final OrderDAO orderDAO;
    

    private List<Product> products;
    private List<Order> orders;

    // CardLayout main content
    private final JPanel contentCards = new JPanel(new CardLayout());

    // Home stat labels
    private JLabel totalProductsLabel, totalOrdersLabel, pendingOrdersLabel, revenueLabel;

    // Tables and models
    private ProductTableModel productTableModel;
    private JTable productsTable;

    private OrderTableModel orderTableModel;
    private JTable ordersTable;

    // Buttons
    private JButton addProductBtn, removeProductBtn, placeOrderBtn, logoutBtn;

    // Toast overlay
    private final JLayeredPane layeredPane = new JLayeredPane();

    // Palette
    private final Color bgGradientStart = new Color(255, 240, 245);
    private final Color bgGradientEnd = new Color(255, 248, 250);
    private final Color sidebarColor = new Color(255, 224, 233);
    private final Color accentColor = new Color(255, 130, 160);
    private final Color cardColor = Color.WHITE;
    private final Color textColor = new Color(38, 32, 40);
    private final Font uiFont = new Font("Segoe UI", Font.PLAIN, 13);

    public DashboardFrame(User user) {
        this.currentUser = user;
        this.productDAO = new ProductDAO();
        this.userDAO = new UserDAO();
        this.orderDAO = new OrderDAO(); 
        this.products = new ArrayList<>();
        this.orders = new ArrayList<>();

        setTitle("DealMate Dashboard - " + user.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 760);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        loadData();
        initUI();
        refreshAll();
    }

    private void loadData() {
        List<Product> p = productDAO.getAllProducts();
        products = (p != null) ? p : new ArrayList<>();
        // orders can be loaded from DAO if implemented
         List<Order> o = orderDAO.getAllOrders();  // read saved orders
    orders = (o != null) ? o : new ArrayList<>();
    }

    private void initUI() {
        // Root panel with gradient background
        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                int w = getWidth(), h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, bgGradientStart, 0, h, bgGradientEnd);
                g2.setPaint(gp);
                g2.fillRect(0, 0, w, h);
            }
        };
        add(root, BorderLayout.CENTER);

        // Layered pane for toast overlay
        layeredPane.setLayout(new BorderLayout());
        root.add(layeredPane, BorderLayout.CENTER);

        // Sidebar & main
        JPanel sidebar = createSidebar();
        layeredPane.add(sidebar, BorderLayout.WEST);

        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setOpaque(false);
        mainArea.setBorder(new EmptyBorder(16, 16, 16, 16));
        layeredPane.add(mainArea, BorderLayout.CENTER);

        // Top header
        JPanel header = createHeader();
        mainArea.add(header, BorderLayout.NORTH);

        // Content area (cards)
        contentCards.setOpaque(false);
        contentCards.setBorder(new EmptyBorder(12, 0, 0, 0));
        mainArea.add(contentCards, BorderLayout.CENTER);

        // Add card views
        contentCards.add(createHomePanel(), "HOME");
        contentCards.add(createProductsPanel(), "PRODUCTS");
        contentCards.add(createOrdersPanel(), "ORDERS");
        contentCards.add(createAccountPanel(), "ACCOUNT");

        // default view
        ((CardLayout) contentCards.getLayout()).show(contentCards, "HOME");
        contentCards.add(new CartPage(cartProducts, contentCards, "HOME", cart -> {
    // This code runs when "Place Order" is clicked
    if (cart.isEmpty()) return;

    for (Product p : cart) {
        OrderItem it = new OrderItem();
        it.setProduct(p);
        it.setQuantity(1); // default quantity, can change later

        Order o = new Order();
        o.setId(orders.size() + 1);
        o.setUser(currentUser);
        o.getItems().add(it);

        orders.add(o);
    }

    cart.clear(); // empty the cart
    refreshAll(); // refresh home stats and recent orders
    showToast("Order placed for " + cart.size() + " items!");
}), "CART");

 }

    // ---------------- UI pieces ----------------

    private JPanel createSidebar() {
        JPanel side = new RoundedPanel(0, sidebarColor);
        side.setPreferredSize(new Dimension(220, getHeight()));
        side.setLayout(new BorderLayout());

        // Top logo
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        JLabel logo = new JLabel("DealMate");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logo.setForeground(textColor);
        logo.setBorder(new EmptyBorder(14, 14, 14, 14));
        top.add(logo);
        side.add(top, BorderLayout.NORTH);

        // Nav
        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(new EmptyBorder(6, 10, 12, 10));

        nav.add(createNavButton("Home", "HOME", true));
        nav.add(createNavButton("Products", "PRODUCTS", false));
        nav.add(createNavButton("Orders", "ORDERS", false));
        nav.add(createNavButton("Cart", "CART", false)); // <-- NEW CART BUTTON

        nav.add(createNavButton("Account", "ACCOUNT", false));
        nav.add(Box.createVerticalGlue());
        nav.add(createNavButton("Logout", "LOGOUT", false));

        side.add(nav, BorderLayout.CENTER);

        return side;
    }

    private JButton createNavButton(String text, String card, boolean active) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBackground(active ? accentColor : new Color(255, 242, 246));
        btn.setForeground(textColor);

        // hover effect
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(btn.getBackground().darker()); }
            @Override public void mouseExited(MouseEvent e) { btn.setBackground(active ? accentColor : new Color(255, 242, 246)); }
        });

        btn.addActionListener(e -> {
    if ("LOGOUT".equals(card)) {
        logout();
        return;
    }
    if ("CART".equals(card)) {
    ((CardLayout) contentCards.getLayout()).show(contentCards, "CART");
    return;
}

    ((CardLayout) contentCards.getLayout()).show(contentCards, card);
});


        return btn;
    }
    private JPanel searchPanel; // class-level

private void openSearchPage(String query) {
    if (query.isEmpty()) { showToast("Enter a search term."); return; }

    List<Product> results = productDAO.searchProducts(query);

    if (searchPanel == null) {
        searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);
        contentCards.add(searchPanel, "SEARCH");
    }
    searchPanel.removeAll();

    JLabel title = new JLabel("Search Results for: " + query);
    title.setFont(new Font("Segoe UI", Font.BOLD, 15));
    title.setBorder(new EmptyBorder(10, 12, 10, 12));
    title.setForeground(textColor);
    searchPanel.add(title, BorderLayout.NORTH);

    ProductTableModel model = new ProductTableModel(results);
    JTable table = new JTable(model);
    styleTable(table);
    JScrollPane scroll = new JScrollPane(table);
    JPanel container = new RoundedPanel(12, cardColor);
    container.setLayout(new BorderLayout());
    container.setBorder(new EmptyBorder(12, 12, 12, 12));
    container.add(scroll, BorderLayout.CENTER);
    searchPanel.add(container, BorderLayout.CENTER);

    ((CardLayout) contentCards.getLayout()).show(contentCards, "SEARCH");
    contentCards.revalidate();
    contentCards.repaint();
}

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(4, 4, 12, 4));

        // left title + welcome
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.setOpaque(false);
        JLabel title = new JLabel("DealMate Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(textColor);
        left.add(title);

        JLabel welcome = new JLabel("  — Welcome back, " + currentUser.getName());
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        welcome.setForeground(new Color(95, 78, 85));
        left.add(welcome);

        header.add(left, BorderLayout.WEST);

        // right: search, bell, avatar
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

       // Search container (icon + field)
JPanel searchBox = new JPanel(new BorderLayout());
searchBox.setOpaque(false);
searchBox.setBorder(BorderFactory.createLineBorder(new Color(235, 215, 221), 1));
searchBox.setPreferredSize(new Dimension(260, 34));

JLabel searchIcon = new JLabel("🔍");
searchIcon.setBorder(new EmptyBorder(0, 8, 0, 6));
searchIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // make clickable
searchBox.add(searchIcon, BorderLayout.WEST);

// Search field
JTextField search = new JTextField();
search.setBorder(null);
search.setFont(uiFont);
search.setOpaque(false);
searchBox.add(search, BorderLayout.CENTER);

// Placeholder text
search.setText("Search products...");
search.setForeground(Color.GRAY);
search.addFocusListener(new FocusAdapter() {
    @Override
    public void focusGained(FocusEvent e) {
        if (search.getText().equals("Search products...")) {
            search.setText("");
            search.setForeground(Color.BLACK);
        }
    }
    @Override
    public void focusLost(FocusEvent e) {
        if (search.getText().isEmpty()) {
            search.setForeground(Color.GRAY);
            search.setText("Search products...");
        }
    }
});

// Action when Enter is pressed
search.addActionListener(e -> openSearchPage(search.getText().trim()));

// Action when icon is clicked
searchIcon.addMouseListener(new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
        openSearchPage(search.getText().trim());
    }
});

// Add only the panel (remove extra search field add!)
right.add(searchBox);


        search.setFont(uiFont);
        search.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(235, 215, 221), 1),
                new EmptyBorder(6, 8, 6, 8)
        ));
        right.add(search);

        JButton bell = new JButton("\uD83D\uDD14");
        styleIconButton(bell);
        bell.setToolTipText("Notifications");
        right.add(bell);

        // avatar with menu
        JButton avatarBtn = new JButton("\uD83D\uDC64");
        styleIconButton(avatarBtn);
        avatarBtn.setToolTipText(currentUser.getName());
        JPopupMenu avatarMenu = new JPopupMenu();
        JMenuItem profileItem = new JMenuItem("Profile");
        JMenuItem settingsItem = new JMenuItem("Settings");
        avatarMenu.add(profileItem);
        avatarMenu.add(settingsItem);
        avatarBtn.addActionListener(e -> avatarMenu.show(avatarBtn, 0, avatarBtn.getHeight()));
        right.add(avatarBtn);

        header.add(right, BorderLayout.EAST);

        // separator
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(240, 232, 234));
        header.add(sep, BorderLayout.SOUTH);

        return header;
    }

    private JPanel createHomePanel() {
        JPanel home = new JPanel(new BorderLayout());
        home.setOpaque(false);

        // Top stat cards
        JPanel cards = new JPanel(new GridLayout(1, 4, 14, 0));
        cards.setOpaque(false);
        cards.setBorder(new EmptyBorder(8, 0, 12, 0));

        totalProductsLabel = makeStatCard("Total Products", "0");
        totalOrdersLabel = makeStatCard("Total Orders", "0");
        pendingOrdersLabel = makeStatCard("Pending Orders", "0");
        revenueLabel = makeStatCard("Revenue", "৳0.00");

        cards.add(wrapCard(totalProductsLabel));
        cards.add(wrapCard(totalOrdersLabel));
        cards.add(wrapCard(pendingOrdersLabel));
        cards.add(wrapCard(revenueLabel));

        home.add(cards, BorderLayout.NORTH);

        // Center split: charts left, recent orders right
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.65);
        split.setOpaque(false);
        split.setBorder(null);

        // Left: charts stacked
        JPanel chartsStack = new JPanel(new GridLayout(3, 1, 12, 12));
        chartsStack.setOpaque(false);
        chartsStack.add(createChartPanel("Sales (Last 7 days)", new LineChartPanel()));
        chartsStack.add(createChartPanel("Revenue by Day (Bar)", new BarChartPanel()));
        chartsStack.add(createChartPanel("Orders Breakdown", new PieChartPanel()));
        split.setLeftComponent(chartsStack);

        // Right: recent orders
        JPanel right = new RoundedPanel(12, cardColor);
        right.setLayout(new BorderLayout());
        right.setBorder(new EmptyBorder(12, 12, 12, 12));
        JLabel t = new JLabel("Recent Orders");
        t.setFont(new Font("Segoe UI", Font.BOLD, 14));
        t.setForeground(textColor);
        right.add(t, BorderLayout.NORTH);

        orderTableModel = new OrderTableModel(orders);
        ordersTable = new JTable(orderTableModel);
        styleTable(ordersTable);
        JScrollPane orderScroll = new JScrollPane(ordersTable);
        orderScroll.setBorder(BorderFactory.createEmptyBorder());
        right.add(orderScroll, BorderLayout.CENTER);

        split.setRightComponent(right);

        home.add(split, BorderLayout.CENTER);
        return home;
    }

    private JPanel createProductsPanel() {
        JPanel productsPanel = new JPanel(new BorderLayout());
        productsPanel.setOpaque(false);

        // Top quick actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        actions.setOpaque(false);
        addProductBtn = new JButton("＋ Add Product");
        removeProductBtn = new JButton("− Remove Product");
        placeOrderBtn = new JButton("🛒 Add to Cart");

        styleActionButton(addProductBtn);
        styleActionButton(removeProductBtn);
        styleActionButton(placeOrderBtn);

        if ("seller".equalsIgnoreCase(currentUser.getRole())) {
            actions.add(addProductBtn);
            actions.add(removeProductBtn);
        }
        if ("buyer".equalsIgnoreCase(currentUser.getRole()) || "seller".equalsIgnoreCase(currentUser.getRole())) {
            actions.add(placeOrderBtn);
        }
        productsPanel.add(actions, BorderLayout.NORTH);

        // Table
        productTableModel = new ProductTableModel(products);
        productsTable = new JTable(productTableModel);
        styleTable(productsTable);
        JScrollPane productScroll = new JScrollPane(productsTable);
        productScroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel tableOuter = new RoundedPanel(12, cardColor);
        tableOuter.setLayout(new BorderLayout());
        tableOuter.setBorder(new EmptyBorder(12, 12, 12, 12));
        JLabel label = new JLabel("Products");
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(textColor);
        tableOuter.add(label, BorderLayout.NORTH);
        tableOuter.add(productScroll, BorderLayout.CENTER);

        productsPanel.add(tableOuter, BorderLayout.CENTER);

        // hook actions
        addProductBtn.addActionListener(e -> onAddProduct());
        removeProductBtn.addActionListener(e -> onRemoveProduct());
        placeOrderBtn.addActionListener(e -> {
    int row = productsTable.getSelectedRow();
    if (row < 0) return;
    Product p = products.get(row);
    cartProducts.add(p);  // add to cart
    showToast(p.getName() + " added to cart!");
});

        return productsPanel;
    }

    private JPanel createOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        orderTableModel = new OrderTableModel(orders);
        ordersTable = new JTable(orderTableModel);
        styleTable(ordersTable);
        JScrollPane scroll = new JScrollPane(ordersTable);

        JPanel container = new RoundedPanel(12, cardColor);
        container.setLayout(new BorderLayout());
        container.setBorder(new EmptyBorder(12, 12, 12, 12));
        JLabel label = new JLabel("Orders");
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(textColor);
        container.add(label, BorderLayout.NORTH);
        container.add(scroll, BorderLayout.CENTER);

        panel.add(container, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAccountPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel card = new RoundedPanel(12, cardColor);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(uiFont);
        nameLabel.setForeground(textColor);
        card.add(nameLabel, gbc);

        gbc.gridx = 1;
        JLabel nameValue = new JLabel(currentUser.getName());
        nameValue.setFont(uiFont);
        nameValue.setForeground(textColor);
        card.add(nameValue, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(uiFont);
        roleLabel.setForeground(textColor);
        card.add(roleLabel, gbc);

        gbc.gridx = 1;
        JLabel roleValue = new JLabel(currentUser.getRole());
        roleValue.setFont(uiFont);
        roleValue.setForeground(textColor);
        card.add(roleValue, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel idLabel = new JLabel("User ID:");
        idLabel.setFont(uiFont);
        idLabel.setForeground(textColor);
        card.add(idLabel, gbc);

        gbc.gridx = 1;
        JLabel idValue = new JLabel(String.valueOf(currentUser.getId()));
        idValue.setFont(uiFont);
        idValue.setForeground(textColor);
        card.add(idValue, gbc);

        panel.add(card, BorderLayout.NORTH);
        return panel;
    }

    // ---------------- UI helpers ----------------

    private JLabel makeStatCard(String title, String value) {
        JLabel lb = new JLabel("<html><div style='text-align:left;'>" +
                "<div style='font-size:12px;color:#8b737a;'>" + title + "</div>" +
                "<div style='font-size:18px;color:#2b2430;'><b>" + value + "</b></div>" +
                "</div></html>");
        lb.setOpaque(false);
        lb.setBorder(new EmptyBorder(8, 12, 8, 12));
        return lb;
    }

    private JPanel wrapCard(JLabel content) {
        JPanel p = new RoundedPanel(12, cardColor);
        p.setLayout(new BorderLayout());
        p.setBorder(new EmptyBorder(12, 12, 12, 12));
        p.add(content, BorderLayout.CENTER);
        return p;
    }

    private JPanel createChartPanel(String title, JComponent chartComp) {
        JPanel p = new RoundedPanel(12, cardColor);
        p.setLayout(new BorderLayout());
        p.setBorder(new EmptyBorder(12, 12, 12, 12));
        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 13));
        t.setForeground(textColor);
        p.add(t, BorderLayout.NORTH);
        p.add(chartComp, BorderLayout.CENTER);
        return p;
    }

    private void styleTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setRowHeight(28);
        table.setIntercellSpacing(new Dimension(8, 6));
        table.setFont(uiFont);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setReorderingAllowed(false);
    }

    private void styleActionButton(JButton b) {
        b.setBackground(accentColor);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(accentColor.darker()); }
            @Override public void mouseExited(MouseEvent e) { b.setBackground(accentColor); }
        });
    }

    private void styleIconButton(JButton b) {
        b.setBackground(new Color(255, 245, 247));
        b.setForeground(textColor);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(new Color(245, 220, 229), 1));
        b.setPreferredSize(new Dimension(40, 34));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // ---------------- Actions ----------------

    private void onAddProduct() {
        JPanel input = new JPanel(new GridLayout(3, 2, 8, 8));
        input.add(new JLabel("Product Name:"));
        JTextField nameField = new JTextField();
        input.add(nameField);

        input.add(new JLabel("Price:"));
        JTextField priceField = new JTextField();
        input.add(priceField);

        input.add(new JLabel("Seller ID:"));
        JTextField sellerField = new JTextField(String.valueOf(currentUser.getId()));
        sellerField.setEditable(false);
        input.add(sellerField);

        int res = JOptionPane.showConfirmDialog(this, input, "Add Product", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;
        String name = nameField.getText().trim();
        String priceStr = priceField.getText().trim();
        if (name.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }
        double price;
        try { price = Double.parseDouble(priceStr); } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Invalid price."); return; }

        Product p = new Product();
        p.setName(name); p.setPrice(price); p.setSellerId(currentUser.getId());

        productDAO.addProduct(p);
        loadData();
        refreshAll();
        showToast("Product added: " + name);
    }

    private void onRemoveProduct() {
        int row = productsTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a product."); return; }
        Product p = products.get(row);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete " + p.getName() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        productDAO.deleteProduct(p.getId());
        loadData();
        refreshAll();
        showToast("Product removed: " + p.getName());
    }

    private void onPlaceOrder() {
        int row = productsTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a product to order."); return; }
        Product p = products.get(row);
        String qtyStr = JOptionPane.showInputDialog(this, "Enter quantity:", "1");
        if (qtyStr == null || qtyStr.trim().isEmpty()) return;
        int qty;
        try { qty = Integer.parseInt(qtyStr); if (qty <= 0) throw new NumberFormatException(); } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Invalid qty."); return; }

        OrderItem it = new OrderItem();
        it.setProduct(p); it.setQuantity(qty);
        Order o = new Order();
        o.setId(orders.size() + 1);
        o.setUser(currentUser);
        o.getItems().add(it);
        orders.add(o);
        refreshAll();
        showToast("Order placed: " + p.getName() + " x" + qty);
    }

    private void logout() {
        new LoginFrame().setVisible(true);
        dispose();
    }

    // ---------------- Refresh and stats ----------------

    private void refreshAll() {
        // refresh product table
        if (productTableModel == null) {
            productTableModel = new ProductTableModel(products);
        } else {
            productTableModel.productList = products;
            productTableModel.fireTableDataChanged();
        }
        if (productsTable != null) productsTable.revalidate();

        // refresh order table
        if (orderTableModel == null) {
            orderTableModel = new OrderTableModel(orders);
        } else {
            orderTableModel.orderList = orders;
            orderTableModel.fireTableDataChanged();
        }

        // update stat labels
        int totalProducts = products.size();
        int totalOrders = orders.size();
        int pending = totalOrders; // placeholder
        double revenue = 0.0;
        for (Order o : orders) {
            for (OrderItem it : o.getItems()) {
                if (it.getProduct() != null) revenue += it.getQuantity() * it.getProduct().getPrice();
            }
        }

        totalProductsLabel.setText("<html><div style='text-align:left;'><div style='font-size:12px;color:#8a6f77;'>Total Products</div><div style='font-size:18px;color:#2b2430;'><b>" + totalProducts + "</b></div></div></html>");
        totalOrdersLabel.setText("<html><div style='text-align:left;'><div style='font-size:12px;color:#8a6f77;'>Total Orders</div><div style='font-size:18px;color:#2b2430;'><b>" + totalOrders + "</b></div></div></html>");
        pendingOrdersLabel.setText("<html><div style='text-align:left;'><div style='font-size:12px;color:#8a6f77;'>Pending Orders</div><div style='font-size:18px;color:#2b2430;'><b>" + pending + "</b></div></div></html>");
        revenueLabel.setText("<html><div style='text-align:left;'><div style='font-size:12px;color:#8a6f77;'>Revenue</div><div style='font-size:18px;color:#2b2430;'><b>৳" + String.format("%.2f", revenue) + "</b></div></div></html>");

        // repaint charts (they read products/orders for data)
        contentCards.revalidate();
        contentCards.repaint();
    }

    // Simple transient toast
    private void showToast(String message) {
        JLabel toast = new JLabel(message);
        toast.setOpaque(true);
        toast.setBackground(new Color(60, 50, 60, 230));
        toast.setForeground(Color.WHITE);
        toast.setBorder(new EmptyBorder(10, 14, 10, 14));
        toast.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        Dimension size = toast.getPreferredSize();
        toast.setBounds(getWidth()/2 - size.width/2, 30, size.width, size.height);
        layeredPane.add(toast, JLayeredPane.POPUP_LAYER);
        layeredPane.repaint();

        // fade out after 2.3s
       javax.swing.Timer t = new javax.swing.Timer(2300, e -> layeredPane.remove(toast));
t.setRepeats(false);
t.start();

    }

    // ----------------- Table models -----------------

    private class ProductTableModel extends AbstractTableModel {
        private final String[] cols = {"ID", "Name", "Price", "Seller ID"};
        private List<Product> productList;

        public ProductTableModel(List<Product> list) { this.productList = (list != null) ? list : new ArrayList<>(); }

        @Override public int getRowCount() { return productList.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int col) { return cols[col]; }
        @Override
        public Object getValueAt(int row, int col) {
            Product p = productList.get(row);
            switch (col) {
                case 0: return p.getId();
                case 1: return p.getName();
                case 2: return p.getPrice();
                case 3: return p.getSellerId();
                default: return null;
            }
        }
    }

    private class OrderTableModel extends AbstractTableModel {
        private final String[] cols = {"Order ID", "User", "Product", "Qty", "Total"};
        private List<Order> orderList;

        public OrderTableModel(List<Order> list) { this.orderList = (list != null) ? list : new ArrayList<>(); }

        @Override
        public int getRowCount() {
            int rows = 0;
            for (Order o : orderList) rows += o.getItems().size();
            return rows;
        }

        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int col) { return cols[col]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            int idx = 0;
            for (Order o : orderList) {
                for (OrderItem it : o.getItems()) {
                    if (idx == rowIndex) {
                        switch (columnIndex) {
                            case 0: return o.getId();
                            case 1: return (o.getUser() != null ? o.getUser().getName() : "Unknown");
                            case 2: return (it.getProduct() != null ? it.getProduct().getName() : "—");
                            case 3: return it.getQuantity();
                            case 4: return it.getQuantity() * (it.getProduct() != null ? it.getProduct().getPrice() : 0.0);
                        }
                    }
                    idx++;
                }
            }
            return null;
        }
    }

    // ----------------- RoundedPanel -----------------

    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bg;

        public RoundedPanel(int radius, Color bg) {
            super();
            this.radius = radius;
            this.bg = bg;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            int w = getWidth(), h = getHeight();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, w, h, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ----------------- Custom Charts (Pure Swing) -----------------

    /**
     * LineChartPanel - draws a smooth-ish line representing recent sales.
     * Pulls data from products/orders lists (simple synthetic sample derived from orders).
     */
    private class LineChartPanel extends JPanel {
        public LineChartPanel() { setPreferredSize(new Dimension(200, 120)); setOpaque(false); }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Background lightly
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth(), h = getHeight();
            g2.setColor(new Color(255, 255, 255, 0));
            g2.fillRect(0, 0, w, h);

            // sample data: sales for last 7 days computed from orders (naive)
            double[] data = new double[7];
            Calendar c = Calendar.getInstance();
            for (Order o : orders) {
                // map all orders to index based on days difference (naive recent only)
                // we don't have timestamps in model here; so distribute randomly for demo
                int idx = (int) (Math.abs(o.getId()) % 7);
                double sum = 0;
                for (OrderItem it : o.getItems()) sum += it.getQuantity() * (it.getProduct() != null ? it.getProduct().getPrice() : 0);
                data[idx] += sum;
            }

            // If no orders, generate tiny dummy points so chart shows graceful baseline
            boolean empty = true;
            for (double v : data) if (v > 0) empty = false;
            if (empty) for (int i = 0; i < data.length; i++) data[i] = (i + 1) * 5;

            double max = Arrays.stream(data).max().orElse(1);

            int padding = 14;
            int graphW = w - padding*2;
            int graphH = h - padding*2;

            // grid lines
            g2.setColor(new Color(245, 235, 237));
            for (int i = 0; i <= 4; i++) {
                int y = padding + i * (graphH / 4);
                g2.drawLine(padding, y, padding + graphW, y);
            }

            // polyline
            g2.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(255, 120, 150));
            int prevX = -1, prevY = -1;
            for (int i = 0; i < data.length; i++) {
                int x = padding + (int) ((i / (double)(data.length - 1)) * graphW);
                int y = padding + graphH - (int) ((data[i] / max) * graphH);
                g2.fillOval(x-3, y-3, 6, 6);
                if (prevX >= 0) g2.drawLine(prevX, prevY, x, y);
                prevX = x; prevY = y;
            }

            g2.dispose();
        }
    }

    /**
     * BarChartPanel - draws bars for revenue per day (synthetic).
     */
    private class BarChartPanel extends JPanel {
        public BarChartPanel() { setPreferredSize(new Dimension(200, 120)); setOpaque(false); }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth(), h = getHeight();
            int padding = 16;
            int graphW = w - padding*2;
            int graphH = h - padding*2;

            // sample 7 bars (use product prices or order totals)
            double[] vals = new double[7];
            for (int i = 0; i < vals.length; i++) vals[i] = (i + 1) * 15; // fallback
            // compute from orders somewhat
            for (Order o : orders) {
                int idx = Math.abs(o.getId()) % 7;
                double sum = 0;
                for (OrderItem it : o.getItems()) sum += it.getQuantity() * (it.getProduct() != null ? it.getProduct().getPrice() : 0);
                vals[idx] += sum;
            }
            double max = Arrays.stream(vals).max().orElse(1);

            int barWidth = graphW / vals.length - 8;
            for (int i = 0; i < vals.length; i++) {
                int x = padding + i * (barWidth + 8);
                int barH = (int) ((vals[i] / max) * (graphH - 16));
                int y = padding + (graphH - barH);
                g2.setColor(new Color(255, 160, 185));
                g2.fillRoundRect(x, y, barWidth, barH, 8, 8);
            }
            g2.dispose();
        }
    }

    /**
     * PieChartPanel - simple pie representing order statuses (we don't have statuses, so sample by order id)
     */
    private class PieChartPanel extends JPanel {
        public PieChartPanel() { setPreferredSize(new Dimension(200, 120)); setOpaque(false); }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth(), h = getHeight();
            int size = Math.min(w, h) - 32;

            // sample breakdown: shipped / pending / cancelled (synthetic)
            int shipped = 0, pending = 0, cancelled = 0;
            for (Order o : orders) {
                int v = Math.abs(o.getId()) % 3;
                if (v == 0) shipped++; else if (v == 1) pending++; else cancelled++;
            }
            if (orders.isEmpty()) { shipped = 2; pending = 1; cancelled = 1; }

            int total = shipped + pending + cancelled;
            double start = 0.0;
            int cx = 20 + size/2, cy = 20 + size/2;
            int arcSize = size;

            // shipped - pink
            double angle = (shipped / (double) total) * 360.0;
            g2.setColor(new Color(255, 140, 170));
            g2.fillArc(cx - size/2, cy - size/2, arcSize, arcSize, (int) Math.round(start), (int) Math.round(angle));
            start += angle;

            // pending - pale
            angle = (pending / (double) total) * 360.0;
            g2.setColor(new Color(255, 200, 210));
            g2.fillArc(cx - size/2, cy - size/2, arcSize, arcSize, (int)Math.round(start), (int)Math.round(angle));
            start += angle;

            // cancelled - darker
            angle = (cancelled / (double) total) * 360.0;
            g2.setColor(new Color(240, 120, 150));
            g2.fillArc(cx - size/2, cy - size/2, arcSize, arcSize, (int)Math.round(start), (int)Math.round(angle));

            g2.dispose();
        }
    }

    // ----------------- Entry for quick test -----------------

    public static void main(String[] args) {
        // sample user for testing
        User demo = new User();
        demo.setId(1);
        demo.setName("Maisha");
        demo.setRole("seller");
        SwingUtilities.invokeLater(() -> {
            DashboardFrame d = new DashboardFrame(demo);
            d.setVisible(true);
        });
    }
}
