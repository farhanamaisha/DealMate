package ui;
import javax.swing.SwingUtilities;

public class DealMateApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrontPage());
    }
}
