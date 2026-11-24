import cafestock.modelo.Producto;
import cafestock.modelo.Venta;
import java.math.BigDecimal;
import java.time.LocalDate;

public class PruebaVentaManual {
    public static void main(String[] args) {
        Producto cafe = new Producto("P001", "Café americano", new BigDecimal("1.50"), 10, 3);

        Venta v = new Venta(1, LocalDate.of(2026, 11, 24), cafe, 3);
        if (v.getTotal().compareTo(new BigDecimal("4.50")) != 0)
            throw new AssertionError("Total incorrecto: " + v.getTotal());
        if (v.getCantidad() != 3 || v.getNumero() != 1 || !v.getFecha().equals(LocalDate.of(2026, 11, 24)))
            throw new AssertionError("Datos incorrectos");

        try { new Venta(2, LocalDate.now(), cafe, 0); throw new AssertionError("Aceptó cantidad 0"); }
        catch (IllegalArgumentException esperado) { }
        try { new Venta(3, LocalDate.now(), cafe, -2); throw new AssertionError("Aceptó cantidad negativa"); }
        catch (IllegalArgumentException esperado) { }

        System.out.println("OK: total, datos, cantidad 0 y negativa rechazadas");
    }
}