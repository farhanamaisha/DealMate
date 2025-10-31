package ui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import dao.UserDAO;
import model.User;

public class RegisterFrame extends JFrame {
    public RegisterFrame() {
        setTitle("DealMate - Register");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(20);

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);

        JButton registerBtn = new JButton("Register");
        JButton backBtn = new JButton("Back");

        registerBtn.addActionListener(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            User user = new User(name, email, password, "customer");
            UserDAO dao = new UserDAO();
            dao.register(user);

            JOptionPane.showMessageDialog(this, "Registration successful!");
            new LoginFrame().setVisible(true);
            dispose();
        });

        backBtn.addActionListener(e -> {
            new FrontPage().setVisible(true);
            dispose();
        });

        JPanel panel = new JPanel(new GridLayout(4,2));
        panel.add(nameLabel); panel.add(nameField);
        panel.add(emailLabel); panel.add(emailField);
        panel.add(passwordLabel); panel.add(passwordField);
        panel.add(registerBtn); panel.add(backBtn);

        add(panel);
        setVisible(true);
    }
}
