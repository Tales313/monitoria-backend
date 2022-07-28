package br.com.monitoria.exception;

public class OperacaoNegadaException extends RuntimeException{
    public OperacaoNegadaException(String message) {
        super(message);
    }
}
