package co.edu.notas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/*
 * ENTREGA DEL TALLER
 * Nombre completo: Douglas Ríos Valencia
 * 1. Bug encontrado (método y línea de CarritoCompras.java):
 *    Método agregarProducto, línea 24: validaba "cantidad < 0" y la regla R1
 *    pide cantidad mayor a 0, así que con cantidad 0 no lanzaba excepción.
 *    Se corrigió a "cantidad <= 0".
 * 2. Prueba que lo detectó (nombre del método):
 *    agregarProducto_cantidadNoPositiva_lanzaExcepcion (la fila con cantidad 0).
 * 3. Sugerencia de la IA que rechacé o corregí, y por qué
 *    (si todas eran correctas: cómo verifiqué una de ellas con las reglas):
 *    Usé una IA para proponer pruebas a partir de las reglas R1 a R6, no a partir
 *    del código. Como todas me parecieron correctas, verifiqué una a mano con las
 *    reglas: la de la mochila de 60.000 con BIENVENIDO. R4: el subtotal es mayor
 *    o igual a 50.000, entonces descuenta 5.000 y queda en 55.000. R6: 55.000 x 1,19
 *    = 65.450, y como eso es menor a 150.000 se suma el envío de 12.000, para un
 *    total de 77.450. Coincide con el valor esperado de la prueba.
 */
class CarritoComprasTest {

    private CarritoCompras carrito;

    @BeforeEach
    void setUp() { carrito = new CarritoCompras(); }

    @Test
    @DisplayName("Subtotal = precio × cantidad de cada producto")
    void calcularSubtotal_variosProductos_sumaCorrecta() {
        // Arrange
        carrito.agregarProducto("Cuaderno", 8_000, 3);
        carrito.agregarProducto("Esfero", 2_000, 5);
        // Act
        double subtotal = carrito.calcularSubtotal();
        // Assert
        assertEquals(34_000, subtotal, 0.001);
    }

    // ---------- Parte A: R1 (validación de productos) ----------

    @Test
    @DisplayName("R1: nombre nulo se rechaza")
    void agregarProducto_nombreNulo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto(null, 1_000, 1));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @DisplayName("R1: nombre vacío o solo espacios se rechaza")
    void agregarProducto_nombreVacio_lanzaExcepcion(String nombre) {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto(nombre, 1_000, 1));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, -1})
    @DisplayName("R1: precio cero o negativo se rechaza")
    void agregarProducto_precioNoPositivo_lanzaExcepcion(double precio) {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("Cuaderno", precio, 1));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName("R1: cantidad cero o negativa se rechaza")
    void agregarProducto_cantidadNoPositiva_lanzaExcepcion(int cantidad) {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("Cuaderno", 1_000, cantidad));
    }

    @Test
    @DisplayName("R1: un producto válido se agrega al carrito")
    void agregarProducto_datosValidos_incrementaCantidadDeItems() {
        carrito.agregarProducto("Cuaderno", 8_000, 1);
        assertEquals(1, carrito.cantidadDeItems());
    }

    // ---------- Parte A: R2 (carrito vacío) ----------

    @Test
    @DisplayName("R2: carrito vacío tiene subtotal 0")
    void calcularSubtotal_carritoVacio_retornaCero() {
        assertEquals(0, carrito.calcularSubtotal(), 0.001);
    }

    @Test
    @DisplayName("R2: carrito vacío tiene total 0 (sin cobrar envío)")
    void calcularTotal_carritoVacio_retornaCero() {
        assertEquals(0, carrito.calcularTotal(), 0.001);
    }

    // ---------- Parte B: R3 y R4 (descuentos) ----------

    @Test
    @DisplayName("Sin cupón no hay descuento")
    void calcularDescuento_sinCupon_retornaCero() {
        carrito.agregarProducto("Libro", 100_000, 1);
        assertEquals(0, carrito.calcularDescuento(), 0.001);
    }

    @Test
    @DisplayName("R3: SENA10 descuenta el 10 % del subtotal")
    void calcularDescuento_cuponSena10_retornaDiezPorCiento() {
        carrito.agregarProducto("Libro", 100_000, 1);
        carrito.aplicarCupon("SENA10");
        assertEquals(10_000, carrito.calcularDescuento(), 0.001);
    }

    @Test
    @DisplayName("R4: BIENVENIDO con subtotal exactamente $50.000 descuenta $5.000")
    void calcularDescuento_bienvenidoSubtotalCincuentaMil_retornaCincoMil() {
        carrito.agregarProducto("Calculadora", 50_000, 1);
        carrito.aplicarCupon("BIENVENIDO");
        assertEquals(5_000, carrito.calcularDescuento(), 0.001);
    }

    @Test
    @DisplayName("R4: BIENVENIDO con subtotal $49.999 no descuenta")
    void calcularDescuento_bienvenidoSubtotalBajoElMinimo_retornaCero() {
        carrito.agregarProducto("Calculadora", 49_999, 1);
        carrito.aplicarCupon("BIENVENIDO");
        assertEquals(0, carrito.calcularDescuento(), 0.001);
    }

    // ---------- Parte B: R5 (cupón inválido) ----------

    @Test
    @DisplayName("R5: un cupón desconocido se rechaza")
    void aplicarCupon_codigoDesconocido_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.aplicarCupon("INVENTADO"));
    }

    @Test
    @DisplayName("R5: un cupón nulo se rechaza")
    void aplicarCupon_codigoNulo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.aplicarCupon(null));
    }

    // ---------- Parte B: R6 (total con IVA y envío) ----------

    @Test
    @DisplayName("R6: con envío pagado, 100.000 → 119.000 + 12.000 = 131.000")
    void calcularTotal_conEnvioPagado_incluyeIvaYEnvio() {
        carrito.agregarProducto("Libro", 100_000, 1);
        assertEquals(131_000, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("R6: con envío gratis, 200.000 × 1,19 = 238.000")
    void calcularTotal_conEnvioGratis_soloIncluyeIva() {
        carrito.agregarProducto("Impresora", 200_000, 1);
        assertEquals(238_000, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("R6: justo debajo del mínimo (126.000 → 149.940) paga envío")
    void calcularTotal_valorConIvaBajoElMinimo_cobraEnvio() {
        carrito.agregarProducto("Teclado", 126_000, 1);
        assertEquals(161_940, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("R6: justo encima del mínimo (127.000 → 151.130) envío gratis")
    void calcularTotal_valorConIvaSobreElMinimo_envioGratis() {
        carrito.agregarProducto("Teclado", 127_000, 1);
        assertEquals(151_130, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("R6: monitor de 500.000 con SENA10 → 535.500 (envío gratis)")
    void calcularTotal_conCuponSena10_aplicaDescuentoAntesDelIva() {
        carrito.agregarProducto("Monitor", 500_000, 1);
        carrito.aplicarCupon("SENA10");
        assertEquals(535_500, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("R6: 60.000 con BIENVENIDO → 55.000 × 1,19 + 12.000 = 77.450")
    void calcularTotal_conCuponBienvenido_descuentaAntesDelIva() {
        carrito.agregarProducto("Mochila", 60_000, 1);
        carrito.aplicarCupon("BIENVENIDO");
        assertEquals(77_450, carrito.calcularTotal(), 0.001);
    }
}
