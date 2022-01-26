package br.com.monitoria.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Entity
public class Edital {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotBlank
    @Pattern(regexp = "^\\d{4}\\.\\d$", message = "O semestre deve ter o formato '2022.1'")
    private String semestre;

    @NotNull
    private LocalDate inicioInscricoes;

    @NotNull
    private LocalDate fimInscricoes;

    public Edital() {
    }

    public Edital(@NotBlank String semestre, @NotNull LocalDate inicioInscricoes, @NotNull LocalDate fimInscricoes) {
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
