package br.com.monitoria.web.request;

import br.com.monitoria.domain.Edital;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

public class EditalRequest {

    @NotBlank(message = "O semestre não deve estar em branco")
    @Pattern(regexp = "^\\d{4}\\.\\d$", message = "O semestre deve ter o formato '2022.1'")
    private String semestre;

    @NotNull(message = "A data de inicio das incrições deve ser informada")
    private LocalDate inicioInscricoes;

    @NotNull(message = "A data de fim das inscrições deve ser informada")
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
