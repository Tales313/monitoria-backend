package br.com.monitoria.domain;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Entity
public class Vaga {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_vaga")
    @SequenceGenerator(name = "generator_vaga", sequenceName = "sequence_id_vaga", allocationSize = 1)
    private Long id;

    @NotBlank(message = "A disciplina deve ser informada")
    private String disciplina;

    @NotBlank(message = "O periodo deve ser informado")
    private String periodo;

    @NotNull(message = "A quantidade deve ser informada")
    @Positive(message = "A quantidade deve ter valor positivo")
    private Integer quantidade;

    @NotNull(message = "Uma vaga deve pertencer a um edital")
    @ManyToOne
    private Edital edital;

    @NotNull(message = "Uma vaga deve ter sido cadastrado por um usuario")
    @ManyToOne
    private Usuario usuario;

    public Vaga() {
    }

    public Vaga(@NotBlank(message = "A disciplina deve ser informada") String disciplina,
                @NotBlank(message = "O periodo deve ser informado") String periodo,
                @NotNull(message = "A quantidade deve ser informada") @Positive(message = "A quantidade deve ter valor positivo") Integer quantidade,
                @NotNull(message = "Uma vaga deve pertencer a um edital") Edital edital,
                @NotNull(message = "Uma vaga deve ter sido cadastrado por um usuario") Usuario usuario) {
        this.disciplina = disciplina;
        this.periodo = periodo;
        this.quantidade = quantidade;
        this.edital = edital;
        this.usuario = usuario;
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

    public Edital getEdital() {
        return edital;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
