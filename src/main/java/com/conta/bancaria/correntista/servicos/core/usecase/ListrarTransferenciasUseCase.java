package com.conta.bancaria.correntista.servicos.core.usecase;

import com.conta.bancaria.correntista.servicos.adapter.dto.TransferenciaDto;
import com.conta.bancaria.correntista.servicos.adapter.mapper.TransferenciaMapper;
import com.conta.bancaria.correntista.servicos.core.domain.model.Transferencia;
import com.conta.bancaria.correntista.servicos.framework.repository.TransferenciaRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListrarTransferenciasUseCase {

    private final TransferenciaRepository transferenciaRepository;

    @Autowired
    private final TransferenciaMapper transferenciaMapper;

    public List<TransferenciaDto> execute(Long idCorrentista) {
        List<Transferencia> transferencias = transferenciaRepository.findByCorrentistaId(idCorrentista);
        return transferencias.stream()
                .map(transferenciaMapper::toDto)
                .collect(Collectors.toList());
    }
}
