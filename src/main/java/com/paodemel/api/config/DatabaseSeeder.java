package com.paodemel.api.config;

import com.paodemel.api.model.Cliente;
import com.paodemel.api.model.Fornada;
import com.paodemel.api.model.Insumo;
import com.paodemel.api.model.OrderEntity;
import com.paodemel.api.model.Perfil;
import com.paodemel.api.model.Usuario;
import com.paodemel.api.model.Venda;
import com.paodemel.api.repository.ClienteRepository;
import com.paodemel.api.repository.FornadaRepository;
import com.paodemel.api.repository.InsumoRepository;
import com.paodemel.api.repository.OrderRepository;
import com.paodemel.api.repository.UsuarioRepository;
import com.paodemel.api.repository.VendaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

  private final UsuarioRepository usuarioRepository;
  private final ClienteRepository clienteRepository;
  private final OrderRepository orderRepository;
  private final InsumoRepository insumoRepository;
  private final FornadaRepository fornadaRepository;
  private final VendaRepository vendaRepository;

  public DatabaseSeeder(
      UsuarioRepository usuarioRepository,
      ClienteRepository clienteRepository,
      OrderRepository orderRepository,
      InsumoRepository insumoRepository,
      FornadaRepository fornadaRepository,
      VendaRepository vendaRepository
  ) {
    this.usuarioRepository = usuarioRepository;
    this.clienteRepository = clienteRepository;
    this.orderRepository = orderRepository;
    this.insumoRepository = insumoRepository;
    this.fornadaRepository = fornadaRepository;
    this.vendaRepository = vendaRepository;
  }

  @Override
  public void run(String... args) {
    seedUsuarios();
    seedEncomendas();
    seedEstoque();
    seedFornadas();
    seedVendas();
  }

  private void seedUsuarios() {
    if (usuarioRepository.count() > 0) {
      return;
    }

    String cpfCliente = "12345678901";
    if (!clienteRepository.existsById(cpfCliente)) {
      clienteRepository.save(new Cliente(
          cpfCliente,
          "Cliente Final",
          LocalDate.of(1990, 1, 1),
          "F",
          "Rua das Flores",
          "100",
          "Apto 1",
          "00000000",
          "Centro",
          "Sao Paulo",
          "SP",
          "(11) 96666-0000",
          "cliente@email.com"
      ));
    }

    usuarioRepository.save(new Usuario("Gerente Pao de Mel", "(11) 99999-0000", "gerente@paodemel.com", "12345678", Perfil.GERENTE, "GER-001"));
    usuarioRepository.save(new Usuario("Atendente Balcao", "(11) 98888-0000", "atendente@paodemel.com", "12345678", Perfil.ATENDENTE, "ATE-001"));
    usuarioRepository.save(new Usuario("Confeiteiro Principal", "(11) 97777-0000", "confeiteiro@paodemel.com", "12345678", Perfil.CONFEITEIRO, "CON-001"));
    usuarioRepository.save(new Usuario("Cliente Final", "(11) 96666-0000", "cliente@email.com", "12345678", Perfil.CLIENTE, null, cpfCliente));
  }

  private void seedEncomendas() {
    if (orderRepository.count() > 0) {
      return;
    }

    orderRepository.save(new OrderEntity("#PM-1048", "Ana Clara", "Chocolate", "Ninho com morango", "Hoje, 15:00", "Aguardando Producao"));
    orderRepository.save(new OrderEntity("#PM-1049", "Roberto Lima", "Baunilha", "Doce de leite", "Hoje, 17:30", "Em Producao"));
    orderRepository.save(new OrderEntity("#PM-1050", "Marina Souza", "Red velvet", "Cream cheese", "Amanha, 10:00", "Pronto"));
  }

  private void seedEstoque() {
    if (insumoRepository.count() > 0) {
      return;
    }

    insumoRepository.save(new Insumo("Farinha de trigo", 42, "kg", 20));
    insumoRepository.save(new Insumo("Chocolate 70%", 4, "kg", 8));
    insumoRepository.save(new Insumo("Creme de leite", 10, "l", 12));
    insumoRepository.save(new Insumo("Acucar refinado", 35, "kg", 18));
  }

  private void seedFornadas() {
    if (fornadaRepository.count() > 0) {
      return;
    }

    fornadaRepository.save(new Fornada("Pao frances", 120, "08:30"));
    fornadaRepository.save(new Fornada("Croissant", 36, "09:00"));
  }

  private void seedVendas() {
    if (vendaRepository.count() > 0) {
      return;
    }

    vendaRepository.save(new Venda(new BigDecimal("8420.00")));
  }
}
