package com.conta.bancaria.correntista.servicos.adapter.dto;

import com.conta.bancaria.correntista.servicos.core.domain.model.StatusBacen;
import com.conta.bancaria.correntista.servicos.core.domain.model.StatusTransacao;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

public record TransferenciaDto(
    Long idCorrentistaOrigem,
    Long idCorrentistaDestino,
    BigDecimal valor,
    StatusTransacao statusTransacao,
    StatusBacen statusBacen){}