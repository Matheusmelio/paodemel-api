package com.paodemel.api.repository;

import com.paodemel.api.model.Fornada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FornadaRepository extends JpaRepository<Fornada, Long> {

  @Query("select coalesce(sum(f.quantidadeProduzida), 0) from Fornada f")
  long totalPaesProduzidos();
}
