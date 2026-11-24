package cafestock.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/** Venta de un solo producto. Su total es precio × cantidad. */
public final class Venta {
    private final int numero;
    private final LocalDate fecha;
    private final Producto producto;
    private final int cantidad;
    private final BigDecimal total;

    public Venta(int numero, LocalDate fecha, Producto producto, int cantidad) {
        // Se valida antes de asignar los campos, para que una venta inválida nunca quede creada.
        if (numero <= 0)
            throw new IllegalArgumentException("El número de venta debe ser mayor que cero.");
        this.fecha = Objects.requireNonNull(fecha, "La fecha es obligatoria.");
        this.producto = Objects.requireNonNull(producto, "El producto es obligatorio.");
        if (cantidad <= 0)
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");

        this.numero = numero;
        this.cantidad = cantidad;
        // El total se calcula una sola vez al crear la venta, así no cambia si el precio del producto cambia después.
        this.total = producto.getPrecio().multiply(BigDecimal.valueOf(cantidad));
    }

    public int getNumero() { return numero; }
    public LocalDate getFecha() { return fecha; }
    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public BigDecimal getTotal() { return total; }

   
}