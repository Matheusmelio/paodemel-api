package com.paodemel.api.controller;

import com.paodemel.api.model.Perfil;
import com.paodemel.api.service.PerfilService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

  private final PerfilService perfilService;

  public AdminController(PerfilService perfilService) {
    this.perfilService = perfilService;
  }

  @GetMapping
  public Map<String, Object> administracao(@RequestHeader("X-Perfil") Perfil perfil) {
    return perfilService.administracao(perfil);
  }
}
