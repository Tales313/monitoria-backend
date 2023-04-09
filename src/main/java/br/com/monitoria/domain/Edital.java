package br.com.monitoria.domain;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Entity
public class Edital {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_edital")
    @SequenceGenerator(name = "generator_edital", sequenceName = "sequence_id_edital", allocationSize = 1)
    private Long id;

    @NotBlank(message = "edital.semestre.nao.informado")
    @Pattern(regexp = "^\\d{4}\\.\\d$", message = "edital.semestre.formato")
    private String semestre;

    @NotNull(message = "edital.data.inicio.nao.informado")
    private LocalDate inicioInscricoes;

    @NotNull(message = "edital.data.fim.nao.informado")
    private LocalDate fimInscricoes;

    @NotNull(message = "Um edital deve ter sido cadastrado por um usuario")
    @ManyToOne
    private Usuario usuario;

    public Edital() {
    }

    public Edital(@NotBlank(message = "edital.semestre.nao.informado") @Pattern(regexp = "^\\d{4}\\.\\d$", message = "edital.semestre.formato") String semestre,
                  @NotNull(message = "edital.data.inicio.nao.informado") LocalDate inicioInscricoes,
                  @NotNull(message = "edital.data.fim.nao.informado") LocalDate fimInscricoes,
                  @NotNull(message = "Um edital deve ter sido cadastrado por um usuario") Usuario usuario) {
        this.semestre = semestre;
        this.inicioInscricoes = inicioInscricoes;
        this.fimInscricoes = fimInscricoes;
        this.usuario = usuario;
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

    public Usuario getUsuario() {
        return usuario;
    }
}
