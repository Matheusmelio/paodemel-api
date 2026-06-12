package com.paodemel.api.orders;

public record OrderDto(
    String codigo,
    String cliente,
    String massa,
    String recheio,
    String dataEntrega,
    String status
) {
}
