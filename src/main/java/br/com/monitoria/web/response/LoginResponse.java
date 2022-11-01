package br.com.monitoria.web.response;

import br.com.monitoria.domain.PerfilEnum;

public class LoginResponse {

    private String token;
    private String tipo;
    private PerfilEnum perfil;

    public LoginResponse(String token, String tipo, PerfilEnum perfil) {
        this.token = token;
        this.tipo = tipo;
        this.perfil = perfil;
    }

    public LoginResponse() {
    }

    public String getToken() {
        return token;
    }

    public String getTipo() {
        return tipo;
    }

    public PerfilEnum getPerfil() {
        return perfil;
    }
}