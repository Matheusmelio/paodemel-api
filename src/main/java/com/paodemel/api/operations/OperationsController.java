package com.paodemel.api.operations;

import com.paodemel.api.auth.AuthService;
import com.paodemel.api.auth.Perfil;
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

  public OperationsController(AuthService authService) {
    this.authService = authService;
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

  @PostMapping("/fornadas")
  public Map<String, Object> registrarFornada(@RequestBody Map<String, Object> request) {
    return Map.of(
        "mensagem", "Fornada registrada. Estoque atualizado automaticamente.",
        "fornada", request
    );
  }

  @GetMapping("/estoque")
  public List<Map<String, Object>> estoque() {
    return List.of(
        Map.of("insumo", "Farinha de trigo", "quantidadeAtual", 42, "unidade", "kg", "estoqueMinimo", 20, "status", "Normal"),
        Map.of("insumo", "Chocolate 70%", "quantidadeAtual", 4, "unidade", "kg", "estoqueMinimo", 8, "status", "Critico"),
        Map.of("insumo", "Creme de leite", "quantidadeAtual", 10, "unidade", "l", "estoqueMinimo", 12, "status", "Atencao")
    );
  }

  @PostMapping("/vendas")
  public Map<String, Object> registrarVenda(@RequestBody Map<String, Object> request) {
    return Map.of(
        "mensagem", "Venda registrada com sucesso.",
        "venda", request
    );
  }

  @GetMapping("/relatorios")
  public Map<String, Object> relatorios(@RequestHeader("X-Perfil") Perfil perfil) {
    authService.exigirGerente(perfil);
    return Map.of(
        "receita", 8420,
        "lucro", 3180,
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
        "nome", "Matheus Oliveira",
        "email", "matheus@paodemel.com",
        "telefone", "(11) 99999-0000",
        "perfil", perfil,
        "permissoes", authService.permissoes(perfil)
    );
  }
}
