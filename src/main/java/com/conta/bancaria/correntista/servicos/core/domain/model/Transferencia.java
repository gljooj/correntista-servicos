package com.conta.bancaria.correntista.servicos.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transferencias")
@Getter
@Setter
@AllArgsConstructor
public class Transferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idCorrentistaOrigem;
    private Long idCorrentistaDestino;

    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    private StatusBacen statusBacen;

    @Enumerated(EnumType.STRING)
    private StatusTransacao statusTransacao;


    private LocalDateTime dataTransferencia;

    public Transferencia() {
        this.dataTransferencia = LocalDateTime.now();
    }
}

