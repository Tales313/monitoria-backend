package br.com.monitoria.util;

import br.com.monitoria.exception.CalculoMediaException;

public class CalculaMedia {

    public static Double calcular(Double notaDisciplina, Double cre) {

        if(notaDisciplina < 0 || notaDisciplina > 10)
            throw new CalculoMediaException("A nota da disciplina deve estar entre 0 e 10");

        if(cre < 0 || cre > 10)
            throw new CalculoMediaException("O CRE deve estar entre 0 e 10");

        return (7 * notaDisciplina + 3 * cre)/10;
    }

}
