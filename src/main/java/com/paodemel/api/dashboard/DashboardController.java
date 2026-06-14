package com.paodemel.api.dashboard;

import com.paodemel.api.operations.FornadaRepository;
import com.paodemel.api.operations.InsumoRepository;
import com.paodemel.api.orders.OrderRepository;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

  private final OrderRepository orderRepository;
  private final InsumoRepository insumoRepository;
  private final FornadaRepository fornadaRepository;

  public DashboardController(
      OrderRepository orderRepository,
      InsumoRepository insumoRepository,
      FornadaRepository fornadaRepository
  ) {
    this.orderRepository = orderRepository;
    this.insumoRepository = insumoRepository;
    this.fornadaRepository = fornadaRepository;
  }

  @GetMapping
  public Map<String, Object> resumo() {
    long totalEncomendas = orderRepository.count();
    long estoqueBaixo = insumoRepository.countComEstoqueBaixo();

    return Map.of(
        "kpis", Map.of(
            "encomendasHoje", totalEncomendas,
            "bolosEmProducao", totalEncomendas,
            "paesDisponiveis", fornadaRepository.count(),
            "estoqueBaixo", estoqueBaixo,
            "pedidosEntregues", 0
        ),
        "alertas", List.of(
            estoqueBaixo > 0 ? "Existem insumos abaixo do estoque minimo" : "Estoque dentro do minimo",
            "Dados carregados do PostgreSQL Paoemel",
            "Sistema pronto para operacao"
        )
    );
  }
}
