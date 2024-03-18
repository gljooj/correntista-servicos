package com.conta.bancaria.correntista.servicos.service;

import com.conta.bancaria.correntista.servicos.dto.CorrentistaDto;
import com.conta.bancaria.correntista.servicos.model.Correntista;
import com.conta.bancaria.correntista.servicos.repository.CorrentistaRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
public class CorrentistaService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CorrentistaRepository CorrentistaRepository;


    public BigDecimal consultaSaldoById(Long id) {
        Correntista correntista = CorrentistaRepository.getById(id);
        if (correntista == null) {
            throw new EntityNotFoundException();
        }

        return correntista.getSaldo();
    }

    public CorrentistaDto getCorrentistaById(Long id) {
        Correntista correntista = CorrentistaRepository.getById(id);

        return modelMapper.map(correntista, CorrentistaDto.class);
    }

    public CorrentistaDto getCorrentistaByUsuarioId(Long id) {

        Correntista correntista = CorrentistaRepository.findByIdUsuario(id);
        return modelMapper.map(correntista, CorrentistaDto.class);
    }


}
