package com.paodemel.api.controller;

import com.paodemel.api.dto.OrderDto;
import com.paodemel.api.service.OrderService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/encomendas")
public class OrderController {

  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @GetMapping
  public List<OrderDto> listar() {
    return orderService.listar();
  }

  @PostMapping
  public OrderDto criar(@RequestBody OrderDto order) {
    return orderService.criar(order);
  }
}
