package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FrontPage extends JFrame {

    private float alpha = 0f; // for welcome fade
    private boolean fadeOut = false;

    public FrontPage() {
        setTitle("DealMate - Home");
        setSize(600, 450);  // smaller frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // LOGIN button (now smaller + white)
        JButton loginBtn = new JButton("Login");
        loginBtn.setForeground(Color.BLACK);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 16));     // smaller text
        loginBtn.setBackground(Color.WHITE);                     // white button
        loginBtn.setFocusPainted(false);
        loginBtn.setVisible(false);

        // manual size
        loginBtn.setPreferredSize(new Dimension(140, 40));
        loginBtn.setMaximumSize(new Dimension(140, 40));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        // SIGNUP plain-text button (now visible + small + centered)
        JButton signupBtn = new JButton("If you don't have an account?");
        signupBtn.setForeground(Color.WHITE);
        signupBtn.setFont(new Font("Arial", Font.PLAIN, 14));     
        signupBtn.setContentAreaFilled(false);
        signupBtn.setBorderPainted(false);
        signupBtn.setFocusPainted(false);
        signupBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signupBtn.setVisible(false);

        signupBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        signupBtn.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });

        // BUTTON panel (BoxLayout so buttons stop being giant)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        buttonPanel.add(loginBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));   // small gap
        buttonPanel.add(signupBtn);

        // MAIN panel with pastel pink gradient
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Pastel pink gradient background
                Color pink1 = new Color(255, 182, 193); // light pastel pink
                Color pink2 = new Color(255, 160, 180); // deeper pastel pink
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, pink1, 0, getHeight(), pink2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Neon-white welcome text
                g2d.setFont(new Font("Arial", Font.BOLD, 40));
                g2d.setColor(new Color(255, 255, 255, (int)(alpha * 255)));
                String text = "Welcome to DealMate!";
                int stringWidth = g2d.getFontMetrics().stringWidth(text);
                int x = (getWidth() - stringWidth) / 2;
                int y = getHeight() / 3;

                g2d.drawString(text, x, y);
            }
        };

        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(80, 80, 80, 80));
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);

        // FADE animation timer
        Timer timer = new Timer(40, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!fadeOut) {
                    alpha += 0.03f;
                    if (alpha >= 1f) fadeOut = true;
                } else {
                    alpha -= 0.03f;
                    if (alpha <= 0f) {
                        // Show buttons after fade
                        loginBtn.setVisible(true);
                        signupBtn.setVisible(true);
                        ((Timer) e.getSource()).stop();
                    }
                }
                mainPanel.repaint();
            }
        });

        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FrontPage::new);
    }
}
