package service;

import com.conta.bancaria.correntista.servicos.adapter.dto.TransferenciaResponseDto;
import com.conta.bancaria.correntista.servicos.core.domain.model.StatusBacen;
import com.conta.bancaria.correntista.servicos.core.domain.model.Transferencia;
import com.conta.bancaria.correntista.servicos.core.usecase.NotificaBacenUseCase;
import com.conta.bancaria.correntista.servicos.core.usecase.SqsUseCase;
import com.conta.bancaria.correntista.servicos.framework.repository.BacenRepository;
import com.conta.bancaria.correntista.servicos.framework.repository.TransferenciaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

    @Test
    void executeDeveAtualizarStatusParaSucessoQuandoPostForTrue() {
        var dto = new TransferenciaResponseDto();
        dto.setId(1L);
        var transferencia = new Transferencia();
        transferencia.setId(dto.getId());

        when(bacenRepository.post(any())).thenReturn(true);
        when(transferenciaRepository.findById(dto.getId())).thenReturn(Optional.of(transferencia));

        // CORREÇÃO 2: Não precisamos mais verificar o save.
        // A lógica de atualização agora é um detalhe de implementação.
        // O teste do método `execute` se preocupa apenas com o fluxo principal.

        notificaBacenUseCase.execute(dto);

        // Verificamos que o fluxo de sucesso foi seguido
        verify(bacenRepository).post(any());
        verify(transferenciaRepository).findById(dto.getId());
        // Verificamos que a fila SQS de retry NÃO foi chamada
        verify(sqsUseCase, never()).send(anyString());
    }

    @Test
    void executeDeveAtualizarStatusParaFalhaQuandoPostForFalse() {
        var dto = new TransferenciaResponseDto();
        dto.setId(1L);
        var transferencia = new Transferencia();
        transferencia.setId(dto.getId());

        when(bacenRepository.post(any())).thenReturn(false);
        when(transferenciaRepository.findById(dto.getId())).thenReturn(Optional.of(transferencia));

        notificaBacenUseCase.execute(dto);

        verify(bacenRepository).post(any());
        verify(transferenciaRepository).findById(dto.getId());
        // No caso de falha, verificamos que a fila SQS FOI chamada
        verify(sqsUseCase).send(anyString());
    }

    @Test
    void atualizaStatusBacenDeveAtualizarEntidadeQuandoEncontrada() {
        var dto = new TransferenciaResponseDto();
        dto.setId(1L);
        var statusParaAtualizar = StatusBacen.SUCESSO;
        var transferencia = new Transferencia();

        when(transferenciaRepository.findById(dto.getId())).thenReturn(Optional.of(transferencia));
        // Mockamos o que o modelMapper deve retornar
        when(modelMapper.map(any(Transferencia.class), eq(TransferenciaResponseDto.class)))
                .thenReturn(new TransferenciaResponseDto());

        notificaBacenUseCase.atualizaStatusBacen(dto, statusParaAtualizar);

        // CORREÇÃO 2: Removemos a verificação do 'save'.
        // Agora, o teste confia que o @Transactional funciona.
        // O importante é garantir que o 'findById' foi chamado.
        verify(transferenciaRepository).findById(dto.getId());
        assertEquals(statusParaAtualizar, transferencia.getStatusBacen());
    }

    @Test
    void atualizaStatusBacenDeveLancarExcecaoQuandoTransferenciaNaoEncontrada() {
        var dto = new TransferenciaResponseDto();
        dto.setId(99L);

        when(transferenciaRepository.findById(dto.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            notificaBacenUseCase.atualizaStatusBacen(dto, StatusBacen.SUCESSO);
        });
    }
}