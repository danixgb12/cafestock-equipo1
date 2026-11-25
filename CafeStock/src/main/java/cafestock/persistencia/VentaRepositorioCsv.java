package cafestock.persistencia;

import cafestock.modelo.Producto;
import cafestock.modelo.Venta;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Lee y escribe ventas.csv, conservando el precio histórico de la venta. */
public class VentaRepositorioCsv {
    private static final String ENCABEZADO =
        "numero,fecha,codigoProducto,nombreProducto,precioUnitario,cantidad,total";
    private final Path archivo;

    public VentaRepositorioCsv(Path archivo) {
        this.archivo = archivo.toAbsolutePath().normalize();
    }

    public List<Venta> leerTodos() throws IOException {
        if (Files.notExists(archivo)) escribirTodos(List.of());
        List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);
        if (lineas.isEmpty() || !ENCABEZADO.equals(lineas.get(0).replace("\uFEFF", ""))) {
            throw new IOException("Encabezado inválido en " + archivo);
        }
        List<Venta> ventas = new ArrayList<>();
        Set<Integer> numeros = new HashSet<>();
        for (int i = 1; i < lineas.size(); i++) {
            if (lineas.get(i).isBlank()) continue;
            try {
                List<String> d = separar(lineas.get(i));
                if (d.size() != 7) throw new IllegalArgumentException("Se esperaban 7 columnas");
                int numero = Integer.parseInt(d.get(0));
                if (!numeros.add(numero)) throw new IllegalArgumentException("Número duplicado: " + numero);
                LocalDate fecha = LocalDate.parse(d.get(1));
                BigDecimal precio = new BigDecimal(d.get(4));
                int cantidad = Integer.parseInt(d.get(5));
                // Fotografía histórica del producto. El stock actual se gestiona en productos.csv.
                Producto productoHistorico = new Producto(d.get(2), d.get(3), precio, 0, 0);
                Venta venta = new Venta(numero, fecha, productoHistorico, cantidad);
                if (venta.getTotal().compareTo(new BigDecimal(d.get(6))) != 0) {
                    throw new IllegalArgumentException("Total no coincide con precio por cantidad");
                }
                ventas.add(venta);
            } catch (RuntimeException ex) {
                throw new IOException("Venta inválida, línea " + (i + 1) + ": " + ex.getMessage(), ex);
            }
        }
        return ventas;
    }

    /** Escritura mediante archivo temporal para evitar archivos parciales. */
    public void escribirTodos(List<Venta> ventas) throws IOException {
        Path carpeta = archivo.getParent();
        Files.createDirectories(carpeta);
        Path temporal = Files.createTempFile(carpeta, "ventas-", ".tmp");
        try {
            List<String> lineas = new ArrayList<>();
            lineas.add(ENCABEZADO);
            for (Venta v : ventas) {
                lineas.add(v.getNumero() + "," + v.getFecha() + ","
                    + escapar(v.getProducto().getCodigo()) + ","
                    + escapar(v.getProducto().getNombre()) + ","
                    + v.getProducto().getPrecio().toPlainString() + ","
                    + v.getCantidad() + "," + v.getTotal().toPlainString());
            }
            Files.write(temporal, lineas, StandardCharsets.UTF_8);
            try {
                Files.move(temporal, archivo,
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporal);
        }
    }

    private static String escapar(String valor) {
        if (valor.contains(",") || valor.contains("\"") || valor.contains("\n")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }

    private static List<String> separar(String linea) {
        List<String> resultado = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        boolean enComillas = false;
        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
            if (c == '\"') {
                if (enComillas && i + 1 < linea.length() && linea.charAt(i + 1) == '\"') {
                    actual.append('\"'); i++;
                } else if (enComillas || actual.length() == 0) {
                    enComillas = !enComillas;
                } else throw new IllegalArgumentException("Comillas inesperadas");
            } else if (c == ',' && !enComillas) {
                resultado.add(actual.toString());
                actual.setLength(0);
            } else actual.append(c);
        }
        if (enComillas) throw new IllegalArgumentException("Comillas sin cerrar");
        resultado.add(actual.toString());
        return resultado;
    }
}
