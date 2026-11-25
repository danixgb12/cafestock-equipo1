package cafestock.persistencia;

import cafestock.modelo.Producto;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/** Almacenamiento CSV de los cinco atributos de Producto (UTF-8). */
public class ProductoRepositorioCsv {
    private static final String ENCABEZADO = "codigo,nombre,precio,stock,stockMinimo";
    private final Path archivo;

    public ProductoRepositorioCsv(Path archivo) {
        this.archivo = archivo.toAbsolutePath().normalize();
    }

    public Path rutaArchivo() {
        return archivo;
    }

    public List<Producto> leerTodos() throws IOException {
        if (Files.notExists(archivo)) {
            escribirTodos(List.of()); // crea encabezado la primera vez
        }
        List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);
        List<Producto> productos = new ArrayList<>();
        if (lineas.isEmpty() || !ENCABEZADO.equals(lineas.get(0).replace("\uFEFF", ""))) {
            throw new IOException("Encabezado CSV incorrecto en " + archivo);
        }
        for (int n = 1; n < lineas.size(); n++) {
            if (lineas.get(n).isBlank()) continue;
            try {
                List<String> datos = dividirCsv(lineas.get(n));
                if (datos.size() != 5) throw new IllegalArgumentException("Se esperaban 5 columnas");
                Producto p = new Producto(datos.get(0), datos.get(1),
                    new BigDecimal(datos.get(2)), Integer.parseInt(datos.get(3)), Integer.parseInt(datos.get(4)));
                if (productos.stream().anyMatch(x -> x.getCodigo().equalsIgnoreCase(p.getCodigo()))) {
                    throw new IllegalArgumentException("Código repetido: " + p.getCodigo());
                }
                productos.add(p);
            } catch (RuntimeException ex) {
                throw new IOException("Registro CSV inválido en línea " + (n + 1) + ": " + ex.getMessage(), ex);
            }
        }
        return productos;
    }

    /** Reemplaza el archivo solo después de escribir su contenido completo. */
    public void escribirTodos(List<Producto> productos) throws IOException {
        Path carpeta = archivo.getParent();
        Files.createDirectories(carpeta);
        Path temporal = Files.createTempFile(carpeta, "productos-", ".tmp");
        try {
            List<String> lineas = new ArrayList<>();
            lineas.add(ENCABEZADO);
            for (Producto p : productos) {
                lineas.add(escapar(p.getCodigo()) + "," + escapar(p.getNombre()) + ","
                    + p.getPrecio().toPlainString() + "," + p.getStock() + "," + p.getStockMinimo());
            }
            Files.write(temporal, lineas, StandardCharsets.UTF_8);
            try {
                Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporal);
        }
    }

    private static String escapar(String texto) {
        if (texto.contains(",") || texto.contains("\"") || texto.contains("\n")) {
            return "\"" + texto.replace("\"", "\"\"") + "\"";
        }
        return texto;
    }

    private static List<String> dividirCsv(String linea) {
        List<String> valores = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        boolean comillas = false;
        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
            if (c == '"') {
                if (comillas && i + 1 < linea.length() && linea.charAt(i + 1) == '"') {
                    actual.append('"'); i++;
                } else if (comillas || actual.length() == 0) {
                    comillas = !comillas;
                } else {
                    throw new IllegalArgumentException("Comillas inesperadas");
                }
            } else if (c == ',' && !comillas) {
                valores.add(actual.toString()); actual.setLength(0);
            } else {
                actual.append(c);
            }
        }
        if (comillas) throw new IllegalArgumentException("Comillas sin cerrar");
        valores.add(actual.toString());
        return valores;
    }
}
