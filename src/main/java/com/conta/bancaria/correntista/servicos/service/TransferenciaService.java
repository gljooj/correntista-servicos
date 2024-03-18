package com.conta.bancaria.correntista.servicos.service;

import com.conta.bancaria.correntista.servicos.dto.TransferenciaDto;
import com.conta.bancaria.correntista.servicos.dto.TransferenciaRequestDto;
import com.conta.bancaria.correntista.servicos.dto.TransferenciaResponseDto;
import com.conta.bancaria.correntista.servicos.exception.ContaInativaException;
import com.conta.bancaria.correntista.servicos.exception.CorrentistaNotFoundException;
import com.conta.bancaria.correntista.servicos.exception.SaldoInsuficienteException;
import com.conta.bancaria.correntista.servicos.model.*;
import com.conta.bancaria.correntista.servicos.repository.CorrentistaRepository;
import com.conta.bancaria.correntista.servicos.repository.TransferenciaRepository;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransferenciaService {

    @Autowired
    private CorrentistaRepository correntistaRepository;
    @Autowired
    private TransferenciaRepository transferenciaRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BacenService bacenService;

    private static final Logger log = LoggerFactory.getLogger(TransferenciaService.class);



    public TransferenciaService() {

    }

    public TransferenciaResponseDto realizarTransferencia(Long idCorrentista, TransferenciaRequestDto dto) throws CorrentistaNotFoundException, ContaInativaException {
        Correntista correntistaOrigem = correntistaRepository.getById(idCorrentista);
        Correntista correntistaDestino = correntistaRepository.getById(dto.getIdCorrentistaDestino());

        if (correntistaOrigem == null || correntistaDestino == null) {

            log.error("Correntista não encontrado");
            throw new CorrentistaNotFoundException("Correntista não encontrado");
        }

        if (correntistaOrigem.getStatusConta() == StatusConta.INATIVO){
            log.error("Conta INATIVA");
            throw new ContaInativaException("Conta INATIVA");
        }

        BigDecimal saldoOrigem = correntistaOrigem.getSaldo();
        BigDecimal limiteDiario = correntistaOrigem.getLimiteDiario();

        if (saldoOrigem.compareTo(dto.getValor()) < 0) {
            log.error("Saldo insuficiente para realizar a transferência");
            throw new SaldoInsuficienteException("Saldo insuficiente para realizar a transferência");
        }

        if (limiteDiario.compareTo(dto.getValor()) < 0) {
            log.error("Voce bateu o limite de transferencia diaria");
            throw new SaldoInsuficienteException("Voce bateu o limite de transferencia diaria");
        }

        BigDecimal novoSaldoOrigem = saldoOrigem.subtract(dto.getValor());
        BigDecimal novoSaldoDiario = limiteDiario.subtract(dto.getValor());


        BigDecimal novoSaldoDestino = new BigDecimal(String.valueOf(correntistaOrigem.getSaldo().add(dto.getValor())));

        correntistaOrigem.setSaldo(novoSaldoOrigem);
        correntistaOrigem.setLimiteDiario(novoSaldoDiario);

        correntistaDestino.setSaldo(novoSaldoDestino);

        correntistaRepository.save(correntistaOrigem);
        correntistaRepository.save(correntistaDestino);

        TransferenciaDto transferencia = new TransferenciaDto(
                correntistaOrigem.getId(),
                correntistaDestino.getId(),
                dto.getValor(), StatusTransacao.SUCESSO, StatusBacen.EM_PROGRESSO);

        System.out.println("transacao "+ transferencia);

        TransferenciaResponseDto transferenciaResponse = this.salvarTransacao(transferencia);

        log.info("Sucesso ao realizar transferencia");
        return this.bacenService.notificaBacen(transferenciaResponse);
    }

    @Transactional
    public TransferenciaResponseDto salvarTransacao(TransferenciaDto transferenciaDto) {

        Transferencia transferencia = modelMapper.map(transferenciaDto, Transferencia.class);

        Transferencia response = transferenciaRepository.save(transferencia);
        log.info("Historico Transacao salvo com sucesso");
        return modelMapper.map(response, TransferenciaResponseDto.class);
    }

    public List<TransferenciaDto> listarTransferencias(Long idCorrentista) {
        List<Transferencia> transferencias = transferenciaRepository.findByCorrentistaId(idCorrentista);
        return convertToDtoList(transferencias);
    }

    private List<TransferenciaDto> convertToDtoList(List<Transferencia> transferencias) {
        return transferencias.stream()
                .map(p -> modelMapper.map(p, TransferenciaDto.class))
                .collect(Collectors.toList());
    }


}

