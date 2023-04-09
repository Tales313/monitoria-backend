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
    @SequenceGenerator(name = "generator_inscricao", sequenceName = "sequence_id_inscricao", allocationSize = 1)
    private Long id;

    private LocalDate dataInscricao;

    @NotNull(message = "inscricao.opcao.nao.informada")
    @Range(min = 1, max = 2, message = "inscricao.opcao.range")
    private Integer opcao;

    @NotNull(message = "inscricao.nota.nao.informada")
    @Range(min = 70, max = 100, message = "inscricao.nota.range")
    private Double notaDisciplina;

    @NotNull(message = "inscricao.cre.nao.informado")
    @Range(min = 0, max = 100, message = "inscricao.cre.range")
    private Double cre;

    @NotNull(message = "inscricao.media.nao.informada")
    @Positive(message = "inscricao.media.negativa")
    private Double media;

    @NotNull(message = "inscricao.resultado.nao.informado")
    @Enumerated(EnumType.STRING)
    private ResultadoEnum resultado;

    @NotNull(message = "inscricao.vaga.nao.informada")
    @ManyToOne
    private Vaga vaga;

    @NotNull(message = "inscricao.usuario.nao.informado")
    @ManyToOne
    private Usuario usuario;

    public Inscricao() {
    }

    public Inscricao(@NotNull(message = "inscricao.opcao.nao.informada") @Range(min = 1, max = 2, message = "inscricao.opcao.range") Integer opcao,
                     @NotNull(message = "inscricao.nota.nao.informada") @Positive(message = "inscricao.nota.range") Double notaDisciplina,
                     @NotNull(message = "inscricao.cre.nao.informado") @Range(min = 0, max = 100, message = "inscricao.cre.range") Double cre,
                     @NotNull(message = "inscricao.media.nao.informada") @Positive(message = "inscricao.media.negativa") Double media,
                     @NotNull(message = "inscricao.vaga.nao.informada") Vaga vaga,
                     @NotNull(message = "inscricao.usuario.nao.informado") Usuario usuario) {
        this.dataInscricao = LocalDate.now();
        this.opcao = opcao;
        this.notaDisciplina = notaDisciplina;
        this.cre = cre;
        this.media = media;
        this.resultado = ResultadoEnum.AGUARDANDO;
        this.vaga = vaga;
        this.usuario = usuario;
    }

    public Boolean isSegundaOpcao() {
        return this.opcao == 2;
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

    public void setResultado(ResultadoEnum resultado) {
        this.resultado = resultado;
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
