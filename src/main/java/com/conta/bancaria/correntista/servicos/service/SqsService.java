package com.conta.bancaria.correntista.servicos.service;

import org.springframework.stereotype.Service;

@Service
public class SqsService {


    public void send(String message) {
        String queueName = "retry-transferencia";
        System.out.println("Enviando para fila de retry");
    }
}
