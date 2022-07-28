package br.com.monitoria.util;

import br.com.monitoria.exception.CalculoMediaException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculaMediaUnitTest {

    @Test
    void sucessoEmCalcularMediaDeAluno() {
        Double media = CalculaMedia.calcular(85.0, 70.0);
        assertEquals(80.5, media);
    }

    @Test
    void deveLancarExcecaoAoCalcularComNotaMenorQueZero() {
        CalculoMediaException exception =
                assertThrows(CalculoMediaException.class, () -> CalculaMedia.calcular(-1.0, 80.0));

        assertEquals("A nota da disciplina deve estar entre 0 e 100", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoAoCalcularComNotaMaiorQueCem() {
        CalculoMediaException exception =
                assertThrows(CalculoMediaException.class, () -> CalculaMedia.calcular(101.0, 80.0));

        assertEquals("A nota da disciplina deve estar entre 0 e 100", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoAoCalcularComCREMenorQueZero() {
        CalculoMediaException exception =
                assertThrows(CalculoMediaException.class, () -> CalculaMedia.calcular(80.0, -1.0));

        assertEquals("O CRE deve estar entre 0 e 100", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoAoCalcularComCREMaiorQueCem() {
        CalculoMediaException exception =
                assertThrows(CalculoMediaException.class, () -> CalculaMedia.calcular(80.0, 101.0));

        assertEquals("O CRE deve estar entre 0 e 100", exception.getMessage());
    }

}