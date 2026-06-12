package com.paodemel.api.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank String nome,
    @NotBlank String telefone,
    @Email @NotBlank String email,
    @NotNull Perfil perfil,
    @Size(min = 8) String senha,
    @Size(min = 8) String confirmarSenha,
    String codigoInterno
) {
}
