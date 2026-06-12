package com.paodemel.api.auth;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuthService {

  private static final Map<Perfil, List<String>> PERMISSOES = Map.of(
      Perfil.GERENTE, List.of("DASHBOARD", "ENCOMENDAS", "PRODUCAO", "FORNADAS", "ESTOQUE", "VENDAS", "RELATORIOS", "ADMINISTRACAO"),
      Perfil.ATENDENTE, List.of("DASHBOARD", "ENCOMENDAS", "ESTOQUE", "VENDAS"),
      Perfil.CONFEITEIRO, List.of("DASHBOARD", "ENCOMENDAS", "PRODUCAO", "ESTOQUE"),
      Perfil.CLIENTE, List.of("DASHBOARD", "NOVA_ENCOMENDA", "TIMELINE_PEDIDOS")
  );

  public AuthResponse login(AuthRequest request) {
    return new AuthResponse(
        buildToken(request.perfil()),
        nomePadrao(request.login()),
        request.login().contains("@") ? request.login() : request.login() + "@paodemel.com",
        request.perfil(),
        permissoes(request.perfil())
    );
  }

  public AuthResponse register(RegisterRequest request) {
    if (!request.senha().equals(request.confirmarSenha())) {
      throw new IllegalArgumentException("As senhas informadas nao conferem.");
    }

    if (isPerfilInterno(request.perfil()) && !StringUtils.hasText(request.codigoInterno())) {
      throw new IllegalArgumentException("Codigo interno e obrigatorio para perfis da equipe.");
    }

    return new AuthResponse(
        buildToken(request.perfil()),
        request.nome(),
        request.email(),
        request.perfil(),
        permissoes(request.perfil())
    );
  }

  public List<String> permissoes(Perfil perfil) {
    return PERMISSOES.getOrDefault(perfil, List.of());
  }

  public void exigirGerente(Perfil perfil) {
    if (perfil != Perfil.GERENTE) {
      throw new AccessDeniedException("Acesso permitido apenas para Gerente.");
    }
  }

  private boolean isPerfilInterno(Perfil perfil) {
    return perfil == Perfil.GERENTE || perfil == Perfil.ATENDENTE || perfil == Perfil.CONFEITEIRO;
  }

  private String buildToken(Perfil perfil) {
    return "demo-" + perfil.name().toLowerCase() + "-" + UUID.randomUUID();
  }

  private String nomePadrao(String login) {
    if (!StringUtils.hasText(login)) {
      return "Usuario";
    }

    String base = login.contains("@") ? login.substring(0, login.indexOf("@")) : login;
    return base.replace(".", " ");
  }
}
