package com.conta.bancaria.correntista.servicos.repository;

import com.conta.bancaria.correntista.servicos.dto.TransferenciaResponseDto;
import org.springframework.stereotype.Repository;

@Repository
public class BacenRepository {

    public boolean post(TransferenciaResponseDto transferencia){
        System.out.println("Postado");
        return true;
    }
}
