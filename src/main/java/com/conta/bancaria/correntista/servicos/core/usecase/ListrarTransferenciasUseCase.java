package com.conta.bancaria.correntista.servicos.core.usecase;

import com.conta.bancaria.correntista.servicos.adapter.dto.TransferenciaDto;
import com.conta.bancaria.correntista.servicos.core.domain.model.Transferencia;
import com.conta.bancaria.correntista.servicos.framework.repository.TransferenciaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class ListrarTransferenciasUseCase {

    @Autowired
    private TransferenciaRepository transferenciaRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<TransferenciaDto> execute(Long idCorrentista) {
        List<Transferencia> transferencias = transferenciaRepository.findByCorrentistaId(idCorrentista);
        return transferencias.stream()
                .map(p -> modelMapper.map(p, TransferenciaDto.class))
                .collect(Collectors.toList());
    }
}
