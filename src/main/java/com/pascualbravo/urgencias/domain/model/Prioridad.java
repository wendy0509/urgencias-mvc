package com.pascualbravo.urgencias.domain.model;

/**
 * Prioridad de atención calculada a partir de la gravedad y la edad (RF02).
 * Regla de negocio pura: no depende de Spring, de la base de datos ni de la web.
 */
public enum Prioridad {
    BAJA, MEDIA, ALTA, CRITICA;

    public static final int EDAD_MAXIMA_NINO = 5;
    public static final int EDAD_MINIMA_ADULTO_MAYOR = 70;

    /**
     * Suma la gravedad (1–5) y un punto extra si el paciente es niño (≤ 5 años)
     * o adulto mayor (≥ 70 años). 5 o más = CRITICA, 4 = ALTA, 3 = MEDIA, resto = BAJA.
     */
    public static Prioridad calcular(int nivelGravedad, int edad) {
        int puntos = nivelGravedad;
        if (edad <= EDAD_MAXIMA_NINO || edad >= EDAD_MINIMA_ADULTO_MAYOR) {
            puntos++;
        }
        if (puntos >= 5) return CRITICA;
        if (puntos == 4) return ALTA;
        if (puntos == 3) return MEDIA;
        return BAJA;
    }
}
