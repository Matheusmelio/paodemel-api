package com.paodemel.api.auth;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuthService {

  private final UsuarioRepository usuarioRepository;

  private static final Map<Perfil, List<String>> PERMISSOES = Map.of(
      Perfil.GERENTE, List.of("DASHBOARD", "ENCOMENDAS", "PRODUCAO", "FORNADAS", "ESTOQUE", "VENDAS", "RELATORIOS", "ADMINISTRACAO"),
      Perfil.ATENDENTE, List.of("DASHBOARD", "ENCOMENDAS", "ESTOQUE", "VENDAS"),
      Perfil.CONFEITEIRO, List.of("DASHBOARD", "ENCOMENDAS", "PRODUCAO", "ESTOQUE"),
      Perfil.CLIENTE, List.of("DASHBOARD", "NOVA_ENCOMENDA", "TIMELINE_PEDIDOS")
  );

  public AuthService(UsuarioRepository usuarioRepository) {
    this.usuarioRepository = usuarioRepository;
  }

  public AuthResponse login(AuthRequest request) {
    String email = normalizarEmail(request.login());
    Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
        .orElseThrow(() -> new IllegalArgumentException("E-mail nao cadastrado. Verifique o e-mail informado ou crie uma conta."));

    if (!usuario.getSenha().equals(request.senha())) {
      throw new IllegalArgumentException("Senha incorreta. Confira sua senha e tente novamente.");
    }

    if (usuario.getPerfil() != request.perfil()) {
      throw new IllegalArgumentException("Perfil de acesso incorreto. Selecione o perfil cadastrado para este usuario.");
    }

    return new AuthResponse(
        buildToken(usuario.getPerfil()),
        usuario.getNome(),
        usuario.getEmail(),
        usuario.getPerfil(),
        permissoes(usuario.getPerfil())
    );
  }

  public AuthResponse register(RegisterRequest request) {
    if (!request.senha().equals(request.confirmarSenha())) {
      throw new IllegalArgumentException("As senhas informadas nao conferem.");
    }

    if (isPerfilInterno(request.perfil()) && !StringUtils.hasText(request.codigoInterno())) {
      throw new IllegalArgumentException("Codigo interno e obrigatorio para perfis da equipe.");
    }

    if (usuarioRepository.existsByEmailIgnoreCase(request.email())) {
      throw new IllegalArgumentException("Ja existe um usuario cadastrado com este e-mail.");
    }

    Usuario usuario = usuarioRepository.save(new Usuario(
        request.nome(),
        request.telefone(),
        request.email(),
        request.senha(),
        request.perfil(),
        request.codigoInterno()
    ));

    return new AuthResponse(
        buildToken(usuario.getPerfil()),
        usuario.getNome(),
        usuario.getEmail(),
        usuario.getPerfil(),
        permissoes(usuario.getPerfil())
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

  private String normalizarEmail(String login) {
    return login.contains("@") ? login : login + "@paodemel.com";
  }
}
