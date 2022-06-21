package br.com.monitoria.util;

import br.com.monitoria.exception.CalculoMediaException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculaMediaUnitTest {

    @Test
    void sucessoEmCalcularMediaDeAluno() {
        Double media = CalculaMedia.calcular(8.5, 7.0);
        assertEquals(8.05, media);
    }

    @Test
    void deveLancarExcecaoAoCalcularComNotaMenorQueZero() {
        CalculoMediaException exception =
                assertThrows(CalculoMediaException.class, () -> CalculaMedia.calcular(-0.01, 8.0));

        assertEquals("A nota da disciplina deve estar entre 0 e 10", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoAoCalcularComNotaMaiorQueDez() {
        CalculoMediaException exception =
                assertThrows(CalculoMediaException.class, () -> CalculaMedia.calcular(10.01, 8.0));

        assertEquals("A nota da disciplina deve estar entre 0 e 10", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoAoCalcularComCREMenorQueZero() {
        CalculoMediaException exception =
                assertThrows(CalculoMediaException.class, () -> CalculaMedia.calcular(8.0, -0.01));

        assertEquals("O CRE deve estar entre 0 e 10", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoAoCalcularComCREMaiorQueDez() {
        CalculoMediaException exception =
                assertThrows(CalculoMediaException.class, () -> CalculaMedia.calcular(8.0, 10.01));

        assertEquals("O CRE deve estar entre 0 e 10", exception.getMessage());
    }

}