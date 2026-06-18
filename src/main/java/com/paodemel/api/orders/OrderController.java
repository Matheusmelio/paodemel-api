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
    String cliente = normalize(order.cliente());
    String massa = normalize(order.massa());
    String recheio = normalize(order.recheio());
    String dataEntrega = normalize(order.dataEntrega());

    if (cliente == null || massa == null || recheio == null || dataEntrega == null) {
      throw new IllegalArgumentException("Informe cliente, massa, recheio e data de entrega.");
    }

    String codigo = "#PM-" + (1050 + orderRepository.count() + 1);
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
