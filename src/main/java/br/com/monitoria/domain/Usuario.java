package br.com.monitoria.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

@Entity
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_usuario")
    @SequenceGenerator(name = "generator_usuario", sequenceName = "sequence_id_usuario")
    private Long id;

    @NotBlank
    @Email
    private String login;

    @NotBlank
    private String senha;

    @NotNull
    private LocalDateTime dataCadastro;

    @NotBlank
    private String matricula;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "usuarios")
    private Set<Perfil> perfis;

    public Usuario() {
        this.dataCadastro = LocalDateTime.now();
    }

    /**
     *
     * @param login
     * @param senha deve entrar ja criptografada
     */
    public Usuario(String login, String senha, String matricula) {
        this.login = login;
        this.senha = senha;
        this.dataCadastro = LocalDateTime.now();
        this.matricula = matricula;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getSenha() {
        return senha;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public String getMatricula() {
        return matricula;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.perfis;
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
