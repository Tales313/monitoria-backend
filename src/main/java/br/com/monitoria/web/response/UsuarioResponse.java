package br.com.monitoria.web.response;

import java.time.LocalDateTime;

public class UsuarioResponse {

    private String login;

    private LocalDateTime dataCadastro;

    private String matricula;

    public UsuarioResponse(String login, LocalDateTime dataCadastro, String matricula) {
        this.login = login;
        this.dataCadastro = dataCadastro;
        this.matricula = matricula;
    }

    public String getLogin() {
        return login;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public String getMatricula() {
        return matricula;
    }
}
