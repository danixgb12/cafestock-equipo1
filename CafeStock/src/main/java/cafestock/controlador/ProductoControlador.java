package cafestock.controlador;

import cafestock.modelo.Producto;
import cafestock.servicio.ProductoServicio;
import java.io.IOException;
import java.util.List;

/** Puente entre los componentes Swing y el inventario persistente. */
public class ProductoControlador {
    private final ProductoServicio servicio;

    public ProductoControlador(ProductoServicio servicio) {
        this.servicio = servicio;
    }

    public List<Producto> obtenerProductos() {
        return servicio.listar();
    }

    public void registrar(Producto producto) throws IOException {
        servicio.registrar(producto);
    }


    public void actualizar(Producto producto) throws IOException {
        servicio.actualizar(
            producto.getCodigo(),
            producto.getNombre(),
            producto.getPrecio(),
            producto.getStock(),
            producto.getStockMinimo()
        );
    }

}
