package cafestock.vista;

import cafestock.controlador.VentaControlador;
import cafestock.modelo.Producto;
import cafestock.modelo.Venta;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.Box;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Pantalla de la pestaña Ventas. Muestra el formulario y el total antes de confirmar.
 * La lógica de negocio está en VentaControlador.
 */
public class PanelVentas extends JPanel {
    // Colores de la app (mismos de VentanaPrincipal).
    private static final Color FONDO = new Color(247, 243, 237);
    private static final Color VERDE = new Color(35, 70, 56);
    private static final Color BORDE_TARJETA = new Color(225, 219, 208);
    private static final Color TEXTO = new Color(45, 45, 45);
    // Fuentes del formulario.
    private static final Font FUENTE = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font FUENTE_ETIQUETA = new Font("Segoe UI", Font.BOLD, 14);

    // El controlador hace los cálculos y guarda las ventas; la vista solo lo llama.
    private final VentaControlador controlador;
    private final JComboBox<Producto> cmbProducto = new JComboBox<>();
    private final JTextField txtCantidad = new JTextField();
    // La fecha arranca con la de hoy, pero el usuario puede cambiarla.
    private final JTextField txtFecha = new JTextField(LocalDate.now().toString());
    // Muestra el total calculado antes de confirmar la venta.
    private final JLabel lblTotal = new JLabel("$0.00");
    // Muestra los mensajes de éxito o de error junto al botón.
    private final JLabel lblMensaje = new JLabel(" ");

    /**
     * Arma la pantalla, llena la lista de productos y conecta los cambios
     * del formulario con el cálculo del total.
     */
    public PanelVentas(VentaControlador controlador) {
        this.controlador = controlador;

        recargarProductos();

        // Cada vez que se muestra la pestaña, el combo se vuelve a llenar con los productos actuales.
        addComponentListener(new ComponentAdapter() {
            @Override public void componentShown(ComponentEvent e) { recargarProductos(); }
        });

        // Cada cambio de producto o de cantidad recalcula el total en pantalla.
        cmbProducto.addActionListener(e -> actualizarTotal());
        txtCantidad.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { actualizarTotal(); }
            @Override public void removeUpdate(DocumentEvent e) { actualizarTotal(); }
            @Override public void changedUpdate(DocumentEvent e) { actualizarTotal(); }
        });

        setLayout(new BorderLayout(10, 10));
        setBackground(FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitulo = new JLabel("Registrar venta");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(VERDE);

        // Tarjeta blanca con borde suave. GridLayout de 2 columnas: etiqueta y campo por fila.
        JPanel tarjeta = new JPanel(new GridLayout(0, 2, 20, 18));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDE_TARJETA),
            BorderFactory.createEmptyBorder(24, 24, 24, 24)));
        // Tamaño fijo para que la tarjeta no se estire a todo el ancho de la ventana.
        tarjeta.setPreferredSize(new Dimension(820, 330));
        tarjeta.setMaximumSize(new Dimension(820, 330));
        tarjeta.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblProducto = new JLabel("Producto:");
        JLabel lblCantidad = new JLabel("Cantidad:");
        JLabel lblFecha = new JLabel("Fecha (aaaa-mm-dd):");
        JLabel lblTotalTexto = new JLabel("Total (USD):");
        for (JLabel etiqueta : new JLabel[]{lblProducto, lblCantidad, lblFecha, lblTotalTexto}) {
            etiqueta.setFont(FUENTE_ETIQUETA);
            etiqueta.setForeground(TEXTO);
        }
        cmbProducto.setFont(FUENTE);
        txtCantidad.setFont(FUENTE);
        txtFecha.setFont(FUENTE);
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotal.setForeground(VERDE);

        // El orden de add() define la posición: etiqueta, campo, etiqueta, campo...
        tarjeta.add(lblProducto);
        tarjeta.add(cmbProducto);
        tarjeta.add(lblCantidad);
        tarjeta.add(txtCantidad);
        tarjeta.add(lblFecha);
        tarjeta.add(txtFecha);
        tarjeta.add(lblTotalTexto);
        tarjeta.add(lblTotal);

        // El botón llama a registrarVenta() cada vez que se presiona.
        JButton btnRegistrar = new JButton("Registrar venta");
        btnRegistrar.addActionListener(e -> registrarVenta());
        btnRegistrar.setBackground(VERDE);
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegistrar.setFocusPainted(false);
        // setOpaque y setBorderPainted hacen que el color verde se vea con el look and feel Nimbus.
        btnRegistrar.setOpaque(true);
        btnRegistrar.setBorderPainted(false);
        btnRegistrar.setPreferredSize(new Dimension(180, 40));

        lblMensaje.setForeground(VERDE);
        lblMensaje.setFont(FUENTE);

        // Fila del botón y del mensaje, justo debajo de la tarjeta.
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        fila.setBackground(FONDO);
        fila.setAlignmentX(LEFT_ALIGNMENT);
        fila.add(btnRegistrar);
        fila.add(lblMensaje);

        // BoxLayout vertical: tarjeta, espacio y fila del botón, todo alineado a la izquierda.
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(FONDO);
        contenido.add(tarjeta);
        contenido.add(Box.createVerticalStrut(18));
        contenido.add(fila);

        add(lblTitulo, BorderLayout.NORTH);
        add(contenido, BorderLayout.CENTER);

        // Calcula el total inicial con el primer producto de la lista.
        actualizarTotal();
    }

    /**
     * Vuelve a llenar el combo con los productos actuales, conservando el elegido
     * si sigue existiendo. Así los productos nuevos aparecen sin reiniciar la app.
     */
    private void recargarProductos() {
        Object seleccionado = cmbProducto.getSelectedItem();
        cmbProducto.removeAllItems();
        for (Producto p : controlador.obtenerProductos()) cmbProducto.addItem(p);
        if (seleccionado != null) cmbProducto.setSelectedItem(seleccionado);
        actualizarTotal();
    }

    /** Pide al controlador el total con los datos del formulario y lo muestra. */
    private void actualizarTotal() {
        Producto producto = (Producto) cmbProducto.getSelectedItem();
        // Texto no numérico (letras o vacío): no se calcula total todavía.
        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
        } catch (NumberFormatException ex) {
            lblTotal.setText("$0.00");
            return;
        }
        if (producto == null) {
            lblTotal.setText("$0.00");
            return;
        }
        lblTotal.setText("$" + controlador.calcularTotal(producto, cantidad));
    }

    /**
     * Revisa el formato de lo que escribe el usuario y pide al controlador registrar la venta.
     * Si algo no es válido, muestra el mensaje y no registra nada.
     */
    private void registrarVenta() {
        Producto producto = (Producto) cmbProducto.getSelectedItem();
        if (producto == null) {
            lblMensaje.setText("Seleccione un producto.");
            return;
        }

        // La cantidad se lee como texto y se convierte a entero.
        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
        } catch (NumberFormatException ex) {
            lblMensaje.setText("La cantidad debe ser un número entero.");
            return;
        }

        // La fecha debe estar en formato aaaa-mm-dd (ISO).
        LocalDate fecha;
        try {
            fecha = LocalDate.parse(txtFecha.getText().trim());
        } catch (DateTimeParseException ex) {
            lblMensaje.setText("La fecha debe tener formato aaaa-mm-dd.");
            return;
        }

        try {
            Venta venta = controlador.registrar(producto, cantidad, fecha);
            lblMensaje.setText("Venta registrada: " + producto.getNombre() + " x" + cantidad
                + " — total $" + venta.getTotal());
            // Solo se limpia la cantidad. La fecha se conserva para registrar más ventas del mismo día.
            txtCantidad.setText("");
        } catch (IllegalArgumentException ex) {
            // La clase Venta rechaza cantidades cero o negativas; el mensaje viene de ella.
            lblMensaje.setText(ex.getMessage());
        }
    }
}