package repository;

import com.conta.bancaria.correntista.servicos.adapter.dto.TransferenciaResponseDto;
import com.conta.bancaria.correntista.servicos.framework.repository.BacenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class BacenRepositoryTest {

    @InjectMocks
    private BacenRepository bacenRepository;

    @Test
    void postTransferenciaDto() {
        // Given
        TransferenciaResponseDto transferenciaResponseDto = new TransferenciaResponseDto();

        // When
        boolean result = bacenRepository.post(transferenciaResponseDto);

        // Then
        assertTrue(result);
    }
}
