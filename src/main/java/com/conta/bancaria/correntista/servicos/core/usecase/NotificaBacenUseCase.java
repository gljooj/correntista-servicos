package com.conta.bancaria.correntista.servicos.core.usecase;

import com.conta.bancaria.correntista.servicos.adapter.dto.TransferenciaResponseDto;
import com.conta.bancaria.correntista.servicos.core.domain.model.StatusBacen;
import com.conta.bancaria.correntista.servicos.core.domain.model.Transferencia;
import com.conta.bancaria.correntista.servicos.framework.repository.BacenRepository;
import com.conta.bancaria.correntista.servicos.framework.repository.TransferenciaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class NotificaBacenUseCase {

    private final BacenRepository bacenRepository;
    private final TransferenciaRepository transferenciaRepository;
    private final SqsUseCase sqsUseCase;

    private final ModelMapper modelMapper;

    private static final Logger log = LoggerFactory.getLogger(NotificaBacenUseCase.class);


    public TransferenciaResponseDto execute(TransferenciaResponseDto dto){
        try{
            boolean notifyBacen = bacenRepository.post(dto);

            if(!notifyBacen){
                log.error("Nao foi possivel enviar ao bacen, enviando a fila para retry");
                sqsUseCase.send(dto.toString());
                return this.atualizaStatusBacen(dto, StatusBacen.FALHA);
            }

            log.info("Sucesso ao enviar para o bacen");


        } catch (Exception e) {
            this.atualizaStatusBacen(dto, StatusBacen.FALHA);
            log.error("Erro durante o processo de notificacao ao Bacen");
            sqsUseCase.send(dto.toString());
        }

        return this.atualizaStatusBacen(dto, StatusBacen.SUCESSO);
    }

    @Transactional
    public TransferenciaResponseDto atualizaStatusBacen(TransferenciaResponseDto transferenciaResponseDto, StatusBacen statusBacen) {
        Transferencia transferencia = transferenciaRepository.findById(transferenciaResponseDto.id())
                .orElseThrow(() -> new EntityNotFoundException("Transferência com ID "
                        + transferenciaResponseDto.id() + " não encontrada."));

        transferencia.setStatusBacen(statusBacen);

        return modelMapper.map(transferencia, TransferenciaResponseDto.class);
    }
}
