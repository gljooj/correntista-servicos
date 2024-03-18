package com.conta.bancaria.correntista.servicos.dto;

import com.conta.bancaria.correntista.servicos.model.StatusBacen;
import com.conta.bancaria.correntista.servicos.model.StatusTransacao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransferenciaDto {
    private Long idCorrentistaOrigem;
    private Long idCorrentistaDestino;
    private BigDecimal valor;
    private StatusTransacao statusTransacao;
    private StatusBacen statusBacen;
}