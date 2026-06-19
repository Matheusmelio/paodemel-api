package com.paodemel.api.dto;

import com.paodemel.api.model.Perfil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthRequest(
    @NotBlank String login,
    @NotBlank String senha,
    @NotNull Perfil perfil
) {
}
