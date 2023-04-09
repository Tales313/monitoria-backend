package br.com.monitoria.web.request;

import br.com.monitoria.domain.Usuario;
import br.com.monitoria.util.validators.ValorUnico;

import javax.validation.constraints.*;
import java.time.LocalDate;

public class UsuarioRequest {

    @NotBlank(message = "usuario.login.branco")
    @Email(message = "usuario.email.invalido")
    @ValorUnico(classe = Usuario.class, nomeDoCampo = "login", message = "usuario.email.ja.existe")
    private String login;

    @NotBlank(message = "usuario.nome.branco")
    private String nome;

    @NotNull(message = "usuario.senha.nao.informada")
    @Size(min = 6, max = 20, message = "usuario.senha.tamanho")
    private String senha;

    @NotBlank(message = "usuario.matricula.branco")
    private String matricula;

    @NotNull(message = "usuario.dataNascimento.nao.informada")
    @Past(message = "usuario.dataNascimento.futuro")
    private LocalDate dataNascimento;

    public UsuarioRequest(String login, String nome, String senha, String matricula, LocalDate dataNascimento) {
        this.login = login;
        this.nome = nome;
        this.senha = senha;
        this.matricula = matricula;
        this.dataNascimento = dataNascimento;
    }

    public String getLogin() {
        return login;
    }

    public String getNome() {
        return nome;
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
}