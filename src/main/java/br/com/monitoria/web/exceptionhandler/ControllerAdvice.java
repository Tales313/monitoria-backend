package br.com.monitoria.web.exceptionhandler;

import br.com.monitoria.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestControllerAdvice
public class ControllerAdvice {

    @Autowired
    private MessageSource messageSource;

    private String getMessageSource(String defaultMessage) {
        return messageSource.getMessage(defaultMessage, null, defaultMessage, LocaleContextHolder.getLocale());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception ex, HttpServletRequest request) {
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getPathInfo());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {

        String mensagem = "";

        List<FieldError> fieldErrorList = ex.getBindingResult().getFieldErrors();

        for(FieldError fieldError : fieldErrorList) {
            mensagem = mensagem.concat(getMessageSource(fieldError.getDefaultMessage()) + ";");
        }

        mensagem = mensagem.substring(0, mensagem.length()-1);

        return new ErrorResponse(HttpStatus.BAD_REQUEST, mensagem, request.getPathInfo());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotFoundException(NotFoundException ex, HttpServletRequest request) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST, getMessageSource(ex.getMessage()), request.getPathInfo());
    }

    @ExceptionHandler(DataInscricoesException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataInscricoesException(DataInscricoesException ex, HttpServletRequest request) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST, getMessageSource(ex.getMessage()), request.getPathInfo());
    }

    @ExceptionHandler(OperacaoNegadaException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleOperacaoNaoPermitidaException(OperacaoNegadaException ex, HttpServletRequest request) {
        return new ErrorResponse(HttpStatus.FORBIDDEN, getMessageSource(ex.getMessage()), request.getPathInfo());
    }

    @ExceptionHandler(InscricaoException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleInscricaoException(InscricaoException ex, HttpServletRequest request) {
        return new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, getMessageSource(ex.getMessage()), request.getPathInfo());
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleInternalAuthenticationServiceException(InternalAuthenticationServiceException ex,
                                                                      HttpServletRequest request) {
        return new ErrorResponse(HttpStatus.UNAUTHORIZED, "Login inexistente", request.getPathInfo());
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleBadCredentialsException(BadCredentialsException ex, HttpServletRequest request) {
        return new ErrorResponse(HttpStatus.UNAUTHORIZED, "Senha incorreta", request.getPathInfo());
    }

    @ExceptionHandler(SemEditalAtivoException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleSemEditalAtivoException(SemEditalAtivoException ex, HttpServletRequest request) {
        return new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, getMessageSource(ex.getMessage()), request.getPathInfo());
    }

}
