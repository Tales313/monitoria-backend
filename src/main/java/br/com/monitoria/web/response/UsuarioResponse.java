package br.com.monitoria.web.response;

import java.time.LocalDateTime;

public class UsuarioResponse {

    private String login;

    private String matricula;

    public UsuarioResponse(String login, String matricula) {
        this.login = login;
        this.matricula = matricula;
    }

    public String getLogin() {
        return login;
    }

    public String getMatricula() {
        return matricula;
    }
}
