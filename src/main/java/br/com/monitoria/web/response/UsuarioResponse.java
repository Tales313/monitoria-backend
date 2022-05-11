package br.com.monitoria.web.response;

import java.time.LocalDateTime;

public class UsuarioResponse {

    private String login;

    private String senha;

    private LocalDateTime dataCadastro;

    public UsuarioResponse(String login, String senha, LocalDateTime dataCadastro) {
        this.login = login;
        this.senha = senha;
        this.dataCadastro = dataCadastro;
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

}
