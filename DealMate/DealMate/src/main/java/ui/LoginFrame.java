package ui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import dao.UserDAO;
import model.User;

public class LoginFrame extends JFrame {
    public LoginFrame() {
        setTitle("DealMate - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);

        JButton loginBtn = new JButton("Login");
        JButton backBtn = new JButton("Back");

        loginBtn.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            UserDAO dao = new UserDAO();
            User user = dao.login(email, password);
            if(user != null) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                new DashboardFrame().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }
        });

        backBtn.addActionListener(e -> {
            new FrontPage().setVisible(true);
            dispose();
        });

        JPanel panel = new JPanel(new GridLayout(3,2));
        panel.add(emailLabel); panel.add(emailField);
        panel.add(passwordLabel); panel.add(passwordField);
        panel.add(loginBtn); panel.add(backBtn);

        add(panel);
        setVisible(true);
    }
}
