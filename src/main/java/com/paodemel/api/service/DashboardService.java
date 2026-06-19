package com.paodemel.api.service;

import com.paodemel.api.repository.FornadaRepository;
import com.paodemel.api.repository.InsumoRepository;
import com.paodemel.api.repository.OrderRepository;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

  private final OrderRepository orderRepository;
  private final InsumoRepository insumoRepository;
  private final FornadaRepository fornadaRepository;

  public DashboardService(
      OrderRepository orderRepository,
      InsumoRepository insumoRepository,
      FornadaRepository fornadaRepository
  ) {
    this.orderRepository = orderRepository;
    this.insumoRepository = insumoRepository;
    this.fornadaRepository = fornadaRepository;
  }

  public Map<String, Object> resumo() {
    long totalEncomendas = orderRepository.count();
    long emProducao = orderRepository.countByStatus("Em Producao");
    long aguardando = orderRepository.countByStatus("Aguardando Producao");
    long entregues = orderRepository.countByStatus("Entregue");
    long estoqueBaixo = insumoRepository.countComEstoqueBaixo();
    long estoqueCritico = insumoRepository.countComEstoqueCritico();
    long paesDisponiveis = fornadaRepository.totalPaesProduzidos();

    return Map.of(
        "kpis", Map.of(
            "encomendasHoje", totalEncomendas,
            "bolosEmProducao", emProducao,
            "paesDisponiveis", paesDisponiveis,
            "estoqueBaixo", estoqueBaixo,
            "pedidosEntregues", entregues
        ),
        "detalhes", Map.of(
            "aguardandoProducao", aguardando,
            "itensCriticos", estoqueCritico
        ),
        "alertas", List.of(
            estoqueBaixo > 0 ? "Existem insumos abaixo do estoque minimo" : "Estoque dentro do minimo",
            "Dados carregados do PostgreSQL Paoemel",
            "Sistema pronto para operacao"
        )
    );
  }
}
