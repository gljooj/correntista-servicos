package controller;

import com.conta.bancaria.correntista.servicos.adapter.controller.ContaController;
import com.conta.bancaria.correntista.servicos.adapter.dto.*;
import com.conta.bancaria.correntista.servicos.core.domain.model.Correntista;
import com.conta.bancaria.correntista.servicos.core.domain.model.StatusConta;
import com.conta.bancaria.correntista.servicos.core.domain.model.StatusTransacao;
import com.conta.bancaria.correntista.servicos.core.domain.model.StatusBacen;
import com.conta.bancaria.correntista.servicos.core.usecase.ListrarTransferenciasUseCase;
import com.conta.bancaria.correntista.servicos.core.usecase.RealizarTransferenciaUseCase;
import com.conta.bancaria.correntista.servicos.core.service.CorrentistaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@ExtendWith(SpringExtension.class)
class ContaControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CorrentistaService correntistaService;
    @Mock
    private RealizarTransferenciaUseCase realizarTransferenciaUseCase;
    @Mock
    private ListrarTransferenciasUseCase listrarTransferenciasUseCase;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ContaController contaController;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(contaController).build();
    }

    @Test
    @DisplayName("Deve buscar correntista por ID com sucesso")
    void getCorrentistasByIdCorrentista() throws Exception {

        var idCorrentista = 1L;

        var correntista = new Correntista(idCorrentista, 222434531L, StatusConta.ATIVO, new BigDecimal("1000.00"), new BigDecimal("5000.00"));
        given(correntistaService.getCorrentistaById(idCorrentista)).willReturn(correntista);

        mockMvc.perform(get("/correntistas/{idCorrentista}", idCorrentista)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idCorrentista))
                .andExpect(jsonPath("$.idUsuario").value(100L))
                .andExpect(jsonPath("$.statusConta").value("ATIVO"))
                .andExpect(jsonPath("$.saldo").value("1000.0"))
                .andExpect(jsonPath("$.limiteDiario").value("5000.0"));
    }


    /***
    #TODO: Criar testes abaixo
    @Test
    @DisplayName("Deve retornar 404 Not Found quando correntista não existir")
    void getIdCorrentistaNotFound() throws Exception {

        var idCorrentista = 2L;

        given(correntistaService.getCorrentistaById(idCorrentista)).willThrow(new RecursoNaoEncontradoException("Correntista não encontrado"));


        mockMvc.perform(get("/correntistas/{idCorrentista}", idCorrentista))
                .andExpect(status().isNotFound());
    } ***/

    @Test
    @DisplayName("Deve retornar 500 Internal Server Error para erros inesperados")
    void internalServerError() throws Exception {

        var idCorrentista = 3L;

        willThrow(new RuntimeException("Erro inesperado")).given(correntistaService).getCorrentistaById(idCorrentista);

        mockMvc.perform(get("/correntistas/{idCorrentista}", idCorrentista))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Deve realizar transferência com sucesso")
    void transferenciaRealizadaComSucesso() throws Exception {
        // Arrange
        var idCorrentista = 1L;
        // CORREÇÃO: Instanciamos os records usando seus construtores
        var requestDto = new TransferenciaRequestDto(idCorrentista, 2L, new BigDecimal("100.0"));
        var responseDto = new TransferenciaResponseDto(1L, idCorrentista, 2L, new BigDecimal("100.0"), StatusTransacao.SUCESSO, StatusBacen.SUCESSO);
        given(realizarTransferenciaUseCase.execute(any(Long.class), any(TransferenciaRequestDto.class))).willReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/correntistas/{idCorrentista}/transferencias", idCorrentista)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCorrentistaOrigem").value(idCorrentista))
                .andExpect(jsonPath("$.valor").value(100.0))
                .andExpect(jsonPath("$.statusTransacao").value("SUCESSO"));
    }

    @Test
    @DisplayName("Deve listar transferências de um correntista com sucesso")
    void obterTransferenciasComSucesso() throws Exception {
        Long idCorrentista = 1L;
        TransferenciaDto transferenciaDto1 = new TransferenciaDto(1L, 2L, BigDecimal.valueOf(100.0),
                StatusTransacao.SUCESSO, StatusBacen.SUCESSO);
        TransferenciaDto transferenciaDto2 = new TransferenciaDto(3L, 1L, BigDecimal.valueOf(50.0),
                StatusTransacao.SUCESSO, StatusBacen.SUCESSO);
        List<TransferenciaDto> transferencias = Arrays.asList(transferenciaDto1, transferenciaDto2);

        given(listrarTransferenciasUseCase.execute(idCorrentista)).willReturn(transferencias);

        mockMvc.perform(get("/correntistas/{idCorrentista}/transferencias", idCorrentista)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idCorrentistaOrigem").value(1L))
                .andExpect(jsonPath("$[0].idCorrentistaDestino").value(2L))
                .andExpect(jsonPath("$[0].valor").value(100.0))
                .andExpect(jsonPath("$[0].statusTransacao").value("SUCESSO"))
                .andExpect(jsonPath("$[0].statusBacen").value("SUCESSO"))
                .andExpect(jsonPath("$[1].idCorrentistaOrigem").value(3L))
                .andExpect(jsonPath("$[1].idCorrentistaDestino").value(1L))
                .andExpect(jsonPath("$[1].valor").value(50.0))
                .andExpect(jsonPath("$[1].statusTransacao").value("SUCESSO"))
                .andExpect(jsonPath("$[1].statusBacen").value("SUCESSO"));
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando não houver transferências")
    void obterTransferenciasSemResultado() throws Exception {
        var idCorrentista = 1L;

        given(listrarTransferenciasUseCase.execute(idCorrentista)).willReturn(Collections.emptyList());

        mockMvc.perform(get("/correntistas/{idCorrentista}/transferencias", idCorrentista))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Deve obter saldo com sucesso")
    void obterSaldosComSucesso() throws Exception {
        // Arrange
        var idCorrentista = 1L;
        var saldo = new BigDecimal("1000.00");
        given(correntistaService.consultaSaldoById(idCorrentista)).willReturn(saldo);


        mockMvc.perform(get("/correntistas/{idCorrentista}/saldos", idCorrentista))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.saldo").value(1000.00));
    }

    /***
     #TODO: Criar testes abaixo
    @Test
    @DisplayName("Deve retornar 404 Not Found ao buscar saldo de correntista inexistente")
    void obterSaldosSemSucesso() throws Exception {
        // Arrange
        var idCorrentista = 1L;

        given(correntistaService.consultaSaldoById(idCorrentista)).willThrow(new RecursoNaoEncontradoException("Saldo não encontrado"));


        mockMvc.perform(get("/correntistas/{idCorrentista}/saldos", idCorrentista))
                .andExpect(status().isNotFound());
    }
    ***/
}