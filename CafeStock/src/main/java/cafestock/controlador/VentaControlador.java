package cafestock.controlador;

import cafestock.modelo.Producto;
import cafestock.modelo.Venta;
import cafestock.servicio.ProductoServicio;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentaControlador {
    private final ProductoServicio servicio;
    private final List<Venta> ventas = new ArrayList<>();

    public VentaControlador(ProductoServicio servicio) {
        this.servicio = servicio;
    }

    public List<Producto> obtenerProductos() {
        return servicio.listar();
    }

    public BigDecimal calcularTotal(Producto producto, int cantidad) {
        if (cantidad <= 0)
            return BigDecimal.ZERO.setScale(2);
        return Venta.calcularTotal(producto.getPrecio(), cantidad);
    }

    /**
     * Valida cantidad y stock, descuenta el stock y guarda el producto
     * antes de registrar la venta en memoria.
     */
    public Venta registrar(Producto producto, int cantidad, LocalDate fecha) throws IOException {
        if (cantidad <= 0)
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        if (cantidad > producto.getStock())
            throw new IllegalArgumentException(
                "Stock insuficiente. Disponible: " + producto.getStock() + ".");

        Venta venta = new Venta(ventas.size() + 1, fecha, producto, cantidad);
        Producto actualizado = producto.conStock(producto.getStock() - cantidad);
        servicio.actualizar(actualizado.getCodigo(), actualizado.getNombre(),
    actualizado.getPrecio(), actualizado.getStock(), actualizado.getStockMinimo());
        ventas.add(venta);
        return venta;
    }
}