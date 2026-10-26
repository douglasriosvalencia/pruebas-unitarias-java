package co.edu.notas;

public class CalculadoraNotas {

    public static final double NOTA_MINIMA = 0.0;
    public static final double NOTA_MAXIMA = 5.0;
    public static final double NOTA_APROBATORIA = 3.0;

    /** Calcula la nota final: 30% seguimiento + 30% parcial + 40% proyecto. */
    public double calcularNotaFinal(double seguimiento, double parcial, double proyecto) {
        validarNota(seguimiento);
        validarNota(parcial);
        validarNota(proyecto);
        double resultado = seguimiento * 0.30 + parcial * 0.30 + proyecto * 0.40;
        return Math.round(resultado * 10) / 10.0; // redondea a una décima
    }

    /** Aprueba quien obtiene 3.0 o más. */
    public boolean estaAprobado(double notaFinal) {
        validarNota(notaFinal);
        return notaFinal > NOTA_APROBATORIA;
    }

    /** Escala de desempeño: Superior, Alto, Básico, Bajo. */
    public String desempeno(double notaFinal) {
        validarNota(notaFinal);
        if (notaFinal >= 4.6) return "Superior";
        if (notaFinal >= 4.0) return "Alto";
        if (notaFinal >= 3.0) return "Básico";
        return "Bajo";
    }

    private void validarNota(double nota) {
        if (nota < NOTA_MINIMA || nota > NOTA_MAXIMA) {
            throw new IllegalArgumentException("La nota debe estar entre 0.0 y 5.0: " + nota);
        }
    }
}
