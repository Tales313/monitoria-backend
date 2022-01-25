package br.com.monitoria.web.response;

import java.time.LocalDate;

public class EditalResponse {

    private Long id;

    private String semestre;

    private LocalDate inicioInscricoes;

    private LocalDate fimInscricoes;

    public EditalResponse(Long id, String semestre, LocalDate inicioInscricoes, LocalDate fimInscricoes) {
        this.id = id;
        this.semestre = semestre;
        this.inicioInscricoes = inicioInscricoes;
        this.fimInscricoes = fimInscricoes;
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
