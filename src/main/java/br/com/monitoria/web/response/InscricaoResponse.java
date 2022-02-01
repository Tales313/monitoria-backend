package br.com.monitoria.web.response;

import br.com.monitoria.domain.Inscricao;
import br.com.monitoria.domain.ResultadoEnum;

import java.time.LocalDate;


public class InscricaoResponse {

    private Long id;

    private LocalDate dataInscricao;

    private Integer opcao;

    private Double notaDisciplina;

    private Double cre;

    private Double media;

    private ResultadoEnum resultado;

    public InscricaoResponse(Inscricao inscricao) {
        this.id = inscricao.getId();
        this.dataInscricao = inscricao.getDataInscricao();
        this.opcao = inscricao.getOpcao();
        this.notaDisciplina = inscricao.getNotaDisciplina();
        this.cre = inscricao.getCre();
        this.media = inscricao.getMedia();
        this.resultado = inscricao.getResultado();
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDataInscricao() {
        return dataInscricao;
    }

    public Integer getOpcao() {
        return opcao;
    }

    public Double getNotaDisciplina() {
        return notaDisciplina;
    }

    public Double getCre() {
        return cre;
    }

    public Double getMedia() {
        return media;
    }

    public ResultadoEnum getResultado() {
        return resultado;
    }
}
