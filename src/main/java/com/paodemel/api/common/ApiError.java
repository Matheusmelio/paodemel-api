package com.paodemel.api.common;

public record ApiError(
    int status,
    String mensagem
) {
}
