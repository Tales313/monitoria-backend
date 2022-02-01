package br.com.monitoria.web.response;

import br.com.monitoria.domain.Vaga;

public class VagaResponse {

    private Long id;

    private String disciplina;

    private String periodo;

    private Integer quantidade;

    public VagaResponse(Vaga vaga) {
        this.id = vaga.getId();
        this.disciplina = vaga.getDisciplina();
        this.periodo = vaga.getPeriodo();
        this.quantidade = vaga.getQuantidade();
    }

    public Long getId() {
        return id;
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
}
