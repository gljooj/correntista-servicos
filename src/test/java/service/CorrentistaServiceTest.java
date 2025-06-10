package service;

import com.conta.bancaria.correntista.servicos.adapter.dto.CorrentistaDto;
import com.conta.bancaria.correntista.servicos.core.domain.model.Correntista;
import com.conta.bancaria.correntista.servicos.core.domain.model.StatusConta;
import com.conta.bancaria.correntista.servicos.core.service.CorrentistaService;
import com.conta.bancaria.correntista.servicos.framework.repository.CorrentistaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CorrentistaServiceTest {

    @Mock
    private CorrentistaRepository correntistaRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CorrentistaService correntistaService;

    @Test
    void consultaSaldoById_deveRetornarSaldo_quandoIdValido() {
        // Arrange
        long idCorrentista = 1L;
        Correntista correntista = new Correntista();
        correntista.setId(idCorrentista);
        correntista.setSaldo(new BigDecimal("500.00"));

        when(correntistaRepository.findById(idCorrentista)).thenReturn(Optional.of(correntista));

        // Act
        BigDecimal result = correntistaService.consultaSaldoById(idCorrentista);

        // Assert
        assertEquals(correntista.getSaldo(), result);
    }

    @Test
    void consultaSaldoByIdInvalidId() {
        long id = 1L;
        when(correntistaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            correntistaService.consultaSaldoById(id);
        });

        verify(correntistaRepository).findById(id);

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

        CorrentistaDto result = correntistaService.getCorrentistaByUsuarioId(usuarioId);

        assertNotNull(result);
        assertEquals(usuarioId, result.getIdUsuario());

    }

    @Test
    void getCorrentistaByUsuarioIdNull() {
        long usuarioId = 1L;

        when(correntistaRepository.findByIdUsuario(usuarioId)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> {
            correntistaService.getCorrentistaByUsuarioId(usuarioId);
        });

        verify(correntistaRepository).findByIdUsuario(usuarioId);
    }
}
