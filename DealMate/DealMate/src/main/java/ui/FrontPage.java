package ui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FrontPage extends JFrame {
    public FrontPage() {
        setTitle("DealMate - Home");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        loginBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        registerBtn.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });

        JPanel panel = new JPanel();
        panel.add(loginBtn);
        panel.add(registerBtn);

        add(panel);
        setVisible(true);
    }
}
