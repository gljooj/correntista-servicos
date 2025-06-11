package com.conta.bancaria.correntista.servicos.core.usecase;

import com.conta.bancaria.correntista.servicos.adapter.dto.CorrentistaDto;
import com.conta.bancaria.correntista.servicos.adapter.dto.TransferenciaDto;
import com.conta.bancaria.correntista.servicos.adapter.dto.TransferenciaRequestDto;
import com.conta.bancaria.correntista.servicos.adapter.dto.TransferenciaResponseDto;
import com.conta.bancaria.correntista.servicos.core.domain.model.*;
import com.conta.bancaria.correntista.servicos.core.exception.ContaInativaException;
import com.conta.bancaria.correntista.servicos.core.exception.CorrentistaNotFoundException;
import com.conta.bancaria.correntista.servicos.core.exception.SaldoInsuficienteException;
import com.conta.bancaria.correntista.servicos.core.service.CorrentistaService;
import com.conta.bancaria.correntista.servicos.core.service.TransacaoService;
import com.conta.bancaria.correntista.servicos.framework.repository.TransferenciaRepository;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class RealizarTransferenciaUseCase {

    private final CorrentistaService correntistaService;

    private final TransferenciaRepository transferenciaRepository;

    private final TransacaoService transacaoService;


    private final ModelMapper modelMapper;

    private final NotificaBacenUseCase notificaBacenUseCase;

    private static final Logger log = LoggerFactory.getLogger(RealizarTransferenciaUseCase.class);

    public TransferenciaResponseDto execute(Long idCorrentista, TransferenciaRequestDto dto) throws CorrentistaNotFoundException, ContaInativaException {
        Correntista correntistaOrigem = correntistaService.getCorrentistaById(idCorrentista);
        Correntista correntistaDestino = correntistaService.getCorrentistaById(dto.idCorrentistaDestino());

        BigDecimal saldoOrigem = correntistaOrigem.getSaldo();
        BigDecimal limiteDiario = correntistaOrigem.getLimiteDiario();

        if (saldoOrigem.compareTo(dto.valor()) < 0) {
            log.error("Saldo insuficiente para realizar a transferência");
            throw new SaldoInsuficienteException("Saldo insuficiente para realizar a transferência");
        }

        if (limiteDiario.compareTo(dto.valor()) < 0) {
            log.error("Voce bateu o limite de transferencia diaria");
            throw new SaldoInsuficienteException("Voce bateu o limite de transferencia diaria");
        }

        BigDecimal novoSaldoOrigem = saldoOrigem.subtract(dto.valor());
        BigDecimal novoSaldoDiario = limiteDiario.subtract(dto.valor());


        BigDecimal novoSaldoDestino = new BigDecimal(String.valueOf(correntistaOrigem.getSaldo().add(dto.valor())));

        correntistaOrigem.setSaldo(novoSaldoOrigem);
        correntistaOrigem.setLimiteDiario(novoSaldoDiario);

        correntistaDestino.setSaldo(novoSaldoDestino);

        correntistaService.save(correntistaOrigem);
        correntistaService.save(correntistaDestino);

        TransferenciaDto transferencia = new TransferenciaDto(
                correntistaOrigem.getId(),
                correntistaDestino.getId(),
                dto.valor(), StatusTransacao.SUCESSO, StatusBacen.EM_PROGRESSO);

        System.out.println("transacao "+ transferencia);

        TransferenciaResponseDto transferenciaResponse = this.transacaoService.salvarTransacao(transferencia);

        log.info("Sucesso ao realizar transferencia");
        return this.notificaBacenUseCase.execute(transferenciaResponse);
    }

}
