package com.conta.bancaria.correntista.servicos.adapter.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferenciaRequestDto(
        Long idCorrentistaOrigem,
        @NotNull(message = "Id correntista destino é obrigatório")
        Long idCorrentistaDestino,

        @NotNull(message = "Valor é obrigatório")
        BigDecimal valor
) {}
