package com.paodemel.api.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record RegisterRequest(
    @NotBlank String nome,
    @NotBlank String telefone,
    @Email @NotBlank String email,
    @NotNull Perfil perfil,
    @Size(min = 8) String senha,
    @Size(min = 8) String confirmarSenha,
    String codigoInterno,
    String cpfCliente,
    LocalDate nascimento,
    String sexo,
    String logradouro,
    String numero,
    String complemento,
    String cep,
    String bairro,
    String cidade,
    String estado
) {
}
