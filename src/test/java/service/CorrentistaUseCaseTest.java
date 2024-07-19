package service;

import com.conta.bancaria.correntista.servicos.adapter.dto.CorrentistaDto;
import com.conta.bancaria.correntista.servicos.core.domain.model.Correntista;
import com.conta.bancaria.correntista.servicos.core.domain.model.StatusConta;
import com.conta.bancaria.correntista.servicos.framework.repository.CorrentistaRepository;
import com.conta.bancaria.correntista.servicos.framework.repository.TransferenciaRepository;
import com.conta.bancaria.correntista.servicos.core.usecase.CorrentistaUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CorrentistaUseCaseTest {

    @Mock
    private CorrentistaRepository correntistaRepository;

    @Mock
    private TransferenciaRepository transferenciaRepository;


    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CorrentistaUseCase correntistaUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void consultaSaldoById() {
        long idCorrentista = 1L;
        BigDecimal valorTransferencia = BigDecimal.valueOf(100);

        Correntista correntista = new Correntista();
        correntista.setId(idCorrentista);
        correntista.setStatusConta(StatusConta.ATIVO);
        correntista.setSaldo(BigDecimal.valueOf(500));
        correntista.setLimiteDiario(BigDecimal.valueOf(1000));

        when(correntistaRepository.getById(idCorrentista)).thenReturn(correntista);

        BigDecimal result = correntistaUseCase.consultaSaldoById(idCorrentista);

        assertEquals(correntista.getSaldo(), result);
    }

    @Test
    void consultaSaldoByIdInvalidId() {
        long id = 1L;
        when(correntistaRepository.getById(id)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> {
            correntistaUseCase.consultaSaldoById(id);
        });
    }


    @Test
    void getCorrentistaByUsuarioId() {
        long usuarioId = 1L;
        long idCorrentista = 1L;
        Correntista correntista = new Correntista();
        correntista.setId(idCorrentista);
        correntista.setIdUsuario(idCorrentista);
        correntista.setStatusConta(StatusConta.ATIVO);
        correntista.setSaldo(BigDecimal.valueOf(500));
        correntista.setLimiteDiario(BigDecimal.valueOf(1000));
        correntista.setIdUsuario(usuarioId);


        CorrentistaDto correntistaDto = new CorrentistaDto();
        correntistaDto.setId(idCorrentista);
        correntistaDto.setIdUsuario(idCorrentista);
        correntistaDto.setStatusConta(StatusConta.ATIVO);
        correntistaDto.setSaldo(BigDecimal.valueOf(500));
        correntistaDto.setLimiteDiario(BigDecimal.valueOf(1000));

        when(correntistaRepository.findByIdUsuario(usuarioId)).thenReturn((correntista));
        when(modelMapper.map(correntista, CorrentistaDto.class)).thenReturn(correntistaDto);

        CorrentistaDto result = correntistaUseCase.getCorrentistaByUsuarioId(usuarioId);

        assertNotNull(result);
        assertEquals(usuarioId, result.getIdUsuario());

    }

    @Test
    void getCorrentistaByUsuarioIdNull() {
        long usuarioId = 1L;

        when(correntistaRepository.findByIdUsuario(usuarioId)).thenReturn(null);

        CorrentistaDto result = correntistaUseCase.getCorrentistaByUsuarioId(usuarioId);

        assertNull(result);
    }
}
