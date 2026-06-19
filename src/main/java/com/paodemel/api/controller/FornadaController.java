package com.paodemel.api.controller;

import com.paodemel.api.service.FornadaService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fornadas")
public class FornadaController {

  private final FornadaService fornadaService;

  public FornadaController(FornadaService fornadaService) {
    this.fornadaService = fornadaService;
  }

  @GetMapping
  public List<Map<String, Object>> listar() {
    return fornadaService.listar();
  }

  @PostMapping
  public Map<String, Object> registrar(@RequestBody Map<String, Object> request) {
    return fornadaService.registrar(request);
  }
}
