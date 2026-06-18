package com.paodemel.api.operations;

import com.paodemel.api.auth.AuthService;
import com.paodemel.api.auth.Perfil;
import com.paodemel.api.auth.UsuarioRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class OperationsController {

  private final AuthService authService;
  private final InsumoRepository insumoRepository;
  private final FornadaRepository fornadaRepository;
  private final VendaRepository vendaRepository;
  private final UsuarioRepository usuarioRepository;

  public OperationsController(
      AuthService authService,
      InsumoRepository insumoRepository,
      FornadaRepository fornadaRepository,
      VendaRepository vendaRepository,
      UsuarioRepository usuarioRepository
  ) {
    this.authService = authService;
    this.insumoRepository = insumoRepository;
    this.fornadaRepository = fornadaRepository;
    this.vendaRepository = vendaRepository;
    this.usuarioRepository = usuarioRepository;
  }

  @GetMapping("/producao")
  public Map<String, Object> producao() {
    return Map.of(
        "colunas", List.of("Aguardando Producao", "Preparando Massa", "Recheando", "Decorando", "Finalizado"),
        "itens", List.of(
            Map.of("cliente", "Ana Clara", "horario", "15:00", "prioridade", "Alta"),
            Map.of("cliente", "Roberto Lima", "horario", "17:30", "prioridade", "Alta")
        )
    );
  }

  @GetMapping("/fornadas")
  public List<Map<String, Object>> listarFornadas() {
    return fornadaRepository.findAll().stream()
        .sorted((left, right) -> Long.compare(right.getId(), left.getId()))
        .map(fornada -> Map.<String, Object>of(
            "tipoPao", fornada.getTipoPao(),
            "quantidadeProduzida", fornada.getQuantidadeProduzida(),
            "horaSaida", fornada.getHoraSaida()
        ))
        .toList();
  }

  @PostMapping("/fornadas")
  public Map<String, Object> registrarFornada(@RequestBody Map<String, Object> request) {
    String tipoPao = String.valueOf(request.getOrDefault("tipoPao", "")).trim();
    int quantidadeProduzida;

    try {
      quantidadeProduzida = Integer.parseInt(String.valueOf(request.getOrDefault("quantidadeProduzida", 0)));
    } catch (NumberFormatException exception) {
      throw new IllegalArgumentException("Informe uma quantidade produzida valida.");
    }

    String horaSaida = String.valueOf(request.getOrDefault("horaSaida", "")).trim();

    if (tipoPao.isEmpty()) {
      throw new IllegalArgumentException("Informe o tipo de pao.");
    }

    if (quantidadeProduzida <= 0) {
      throw new IllegalArgumentException("Informe uma quantidade produzida maior que zero.");
    }

    if (horaSaida.isEmpty()) {
      throw new IllegalArgumentException("Informe a hora de saida da fornada.");
    }

    Fornada fornada = fornadaRepository.save(new Fornada(
        tipoPao,
        quantidadeProduzida,
        horaSaida
    ));

    return Map.of(
        "mensagem", "Fornada registrada. Estoque atualizado automaticamente.",
        "fornada", Map.of(
            "tipoPao", fornada.getTipoPao(),
            "quantidadeProduzida", fornada.getQuantidadeProduzida(),
            "horaSaida", fornada.getHoraSaida()
        )
    );
  }

  @GetMapping("/estoque")
  public List<Map<String, Object>> estoque() {
    return insumoRepository.findAll().stream()
        .map(insumo -> Map.<String, Object>of(
            "insumo", insumo.getNome(),
            "quantidadeAtual", insumo.getQuantidadeAtual(),
            "unidade", insumo.getUnidade(),
            "estoqueMinimo", insumo.getEstoqueMinimo(),
            "status", insumo.getStatus()
        ))
        .toList();
  }

  @PostMapping("/vendas")
  public Map<String, Object> registrarVenda(@RequestBody Map<String, Object> request) {
    BigDecimal total = new BigDecimal(String.valueOf(request.getOrDefault("total", "0")));
    Venda venda = vendaRepository.save(new Venda(total));

    return Map.of(
        "mensagem", "Venda registrada com sucesso.",
        "venda", Map.of(
            "total", venda.getTotal(),
            "criadaEm", venda.getCriadaEm()
        )
    );
  }

  @GetMapping("/relatorios")
  public Map<String, Object> relatorios(@RequestHeader("X-Perfil") Perfil perfil) {
    authService.exigirGerente(perfil);
    return Map.of(
        "receita", vendaRepository.receitaTotal(),
        "lucro", vendaRepository.receitaTotal().multiply(new BigDecimal("0.37")),
        "produtosMaisVendidos", List.of("Pao frances", "Croissant", "Pao de mel"),
        "bolosMaisEncomendados", List.of("Chocolate", "Red velvet", "Cenoura")
    );
  }

  @GetMapping("/admin")
  public Map<String, Object> administracao(@RequestHeader("X-Perfil") Perfil perfil) {
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

  @GetMapping("/perfil")
  public Map<String, Object> perfil(@RequestHeader("X-Perfil") Perfil perfil) {
    return Map.of(
        "usuariosCadastrados", usuarioRepository.count(),
        "perfil", perfil,
        "permissoes", authService.permissoes(perfil)
    );
  }
}
