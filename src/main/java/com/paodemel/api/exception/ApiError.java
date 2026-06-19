package com.paodemel.api.exception;

public record ApiError(
    int status,
    String mensagem
) {
}
