package br.com.monitoria.web.response;

import java.time.LocalDate;

public class UsuarioResponse {

    private String login;

    private String matricula;

    private LocalDate dataNascimento;

    public UsuarioResponse(String login, String matricula, LocalDate dataNascimento) {
        this.login = login;
        this.matricula = matricula;
        this.dataNascimento = dataNascimento;
    }

    public String getLogin() {
        return login;
    }

    public String getMatricula() {
        return matricula;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }
}
