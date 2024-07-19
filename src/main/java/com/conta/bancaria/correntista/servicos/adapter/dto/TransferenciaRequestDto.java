package com.conta.bancaria.correntista.servicos.adapter.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransferenciaRequestDto {
    private Long idCorrentistaOrigem;
    private Long idCorrentistaDestino;
    private BigDecimal valor;
}