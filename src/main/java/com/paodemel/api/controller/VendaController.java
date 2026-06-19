package com.paodemel.api.controller;

import com.paodemel.api.service.VendaService;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vendas")
public class VendaController {

  private final VendaService vendaService;

  public VendaController(VendaService vendaService) {
    this.vendaService = vendaService;
  }

  @PostMapping
  public Map<String, Object> registrar(@RequestBody Map<String, Object> request) {
    return vendaService.registrar(request);
  }
}
