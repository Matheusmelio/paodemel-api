package com.paodemel.api.service;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ProducaoService {

  public Map<String, Object> quadro() {
    return Map.of(
        "colunas", List.of("Aguardando Producao", "Preparando Massa", "Recheando", "Decorando", "Finalizado"),
        "itens", List.of(
            Map.of("cliente", "Ana Clara", "horario", "15:00", "prioridade", "Alta"),
            Map.of("cliente", "Roberto Lima", "horario", "17:30", "prioridade", "Alta")
        )
    );
  }
}
