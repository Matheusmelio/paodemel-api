package com.paodemel.api.operations;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendas")
public class Venda {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private BigDecimal total;

  @Column(nullable = false)
  private LocalDateTime criadaEm;

  protected Venda() {
  }

  public Venda(BigDecimal total) {
    this.total = total;
    this.criadaEm = LocalDateTime.now();
  }

  public BigDecimal getTotal() {
    return total;
  }

  public LocalDateTime getCriadaEm() {
    return criadaEm;
  }
}
