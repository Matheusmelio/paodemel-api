package com.paodemel.api.common;

import com.paodemel.api.auth.AccessDeniedException;
import org.springframework.http.HttpStatus;
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

  @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiError handleBadRequest(Exception exception) {
    return new ApiError(HttpStatus.BAD_REQUEST.value(), "Dados invalidos. Verifique as informacoes enviadas.");
  }
}
