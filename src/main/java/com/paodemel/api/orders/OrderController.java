package com.paodemel.api.orders;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/encomendas")
public class OrderController {

  private final OrderRepository orderRepository;

  public OrderController(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  @GetMapping
  public List<OrderDto> listar() {
    return orderRepository.findAll().stream()
        .map(this::toDto)
        .toList();
  }

  @PostMapping
  public OrderDto criar(@RequestBody OrderDto order) {
    OrderEntity created = orderRepository.save(new OrderEntity(
        "#PM-" + (1051 + orderRepository.count()),
        order.cliente(),
        order.massa(),
        order.recheio(),
        order.dataEntrega(),
        "Aguardando Producao"
    ));
    return toDto(created);
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
