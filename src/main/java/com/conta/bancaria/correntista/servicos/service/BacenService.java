package com.conta.bancaria.correntista.servicos.service;

import com.conta.bancaria.correntista.servicos.dto.TransferenciaResponseDto;
import com.conta.bancaria.correntista.servicos.model.StatusBacen;
import com.conta.bancaria.correntista.servicos.model.Transferencia;
import com.conta.bancaria.correntista.servicos.repository.BacenRepository;
import com.conta.bancaria.correntista.servicos.repository.TransferenciaRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class BacenService {

    @Autowired
    private BacenRepository bacenRepository;
    @Autowired
    private TransferenciaRepository transferenciaRepository;
    @Autowired
    private SqsService sqsService;

    @Autowired
    private final ModelMapper modelMapper;

    private static final Logger log = LoggerFactory.getLogger(BacenService.class);


    public TransferenciaResponseDto notificaBacen(TransferenciaResponseDto dto){
        try{
            boolean notifyBacen = bacenRepository.post(dto);

            if(!notifyBacen){
                log.error("Nao foi possivel enviar ao bacen, enviando a fila para retry");
                sqsService.send(dto.toString());
                return this.atualizaStatusBacen(dto, StatusBacen.FALHA);
            }

            log.info("Sucesso ao enviar para o bacen");


        } catch (Exception e) {
            this.atualizaStatusBacen(dto, StatusBacen.FALHA);
            log.error("Erro durante o processo de notificacao ao Bacen");
            sqsService.send(dto.toString());
        }

        return this.atualizaStatusBacen(dto, StatusBacen.SUCESSO);
    }

    @Transactional
    public TransferenciaResponseDto atualizaStatusBacen(TransferenciaResponseDto transferenciaResponseDto, StatusBacen statusBacen){
        Transferencia transferencia = transferenciaRepository.getById(transferenciaResponseDto.getId());

        transferencia.setStatusBacen(statusBacen);
        transferenciaRepository.save(transferencia);

        Transferencia response = modelMapper.map(transferencia, Transferencia.class);

        return modelMapper.map(response, TransferenciaResponseDto.class);
    }
}
