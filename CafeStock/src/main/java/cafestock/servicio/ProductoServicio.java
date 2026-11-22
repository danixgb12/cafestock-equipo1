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
    
    
    public void actualizar(String codigo, String nombre,
            java.math.BigDecimal precio, int stock, int stockMinimo)
            throws IOException {

        int indice = -1;

        // Buscar el producto por su código.
        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).getCodigo().equalsIgnoreCase(codigo)) {
                indice = i;
                break;
            }
        }

        if (indice == -1) {
            throw new IllegalArgumentException(
                "No existe un producto con el código " + codigo
            );
        }

        // Crear el producto actualizado conservando su código.
        Producto actualizado = productos.get(indice).conCambios(
            nombre, precio, stock, stockMinimo
        );

        // Preparar los cambios sin alterar todavía el inventario.
        List<Producto> nuevaLista = new ArrayList<>(productos);
        nuevaLista.set(indice, actualizado);

        // Guardar primero en el archivo CSV.
        repositorio.escribirTodos(nuevaLista);

        // Actualizar la memoria solamente si se guardó correctamente.
        productos.set(indice, actualizado);
    }

}
