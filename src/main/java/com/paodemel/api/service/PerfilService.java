package com.paodemel.api.service;

import com.paodemel.api.model.Perfil;
import com.paodemel.api.repository.UsuarioRepository;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class PerfilService {

  private final AuthService authService;
  private final UsuarioRepository usuarioRepository;

  public PerfilService(AuthService authService, UsuarioRepository usuarioRepository) {
    this.authService = authService;
    this.usuarioRepository = usuarioRepository;
  }

  public Map<String, Object> consultar(Perfil perfil) {
    return Map.of(
        "usuariosCadastrados", usuarioRepository.count(),
        "perfil", perfil,
        "permissoes", authService.permissoes(perfil)
    );
  }

  public Map<String, Object> administracao(Perfil perfil) {
    authService.exigirGerente(perfil);

    return Map.of(
        "perfis", List.of("GERENTE", "ATENDENTE", "CONFEITEIRO", "CLIENTE"),
        "permissoes", Map.of(
            "GERENTE", authService.permissoes(Perfil.GERENTE),
            "ATENDENTE", authService.permissoes(Perfil.ATENDENTE),
            "CONFEITEIRO", authService.permissoes(Perfil.CONFEITEIRO),
            "CLIENTE", authService.permissoes(Perfil.CLIENTE)
        )
    );
  }
}
