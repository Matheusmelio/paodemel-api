package com.paodemel.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "fornadas")
public class Fornada {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String tipoPao;
  private int quantidadeProduzida;
  private String horaSaida;

  protected Fornada() {
  }

  public Fornada(String tipoPao, int quantidadeProduzida, String horaSaida) {
    this.tipoPao = tipoPao;
    this.quantidadeProduzida = quantidadeProduzida;
    this.horaSaida = horaSaida;
  }

  public Long getId() {
    return id;
  }

  public String getTipoPao() {
    return tipoPao;
  }

  public int getQuantidadeProduzida() {
    return quantidadeProduzida;
  }

  public String getHoraSaida() {
    return horaSaida;
  }
}
