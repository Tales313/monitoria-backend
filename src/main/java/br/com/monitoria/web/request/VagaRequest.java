package br.com.monitoria.web.request;

import br.com.monitoria.domain.Edital;
import br.com.monitoria.domain.Usuario;
import br.com.monitoria.domain.Vaga;
import br.com.monitoria.exception.NotFoundException;
import br.com.monitoria.repository.EditalRepository;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class VagaRequest {

    @NotBlank(message = "vaga.disciplina.nao.informada")
    private String disciplina;

    @NotBlank(message = "vaga.periodo.nao.informado")
    private String periodo;

    @NotNull(message = "vaga.quantidade.nao.informada")
    @Positive(message = "vaga.quantidade.negativa")
    private Integer quantidade;

    @NotNull(message = "vaga.idEdital.nao.informado")
    private Long editalId;

    public VagaRequest(String disciplina, String periodo, Integer quantidade, Long editalId) {
        this.disciplina = disciplina;
        this.periodo = periodo;
        this.quantidade = quantidade;
        this.editalId = editalId;
    }

    public Vaga toModel(EditalRepository editalRepository, Usuario usuario) {
        Edital edital = editalRepository.findById(editalId).orElseThrow(
                () -> new NotFoundException("edital.nao.encontrado"));
        return new Vaga(disciplina, periodo, quantidade, edital, usuario);
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
