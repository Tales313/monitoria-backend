package br.com.monitoria.util;

import br.com.monitoria.exception.CalculoMediaException;

public class CalculaMedia {

    public static Double calcular(Double notaDisciplina, Double cre) {

        if(notaDisciplina < 0 || notaDisciplina > 100)
            throw new CalculoMediaException("A nota da disciplina deve estar entre 0 e 100");

        if(cre < 0 || cre > 100)
            throw new CalculoMediaException("O CRE deve estar entre 0 e 100");

        return (7 * notaDisciplina + 3 * cre)/10;
    }

}
