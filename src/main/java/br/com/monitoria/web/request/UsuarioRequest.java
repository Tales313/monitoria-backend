package br.com.monitoria.web.request;

import br.com.monitoria.domain.Usuario;
import br.com.monitoria.util.ValorUnico;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UsuarioRequest {

    @NotBlank
    @Email
    @ValorUnico(classe = Usuario.class, nomeDoCampo = "login", message = "Já existe um usuário com este email")
    private String login;

    @NotBlank
    @Size(min = 8, max = 20)
    private String senha;

    @NotBlank
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