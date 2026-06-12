package com.paodemel.api.dashboard;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

  @GetMapping
  public Map<String, Object> resumo() {
    return Map.of(
        "kpis", Map.of(
            "encomendasHoje", 18,
            "bolosEmProducao", 9,
            "paesDisponiveis", 342,
            "estoqueBaixo", 7,
            "pedidosEntregues", 26
        ),
        "alertas", List.of(
            "Produtos esgotados: Brioche e pao australiano",
            "Encomendas atrasadas: 2 pedidos",
            "Estoque critico: Chocolate 70%, creme de leite e farinha"
        )
    );
  }
}
