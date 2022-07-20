package br.com.monitoria.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

@Entity
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_usuario")
    @SequenceGenerator(name = "generator_usuario", sequenceName = "sequence_id_usuario", allocationSize = 1)
    private Long id;

    @NotBlank
    @Email
    private String login;

    @NotBlank
    private String senha;

    @NotBlank
    private String matricula;

    @NotNull
    private LocalDate dataNascimento;

    @NotNull
    private LocalDateTime dataCadastro;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "usuarios", cascade = CascadeType.PERSIST)
    private Set<Perfil> perfis;

    public Usuario() {
        this.dataCadastro = LocalDateTime.now();
    }

    /**
     *
     * @param login
     * @param senha deve entrar ja criptografada
     */
    public Usuario(@NotBlank @Email String login, @NotBlank String senha, @NotBlank String matricula,
                   @NotNull LocalDate dataNascimento, @NotNull Perfil perfil) {
        this.login = login;
        this.senha = senha;
        this.matricula = matricula;
        this.dataNascimento = dataNascimento;
        this.dataCadastro = LocalDateTime.now();
        this.perfis = Set.of(perfil);
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

    public String getMatricula() {
        return matricula;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    // Um usuario sempre terá um perfil, e será apenas ele
    public Perfil getPerfilUnico() {
        return this.perfis.stream().findFirst().get();
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
