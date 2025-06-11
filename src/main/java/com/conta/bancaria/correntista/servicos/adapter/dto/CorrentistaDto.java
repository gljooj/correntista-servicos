package com.conta.bancaria.correntista.servicos.adapter.dto;

import com.conta.bancaria.correntista.servicos.core.domain.model.StatusConta;

import java.math.BigDecimal;

public record CorrentistaDto(
        Long id,
        Long idUsuario,
        StatusConta statusConta,
        BigDecimal saldo,
        BigDecimal limiteDiario
) {}

