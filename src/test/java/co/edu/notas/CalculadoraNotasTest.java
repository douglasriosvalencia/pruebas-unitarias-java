package co.edu.notas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CalculadoraNotasTest {

    private CalculadoraNotas calculadora;

    @BeforeEach
    void setUp() {
        calculadora = new CalculadoraNotas(); // objeto nuevo para cada prueba
    }

    @Test
    @DisplayName("Calcula la nota final ponderada 30/30/40")
    void calcularNotaFinal_notasValidas_retornaPromedioPonderado() {
        // Arrange
        double seguimiento = 4.0, parcial = 3.0, proyecto = 5.0;
        // Act
        double resultado = calculadora.calcularNotaFinal(seguimiento, parcial, proyecto);
        // Assert
        assertEquals(4.1, resultado, 0.001);
    }

    @Test
    @DisplayName("Un estudiante con 3.5 aprueba")
    void estaAprobado_notaMayorATres_retornaTrue() {
        assertTrue(calculadora.estaAprobado(3.5));
    }

    @Test
    @DisplayName("Un estudiante con 2.9 reprueba")
    void estaAprobado_notaMenorATres_retornaFalse() {
        assertFalse(calculadora.estaAprobado(2.9));
    }

    @Test
    @DisplayName("Caso límite: exactamente 3.0 aprueba")
    void estaAprobado_notaExactamenteTres_retornaTrue() {
        assertTrue(calculadora.estaAprobado(3.0));
    }

    @ParameterizedTest(name = "{0} → {1}")
    @CsvSource({
        "5.0, Superior", "4.6, Superior",
        "4.5, Alto",     "4.0, Alto",
        "3.9, Básico",   "3.0, Básico",
        "2.9, Bajo",     "0.0, Bajo"
    })
    @DisplayName("Clasifica el desempeño según la escala")
    void desempeno_segunNota_retornaCategoria(double nota, String esperado) {
        assertEquals(esperado, calculadora.desempeno(nota));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.1, 5.1, 10.0})
    @DisplayName("Rechaza notas fuera del rango 0.0 – 5.0")
    void calcularNotaFinal_notaFueraDeRango_lanzaExcepcion(double notaInvalida) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> calculadora.calcularNotaFinal(notaInvalida, 3.0, 3.0));
        assertTrue(ex.getMessage().contains("entre 0.0 y 5.0"));
    }

    // ↓ Las pruebas de los pasos 7, 8 y 9 se pegan AQUÍ, antes de la última llave }
}
