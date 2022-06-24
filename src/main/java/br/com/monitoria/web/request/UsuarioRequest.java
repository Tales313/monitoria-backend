package br.com.monitoria.web.request;

import br.com.monitoria.domain.Usuario;
import br.com.monitoria.util.validators.ValorUnico;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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

    public UsuarioRequest(String login, String senha, String matricula) {
        this.login = login;
        this.senha = senha;
        this.matricula = matricula;
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
}