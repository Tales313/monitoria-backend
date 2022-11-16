package br.com.monitoria.web.response;

import br.com.monitoria.domain.Inscricao;
import br.com.monitoria.domain.ResultadoEnum;

public class ResultadoResponse {

    private String nomeAluno;

    private Double nota;

    private Double cre;

    private Double media;

    private String disciplina;

    private Integer opcao;

    private ResultadoEnum resultado;

    public ResultadoResponse(Inscricao inscricao) {
        this.nomeAluno = inscricao.getUsuario().getNome();
        this.nota = inscricao.getNotaDisciplina();
        this.cre = inscricao.getCre();
        this.media = inscricao.getMedia();
        this.disciplina = inscricao.getVaga().getDisciplina();
        this.opcao = inscricao.getOpcao();
        this.resultado = inscricao.getResultado();
    }

    public String getNomeAluno() {
        return nomeAluno;
    }

    public Double getNota() {
        return nota;
    }

    public Double getCre() {
        return cre;
    }

    public Double getMedia() {
        return media;
    }

    public String getDisciplina() {
        return disciplina;
    }

    public Integer getOpcao() {
        return opcao;
    }

    public ResultadoEnum getResultado() {
        return resultado;
    }
}
