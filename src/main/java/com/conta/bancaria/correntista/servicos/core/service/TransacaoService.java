package com.conta.bancaria.correntista.servicos.core.service;

import com.conta.bancaria.correntista.servicos.adapter.dto.TransferenciaDto;
import com.conta.bancaria.correntista.servicos.adapter.dto.TransferenciaResponseDto;
import com.conta.bancaria.correntista.servicos.core.domain.model.Transferencia;
import com.conta.bancaria.correntista.servicos.framework.repository.TransferenciaRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

public class TransacaoService {


    private static final Logger log = LoggerFactory.getLogger(TransacaoService.class);

    @Autowired
    private TransferenciaRepository transferenciaRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public TransferenciaResponseDto salvarTransacao(TransferenciaDto transferenciaDto) {

        Transferencia transferencia = modelMapper.map(transferenciaDto, Transferencia.class);

        Transferencia response = transferenciaRepository.save(transferencia);
        log.info("Historico Transacao salvo com sucesso");
        return modelMapper.map(response, TransferenciaResponseDto.class);
    }

    public List<TransferenciaDto> listarTransferencias(Long idCorrentista) {
        List<Transferencia> transferencias = transferenciaRepository.findByCorrentistaId(idCorrentista);
        return transferencias.stream()
                .map(p -> modelMapper.map(p, TransferenciaDto.class))
                .collect(Collectors.toList());
    }


}
