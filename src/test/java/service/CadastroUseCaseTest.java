package service;

import com.conta.bancaria.correntista.servicos.adapter.dto.UsuarioDto;
import com.conta.bancaria.correntista.servicos.framework.repository.CadastroRepository;
import com.conta.bancaria.correntista.servicos.core.usecase.CadastroUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CadastroUseCaseTest {

    @Mock
    private CadastroRepository cadastroRepository;

    @InjectMocks
    private CadastroUseCase cadastroUseCase;

    @Test
    void getByNome_ExistingUser_ReturnsUser() throws IOException {
        String nome = "Alice";
        UsuarioDto usuarioDto = new UsuarioDto(1L, "Alice", "lili@test.com.br", "1111111111", "ATIVO");

        when(cadastroRepository.getByNome(nome)).thenReturn(usuarioDto);
        UsuarioDto result = cadastroUseCase.getByNome(nome);
        assertEquals(usuarioDto, result);
    }

    @Test
    void getByNome_NonExistingUser_ReturnsNull() throws IOException {
        String nome = "Bob";

        when(cadastroRepository.getByNome(nome)).thenReturn(null);
        UsuarioDto result = cadastroUseCase.getByNome(nome);

        assertEquals(null, result);
    }

    @Test
    void getById_ExistingUser_ReturnsUser() throws IOException {

        Long id = 1L;

        UsuarioDto usuarioDto = new UsuarioDto(1L, "Alice", "lili@test.com.br", "1111111111", "ATIVO");

        when(cadastroRepository.getById(id)).thenReturn(usuarioDto);
        UsuarioDto result = cadastroUseCase.getById(id);

        assertEquals(usuarioDto, result);
    }

    @Test
    void getById_NonExistingUser_ReturnsNull() throws IOException {
        Long id = 999L;

        when(cadastroRepository.getById(id)).thenReturn(null);
        UsuarioDto result = cadastroUseCase.getById(id);

        assertEquals(null, result);
    }
}
