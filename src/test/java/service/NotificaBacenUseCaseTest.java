package service;

import com.conta.bancaria.correntista.servicos.adapter.dto.TransferenciaResponseDto;
import com.conta.bancaria.correntista.servicos.core.domain.model.StatusBacen;
import com.conta.bancaria.correntista.servicos.core.domain.model.Transferencia;
import com.conta.bancaria.correntista.servicos.core.usecase.NotificaBacenUseCase;
import com.conta.bancaria.correntista.servicos.core.usecase.SqsUseCase;
import com.conta.bancaria.correntista.servicos.framework.repository.BacenRepository;
import com.conta.bancaria.correntista.servicos.framework.repository.TransferenciaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificaBacenUseCaseTest {

    @Mock
    private BacenRepository bacenRepository;
    @Mock
    private TransferenciaRepository transferenciaRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private SqsUseCase sqsUseCase;


    @InjectMocks
    private NotificaBacenUseCase notificaBacenUseCase;

    private Transferencia transferenciaSalva;
    private TransferenciaResponseDto transferenciaDto;

    @BeforeEach
    void setUp() {
        // Criamos o objeto de domínio que simularemos estar no banco
        transferenciaSalva = new Transferencia();
        transferenciaSalva.setId(1L);
        transferenciaSalva.setIdCorrentistaOrigem(10L);
        transferenciaSalva.setIdCorrentistaDestino(20L);
        transferenciaSalva.setValor(new BigDecimal("100.00"));
        transferenciaSalva.setStatusBacen(null); // Estado inicial

        // Criamos o DTO (record) usando seu construtor
        transferenciaDto = new TransferenciaResponseDto(
                transferenciaSalva.getId(),
                transferenciaSalva.getIdCorrentistaOrigem(),
                transferenciaSalva.getIdCorrentistaDestino(),
                transferenciaSalva.getValor(),
                null, // statusTransacao, se houver
                null  // statusBacen, se houver
        );
    }

    @Test
    @DisplayName("Deve notificar com sucesso e atualizar status quando Bacen responder positivamente")
    void execute_DeveAtualizarStatusParaSucesso_QuandoPostForTrue() {
        // Arrange (Organização)
        when(bacenRepository.post(any())).thenReturn(true);
        when(transferenciaRepository.findById(transferenciaDto.id())).thenReturn(Optional.of(transferenciaSalva));

        notificaBacenUseCase.execute(transferenciaDto);

        verify(bacenRepository).post(any());
        verify(transferenciaRepository).findById(transferenciaDto.id());
        verify(sqsUseCase, never()).send(anyString());
        assertEquals(StatusBacen.SUCESSO, transferenciaSalva.getStatusBacen());
    }

    @Test
    @DisplayName("Deve falhar a notificação e enfileirar para retry quando Bacen responder negativamente")
    void execute_DeveAtualizarStatusParaFalha_QuandoPostForFalse() {
        when(bacenRepository.post(any())).thenReturn(false);
        when(transferenciaRepository.findById(transferenciaDto.id())).thenReturn(Optional.of(transferenciaSalva));

        notificaBacenUseCase.execute(transferenciaDto);

        verify(bacenRepository).post(any());
        verify(transferenciaRepository).findById(transferenciaDto.id());
        verify(sqsUseCase).send(anyString());

        assertEquals(StatusBacen.FALHA, transferenciaSalva.getStatusBacen()); // ou o status de falha correto
    }

    @Test
    @DisplayName("Deve lançar exceção quando a transferência informada no DTO não for encontrada")
    void execute_DeveLancarExcecao_QuandoTransferenciaNaoEncontrada() {

        when(transferenciaRepository.findById(transferenciaDto.id())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            notificaBacenUseCase.execute(transferenciaDto);
        });
    }
}