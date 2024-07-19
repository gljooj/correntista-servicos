package com.conta.bancaria.correntista.servicos.adapter.dto;


import com.conta.bancaria.correntista.servicos.core.domain.model.StatusConta;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CorrentistaDto {
    private Long id;
    private Long idUsuario;
    private StatusConta statusConta;
    private BigDecimal saldo;
    private BigDecimal limiteDiario;
}

