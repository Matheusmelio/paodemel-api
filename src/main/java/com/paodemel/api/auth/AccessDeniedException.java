package com.paodemel.api.auth;

public class AccessDeniedException extends RuntimeException {

  public AccessDeniedException(String message) {
    super(message);
  }
}
