package com.paodemel.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "insumos")
public class Insumo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String nome;

  @Column(nullable = false)
  private double quantidadeAtual;

  @Column(nullable = false)
  private String unidade;

  @Column(nullable = false)
  private double estoqueMinimo;

  protected Insumo() {
  }

  public Insumo(String nome, double quantidadeAtual, String unidade, double estoqueMinimo) {
    this.nome = nome;
    this.quantidadeAtual = quantidadeAtual;
    this.unidade = unidade;
    this.estoqueMinimo = estoqueMinimo;
  }

  public String getNome() {
    return nome;
  }

  public double getQuantidadeAtual() {
    return quantidadeAtual;
  }

  public String getUnidade() {
    return unidade;
  }

  public double getEstoqueMinimo() {
    return estoqueMinimo;
  }

  public String getStatus() {
    if (quantidadeAtual <= estoqueMinimo * 0.5) {
      return "Critico";
    }
    if (quantidadeAtual <= estoqueMinimo) {
      return "Atencao";
    }
    return "Normal";
  }
}
