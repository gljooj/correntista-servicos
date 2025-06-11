package repository;

import com.conta.bancaria.correntista.servicos.adapter.dto.TransferenciaResponseDto;
import com.conta.bancaria.correntista.servicos.core.domain.model.StatusBacen;
import com.conta.bancaria.correntista.servicos.core.domain.model.StatusTransacao;
import com.conta.bancaria.correntista.servicos.framework.repository.BacenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class BacenRepositoryTest {

    @InjectMocks
    private BacenRepository bacenRepository;

    @Test
    void postTransferenciaDto() {
        // Given
        TransferenciaResponseDto transferenciaResponseDto = new TransferenciaResponseDto(
                1L, 1L, 2L,
                new BigDecimal("100.00"), StatusTransacao.SUCESSO, StatusBacen.SUCESSO);

        // When
        boolean result = bacenRepository.post(transferenciaResponseDto);

        // Then
        assertTrue(result);
    }
}
