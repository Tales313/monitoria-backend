package br.com.monitoria.domain;

import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Entity
public class Inscricao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dataInscricao;

    @NotNull(message = "A opcao deve ser informada")
    @Range(min = 1, max = 2, message = "A opcao deve ser 1 ou 2")
    private Integer opcao;

    @NotNull(message = "A nota da disciplina deve ser informada")
    @Positive(message = "A nota da disciplina deve ter valor positivo")
    private Double notaDisciplina;

    @NotNull(message = "O CRE deve ser informado")
    @Positive(message = "O CRE deve ter valor positivo")
    private Double cre;

    @NotNull(message = "A média deve ser informada")
    @Positive(message = "A média deve ter valor positivo")
    private Double media;

    @NotNull(message = "O resultado deve ser informado")
    private ResultadoEnum resultado;

    @NotNull(message = "Uma inscrição deve ter uma vaga")
    @ManyToOne
    private Vaga vaga;

    public Inscricao() {
    }

    public Inscricao(@NotNull(message = "A opcao deve ser informada") @Range(min = 1, max = 2, message = "A opcao deve ser 1 ou 2") Integer opcao,
                     @NotNull(message = "A nota da disciplina deve ser informada") @Positive(message = "A nota da disciplina deve ter valor positivo") Double notaDisciplina,
                     @NotNull(message = "O CRE deve ser informado") @Positive(message = "O CRE deve ter valor positivo") Double cre,
                     @NotNull(message = "A média deve ser informada") @Positive(message = "A média deve ter valor positivo") Double media,
                     @NotNull(message = "Uma inscrição deve ter uma vaga") Vaga vaga) {
        this.dataInscricao = LocalDate.now();
        this.opcao = opcao;
        this.notaDisciplina = notaDisciplina;
        this.cre = cre;
        this.media = media;
        this.resultado = ResultadoEnum.AGUARDANDO;
        this.vaga = vaga;
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

    public Vaga getVaga() {
        return vaga;
    }
}
