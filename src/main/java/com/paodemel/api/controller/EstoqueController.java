package com.paodemel.api.controller;

import com.paodemel.api.service.EstoqueService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/estoque")
public class EstoqueController {

  private final EstoqueService estoqueService;

  public EstoqueController(EstoqueService estoqueService) {
    this.estoqueService = estoqueService;
  }

  @GetMapping
  public List<Map<String, Object>> listar() {
    return estoqueService.listar();
  }
}
