package com.conta.bancaria.correntista.servicos.core.exception;

public class SaldoInsuficienteException extends RuntimeException {

    public SaldoInsuficienteException(String message) {
        super(message);
    }
}
