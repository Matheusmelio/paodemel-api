package com.paodemel.api.repository;

import com.paodemel.api.model.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InsumoRepository extends JpaRepository<Insumo, Long> {

  @Query("select count(i) from Insumo i where i.quantidadeAtual <= i.estoqueMinimo")
  long countComEstoqueBaixo();

  @Query("select count(i) from Insumo i where i.quantidadeAtual <= i.estoqueMinimo * 0.5")
  long countComEstoqueCritico();
}
