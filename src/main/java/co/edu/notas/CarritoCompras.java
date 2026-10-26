package co.edu.notas;

import java.util.ArrayList;
import java.util.List;

public class CarritoCompras {

    public static final double IVA = 0.19;
    public static final double MINIMO_ENVIO_GRATIS = 150_000;
    public static final double COSTO_ENVIO = 12_000;

    private final List<Item> items = new ArrayList<>();
    private String cupon = null;

    public record Item(String nombre, double precio, int cantidad) { }

    public void agregarProducto(String nombre, double precio, int cantidad) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser positivo");
        }
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        items.add(new Item(nombre, precio, cantidad));
    }

    public int cantidadDeItems() {
        return items.size();
    }

    public double calcularSubtotal() {
        double subtotal = 0;
        for (Item item : items) {
            subtotal += item.precio() * item.cantidad();
        }
        return subtotal;
    }

    /** Cupones válidos: "SENA10" (10 % de descuento) y "BIENVENIDO" ($5.000 si el subtotal es ≥ $50.000). */
    public void aplicarCupon(String codigo) {
        if (!"SENA10".equals(codigo) && !"BIENVENIDO".equals(codigo)) {
            throw new IllegalArgumentException("Cupón inválido: " + codigo);
        }
        this.cupon = codigo;
    }

    public double calcularDescuento() {
        double subtotal = calcularSubtotal();
        if ("SENA10".equals(cupon)) return subtotal * 0.10;
        if ("BIENVENIDO".equals(cupon) && subtotal >= 50_000) return 5_000;
        return 0;
    }

    /** Total = (subtotal − descuento) + IVA + envío (gratis desde $150.000 antes de envío). */
    public double calcularTotal() {
        if (items.isEmpty()) return 0;
        double base = calcularSubtotal() - calcularDescuento();
        double conIva = base * (1 + IVA);
        double envio = conIva >= MINIMO_ENVIO_GRATIS ? 0 : COSTO_ENVIO;
        return Math.round(conIva + envio);
    }
}
