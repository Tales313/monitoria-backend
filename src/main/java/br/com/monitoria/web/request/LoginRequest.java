package br.com.monitoria.web.request;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class LoginRequest {

    private String login;
    private String senha;

    public LoginRequest(String login, String senha) {
        this.login = login;
        this.senha = senha;
    }

    public String getLogin() {
        return login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public UsernamePasswordAuthenticationToken converter() {
        return new UsernamePasswordAuthenticationToken(this.login, this.senha);
    }

}