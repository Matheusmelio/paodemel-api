package com.paodemel.api.service;

import com.paodemel.api.model.Insumo;
import com.paodemel.api.repository.InsumoRepository;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class EstoqueService {

  private final InsumoRepository insumoRepository;

  public EstoqueService(InsumoRepository insumoRepository) {
    this.insumoRepository = insumoRepository;
  }

  public List<Map<String, Object>> listar() {
    return insumoRepository.findAll().stream()
        .map(this::toMap)
        .toList();
  }

  private Map<String, Object> toMap(Insumo insumo) {
    return Map.of(
        "insumo", insumo.getNome(),
        "quantidadeAtual", insumo.getQuantidadeAtual(),
        "unidade", insumo.getUnidade(),
        "estoqueMinimo", insumo.getEstoqueMinimo(),
        "status", insumo.getStatus()
    );
  }
}
