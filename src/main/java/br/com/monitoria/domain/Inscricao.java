package br.com.monitoria.domain;

import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Entity
public class Inscricao {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_inscricao")
    @SequenceGenerator(name = "generator_inscricao", sequenceName = "sequence_id_inscricao")
    private Long id;

    private LocalDate dataInscricao;

    @NotNull(message = "A opcao deve ser informada")
    @Range(min = 1, max = 2, message = "A opcao deve ser 1 ou 2")
    private Integer opcao;

    @NotNull(message = "A nota da disciplina deve ser informada")
    @Range(min = 0, max = 100, message = "A nota da disciplina deve ser entre 0 e 100")
    private Double notaDisciplina;

    @NotNull(message = "O CRE deve ser informado")
    @Range(min = 0, max = 100, message = "O CRE deve ser entre 0 e 100")
    private Double cre;

    @NotNull(message = "A média deve ser informada")
    @Positive(message = "A média deve ter valor positivo")
    private Double media;

    @NotNull(message = "O resultado deve ser informado")
    @Enumerated(EnumType.STRING)
    private ResultadoEnum resultado;

    @NotNull(message = "Uma inscrição deve ter uma vaga")
    @ManyToOne
    private Vaga vaga;

    @NotNull(message = "Uma inscrição pertence a um usuario")
    @ManyToOne
    private Usuario usuario;

    public Inscricao() {
    }

    public Inscricao(@NotNull(message = "A opcao deve ser informada") @Range(min = 1, max = 2, message = "A opcao deve ser 1 ou 2") Integer opcao,
                     @NotNull(message = "A nota da disciplina deve ser informada") @Positive(message = "A nota da disciplina deve ter valor positivo") Double notaDisciplina,
                     @NotNull(message = "O CRE deve ser informado") @Positive(message = "O CRE deve ter valor positivo") Double cre,
                     @NotNull(message = "A média deve ser informada") @Positive(message = "A média deve ter valor positivo") Double media,
                     @NotNull(message = "Uma inscrição deve ter uma vaga") Vaga vaga,
                     @NotNull(message = "Uma inscrição pertence a um usuario") Usuario usuario) {
        this.dataInscricao = LocalDate.now();
        this.opcao = opcao;
        this.notaDisciplina = notaDisciplina;
        this.cre = cre;
        this.media = media;
        this.resultado = ResultadoEnum.AGUARDANDO;
        this.vaga = vaga;
        this.usuario = usuario;
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

    public Usuario getUsuario() {
        return usuario;
    }
}
