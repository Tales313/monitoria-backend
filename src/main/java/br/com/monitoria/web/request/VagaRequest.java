package br.com.monitoria.web.request;

import br.com.monitoria.domain.Edital;
import br.com.monitoria.domain.Vaga;
import br.com.monitoria.exception.NotFoundException;
import br.com.monitoria.repository.EditalRepository;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class VagaRequest {

    @NotBlank(message = "A disciplina deve ser informada")
    private String disciplina;

    @NotBlank(message = "O periodo deve ser informado")
    private String periodo;

    @NotNull(message = "A quantidade deve ser informada")
    @Positive(message = "A quantidade deve ter valor positivo")
    private Integer quantidade;

    @NotNull(message = "O id do edital deve ser informado")
    private Long editalId;

    public Vaga toModel(EditalRepository editalRepository) {
        Edital edital = editalRepository.findById(editalId).orElseThrow(
                () -> new NotFoundException("Edital não encontrado"));
        return new Vaga(disciplina, periodo, quantidade, edital);
    }

    public String getDisciplina() {
        return disciplina;
    }

    public String getPeriodo() {
        return periodo;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public Long getEditalId() {
        return editalId;
    }
}
