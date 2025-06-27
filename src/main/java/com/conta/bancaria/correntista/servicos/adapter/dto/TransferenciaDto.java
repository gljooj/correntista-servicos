package com.conta.bancaria.correntista.servicos.adapter.dto;

import com.conta.bancaria.correntista.servicos.core.domain.model.StatusBacen;
import com.conta.bancaria.correntista.servicos.core.domain.model.StatusTransacao;
import java.math.BigDecimal;

public record TransferenciaDto(
    Long idCorrentistaOrigem,
    Long idCorrentistaDestino,
    BigDecimal valor,
    StatusTransacao statusTransacao,
    StatusBacen statusBacen){}