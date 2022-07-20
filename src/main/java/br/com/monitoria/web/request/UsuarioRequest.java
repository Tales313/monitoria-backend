package br.com.monitoria.web.request;

import br.com.monitoria.domain.Usuario;
import br.com.monitoria.util.validators.ValorUnico;

import javax.validation.constraints.*;
import java.time.LocalDate;

public class UsuarioRequest {

    @NotBlank(message = "O login não deve estar em branco")
    @Email
    @ValorUnico(classe = Usuario.class, nomeDoCampo = "login", message = "Já existe um usuário com este email")
    private String login;

    @NotNull(message = "A senha não deve ser nula")
    @Size(min = 6, max = 20)
    private String senha;

    @NotBlank(message = "A matricula não deve estar em branco")
    private String matricula;

    @NotNull(message = "A data de nascimento deve ser informada")
    @Past(message = "A data de nascimento não pode ser no futuro")
    private LocalDate dataNascimento;

    public UsuarioRequest(String login, String senha, String matricula, LocalDate dataNascimento) {
        this.login = login;
        this.senha = senha;
        this.matricula = matricula;
        this.dataNascimento = dataNascimento;
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
}