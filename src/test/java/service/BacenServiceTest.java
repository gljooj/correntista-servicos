package service;

import com.conta.bancaria.correntista.servicos.dto.TransferenciaResponseDto;
import com.conta.bancaria.correntista.servicos.model.StatusBacen;
import com.conta.bancaria.correntista.servicos.model.Transferencia;
import com.conta.bancaria.correntista.servicos.repository.BacenRepository;
import com.conta.bancaria.correntista.servicos.repository.TransferenciaRepository;
import com.conta.bancaria.correntista.servicos.service.BacenService;
import com.conta.bancaria.correntista.servicos.service.SqsService;
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
class BacenServiceTest {

    @Mock
    private BacenRepository bacenRepository;

    @Mock
    private TransferenciaRepository transferenciaRepository;

    @Mock
    private SqsService sqsService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BacenService bacenService;

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
        when(bacenService.atualizaStatusBacen(dto, StatusBacen.SUCESSO)).thenReturn(dto);

        TransferenciaResponseDto result = bacenService.notificaBacen(dto);

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
        when(bacenService.atualizaStatusBacen(dto, StatusBacen.FALHA)).thenReturn(dto);

        TransferenciaResponseDto result = bacenService.notificaBacen(dto);

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
        when(bacenService.atualizaStatusBacen(dto, StatusBacen.SUCESSO)).thenReturn(dto);


        TransferenciaResponseDto result = bacenService.atualizaStatusBacen(dto, StatusBacen.SUCESSO);

        assertEquals(dto, result);
        assertEquals(dto.getStatusBacen(), result.getStatusBacen());
    }
}
