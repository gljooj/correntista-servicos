package service;

import com.conta.bancaria.correntista.servicos.adapter.dto.CorrentistaDto;
import com.conta.bancaria.correntista.servicos.adapter.dto.TransferenciaDto;
import com.conta.bancaria.correntista.servicos.adapter.dto.TransferenciaRequestDto;
import com.conta.bancaria.correntista.servicos.adapter.dto.TransferenciaResponseDto;
import com.conta.bancaria.correntista.servicos.core.domain.model.*;
import com.conta.bancaria.correntista.servicos.core.service.CorrentistaService;
import com.conta.bancaria.correntista.servicos.core.service.TransacaoService;
import com.conta.bancaria.correntista.servicos.framework.repository.CorrentistaRepository;
import com.conta.bancaria.correntista.servicos.core.usecase.NotificaBacenUseCase;
import com.conta.bancaria.correntista.servicos.core.usecase.RealizarTransferenciaUseCase;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RealizarTransferenciaUseCaseTest {

    @Mock
    private CorrentistaRepository correntistaRepository;
    @Mock
    private TransacaoService transacaoService;
    @Mock
    private NotificaBacenUseCase notificaBacenUseCase;

    @InjectMocks
    private RealizarTransferenciaUseCase realizarTransferenciaUseCase;

    @Mock
    private CorrentistaService correntistaService;

    @Test
    void executeDeveRealizarTransferenciaComSucesso() throws Exception {
        long idOrigem = 1L;
        long idDestino = 2L;
        BigDecimal valor = new BigDecimal("100.00");

        Correntista origem = new Correntista(idOrigem, 101L, StatusConta.ATIVO, new BigDecimal("500.00"), new BigDecimal("1000.00"));
        Correntista destino = new Correntista(idDestino, 102L, StatusConta.ATIVO, new BigDecimal("200.00"), new BigDecimal("1000.00"));

        TransferenciaRequestDto requestDto = new TransferenciaRequestDto(origem.getId(), destino.getId(), valor);

        TransferenciaResponseDto responseDtoEsperado = new TransferenciaResponseDto(1L,
                origem.getId(),
                destino.getId(), valor, StatusTransacao.SUCESSO,
                StatusBacen.SUCESSO);

        ModelMapper mapper = new ModelMapper();
        when(correntistaService.getCorrentistaById(idOrigem)).thenReturn(origem);
        when(correntistaService.getCorrentistaById(idDestino)).thenReturn(destino);

        when(transacaoService.salvarTransacao(any(TransferenciaDto.class))).thenReturn(responseDtoEsperado);
        when(notificaBacenUseCase.execute(any(TransferenciaResponseDto.class))).thenReturn(responseDtoEsperado);

        doNothing().when(correntistaService).save(any(Correntista.class));

        TransferenciaResponseDto response = realizarTransferenciaUseCase.execute(idOrigem, requestDto);

        assertNotNull(response);
        assertEquals(StatusBacen.SUCESSO, response.statusBacen());
        verify(correntistaService, times(2)).save(any(Correntista.class));
    }


    @Test
    void executeDeveLancarExcecaoQuandoCorrentistaOrigemNaoEncontrado() {
        long idOrigemInexistente = 99L;
        var requestDto = new TransferenciaRequestDto(1L, 2l, new BigDecimal("100.00"));

        when(correntistaService.getCorrentistaById(idOrigemInexistente))
                .thenThrow(new EntityNotFoundException("Correntista com id " + idOrigemInexistente + " não encontrado."));

        assertThrows(EntityNotFoundException.class, () -> {
            realizarTransferenciaUseCase.execute(idOrigemInexistente, requestDto);
        });
    }

}