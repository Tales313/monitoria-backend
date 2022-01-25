package br.com.monitoria.web.request;

import br.com.monitoria.domain.Edital;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class EditalRequest {

    @NotBlank
    private String semestre;

    @NotNull
    private LocalDate inicioInscricoes;

    @NotNull
    private LocalDate fimInscricoes;

    public Edital toModel() {
        return new Edital(semestre, inicioInscricoes, fimInscricoes);
    }

    public String getSemestre() {
        return semestre;
    }

    public LocalDate getInicioInscricoes() {
        return inicioInscricoes;
    }

    public LocalDate getFimInscricoes() {
        return fimInscricoes;
    }
}
