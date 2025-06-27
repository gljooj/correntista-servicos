package com.conta.bancaria.correntista.servicos.core.service;

import com.conta.bancaria.correntista.servicos.adapter.dto.TransferenciaDto;
import com.conta.bancaria.correntista.servicos.adapter.dto.TransferenciaResponseDto;
import com.conta.bancaria.correntista.servicos.adapter.mapper.TransferenciaMapper;
import com.conta.bancaria.correntista.servicos.core.domain.model.Transferencia;
import com.conta.bancaria.correntista.servicos.framework.repository.TransferenciaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransacaoService {


    private static final Logger log = LoggerFactory.getLogger(TransacaoService.class);

    private final TransferenciaRepository transferenciaRepository;

    @Autowired
    private final TransferenciaMapper transferenciaMapper;

    @Transactional
    public TransferenciaResponseDto salvarTransacao(TransferenciaDto transferenciaDto) {

        Transferencia transferencia = transferenciaMapper.fromDto(transferenciaDto);

        Transferencia response = transferenciaRepository.save(transferencia);
        log.info("Historico Transacao salvo com sucesso");
        return transferenciaMapper.toResponseDto(response);
    }

    public List<TransferenciaDto> listarTransferencias(Long idCorrentista) {
        List<Transferencia> transferencias = transferenciaRepository.findByCorrentistaId(idCorrentista);
        return transferencias.stream()
                .map(transferenciaMapper::toDto)
                .collect(Collectors.toList());
    }


}
