package com.paodemel.api.auth;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AuthService {

  private final UsuarioRepository usuarioRepository;
  private final ClienteRepository clienteRepository;

  private static final Map<Perfil, List<String>> PERMISSOES = Map.of(
      Perfil.GERENTE, List.of("DASHBOARD", "ENCOMENDAS", "PRODUCAO", "FORNADAS", "ESTOQUE", "VENDAS", "RELATORIOS", "ADMINISTRACAO"),
      Perfil.ATENDENTE, List.of("DASHBOARD", "ENCOMENDAS", "ESTOQUE", "VENDAS"),
      Perfil.CONFEITEIRO, List.of("DASHBOARD", "ENCOMENDAS", "PRODUCAO", "ESTOQUE"),
      Perfil.CLIENTE, List.of("DASHBOARD", "NOVA_ENCOMENDA", "TIMELINE_PEDIDOS")
  );

  public AuthService(UsuarioRepository usuarioRepository, ClienteRepository clienteRepository) {
    this.usuarioRepository = usuarioRepository;
    this.clienteRepository = clienteRepository;
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

  @Transactional
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

    String cpfCliente = null;
    if (request.perfil() == Perfil.CLIENTE) {
      Cliente cliente = criarCliente(request);
      clienteRepository.save(cliente);
      cpfCliente = cliente.getCpfCliente();
    }

    Usuario usuario = usuarioRepository.save(new Usuario(
        request.nome(),
        request.telefone(),
        request.email(),
        request.senha(),
        request.perfil(),
        request.codigoInterno(),
        cpfCliente
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

  private Cliente criarCliente(RegisterRequest request) {
    String cpfCliente = somenteDigitos(request.cpfCliente());
    String cep = somenteDigitos(request.cep());
    String sexo = normalizarSexo(request.sexo());

    validarCampo(cpfCliente, "CPF e obrigatorio para cadastro de cliente.");
    validarCampo(request.logradouro(), "Logradouro e obrigatorio para cadastro de cliente.");
    validarCampo(request.numero(), "Numero e obrigatorio para cadastro de cliente.");
    validarCampo(cep, "CEP e obrigatorio para cadastro de cliente.");
    validarCampo(request.bairro(), "Bairro e obrigatorio para cadastro de cliente.");
    validarCampo(request.cidade(), "Cidade e obrigatoria para cadastro de cliente.");
    validarCampo(request.estado(), "Estado e obrigatorio para cadastro de cliente.");

    if (cpfCliente.length() != 11) {
      throw new IllegalArgumentException("CPF deve conter 11 digitos.");
    }

    if (cep.length() != 8) {
      throw new IllegalArgumentException("CEP deve conter 8 digitos.");
    }

    if (request.nascimento() == null) {
      throw new IllegalArgumentException("Data de nascimento e obrigatoria para cadastro de cliente.");
    }

    if (!sexo.equals("M") && !sexo.equals("F")) {
      throw new IllegalArgumentException("Sexo deve ser M ou F.");
    }

    if (request.estado().trim().length() != 2) {
      throw new IllegalArgumentException("Estado deve conter a sigla com 2 letras.");
    }

    if (clienteRepository.existsById(cpfCliente)) {
      throw new IllegalArgumentException("Ja existe um cliente cadastrado com este CPF.");
    }

    return new Cliente(
        cpfCliente,
        request.nome(),
        request.nascimento(),
        sexo,
        request.logradouro(),
        request.numero(),
        request.complemento(),
        cep,
        request.bairro(),
        request.cidade(),
        request.estado().toUpperCase(),
        request.telefone(),
        request.email()
    );
  }

  private String somenteDigitos(String value) {
    return value == null ? "" : value.replaceAll("\\D", "");
  }

  private String normalizarSexo(String sexo) {
    return sexo == null ? "" : sexo.trim().toUpperCase();
  }

  private void validarCampo(String value, String mensagem) {
    if (!StringUtils.hasText(value)) {
      throw new IllegalArgumentException(mensagem);
    }
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
