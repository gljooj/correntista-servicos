package com.conta.bancaria.correntista.servicos.adapter.dto;

import java.math.BigDecimal;

public record TransferenciaRequestDto(
        Long idCorrentistaOrigem,
        Long idCorrentistaDestino,
        BigDecimal valor
) {}
