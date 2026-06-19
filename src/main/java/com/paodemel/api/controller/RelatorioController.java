package com.paodemel.api.controller;

import com.paodemel.api.model.Perfil;
import com.paodemel.api.service.RelatorioService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

  private final RelatorioService relatorioService;

  public RelatorioController(RelatorioService relatorioService) {
    this.relatorioService = relatorioService;
  }

  @GetMapping
  public Map<String, Object> relatorios(@RequestHeader("X-Perfil") Perfil perfil) {
    return relatorioService.gerar(perfil);
  }
}
