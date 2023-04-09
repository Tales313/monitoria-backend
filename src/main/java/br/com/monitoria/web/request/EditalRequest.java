package br.com.monitoria.web.request;

import br.com.monitoria.domain.Edital;
import br.com.monitoria.domain.Usuario;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

public class EditalRequest {

    @NotNull(message = "edital.semestre.nao.informado")
    @Pattern(regexp = "^\\d{4}\\.\\d$", message = "edital.semestre.formato")
    private String semestre;

    @NotNull(message = "edital.data.inicio.nao.informado")
    private LocalDate inicioInscricoes;

    @NotNull(message = "edital.data.fim.nao.informado")
    private LocalDate fimInscricoes;

    public EditalRequest(String semestre, LocalDate inicioInscricoes, LocalDate fimInscricoes) {
        this.semestre = semestre;
        this.inicioInscricoes = inicioInscricoes;
        this.fimInscricoes = fimInscricoes;
    }

    public Edital toModel(Usuario usuario) {
        return new Edital(semestre, inicioInscricoes, fimInscricoes, usuario);
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
