package cafestock.controlador;

import cafestock.modelo.Producto;
import cafestock.modelo.Venta;
import cafestock.servicio.ProductoServicio;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Puente entre la pantalla de ventas y el inventario. Las ventas se guardan en memoria hasta HU-A3-T5. */
public class VentaControlador {
    private final ProductoServicio servicio;
    // Lista de ventas registradas en esta sesión.
    private final List<Venta> ventas = new ArrayList<>();

    public VentaControlador(ProductoServicio servicio) {
        this.servicio = servicio;
    }

    /** Devuelve los productos del inventario para llenar la lista de la pantalla. */
    public List<Producto> obtenerProductos() {
        return servicio.listar();
    }

    /** Pide el total a la clase Venta. Una cantidad no válida da 0.00 sin lanzar error. */
    public BigDecimal calcularTotal(Producto producto, int cantidad) {
        if (cantidad <= 0) return BigDecimal.ZERO.setScale(2);
        return Venta.calcularTotal(producto.getPrecio(), cantidad);
    }

    /** Crea la venta. Si los datos no son válidos, Venta lanza IllegalArgumentException. */
    public Venta registrar(Producto producto, int cantidad, LocalDate fecha) {
        // El número temporal es el siguiente en la lista; T5 lo tomará del CSV.
        Venta venta = new Venta(ventas.size() + 1, fecha, producto, cantidad);
        ventas.add(venta);
        return venta;
    }
}