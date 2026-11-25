package cafestock.persistencia;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * HU-A3-T6: bitácora de recuperación para una venta que afecta dos CSV.
 * No es una transacción de base de datos; recupera el estado previo si la
 * operación falla o queda interrumpida antes de confirmar ambos guardados.
 * Diseñada para una sola instancia de la aplicación (caja de Rosa).
 */
public final class TransaccionVentasCsv {
    private final Path productos;
    private final Path ventas;
    private final Path marca;
    private final Path copiaProductos;
    private final Path copiaVentas;

    public TransaccionVentasCsv(Path productos, Path ventas) {
        this.productos = productos.toAbsolutePath().normalize();
        this.ventas = ventas.toAbsolutePath().normalize();
        Path carpeta = this.productos.getParent();
        if (!carpeta.equals(this.ventas.getParent())) {
            throw new IllegalArgumentException("Los dos CSV deben estar en la misma carpeta de datos.");
        }
        this.marca = carpeta.resolve(".venta-pendiente");
        this.copiaProductos = carpeta.resolve(".productos-antes-venta.bak");
        this.copiaVentas = carpeta.resolve(".ventas-antes-venta.bak");
    }

    public static void recuperarPendiente(Path productos, Path ventas) throws IOException {
        new TransaccionVentasCsv(productos, ventas).recuperarPendiente();
    }

    /** Ejecutar antes de cargar servicios al iniciar la aplicación. */
    public void recuperarPendiente() throws IOException {
        if (Files.exists(marca)) {
            restaurar();
            terminar();
        } else {
            // Restos de copias previas a crear la marca no implican cambios en los CSV.
            limpiarCopias();
        }
    }

    /** Crea las copias ANTES de permitir cualquier escritura de una venta. */
    public void iniciar() throws IOException {
        if (Files.exists(marca)) {
            throw new IOException("Hay una venta pendiente de recuperación. Reinicie CaféStock.");
        }
        if (Files.notExists(productos) || Files.notExists(ventas)) {
            throw new IOException("No se han inicializado los archivos de productos o ventas.");
        }
        Files.createDirectories(marca.getParent());
        Files.copy(productos, copiaProductos, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(ventas, copiaVentas, StandardCopyOption.REPLACE_EXISTING);
        // La marca aparece solo cuando las dos copias están preparadas.
        Files.createFile(marca);
    }

    /** Confirmar solo cuando los DOS CSV se guardaron correctamente. */
    public void confirmar() throws IOException {
        Files.delete(marca); // Punto de confirmación; un reinicio ya no revierte esta venta.
        // Las copias sobrantes no afectan los CSV ni una venta confirmada.
        try { limpiarCopias(); } catch (IOException ignorada) { }
    }

    /** Restaurar el estado previo; las copias permanecen hasta restaurar ambos CSV. */
    public void revertir() throws IOException {
        if (Files.exists(marca)) {
            restaurar();
            terminar();
        }
    }

    private void restaurar() throws IOException {
        if (Files.notExists(copiaProductos) || Files.notExists(copiaVentas)) {
            throw new IOException("No se encuentran las copias de recuperación de la venta pendiente.");
        }
        reemplazarDesdeCopia(copiaProductos, productos);
        reemplazarDesdeCopia(copiaVentas, ventas);
    }

    private static void reemplazarDesdeCopia(Path origen, Path destino) throws IOException {
        Path temporal = Files.createTempFile(destino.getParent(), "recuperar-", ".tmp");
        try {
            Files.copy(origen, temporal, StandardCopyOption.REPLACE_EXISTING);
            try {
                Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporal);
        }
    }

    private void terminar() throws IOException {
        Files.delete(marca);
        try { limpiarCopias(); } catch (IOException ignorada) { }
    }

    private void limpiarCopias() throws IOException {
        Files.deleteIfExists(copiaProductos);
        Files.deleteIfExists(copiaVentas);
    }
}
