package com.paodemel.api.dto;

public record OrderDto(
    String codigo,
    String cliente,
    String massa,
    String recheio,
    String dataEntrega,
    String status
) {
}
