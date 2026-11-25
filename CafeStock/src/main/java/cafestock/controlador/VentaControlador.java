package cafestock.controlador;

import cafestock.modelo.Producto;
import cafestock.modelo.Venta;
import cafestock.servicio.ProductoServicio;
import cafestock.servicio.VentaServicio;
import cafestock.persistencia.TransaccionVentasCsv;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class VentaControlador {
    private final ProductoServicio productos;
    private final VentaServicio ventas;

    public VentaControlador(ProductoServicio productos, VentaServicio ventas) {
        this.productos = productos;
        this.ventas = ventas;
    }

    public List<Producto> obtenerProductos() {
        return productos.listar();
    }

    public List<Venta> obtenerVentas() {
        return ventas.listar();
    }

    public BigDecimal calcularTotal(Producto producto, int cantidad) {
        if (producto == null || cantidad <= 0) return BigDecimal.ZERO.setScale(2);
        return Venta.calcularTotal(producto.getPrecio(), cantidad);
    }

    /**
     * HU-A3-T6: una venta confirma dos CSV o restaura ambos.
     * La consulta del producto vigente evita validar stock desactualizado.
     */
    public synchronized Venta registrar(Producto seleccionado, int cantidad, LocalDate fecha) throws IOException {
        if (seleccionado == null) throw new IllegalArgumentException("Seleccione un producto.");
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        Producto vigente = productos.listar().stream()
            .filter(p -> p.getCodigo().equalsIgnoreCase(seleccionado.getCodigo()))
            .findFirst().orElseThrow(() -> new IllegalArgumentException("El producto ya no existe."));
        if (cantidad > vigente.getStock()) {
            throw new IllegalArgumentException("Stock insuficiente. Disponible: " + vigente.getStock() + ".");
        }
        Venta venta = ventas.crear(vigente, cantidad, fecha);
        TransaccionVentasCsv transaccion = new TransaccionVentasCsv(
            productos.rutaArchivo(), ventas.rutaArchivo());
        transaccion.iniciar();
        try {
            productos.actualizar(vigente.getCodigo(), vigente.getNombre(), vigente.getPrecio(),
                vigente.getStock() - cantidad, vigente.getStockMinimo());
            ventas.guardar(venta);
            transaccion.confirmar();
            return venta;
        } catch (IOException | RuntimeException fallo) {
            try {
                transaccion.revertir();
                productos.recargar();
                ventas.recargar();
            } catch (IOException | RuntimeException recuperacion) {
                fallo.addSuppressed(recuperacion);
            }
            throw fallo;
        }
    }
}
