package br.com.monitoria.web.request;

import br.com.monitoria.domain.Inscricao;
import br.com.monitoria.domain.Usuario;
import br.com.monitoria.domain.Vaga;
import br.com.monitoria.exception.NotFoundException;
import br.com.monitoria.repository.VagaRepository;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class InscricaoRequest {

    @NotNull(message = "A opcao deve ser informada")
    @Range(min = 1, max = 2, message = "A opcao deve ser 1 ou 2")
    private Integer opcao;

    @NotNull(message = "A nota da disciplina deve ser informada")
    @Positive(message = "A nota da disciplina deve ter valor positivo")
    private Double notaDisciplina;

    @NotNull(message = "O CRE deve ser informado")
    @Positive(message = "O CRE deve ter valor positivo")
    private Double cre;

    @NotNull(message = "O id da vaga deve ser informado")
    private Long idVaga;

    public Inscricao toModel(VagaRepository vagaRepository, Double media, Usuario usuario) {

        Vaga vaga = vagaRepository.findById(idVaga).orElseThrow(
                () -> new NotFoundException("Vaga não encontrada"));

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
