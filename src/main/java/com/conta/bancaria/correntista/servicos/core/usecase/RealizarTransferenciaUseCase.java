package com.conta.bancaria.correntista.servicos.core.usecase;

import com.conta.bancaria.correntista.servicos.adapter.dto.TransferenciaDto;
import com.conta.bancaria.correntista.servicos.adapter.dto.TransferenciaRequestDto;
import com.conta.bancaria.correntista.servicos.adapter.dto.TransferenciaResponseDto;
import com.conta.bancaria.correntista.servicos.core.domain.model.*;
import com.conta.bancaria.correntista.servicos.core.exception.ContaInativaException;
import com.conta.bancaria.correntista.servicos.core.exception.CorrentistaNotFoundException;
import com.conta.bancaria.correntista.servicos.core.exception.SaldoInsuficienteException;
import com.conta.bancaria.correntista.servicos.core.service.TransacaoService;
import com.conta.bancaria.correntista.servicos.framework.repository.CorrentistaRepository;
import com.conta.bancaria.correntista.servicos.framework.repository.TransferenciaRepository;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class RealizarTransferenciaUseCase {

    @Autowired
    private CorrentistaRepository correntistaRepository;
    @Autowired
    private TransferenciaRepository transferenciaRepository;

    @Autowired
    private TransacaoService transacaoService;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BacenUseCase bacenUseCase;

    private static final Logger log = LoggerFactory.getLogger(RealizarTransferenciaUseCase.class);


    public TransferenciaResponseDto execute(Long idCorrentista, TransferenciaRequestDto dto) throws CorrentistaNotFoundException, ContaInativaException {
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

        TransferenciaResponseDto transferenciaResponse = this.transacaoService.salvarTransacao(transferencia);

        log.info("Sucesso ao realizar transferencia");
        return this.bacenUseCase.notificaBacen(transferenciaResponse);
    }

}
