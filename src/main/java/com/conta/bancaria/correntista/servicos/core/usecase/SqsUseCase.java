package com.conta.bancaria.correntista.servicos.core.usecase;

import org.springframework.stereotype.Service;

@Service
public class SqsUseCase {


    public void send(String message) {
        String queueName = "retry-transferencia";
        System.out.println("Enviando para fila de retry");
    }
}
