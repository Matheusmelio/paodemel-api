package com.paodemel.api.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "clientes")
public class Cliente {

  @Id
  @Column(name = "cpf_cliente", length = 11)
  private String cpfCliente;

  @Column(nullable = false, length = 50)
  private String nome;

  @Column(nullable = false)
  private LocalDate nascimento;

  @Column(nullable = false, length = 1)
  private String sexo;

  @Column(nullable = false, length = 50)
  private String logradouro;

  @Column(nullable = false, length = 10)
  private String numero;

  @Column(length = 30)
  private String complemento;

  @Column(nullable = false, length = 8)
  private String cep = "00000000";

  @Column(nullable = false, length = 30)
  private String bairro;

  @Column(nullable = false, length = 30)
  private String cidade;

  @Column(nullable = false, length = 2)
  private String estado;

  @Column(nullable = false, length = 15)
  private String telefone;

  @Column(length = 50)
  private String email;

  protected Cliente() {
  }

  public Cliente(
      String cpfCliente,
      String nome,
      LocalDate nascimento,
      String sexo,
      String logradouro,
      String numero,
      String complemento,
      String cep,
      String bairro,
      String cidade,
      String estado,
      String telefone,
      String email
  ) {
    this.cpfCliente = cpfCliente;
    this.nome = nome;
    this.nascimento = nascimento;
    this.sexo = sexo;
    this.logradouro = logradouro;
    this.numero = numero;
    this.complemento = complemento;
    this.cep = cep;
    this.bairro = bairro;
    this.cidade = cidade;
    this.estado = estado;
    this.telefone = telefone;
    this.email = email;
  }

  public String getCpfCliente() {
    return cpfCliente;
  }
}
