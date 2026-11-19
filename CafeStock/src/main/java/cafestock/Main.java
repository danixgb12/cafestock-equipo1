package cafestock;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/** Punto de entrada provisional. Karen integrará la ventana principal. */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame ventana = new JFrame("CaféStock - Equipo 1");
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.add(new JLabel("CaféStock: proyecto MVC iniciado", SwingConstants.CENTER));
            ventana.setSize(480, 240);
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        });
    }
}
