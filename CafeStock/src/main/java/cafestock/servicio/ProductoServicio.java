package cafestock.servicio;

import cafestock.modelo.Producto;
import cafestock.persistencia.ProductoRepositorioCsv;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Inventario en memoria sincronizado con productos.csv. */
public class ProductoServicio {
    private final ProductoRepositorioCsv repositorio;
    private final List<Producto> productos;

    public ProductoServicio(ProductoRepositorioCsv repositorio) throws IOException {
        this.repositorio = repositorio;
        this.productos = new ArrayList<>(repositorio.leerTodos());
    }

    public List<Producto> listar() {
        return List.copyOf(productos);
    }

    public void registrar(Producto producto) throws IOException {
        if (productos.stream().anyMatch(p -> p.getCodigo().equalsIgnoreCase(producto.getCodigo()))) {
            throw new IllegalArgumentException("Ya existe un producto con el código " + producto.getCodigo());
        }
        List<Producto> actualizados = new ArrayList<>(productos);
        actualizados.add(producto);
        // Solo se actualiza la memoria después de guardar correctamente.
        repositorio.escribirTodos(actualizados);
        productos.add(producto);
    }
}
