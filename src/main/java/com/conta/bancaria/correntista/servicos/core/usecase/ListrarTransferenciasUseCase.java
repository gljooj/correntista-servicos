package com.conta.bancaria.correntista.servicos.core.usecase;

import com.conta.bancaria.correntista.servicos.adapter.dto.TransferenciaDto;
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

    private final ModelMapper modelMapper;

    public List<TransferenciaDto> execute(Long idCorrentista) {
        List<Transferencia> transferencias = transferenciaRepository.findByCorrentistaId(idCorrentista);
        return transferencias.stream()
                .map(p -> modelMapper.map(p, TransferenciaDto.class))
                .collect(Collectors.toList());
    }
}
