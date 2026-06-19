package com.paodemel.api.repository;

import com.paodemel.api.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

  long countByStatus(String status);
}
