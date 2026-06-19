package com.paodemel.api.repository;

import com.paodemel.api.model.Venda;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VendaRepository extends JpaRepository<Venda, Long> {

  @Query("select coalesce(sum(v.total), 0) from Venda v")
  BigDecimal receitaTotal();
}
