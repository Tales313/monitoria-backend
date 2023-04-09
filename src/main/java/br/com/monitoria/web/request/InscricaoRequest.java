package br.com.monitoria.web.request;

import br.com.monitoria.domain.Inscricao;
import br.com.monitoria.domain.Usuario;
import br.com.monitoria.domain.Vaga;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

public class InscricaoRequest {

    @NotNull(message = "inscricao.opcao.nao.informada")
    @Range(min = 1, max = 2, message = "inscricao.opcao.range")
    private Integer opcao;

    @NotNull(message = "inscricao.nota.nao.informada")
    @Range(min = 70, max = 100, message = "inscricao.nota.range")
    private Double notaDisciplina;

    @NotNull(message = "inscricao.cre.nao.informado")
    @Range(min = 0, max = 100, message = "inscricao.cre.range")
    private Double cre;

    @NotNull(message = "inscricao.idVaga.nao.informado")
    private Long idVaga;

    public InscricaoRequest(Integer opcao, Double notaDisciplina, Double cre, Long idVaga) {
        this.opcao = opcao;
        this.notaDisciplina = notaDisciplina;
        this.cre = cre;
        this.idVaga = idVaga;
    }

    public Inscricao toModel(Vaga vaga, Double media, Usuario usuario) {
        return new Inscricao(opcao, notaDisciplina, cre, media, vaga, usuario);
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

    public Long getIdVaga() {
        return idVaga;
    }
}
