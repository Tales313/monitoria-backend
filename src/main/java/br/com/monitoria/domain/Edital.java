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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O semestre não deve estar em branco")
    @Pattern(regexp = "^\\d{4}\\.\\d$", message = "O semestre deve ter o formato '2022.1'")
    private String semestre;

    @NotNull(message = "A data de inicio das incrições deve ser informada")
    private LocalDate inicioInscricoes;

    @NotNull(message = "A data de fim das inscrições deve ser informada")
    private LocalDate fimInscricoes;

    public Edital() {
    }

    public Edital(@NotBlank(message = "O semestre não deve estar em branco") @Pattern(regexp = "^\\d{4}\\.\\d$", message = "O semestre deve ter o formato '2022.1'") String semestre,
                  @NotNull(message = "A data de inicio das incrições deve ser informada") LocalDate inicioInscricoes,
                  @NotNull(message = "A data de fim das inscrições deve ser informada") LocalDate fimInscricoes) {
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
