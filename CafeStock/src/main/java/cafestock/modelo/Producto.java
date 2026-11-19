package cafestock.modelo;

import java.math.BigDecimal;
import java.util.Objects;

/** Producto de cafetería. El código es una identidad permanente. */
public final class Producto {
    private final String codigo;
    private final String nombre;
    private final BigDecimal precio;
    private final int stock;
    private final int stockMinimo;

    public Producto(String codigo, String nombre, BigDecimal precio, int stock, int stockMinimo) {
        this.codigo = textoObligatorio(codigo, "Código");
        this.nombre = textoObligatorio(nombre, "Nombre");
        this.precio = Objects.requireNonNull(precio, "Precio obligatorio");
        if (precio.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("El precio debe ser mayor que cero.");
        if (precio.scale() > 2) throw new IllegalArgumentException("El precio admite hasta dos decimales.");
        if (stock < 0 || stockMinimo < 0)
            throw new IllegalArgumentException("El stock y el stock mínimo no pueden ser negativos.");
        this.stock = stock;
        this.stockMinimo = stockMinimo;
    }

    private static String textoObligatorio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty())
            throw new IllegalArgumentException(campo + " es obligatorio.");
        String texto = valor.trim();
        if (texto.contains("\n") || texto.contains("\r"))
            throw new IllegalArgumentException(campo + " no puede contener saltos de línea.");
        return texto;
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public BigDecimal getPrecio() { return precio; }
    public int getStock() { return stock; }
    public int getStockMinimo() { return stockMinimo; }
    public boolean necesitaReposicion() { return stock <= stockMinimo; }

    /** Se crea una copia nueva para actualizar datos sin alterar el código. */
    public Producto conCambios(String nuevoNombre, BigDecimal nuevoPrecio, int nuevoStock, int nuevoMinimo) {
        return new Producto(codigo, nuevoNombre, nuevoPrecio, nuevoStock, nuevoMinimo);
    }

    public Producto conStock(int stockRestante) {
        return conCambios(nombre, precio, stockRestante, stockMinimo);
    }

    @Override public String toString() { return codigo + " — " + nombre + " (stock: " + stock + ")"; }
}
