import cafestock.modelo.Producto;
import cafestock.persistencia.ProductoRepositorioCsv;
import cafestock.servicio.ProductoServicio;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

/** Ejecutar sin dependencias: java -cp target/test-classes:target/classes ... */
public class PruebaPersistenciaManual {
    public static void main(String[] args) throws Exception {
        Path dir = Files.createTempDirectory("prueba-cafestock-");
        Path csv = dir.resolve("datos/productos.csv");
        ProductoServicio servicio = new ProductoServicio(new ProductoRepositorioCsv(csv));
        if (!Files.exists(csv) || !servicio.listar().isEmpty()) throw new AssertionError("Creación CSV");
        servicio.registrar(new Producto("P001", "Café, americano", new BigDecimal("1.50"), 10, 3));
        servicio.registrar(new Producto("P002", "Pan \"especial\"", new BigDecimal("2.00"), 0, 0));
        ProductoServicio reiniciado = new ProductoServicio(new ProductoRepositorioCsv(csv));
        if (reiniciado.listar().size() != 2 || !reiniciado.listar().get(0).getNombre().equals("Café, americano")
            || !reiniciado.listar().get(1).getNombre().equals("Pan \"especial\"")) throw new AssertionError("Recuperación");
        boolean duplicado = false;
        try { reiniciado.registrar(new Producto("p001", "Otro", new BigDecimal("1.00"), 1, 1)); }
        catch (IllegalArgumentException ex) { duplicado = true; }
        if (!duplicado || reiniciado.listar().size() != 2) throw new AssertionError("Duplicados");
        System.out.println("OK: creación, escritura, reinicio, CSV con comas/comillas y duplicados");
    }
}
