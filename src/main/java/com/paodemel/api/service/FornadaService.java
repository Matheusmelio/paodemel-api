package com.paodemel.api.service;

import com.paodemel.api.model.Fornada;
import com.paodemel.api.repository.FornadaRepository;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class FornadaService {

  private final FornadaRepository fornadaRepository;

  public FornadaService(FornadaRepository fornadaRepository) {
    this.fornadaRepository = fornadaRepository;
  }

  public List<Map<String, Object>> listar() {
    return fornadaRepository.findAll().stream()
        .sorted((left, right) -> Long.compare(right.getId(), left.getId()))
        .map(this::toMap)
        .toList();
  }

  public Map<String, Object> registrar(Map<String, Object> request) {
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
        "fornada", toMap(fornada)
    );
  }

  private Map<String, Object> toMap(Fornada fornada) {
    return Map.of(
        "tipoPao", fornada.getTipoPao(),
        "quantidadeProduzida", fornada.getQuantidadeProduzida(),
        "horaSaida", fornada.getHoraSaida()
    );
  }
}
