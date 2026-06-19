package com.paodemel.api.service;

import com.paodemel.api.model.Perfil;
import com.paodemel.api.repository.VendaRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class RelatorioService {

  private final AuthService authService;
  private final VendaRepository vendaRepository;

  public RelatorioService(AuthService authService, VendaRepository vendaRepository) {
    this.authService = authService;
    this.vendaRepository = vendaRepository;
  }

  public Map<String, Object> gerar(Perfil perfil) {
    authService.exigirGerente(perfil);
    BigDecimal receita = vendaRepository.receitaTotal();

    return Map.of(
        "receita", receita,
        "lucro", receita.multiply(new BigDecimal("0.37")),
        "produtosMaisVendidos", List.of("Pao frances", "Croissant", "Pao de mel"),
        "bolosMaisEncomendados", List.of("Chocolate", "Red velvet", "Cenoura")
    );
  }
}
