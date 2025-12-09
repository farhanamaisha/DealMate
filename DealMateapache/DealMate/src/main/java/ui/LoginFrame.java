package ui;

import javax.swing.*;
import java.awt.*;
import dao.UserDAO;
import model.User;

public class LoginFrame extends JFrame {
    public LoginFrame() {
        setTitle("DealMate - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Color bgColor = new Color(255, 200, 220);
        Color textColor = Color.WHITE;
        Color buttonColor = new Color(70, 130, 180);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(textColor);
        JTextField emailField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(textColor);
        JPasswordField passwordField = new JPasswordField(20);

        JLabel roleLabel = new JLabel("Login as:");
        roleLabel.setForeground(textColor);
        String[] roles = {"buyer", "seller"};
        JComboBox<String> roleCombo = new JComboBox<>(roles);

        JLabel sellerIdLabel = new JLabel("Seller ID:");
        sellerIdLabel.setForeground(textColor);
        JTextField sellerIdField = new JTextField(20);
        sellerIdLabel.setVisible(false);
        sellerIdField.setVisible(false);

        roleCombo.addActionListener(e -> {
            boolean isSeller = "seller".equals(roleCombo.getSelectedItem());
            sellerIdLabel.setVisible(isSeller);
            sellerIdField.setVisible(isSeller);
            pack();
        });

        JButton loginBtn = new JButton("Login");
        JButton backBtn = new JButton("Back");
        styleButton(loginBtn, buttonColor, textColor);
        styleButton(backBtn, buttonColor, textColor);

        loginBtn.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String selectedRole = (String) roleCombo.getSelectedItem();

            UserDAO dao = new UserDAO();
            User user = dao.login(email, password);

            if (user != null) {
                user.setRole(selectedRole);

                // Assign Seller ID if seller
                if ("seller".equalsIgnoreCase(selectedRole)) {
                    String sellerIdStr = sellerIdField.getText().trim();
                    try {
                        int sellerId = Integer.parseInt(sellerIdStr);
                        user.setId(sellerId);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Invalid Seller ID!");
                        return;
                    }
                }

                JOptionPane.showMessageDialog(this, "Login successful as " + selectedRole + "!");
                new DashboardFrame(user).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!");
            }
        });

        backBtn.addActionListener(e -> {
            new FrontPage().setVisible(true);
            dispose();
        });

        gbc.gridx = 0; gbc.gridy = 0; panel.add(emailLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; panel.add(emailField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(passwordLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; panel.add(passwordField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(roleLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2; panel.add(roleCombo, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panel.add(sellerIdLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 3; panel.add(sellerIdField, gbc);
        gbc.gridx = 0; gbc.gridy = 4; panel.add(loginBtn, gbc);
        gbc.gridx = 1; gbc.gridy = 4; panel.add(backBtn, gbc);

        add(panel);
        setVisible(true);
    }

    private void styleButton(JButton button, Color bg, Color fg) {
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
    }
}
