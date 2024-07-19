package service;

import com.conta.bancaria.correntista.servicos.adapter.dto.TransferenciaRequestDto;
import com.conta.bancaria.correntista.servicos.adapter.dto.TransferenciaResponseDto;
import com.conta.bancaria.correntista.servicos.core.exception.ContaInativaException;
import com.conta.bancaria.correntista.servicos.core.exception.CorrentistaNotFoundException;
import com.conta.bancaria.correntista.servicos.core.exception.SaldoInsuficienteException;
import com.conta.bancaria.correntista.servicos.core.domain.model.Correntista;
import com.conta.bancaria.correntista.servicos.core.domain.model.StatusBacen;
import com.conta.bancaria.correntista.servicos.core.domain.model.StatusConta;
import com.conta.bancaria.correntista.servicos.core.domain.model.Transferencia;
import com.conta.bancaria.correntista.servicos.framework.repository.CorrentistaRepository;
import com.conta.bancaria.correntista.servicos.framework.repository.TransferenciaRepository;
import com.conta.bancaria.correntista.servicos.core.usecase.BacenUseCase;
import com.conta.bancaria.correntista.servicos.core.usecase.RealizarTransferenciaUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class RealizarTransferenciaUseCaseTest {

    @Mock
    private CorrentistaRepository correntistaRepository;

    @Mock
    private TransferenciaRepository transferenciaRepository;

    @Mock
    private BacenUseCase bacenUseCase;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RealizarTransferenciaUseCase realizarTransferenciaUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void realizarTransferenciaSuccess() throws CorrentistaNotFoundException, ContaInativaException, SaldoInsuficienteException {
        long idCorrentistaOrigem = 1L;
        long idCorrentistaDestino = 2L;
        BigDecimal valorTransferencia = BigDecimal.valueOf(100);

        Correntista correntistaOrigem = new Correntista();
        correntistaOrigem.setId(idCorrentistaOrigem);
        correntistaOrigem.setStatusConta(StatusConta.ATIVO);
        correntistaOrigem.setSaldo(BigDecimal.valueOf(500));
        correntistaOrigem.setLimiteDiario(BigDecimal.valueOf(1000));

        Correntista correntistaDestino = new Correntista();
        correntistaDestino.setId(idCorrentistaDestino);
        correntistaDestino.setStatusConta(StatusConta.ATIVO);
        correntistaDestino.setSaldo(BigDecimal.valueOf(200));
        correntistaDestino.setLimiteDiario(BigDecimal.valueOf(1000));

        TransferenciaRequestDto transferenciaRequestDto = new TransferenciaRequestDto();
        transferenciaRequestDto.setIdCorrentistaDestino(idCorrentistaDestino);
        transferenciaRequestDto.setValor(valorTransferencia);

        when(correntistaRepository.getById(idCorrentistaOrigem)).thenReturn(correntistaOrigem);
        when(correntistaRepository.getById(idCorrentistaDestino)).thenReturn(correntistaDestino);

        TransferenciaResponseDto dto = new TransferenciaResponseDto();
        dto.setId(1L);
        dto.setStatusBacen(StatusBacen.SUCESSO);
        Transferencia transferencia = new Transferencia();
        transferencia.setId(dto.getId());

        when(transferenciaRepository.getById(dto.getId())).thenReturn(transferencia);
        when(bacenUseCase.notificaBacen(any())).thenReturn(dto);


        TransferenciaResponseDto response = realizarTransferenciaUseCase.execute(idCorrentistaOrigem, transferenciaRequestDto);


        assertNotNull(response);
        assertEquals(StatusBacen.SUCESSO, response.getStatusBacen());
    }

    @Test
    void realizarTransferencia_CorrentistaNotFound() {
        // Arrange
        long idCorrentista = 1L;
        TransferenciaRequestDto dto = new TransferenciaRequestDto(1L, 2L, BigDecimal.valueOf(100));

        when(correntistaRepository.getById(idCorrentista)).thenReturn(null);

        // Assert
        assertThrows(CorrentistaNotFoundException.class, () -> {
            // Act
            realizarTransferenciaUseCase.execute(idCorrentista, dto);
        });
    }

}