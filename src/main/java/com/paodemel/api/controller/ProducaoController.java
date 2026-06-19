package com.paodemel.api.controller;

import com.paodemel.api.service.ProducaoService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/producao")
public class ProducaoController {

  private final ProducaoService producaoService;

  public ProducaoController(ProducaoService producaoService) {
    this.producaoService = producaoService;
  }

  @GetMapping
  public Map<String, Object> quadro() {
    return producaoService.quadro();
  }
}
