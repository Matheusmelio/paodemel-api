package com.paodemel.api.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String nome;

  @Column(nullable = false)
  private String telefone;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String senha;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Perfil perfil;

  private String codigoInterno;

  @Column(name = "cpf_cliente", length = 11)
  private String cpfCliente;

  protected Usuario() {
  }

  public Usuario(String nome, String telefone, String email, String senha, Perfil perfil, String codigoInterno) {
    this(nome, telefone, email, senha, perfil, codigoInterno, null);
  }

  public Usuario(String nome, String telefone, String email, String senha, Perfil perfil, String codigoInterno, String cpfCliente) {
    this.nome = nome;
    this.telefone = telefone;
    this.email = email;
    this.senha = senha;
    this.perfil = perfil;
    this.codigoInterno = codigoInterno;
    this.cpfCliente = cpfCliente;
  }

  public String getNome() {
    return nome;
  }

  public String getTelefone() {
    return telefone;
  }

  public String getEmail() {
    return email;
  }

  public String getSenha() {
    return senha;
  }

  public Perfil getPerfil() {
    return perfil;
  }

  public String getCpfCliente() {
    return cpfCliente;
  }
}
