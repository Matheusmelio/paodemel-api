package com.paodemel.api.service;

import com.paodemel.api.model.Venda;
import com.paodemel.api.repository.VendaRepository;
import java.math.BigDecimal;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class VendaService {

  private final VendaRepository vendaRepository;

  public VendaService(VendaRepository vendaRepository) {
    this.vendaRepository = vendaRepository;
  }

  public Map<String, Object> registrar(Map<String, Object> request) {
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
}
