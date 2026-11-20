package com.pascualbravo.urgencias.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Regla de prioridad probada sin Spring ni base de datos. */
class PrioridadTest {

    @Test
    void debeSerCriticaConGravedadCinco() {
        assertEquals(Prioridad.CRITICA, Prioridad.calcular(5, 30));
    }

    @Test
    void debeSumarPuntoExtraAMenorDeCincoAnios() {
        assertEquals(Prioridad.CRITICA, Prioridad.calcular(4, 3));
    }

    @Test
    void debeSumarPuntoExtraAAdultoMayor() {
        assertEquals(Prioridad.ALTA, Prioridad.calcular(3, 75));
    }

    @Test
    void debeSerMediaConGravedadTresEnAdulto() {
        assertEquals(Prioridad.MEDIA, Prioridad.calcular(3, 30));
    }

    @Test
    void debeSerBajaConGravedadUnoEnAdulto() {
        assertEquals(Prioridad.BAJA, Prioridad.calcular(1, 30));
    }
}
