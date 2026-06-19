package com.paodemel.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "encomendas")
public class OrderEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String codigo;

  @Column(nullable = false)
  private String cliente;

  @Column(nullable = false)
  private String massa;

  @Column(nullable = false)
  private String recheio;

  @Column(nullable = false)
  private String dataEntrega;

  @Column(nullable = false)
  private String status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cpf_cliente", referencedColumnName = "cpf_cliente")
  private Cliente clienteCadastro;

  protected OrderEntity() {
  }

  public OrderEntity(String codigo, String cliente, String massa, String recheio, String dataEntrega, String status) {
    this.codigo = codigo;
    this.cliente = cliente;
    this.massa = massa;
    this.recheio = recheio;
    this.dataEntrega = dataEntrega;
    this.status = status;
  }

  public String getCodigo() {
    return codigo;
  }

  public String getCliente() {
    return cliente;
  }

  public String getMassa() {
    return massa;
  }

  public String getRecheio() {
    return recheio;
  }

  public String getDataEntrega() {
    return dataEntrega;
  }

  public String getStatus() {
    return status;
  }
}
