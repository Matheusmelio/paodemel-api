package com.paodemel.api.operations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InsumoRepository extends JpaRepository<Insumo, Long> {

  @Query("select count(i) from Insumo i where i.quantidadeAtual <= i.estoqueMinimo")
  long countComEstoqueBaixo();
}
