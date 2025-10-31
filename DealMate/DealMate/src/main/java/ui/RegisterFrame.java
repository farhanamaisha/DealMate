package ui;

import javax.swing.*;
import java.awt.*;
import dao.UserDAO;
import model.User;

public class RegisterFrame extends JFrame {
    public RegisterFrame() {
        setTitle("DealMate - Register");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Color bgColor = new Color(34, 45, 65);
        Color textColor = Color.WHITE;
        Color buttonColor = new Color(70, 130, 180);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(bgColor);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(textColor);
        JTextField nameField = new JTextField(20);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(textColor);
        JTextField emailField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(textColor);
        JPasswordField passwordField = new JPasswordField(20);

        JButton registerBtn = new JButton("Register");
        JButton backBtn = new JButton("Back");

        styleButton(registerBtn, buttonColor, textColor);
        styleButton(backBtn, buttonColor, textColor);

        registerBtn.addActionListener(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!");
                return;
            }

            UserDAO dao = new UserDAO();
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            // Role is NOT set here
            boolean success = dao.register(user);

            if (success) {
                JOptionPane.showMessageDialog(this, "Registration successful! You can choose your role at login.");
                new LoginFrame().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Email already exists!");
            }
        });

        backBtn.addActionListener(e -> {
            new FrontPage().setVisible(true);
            dispose();
        });

        panel.add(nameLabel); panel.add(nameField);
        panel.add(emailLabel); panel.add(emailField);
        panel.add(passwordLabel); panel.add(passwordField);
        panel.add(registerBtn); panel.add(backBtn);

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
