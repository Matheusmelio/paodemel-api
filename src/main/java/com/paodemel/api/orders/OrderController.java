package com.paodemel.api.orders;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/encomendas")
public class OrderController {

  private final List<OrderDto> orders = new ArrayList<>(List.of(
      new OrderDto("#PM-1048", "Ana Clara", "Chocolate", "Ninho com morango", "Hoje, 15:00", "Aguardando Producao"),
      new OrderDto("#PM-1049", "Roberto Lima", "Baunilha", "Doce de leite", "Hoje, 17:30", "Em Producao"),
      new OrderDto("#PM-1050", "Marina Souza", "Red velvet", "Cream cheese", "Amanha, 10:00", "Pronto")
  ));

  @GetMapping
  public List<OrderDto> listar() {
    return orders;
  }

  @PostMapping
  public OrderDto criar(@RequestBody OrderDto order) {
    OrderDto created = new OrderDto(
        "#PM-" + (1051 + orders.size()),
        order.cliente(),
        order.massa(),
        order.recheio(),
        order.dataEntrega(),
        "Aguardando Producao"
    );
    orders.add(created);
    return created;
  }
}
