package com.paodemel.api.service;

import com.paodemel.api.dto.OrderDto;
import com.paodemel.api.model.OrderEntity;
import com.paodemel.api.repository.OrderRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

  private final OrderRepository orderRepository;

  public OrderService(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  public List<OrderDto> listar() {
    return orderRepository.findAll().stream()
        .map(this::toDto)
        .toList();
  }

  public OrderDto criar(OrderDto order) {
    String cliente = normalize(order.cliente());
    String massa = normalize(order.massa());
    String recheio = normalize(order.recheio());
    String dataEntrega = normalize(order.dataEntrega());

    if (cliente == null || massa == null || recheio == null || dataEntrega == null) {
      throw new IllegalArgumentException("Informe cliente, massa, recheio e data de entrega.");
    }

    String codigo = "#PM-" + (1051 + orderRepository.count());
    OrderEntity created = orderRepository.save(new OrderEntity(
        codigo,
        cliente,
        massa,
        recheio,
        dataEntrega,
        "Aguardando Producao"
    ));
    return toDto(created);
  }

  private String normalize(String value) {
    if (value == null) {
      return null;
    }

    String trimmed = value.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }

  private OrderDto toDto(OrderEntity order) {
    return new OrderDto(
        order.getCodigo(),
        order.getCliente(),
        order.getMassa(),
        order.getRecheio(),
        order.getDataEntrega(),
        order.getStatus()
    );
  }
}
