package controller;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import com.conta.bancaria.correntista.servicos.controller.ContaController;
import com.conta.bancaria.correntista.servicos.dto.*;
import com.conta.bancaria.correntista.servicos.model.StatusBacen;
import com.conta.bancaria.correntista.servicos.model.StatusConta;
import com.conta.bancaria.correntista.servicos.model.StatusTransacao;
import com.conta.bancaria.correntista.servicos.repository.CorrentistaRepository;
import com.conta.bancaria.correntista.servicos.service.CadastroService;
import com.conta.bancaria.correntista.servicos.service.CorrentistaService;
import com.conta.bancaria.correntista.servicos.service.TransferenciaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@ExtendWith(SpringExtension.class)
class ContaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CorrentistaService correntistaService;

    @Mock
    private TransferenciaService transferenciaService;

    @Mock
    private CorrentistaRepository correntistaRepository;

    @Mock
    private CadastroService cadastroService;

    @InjectMocks
    private ContaController contaController;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(contaController).build();
    }

    @Test
    void consultaCorrentistaPorNomeTest() throws Exception {

        UsuarioDto mockUsuarioDto = new UsuarioDto(9999L, "Nome Teste", "email@teste.com", "999999999", "ATIVO");
        CorrentistaDto mockCorrentista = new CorrentistaDto(99L, mockUsuarioDto.getId(), StatusConta.ATIVO,
                new BigDecimal(900), new BigDecimal(10000) );
        when(cadastroService.getByNome("Nome Teste")).thenReturn(mockUsuarioDto);
        when(correntistaService.getCorrentistaByUsuarioId(mockUsuarioDto.getId())).thenReturn(mockCorrentista);

        mockMvc.perform(get("/correntistas?nome=Nome Teste")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockCorrentista.getId()))
                .andExpect(jsonPath("$.idUsuario").value(mockUsuarioDto.getId()))
                .andExpect(jsonPath("$.statusConta").value(StatusConta.ATIVO.toString()));

        verify(cadastroService).getByNome("Nome Teste");
    }

    @Test
    void getCorrentistasByIdCorrentista() throws Exception {
        Long idCorrentista = 1L;
        CorrentistaDto correntistaDto = new CorrentistaDto();
        correntistaDto.setId(idCorrentista);
        correntistaDto.setIdUsuario(100L);
        correntistaDto.setStatusConta(StatusConta.ATIVO);
        correntistaDto.setSaldo(new BigDecimal("1000.00"));
        correntistaDto.setLimiteDiario(new BigDecimal("5000.00"));

        given(correntistaService.getCorrentistaById(idCorrentista)).willReturn(correntistaDto);

        mockMvc.perform(get("/correntistas/{idCorrentista}", idCorrentista)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idCorrentista))
                .andExpect(jsonPath("$.idUsuario").value(100L))
                .andExpect(jsonPath("$.statusConta").value("ATIVO"))
                .andExpect(jsonPath("$.saldo").value("1000.0"))
                .andExpect(jsonPath("$.limiteDiario").value("5000.0"));
    }

    @Test
    void getIdCorrentistaNotFound() throws Exception {
        Long idCorrentista = 2L;
        given(correntistaService.getCorrentistaById(idCorrentista)).willReturn(null);

        mockMvc.perform(get("/correntistas/{idCorrentista}", idCorrentista)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void internalServerErro() throws Exception {

        Long idCorrentista = 3L;
        willThrow(new RuntimeException("Unexpected error")).given(correntistaService).getCorrentistaById(idCorrentista);


        mockMvc.perform(get("/correntistas/{idCorrentista}", idCorrentista)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Ocorreu um erro ao processar a solicitação."));
    }

    @Test
    void transferenciaRealizadaComSucesso() throws Exception {
        Long idCorrentista = 1L;
        TransferenciaRequestDto requestDto = new TransferenciaRequestDto();
        requestDto.setIdCorrentistaDestino(2L);
        requestDto.setValor(BigDecimal.valueOf(100.0));

        TransferenciaResponseDto responseDto = new TransferenciaResponseDto();
        responseDto.setId(1L);
        responseDto.setIdCorrentistaOrigem(idCorrentista);
        responseDto.setIdCorrentistaDestino(requestDto.getIdCorrentistaDestino());
        responseDto.setValor(requestDto.getValor());
        responseDto.setStatusTransacao(StatusTransacao.SUCESSO);
        responseDto.setStatusBacen(StatusBacen.SUCESSO);

        given(transferenciaService.realizarTransferencia(any(Long.class), any(TransferenciaRequestDto.class)))
                .willReturn(responseDto);


        mockMvc.perform(post("/correntistas/{idCorrentista}/transferencias", idCorrentista)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.idCorrentistaOrigem").value(idCorrentista))
                .andExpect(jsonPath("$.idCorrentistaDestino").value(requestDto.getIdCorrentistaDestino()))
                .andExpect(jsonPath("$.valor").value(100.0))
                .andExpect(jsonPath("$.statusTransacao").value("SUCESSO"))
                .andExpect(jsonPath("$.statusBacen").value("SUCESSO"));
    }


    @Test
    void idUsuarioDestinoIgualIdCorrentista() throws Exception {
        Long idCorrentista = 1L;
        TransferenciaRequestDto requestDto = new TransferenciaRequestDto();
        requestDto.setIdCorrentistaDestino(idCorrentista);

        mockMvc.perform(post("/correntistas/{idCorrentista}/transferencias", idCorrentista)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("O idUsuario é igual ao usuario de envio na solicitação"));
    }

    @Test
    void transferenciaNaoRealizada() throws Exception {
        Long idCorrentista = 1L;
        TransferenciaRequestDto requestDto = new TransferenciaRequestDto();
        requestDto.setIdCorrentistaDestino(2L);

        given(transferenciaService.realizarTransferencia(eq(idCorrentista), eq(requestDto)))
                .willReturn(null);

        mockMvc.perform(post("/correntistas/{idCorrentista}/transferencias", idCorrentista)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Não foi possível realizar a transferência."));
    }

    @Test
    void obterTransferenciasComSucesso() throws Exception {
        Long idCorrentista = 1L;
        TransferenciaDto transferenciaDto1 = new TransferenciaDto(1L, 2L, BigDecimal.valueOf(100.0),
                StatusTransacao.SUCESSO, StatusBacen.SUCESSO);
        TransferenciaDto transferenciaDto2 = new TransferenciaDto(3L, 1L, BigDecimal.valueOf(50.0),
                StatusTransacao.SUCESSO, StatusBacen.SUCESSO);
        List<TransferenciaDto> transferencias = Arrays.asList(transferenciaDto1, transferenciaDto2);

        given(transferenciaService.listarTransferencias(idCorrentista)).willReturn(transferencias);

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
    void obterTransferenciasSemSucesso() throws Exception {
        Long idCorrentista = 1L;
        given(transferenciaService.listarTransferencias(idCorrentista)).willReturn(null);

        mockMvc.perform(get("/correntistas/{idCorrentista}/transferencias", idCorrentista)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void obterSaldosComSucesso() throws Exception {
        Long idCorrentista = 1L;
        BigDecimal saldo = BigDecimal.valueOf(1000.0);
        given(correntistaService.consultaSaldoById(idCorrentista)).willReturn(saldo);

        mockMvc.perform(get("/correntistas/{idCorrentista}/saldos", idCorrentista)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1000.0));
    }

    @Test
    void obterSaldosSemSucesso() throws Exception {
        Long idCorrentista = 1L;
        given(correntistaService.consultaSaldoById(idCorrentista)).willReturn(null);

        mockMvc.perform(get("/correntistas/{idCorrentista}/saldos", idCorrentista)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }



    private String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }




}