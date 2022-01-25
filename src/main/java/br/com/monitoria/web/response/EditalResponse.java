package br.com.monitoria.web.response;

import br.com.monitoria.domain.Edital;

import java.time.LocalDate;

public class EditalResponse {

    private Long id;

    private String semestre;

    private LocalDate inicioInscricoes;

    private LocalDate fimInscricoes;

    public EditalResponse(Edital edital) {
        this.id = edital.getId();
        this.semestre = edital.getSemestre();
        this.inicioInscricoes = edital.getInicioInscricoes();
        this.fimInscricoes = edital.getFimInscricoes();
    }

    public Long getId() {
        return id;
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
