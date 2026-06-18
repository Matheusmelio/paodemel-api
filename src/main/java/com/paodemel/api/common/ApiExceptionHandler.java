package com.paodemel.api.common;

import com.paodemel.api.auth.AccessDeniedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ApiError handleAccessDenied(AccessDeniedException exception) {
    return new ApiError(HttpStatus.FORBIDDEN.value(), exception.getMessage());
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiError handleDataIntegrity(DataIntegrityViolationException exception) {
    return new ApiError(HttpStatus.BAD_REQUEST.value(), "Nao foi possivel salvar os dados. Verifique se o registro ja existe ou se os campos estao corretos.");
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiError handleBadRequest(IllegalArgumentException exception) {
    return new ApiError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiError handleValidationError(MethodArgumentNotValidException exception) {
    return new ApiError(HttpStatus.BAD_REQUEST.value(), "Dados invalidos. Preencha todos os campos obrigatorios corretamente.");
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiError handleUnreadableBody(HttpMessageNotReadableException exception) {
    return new ApiError(HttpStatus.BAD_REQUEST.value(), "Corpo da requisicao invalido. Envie os dados em JSON.");
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  public ApiError handleMethodNotAllowed(HttpRequestMethodNotSupportedException exception) {
    return new ApiError(HttpStatus.METHOD_NOT_ALLOWED.value(), "Metodo HTTP nao permitido para esta rota.");
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ApiError handleUnexpected(Exception exception) {
    return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erro interno ao processar a solicitacao.");
  }
}
