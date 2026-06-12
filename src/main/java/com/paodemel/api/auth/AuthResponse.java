package com.paodemel.api.auth;

import java.util.List;

public record AuthResponse(
    String token,
    String nome,
    String email,
    Perfil perfil,
    List<String> permissoes
) {
}
