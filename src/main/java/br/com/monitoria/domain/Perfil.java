package br.com.monitoria.domain;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
public class Perfil implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_perfil")
    @SequenceGenerator(name = "generator_perfil", sequenceName = "sequence_id_perfil")
    private Long id;

    @NotBlank(message = "Um perfil precisa ter um nome")
    private String nome;

    @ManyToMany
    @JoinTable(name = "usuarios_perfis",
               joinColumns = @JoinColumn(name = "perfil_id"),
               inverseJoinColumns = @JoinColumn(name = "usuario_id"))
    private Set<Usuario> usuarios;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String getAuthority() {
        return this.nome;
    }

}
