package cafestock.persistencia;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Ruta portable: proyecto/datos en desarrollo, dist/datos en la entrega. */
public final class RutaDatos {
    private RutaDatos() {}

    public static Path productos() {
        try {
            Path ubicacion = Paths.get(RutaDatos.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI()).toAbsolutePath();
            if (ubicacion.toString().endsWith(".jar")) {
                Path carpetaJar = ubicacion.getParent();
                // Maven genera el JAR en target, pero la entrega lo ubica en dist.
                Path base = carpetaJar.getFileName().toString().equalsIgnoreCase("target")
                    ? carpetaJar.getParent() : carpetaJar;
                return base.resolve("datos/productos.csv");
            }
            // NetBeans/Maven: clases en target/classes.
            if (ubicacion.getFileName().toString().equals("classes")
                    && ubicacion.getParent().getFileName().toString().equals("target")) {
                return ubicacion.getParent().getParent().resolve("datos/productos.csv");
            }
        } catch (URISyntaxException | RuntimeException ex) {
            // Se usa el directorio de trabajo si no puede resolverse la ubicación.
        }
        return Path.of(System.getProperty("user.dir"), "datos", "productos.csv").toAbsolutePath();
    }
}
