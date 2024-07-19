package com.conta.bancaria.correntista.servicos.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "correntistas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Correntista {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idUsuario;

    @Enumerated(EnumType.STRING)
    private StatusConta statusConta;

    private BigDecimal saldo;
    private BigDecimal limiteDiario;

}
