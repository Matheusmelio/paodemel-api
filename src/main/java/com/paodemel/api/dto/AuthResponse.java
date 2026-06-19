package com.paodemel.api.dto;

import com.paodemel.api.model.Perfil;
import java.util.List;

public record AuthResponse(
    String token,
    String nome,
    String email,
    Perfil perfil,
    List<String> permissoes
) {
}
