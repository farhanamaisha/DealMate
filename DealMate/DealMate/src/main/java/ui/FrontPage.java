package ui;

import javax.swing.*;
import java.awt.*;

public class FrontPage extends JFrame {

    public FrontPage() {
        setTitle("DealMate - Home");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set a nice solid background color
        Color bgColor = new Color(34, 45, 65); // dark blueish
        Color textColor = new Color(255, 255, 255); // white text

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome to DealMate!");
        welcomeLabel.setForeground(textColor);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Buttons
        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        loginBtn.setBackground(new Color(70, 130, 180)); // steel blue
        loginBtn.setForeground(Color.WHITE);
        registerBtn.setBackground(new Color(70, 130, 180));
        registerBtn.setForeground(Color.WHITE);

        loginBtn.setFocusPainted(false);
        registerBtn.setFocusPainted(false);

        loginBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        registerBtn.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });

        // Layout
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); // make panel transparent to show bg
        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 20));
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }
}
