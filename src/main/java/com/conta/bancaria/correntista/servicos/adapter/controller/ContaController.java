package com.conta.bancaria.correntista.servicos.adapter.controller;

import com.conta.bancaria.correntista.servicos.adapter.dto.*;
import com.conta.bancaria.correntista.servicos.core.usecase.CadastroUseCase;
import com.conta.bancaria.correntista.servicos.core.usecase.CorrentistaUseCase;
import com.conta.bancaria.correntista.servicos.core.usecase.ListrarTransferenciasUseCase;
import com.conta.bancaria.correntista.servicos.core.usecase.RealizarTransferenciaUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/correntistas")
public class ContaController {

    @Autowired
    private CorrentistaUseCase service;

    @Autowired
    private RealizarTransferenciaUseCase realizarTransferenciaUseCase;


    @Autowired
    private ListrarTransferenciasUseCase listrarTransferenciasUseCase;

    @Autowired
    private CorrentistaUseCase correntistaUseCase;


    @Autowired
    private CadastroUseCase cadastroUseCase;


    private static final Logger log = LoggerFactory.getLogger(ContaController.class);

    @GetMapping
    public ResponseEntity<Object> consultaCadastroFiltro(
            @RequestParam(name = "nome", required = false)String nome,
            @RequestParam(name = "idUsuario", required = false) Long idUsuario) {
        try {
            UsuarioDto usuario;
            if (idUsuario != null) {
                usuario = cadastroUseCase.getById(idUsuario);
            } else {
                usuario = cadastroUseCase.getByNome(nome);
            }

            if (usuario == null) {
                log.error("Usuario nao encontrado");
                return ResponseEntity.notFound().build();
            }

            CorrentistaDto correntista = correntistaUseCase.getCorrentistaByUsuarioId(usuario.getId());
            return ResponseEntity.ok(correntista);
        } catch (Exception e) {
            log.error("Erro interno ao procurar correntista", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{idCorrentista}")
    public ResponseEntity<Object> obterCorrentistaPorId(@PathVariable Long idCorrentista) {
        try {
            CorrentistaDto correntistaDto = correntistaUseCase.getCorrentistaById(idCorrentista);

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
                    idCorrentista + " para o correntistaDestino: " + dto.getIdCorrentistaDestino() +
                    " de Valor: " + dto.getValor());

            if (idCorrentista.equals(dto.getIdCorrentistaDestino())) {

                log.error("O idUsuario é igual ao usuario de envio na solicitação");
                return ResponseEntity.badRequest().body("O idUsuario é igual ao usuario de envio na solicitação");
            }

            TransferenciaResponseDto transferenciaRealizada = realizarTransferenciaUseCase.
                    execute(idCorrentista, dto);

            if (transferenciaRealizada != null) {

                log.info("Sucesso da transferencia do correntistaOrigem " +
                        idCorrentista + " para o correntistaDestino: " + dto.getIdCorrentistaDestino() +
                        " de Valor: " + dto.getValor() + " ID transferencia: " + transferenciaRealizada.getId());

                return ResponseEntity.ok(transferenciaRealizada);
            }
            log.info("Erro ao realizar a transferencia do correntistaOrigem " +
                    idCorrentista + " para o correntistaDestino: " + dto.getIdCorrentistaDestino() +
                    " de Valor: " + dto.getValor());

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
            BigDecimal saldo = correntistaUseCase.consultaSaldoById(idCorrentista);

            if (saldo != null) {

                log.info("saldo encontrado");
                return ResponseEntity.ok(saldo);
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
