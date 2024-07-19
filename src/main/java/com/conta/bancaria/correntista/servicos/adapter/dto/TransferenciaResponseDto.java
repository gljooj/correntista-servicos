package com.conta.bancaria.correntista.servicos.adapter.dto;

import com.conta.bancaria.correntista.servicos.core.domain.model.StatusBacen;
import com.conta.bancaria.correntista.servicos.core.domain.model.StatusTransacao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransferenciaResponseDto {
    private Long id;
    private Long idCorrentistaOrigem;
    private Long idCorrentistaDestino;
    private BigDecimal valor;
    private StatusTransacao statusTransacao;
    private StatusBacen statusBacen;
}