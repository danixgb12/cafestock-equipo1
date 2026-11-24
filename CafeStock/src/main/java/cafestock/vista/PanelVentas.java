package cafestock.vista;

import cafestock.modelo.Producto;
import cafestock.modelo.Venta;
import cafestock.persistencia.ProductoRepositorioCsv;
import cafestock.persistencia.RutaDatos;
import cafestock.servicio.ProductoServicio;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Pantalla de la pestaña Ventas. Permite elegir un producto, ingresar la
 * cantidad y registrar la venta con una fecha que por defecto es la de hoy.
 * Por ahora las ventas solo viven en memoria: se pierden al cerrar la app.
 */
public class PanelVentas extends JPanel {
    // Colores de la app (mismos de VentanaPrincipal).
    private static final Color FONDO = new Color(247, 243, 237);
    private static final Color VERDE = new Color(35, 70, 56);
    // Color del borde suave de la tarjeta y del texto de las etiquetas.
    private static final Color BORDE_TARJETA = new Color(225, 219, 208);
    private static final Color TEXTO = new Color(45, 45, 45);
    // Fuentes del formulario.
    private static final Font FUENTE = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font FUENTE_ETIQUETA = new Font("Segoe UI", Font.BOLD, 14);

    // Las ventas se guardan solo en memoria; el guardado en CSV llega con HU-A3-T5.
    private final List<Venta> ventas = new ArrayList<>();
    // Lista de productos del inventario. Guarda objetos Producto; su toString() muestra código, nombre y stock.
    private final JComboBox<Producto> cmbProducto = new JComboBox<>();
    private final JTextField txtCantidad = new JTextField();
    // La fecha arranca con la de hoy, pero el usuario puede cambiarla.
    private final JTextField txtFecha = new JTextField(LocalDate.now().toString());
    // Muestra los mensajes de éxito o de error junto al botón.
    private final JLabel lblMensaje = new JLabel(" ");

    /**
     * Construye la pantalla: carga los productos desde productos.csv,
     * arma el formulario en una tarjeta y ubica el botón debajo de ella.
     */
    public PanelVentas() {
        // Carga los productos del inventario para elegir uno en la lista.
        // Si el CSV no se puede leer, el combo queda vacío y se muestra el error.
        try {
            List<Producto> lista = new ProductoServicio(new ProductoRepositorioCsv(RutaDatos.productos())).listar();
            for (Producto p : lista) cmbProducto.addItem(p);
        } catch (IOException ex) {
            lblMensaje.setText("No se pudo leer productos.csv: " + ex.getMessage());
        }

        setLayout(new BorderLayout(10, 10));
        setBackground(FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título, con el mismo estilo verde de la pestaña Productos.
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
        tarjeta.setPreferredSize(new Dimension(820, 260));
        tarjeta.setMaximumSize(new Dimension(820, 260));
        tarjeta.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblProducto = new JLabel("Producto:");
        JLabel lblCantidad = new JLabel("Cantidad:");
        JLabel lblFecha = new JLabel("Fecha (aaaa-mm-dd):");
        for (JLabel etiqueta : new JLabel[]{lblProducto, lblCantidad, lblFecha}) {
            etiqueta.setFont(FUENTE_ETIQUETA);
            etiqueta.setForeground(TEXTO);
        }
        cmbProducto.setFont(FUENTE);
        txtCantidad.setFont(FUENTE);
        txtFecha.setFont(FUENTE);

        // El orden de add() define la posición: etiqueta, campo, etiqueta, campo...
        tarjeta.add(lblProducto);
        tarjeta.add(cmbProducto);
        tarjeta.add(lblCantidad);
        tarjeta.add(txtCantidad);
        tarjeta.add(lblFecha);
        tarjeta.add(txtFecha);

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

        // Orden de la pantalla: título arriba y contenido debajo.
        add(lblTitulo, BorderLayout.NORTH);
        add(contenido, BorderLayout.CENTER);
    }

    /**
     * Valida los datos del formulario y, si son correctos, crea la venta.
     * Las validaciones se hacen en este orden: producto, cantidad y fecha.
     * Si alguna falla, muestra el mensaje y no registra nada.
     * La cantidad 0 o negativa la rechaza la clase Venta, no esta vista.
     */
    private void registrarVenta() {
        // Producto seleccionado en el combo. Puede ser null si la lista está vacía.
        Producto producto = (Producto) cmbProducto.getSelectedItem();
        if (producto == null) {
            lblMensaje.setText("Seleccione un producto.");
            return;
        }

        // La cantidad se lee como texto y se convierte a entero.
        // Si no es un número (letras, vacío, decimales), parseInt lanza NumberFormatException.
        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
        } catch (NumberFormatException ex) {
            lblMensaje.setText("La cantidad debe ser un número entero.");
            return;
        }

        // La fecha debe estar en formato aaaa-mm-dd (ISO). Otros formatos, como 24/11/2026, se rechazan.
        LocalDate fecha;
        try {
            fecha = LocalDate.parse(txtFecha.getText().trim());
        } catch (DateTimeParseException ex) {
            lblMensaje.setText("La fecha debe tener formato aaaa-mm-dd.");
            return;
        }

        try {
            // El número temporal es el siguiente en la lista; T5 lo tomará del CSV.
            Venta venta = new Venta(ventas.size() + 1, fecha, producto, cantidad);
            ventas.add(venta);
            lblMensaje.setText("Venta registrada: " + producto.getNombre() + " x" + cantidad);
            // Solo se limpia la cantidad. La fecha se conserva para registrar más ventas del mismo día.
            txtCantidad.setText("");
        } catch (IllegalArgumentException ex) {
            // La clase Venta rechaza cantidades cero o negativas y mantiene la venta fuera de la lista.
            // El mensaje viene de Venta, así que no hay que repetirlo aquí.
            lblMensaje.setText(ex.getMessage());
        }
    }
}