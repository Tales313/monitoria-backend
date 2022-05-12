package br.com.monitoria.domain;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Entity
public class Edital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O semestre não deve estar em branco")
    @Pattern(regexp = "^\\d{4}\\.\\d$", message = "O semestre deve ter o formato '2022.1'")
    private String semestre;

    @NotNull(message = "A data de inicio das incrições deve ser informada")
    private LocalDate inicioInscricoes;

    @NotNull(message = "A data de fim das inscrições deve ser informada")
    private LocalDate fimInscricoes;

    @NotNull(message = "Um edital deve ter sido cadastrado por um usuario")
    @ManyToOne
    private Usuario usuario;

    public Edital() {
    }

    public Edital(@NotBlank(message = "O semestre não deve estar em branco") @Pattern(regexp = "^\\d{4}\\.\\d$", message = "O semestre deve ter o formato '2022.1'") String semestre,
                  @NotNull(message = "A data de inicio das incrições deve ser informada") LocalDate inicioInscricoes,
                  @NotNull(message = "A data de fim das inscrições deve ser informada") LocalDate fimInscricoes,
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
