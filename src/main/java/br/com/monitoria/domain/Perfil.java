package br.com.monitoria.domain;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;

@Entity
public class Perfil implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_perfil")
    @SequenceGenerator(name = "generator_perfil", sequenceName = "sequence_id_perfil", allocationSize = 1)
    private Long id;

    @NotBlank(message = "Um perfil precisa ter um nome")
    @Enumerated(EnumType.STRING)
    private PerfilEnum nome;

    @ManyToMany
    @JoinTable(name = "usuarios_perfis",
               joinColumns = @JoinColumn(name = "perfil_id"),
               inverseJoinColumns = @JoinColumn(name = "usuario_id"))
    private Set<Usuario> usuarios;

    public Perfil() {
    }

    public Long getId() {
        return id;
    }

    public PerfilEnum getNome() {
        return nome;
    }

    public void addUsuario(Usuario usuario) {
        this.usuarios.add(usuario);
    }

    @Override
    public String getAuthority() {
        return this.nome.toString();
    }

}
