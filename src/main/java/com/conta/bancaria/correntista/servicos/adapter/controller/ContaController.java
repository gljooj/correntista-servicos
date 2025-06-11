package com.conta.bancaria.correntista.servicos.adapter.controller;

import com.conta.bancaria.correntista.servicos.adapter.dto.*;
import com.conta.bancaria.correntista.servicos.core.domain.model.Correntista;
import com.conta.bancaria.correntista.servicos.core.service.CadastroService;
import com.conta.bancaria.correntista.servicos.core.service.CorrentistaService;
import com.conta.bancaria.correntista.servicos.core.usecase.ListrarTransferenciasUseCase;
import com.conta.bancaria.correntista.servicos.core.usecase.RealizarTransferenciaUseCase;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/correntistas")
@RequiredArgsConstructor
public class ContaController {

    private final CorrentistaService service;

    private final RealizarTransferenciaUseCase realizarTransferenciaUseCase;

    private final ListrarTransferenciasUseCase listrarTransferenciasUseCase;

    private final CorrentistaService correntistaService;

    private final CadastroService cadastroService;

    private final ModelMapper modelMapper;

    private static final Logger log = LoggerFactory.getLogger(ContaController.class);

    @GetMapping
    public ResponseEntity<Object> consultaCadastroFiltro(
            @RequestParam(name = "nome", required = false)String nome,
            @RequestParam(name = "idUsuario", required = false) Long idUsuario) {
        try {
            UsuarioDto usuario;
            if (idUsuario != null) {
                usuario = cadastroService.getById(idUsuario);
            } else {
                usuario = cadastroService.getByNome(nome);
            }

            if (usuario == null) {
                log.error("Usuario nao encontrado");
                return ResponseEntity.notFound().build();
            }

            Correntista correntista = correntistaService.getCorrentistaByUsuarioId(usuario.id());
            CorrentistaDto dto = modelMapper.map(correntista, CorrentistaDto.class);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            log.error("Erro interno ao procurar correntista", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{idCorrentista}")
    public ResponseEntity<Object> obterCorrentistaPorId(@PathVariable Long idCorrentista) {
        try {
            Correntista correntista = correntistaService.getCorrentistaById(idCorrentista);
            CorrentistaDto correntistaDto = modelMapper.map(correntista, CorrentistaDto.class);
            if (correntistaDto != null) {

                log.info("Correntista encontrado;");
                return ResponseEntity.ok(correntistaDto);
            } else {
                log.error("Correntista não encontrado;");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();

            log.error("Ocorreu um erro ao processar a solicitação.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro ao processar a solicitação.");
        }
    }


    @PostMapping("/{idCorrentista}/transferencias")
    public ResponseEntity<Object> realizarTransferencia(@PathVariable Long idCorrentista,
                                                        @RequestBody TransferenciaRequestDto dto) {

        try {

            log.info("Processando transferencia do correntistaOrigem " +
                    idCorrentista + " para o correntistaDestino: " + dto.idCorrentistaDestino() +
                    " de Valor: " + dto.valor());

            if (idCorrentista.equals(dto.idCorrentistaDestino())) {

                log.error("O idUsuario é igual ao usuario de envio na solicitação");
                return ResponseEntity.badRequest().body("O idUsuario é igual ao usuario de envio na solicitação");
            }

            TransferenciaResponseDto transferenciaRealizada = realizarTransferenciaUseCase.
                    execute(idCorrentista, dto);

            if (transferenciaRealizada != null) {

                log.info("Sucesso da transferencia do correntistaOrigem " +
                        idCorrentista + " para o correntistaDestino: " + dto.idCorrentistaDestino() +
                        " de Valor: " + dto.valor() + " ID transferencia: " + transferenciaRealizada.id());

                return ResponseEntity.ok(transferenciaRealizada);
            }
            log.info("Erro ao realizar a transferencia do correntistaOrigem " +
                    idCorrentista + " para o correntistaDestino: " + dto.idCorrentistaDestino() +
                    " de Valor: " + dto.valor());

            return ResponseEntity.badRequest().body("Não foi possível realizar a transferência.");
        } catch (Exception e) {
            e.printStackTrace();


            log.error("Ocorreu um erro ao processar a solicitação.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro ao processar a solicitação.");
        }
    }

    @GetMapping("/{idCorrentista}/transferencias")
    public ResponseEntity<Object> obterTransferencias(@PathVariable Long idCorrentista) {
        try {
            List<TransferenciaDto> transferencias = listrarTransferenciasUseCase.execute(idCorrentista);

            if (transferencias != null) {

                log.info("Transferencias encontradas");
                return ResponseEntity.ok(transferencias);
            } else {
                log.error("Transferencias não encontrada");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();

            log.error("Ocorreu um erro ao processar a solicitação.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro ao processar a solicitação.");
        }
    }

    @GetMapping("/{idCorrentista}/saldos")
    public ResponseEntity<Object> obtersaldo(@PathVariable Long idCorrentista) {
        try {
            BigDecimal saldo = correntistaService.consultaSaldoById(idCorrentista);
            SaldoResponseDto payload = new SaldoResponseDto(saldo);
            if (saldo != null) {

                log.info("saldo encontrado");
                return ResponseEntity.ok(payload);
            } else {
                log.error("Saldo não encontrada");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();

            log.error("Ocorreu um erro ao processar a solicitação.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro ao processar a solicitação.");
        }
    }


}
