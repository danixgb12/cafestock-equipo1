import cafestock.controlador.VentaControlador;
import cafestock.modelo.Producto;
import cafestock.modelo.Venta;
import cafestock.persistencia.ProductoRepositorioCsv;
import cafestock.persistencia.TransaccionVentasCsv;
import cafestock.persistencia.VentaRepositorioCsv;
import cafestock.servicio.ProductoServicio;
import cafestock.servicio.VentaServicio;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

/** Pruebas sin librerías externas, con CSV aislados del inventario real. */
public class PruebaCoordinacionVentasManual {
    public static void main(String[] args) throws Exception {
        pruebaNormalYReinicio();
        pruebaErrorEscrituraVentas();
        pruebaInterrupcionAntesDeConfirmar();
        System.out.println("OK: venta confirmada, rechazo por stock, reversión y recuperación tras interrupción.");
    }

    private static Producto producto() {
        return new Producto("P001", "Cafe", new BigDecimal("1.50"), 10, 3);
    }

    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
    }

    private static void pruebaNormalYReinicio() throws Exception {
        Path dir = Files.createTempDirectory("cafestock-t6-normal-");
        ProductoRepositorioCsv rp = new ProductoRepositorioCsv(dir.resolve("productos.csv"));
        VentaRepositorioCsv rv = new VentaRepositorioCsv(dir.resolve("ventas.csv"));
        ProductoServicio ps = new ProductoServicio(rp);
        VentaServicio vs = new VentaServicio(rv);
        ps.registrar(producto());
        VentaControlador vc = new VentaControlador(ps, vs);
        Producto referenciaAnterior = ps.listar().get(0);
        Venta venta = vc.registrar(referenciaAnterior, 7, LocalDate.of(2026, 11, 24));
        comprobar(venta.getNumero() == 1, "Número de venta incorrecto");
        comprobar(ps.listar().get(0).getStock() == 3, "Stock incorrecto en memoria");
        try {
            vc.registrar(referenciaAnterior, 7, LocalDate.now());
            throw new AssertionError("Aceptó un stock desactualizado");
        } catch (IllegalArgumentException correcto) { }
        comprobar(vs.listar().size() == 1, "El rechazo modificó el historial");
        TransaccionVentasCsv.recuperarPendiente(rp.rutaArchivo(), rv.rutaArchivo());
        comprobar(new ProductoServicio(rp).listar().get(0).getStock() == 3, "Stock no persistió");
        comprobar(new VentaServicio(rv).listar().size() == 1, "Venta no persistió");
    }

    private static void pruebaErrorEscrituraVentas() throws Exception {
        Path dir = Files.createTempDirectory("cafestock-t6-error-");
        ProductoRepositorioCsv rp = new ProductoRepositorioCsv(dir.resolve("productos.csv"));
        VentaRepositorioCsv rvFallando = new VentaRepositorioCsv(dir.resolve("ventas.csv")) {
            @Override public void escribirTodos(List<Venta> items) throws IOException {
                throw new IOException("Fallo simulado al guardar ventas.csv");
            }
        };
        ProductoServicio ps = new ProductoServicio(rp);
        ps.registrar(producto());
        // El repositorio fallido no puede crear el CSV: inicializar antes con la implementación normal.
        new VentaRepositorioCsv(dir.resolve("ventas.csv")).leerTodos();
        VentaServicio vs = new VentaServicio(rvFallando);
        VentaControlador vc = new VentaControlador(ps, vs);
        try {
            vc.registrar(ps.listar().get(0), 3, LocalDate.now());
            throw new AssertionError("No propagó el fallo de escritura");
        } catch (IOException correcto) { }
        comprobar(ps.listar().get(0).getStock() == 10, "No restauró stock en memoria");
        comprobar(new ProductoServicio(rp).listar().get(0).getStock() == 10, "No restauró stock en CSV");
        comprobar(new VentaRepositorioCsv(dir.resolve("ventas.csv")).leerTodos().isEmpty(), "Dejó venta inválida");
        comprobar(!Files.exists(dir.resolve(".venta-pendiente")), "Dejó marca pendiente tras revertir");
    }

    private static void pruebaInterrupcionAntesDeConfirmar() throws Exception {
        Path dir = Files.createTempDirectory("cafestock-t6-corte-");
        ProductoRepositorioCsv rp = new ProductoRepositorioCsv(dir.resolve("productos.csv"));
        VentaRepositorioCsv rv = new VentaRepositorioCsv(dir.resolve("ventas.csv"));
        ProductoServicio ps = new ProductoServicio(rp);
        VentaServicio vs = new VentaServicio(rv);
        ps.registrar(producto());
        TransaccionVentasCsv tx = new TransaccionVentasCsv(rp.rutaArchivo(), rv.rutaArchivo());
        tx.iniciar();
        ps.actualizar("P001", "Cafe", new BigDecimal("1.50"), 7, 3);
        vs.guardar(vs.crear(producto(), 3, LocalDate.now()));
        // Simular que el proceso terminó sin llamar a confirmar().
        TransaccionVentasCsv.recuperarPendiente(rp.rutaArchivo(), rv.rutaArchivo());
        comprobar(new ProductoServicio(rp).listar().get(0).getStock() == 10, "No restauró productos tras corte");
        comprobar(new VentaServicio(rv).listar().isEmpty(), "No restauró ventas tras corte");
        comprobar(!Files.exists(dir.resolve(".venta-pendiente")), "No eliminó la marca");
    }
}
