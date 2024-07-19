package service;

import com.conta.bancaria.correntista.servicos.adapter.dto.TransferenciaResponseDto;
import com.conta.bancaria.correntista.servicos.core.domain.model.StatusBacen;
import com.conta.bancaria.correntista.servicos.core.domain.model.Transferencia;
import com.conta.bancaria.correntista.servicos.framework.repository.BacenRepository;
import com.conta.bancaria.correntista.servicos.framework.repository.TransferenciaRepository;
import com.conta.bancaria.correntista.servicos.core.usecase.BacenUseCase;
import com.conta.bancaria.correntista.servicos.core.usecase.SqsUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BacenUseCaseTest {

    @Mock
    private BacenRepository bacenRepository;

    @Mock
    private TransferenciaRepository transferenciaRepository;

    @Mock
    private SqsUseCase sqsUseCase;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BacenUseCase bacenUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void notificaBacenSuccess() {
        TransferenciaResponseDto dto = new TransferenciaResponseDto();
        dto.setId(1L);
        dto.setStatusBacen(StatusBacen.EM_PROGRESSO);

        when(bacenRepository.post(any())).thenReturn(true);
        Transferencia transferencia = new Transferencia();
        transferencia.setId(dto.getId());

        when(transferenciaRepository.getById(dto.getId())).thenReturn(transferencia);

        dto.setStatusBacen(StatusBacen.SUCESSO);
        when(bacenUseCase.atualizaStatusBacen(dto, StatusBacen.SUCESSO)).thenReturn(dto);

        TransferenciaResponseDto result = bacenUseCase.notificaBacen(dto);

        assertNotNull(result);
        assertEquals(StatusBacen.SUCESSO, result.getStatusBacen());
    }

    @Test
    void notificaBacenFailure() {
        TransferenciaResponseDto dto = new TransferenciaResponseDto();
        dto.setId(1L);
        dto.setStatusBacen(StatusBacen.EM_PROGRESSO);

        when(bacenRepository.post(any())).thenReturn(false);
        Transferencia transferencia = new Transferencia();
        transferencia.setId(dto.getId());

        when(transferenciaRepository.getById(dto.getId())).thenReturn(transferencia);

        dto.setStatusBacen(StatusBacen.FALHA);
        when(bacenUseCase.atualizaStatusBacen(dto, StatusBacen.FALHA)).thenReturn(dto);

        TransferenciaResponseDto result = bacenUseCase.notificaBacen(dto);

        assertNotNull(result);
        assertEquals(StatusBacen.FALHA, result.getStatusBacen());
    }

    @Test
    void atualizaStatusBacen() {
        TransferenciaResponseDto dto = new TransferenciaResponseDto();
        dto.setId(1L);
        dto.setStatusBacen(StatusBacen.EM_PROGRESSO);

        Transferencia transferencia = new Transferencia();
        transferencia.setId(dto.getId());


        when(transferenciaRepository.getById(dto.getId())).thenReturn(transferencia);

        dto.setStatusBacen(StatusBacen.SUCESSO);
        when(bacenUseCase.atualizaStatusBacen(dto, StatusBacen.SUCESSO)).thenReturn(dto);


        TransferenciaResponseDto result = bacenUseCase.atualizaStatusBacen(dto, StatusBacen.SUCESSO);

        assertEquals(dto, result);
        assertEquals(dto.getStatusBacen(), result.getStatusBacen());
    }
}
