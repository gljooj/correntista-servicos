package com.conta.bancaria.correntista.servicos.adapter.dto;

public record UsuarioDto(
        Long id,
        String nome,
        String email,
        String telefone,
        String status
) {}