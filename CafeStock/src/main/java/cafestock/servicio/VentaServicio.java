package cafestock.servicio;

import cafestock.modelo.Producto;
import cafestock.modelo.Venta;
import cafestock.persistencia.VentaRepositorioCsv;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Historial de ventas persistido; no modifica el inventario. */
public class VentaServicio {
    private final VentaRepositorioCsv repositorio;
    private final List<Venta> ventas;

    public VentaServicio(VentaRepositorioCsv repositorio) throws IOException {
        this.repositorio = repositorio;
        this.ventas = new ArrayList<>(repositorio.leerTodos());
    }

    public java.nio.file.Path rutaArchivo() {
        return repositorio.rutaArchivo();
    }

    /** Recarga el estado confirmado desde el archivo después de una recuperación. */
    public void recargar() throws IOException {
        List<Venta> actuales = repositorio.leerTodos();
        ventas.clear();
        ventas.addAll(actuales);
    }

    public List<Venta> listar() {
        return List.copyOf(ventas);
    }

    public Venta crear(Producto producto, int cantidad, LocalDate fecha) {
        int siguiente = ventas.stream().mapToInt(Venta::getNumero).max().orElse(0) + 1;
        return new Venta(siguiente, fecha, producto, cantidad);
    }

    public void guardar(Venta venta) throws IOException {
        if (ventas.stream().anyMatch(v -> v.getNumero() == venta.getNumero())) {
            throw new IllegalArgumentException("Número de venta repetido: " + venta.getNumero());
        }
        List<Venta> nuevos = new ArrayList<>(ventas);
        nuevos.add(venta);
        repositorio.escribirTodos(nuevos);
        ventas.add(venta);
    }
}
