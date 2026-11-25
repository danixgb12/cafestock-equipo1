import cafestock.controlador.VentaControlador;
import cafestock.modelo.Producto;
import cafestock.modelo.Venta;
import cafestock.persistencia.ProductoRepositorioCsv;
import cafestock.persistencia.VentaRepositorioCsv;
import cafestock.servicio.ProductoServicio;
import cafestock.servicio.VentaServicio;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

public class PruebaPersistenciaVentasManual {
    public static void main(String[] args) throws Exception {
        Path datos = Files.createTempDirectory("cafestock-ventas-test-");
        ProductoRepositorioCsv rp = new ProductoRepositorioCsv(datos.resolve("productos.csv"));
        VentaRepositorioCsv rv = new VentaRepositorioCsv(datos.resolve("ventas.csv"));
        ProductoServicio productos = new ProductoServicio(rp);
        Producto inicial = new Producto("P001", "Cafe, americano", new BigDecimal("1.50"), 10, 3);
        productos.registrar(inicial);
        VentaServicio ventas = new VentaServicio(rv);
        VentaControlador controlador = new VentaControlador(productos, ventas);
        Venta primera = controlador.registrar(inicial, 7, LocalDate.of(2026, 11, 24));
        if (primera.getNumero() != 1 || primera.getTotal().compareTo(new BigDecimal("10.50")) != 0)
            throw new AssertionError("Primera venta incorrecta");
        try {
            controlador.registrar(inicial, 7, LocalDate.now());
            throw new AssertionError("Aceptó venta con stock obsoleto");
        } catch (IllegalArgumentException esperado) { }
        if (rv.leerTodos().size() != 1) throw new AssertionError("Rechazo creó venta");
        ProductoServicio recargados = new ProductoServicio(rp);
        VentaServicio historial = new VentaServicio(rv);
        if (recargados.listar().get(0).getStock() != 3) throw new AssertionError("Stock no persistió");
        Venta historica = historial.listar().get(0);
        if (!historica.getProducto().getNombre().equals("Cafe, americano") ||
            historica.getTotal().compareTo(new BigDecimal("10.50")) != 0)
            throw new AssertionError("Venta histórica no se recuperó");
        VentaControlador siguiente = new VentaControlador(recargados, historial);
        Venta segunda = siguiente.registrar(inicial, 3, LocalDate.of(2026, 11, 25));
        if (segunda.getNumero() != 2 || rv.leerTodos().size() != 2)
            throw new AssertionError("Numeración/reinicio incorrecto");
        System.out.println("OK: CSV, reinicio, numeración, total y stock vigente. Ruta: " + datos);
    }
}
