package br.com.monitoria.web.exceptionhandler;

public class ErrorMessage {

    private Integer status;
    private String error;
    private String message;

    public ErrorMessage(Integer status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}
