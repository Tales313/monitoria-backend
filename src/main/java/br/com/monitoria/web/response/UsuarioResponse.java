package br.com.monitoria.web.response;

import java.time.LocalDateTime;

public class UsuarioResponse {

    private String login;

    private String senha;

    private LocalDateTime dataCadastro;

    private String matricula;

    public UsuarioResponse(String login, String senha, LocalDateTime dataCadastro, String matricula) {
        this.login = login;
        this.senha = senha;
        this.dataCadastro = dataCadastro;
        this.matricula = matricula;
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
}
