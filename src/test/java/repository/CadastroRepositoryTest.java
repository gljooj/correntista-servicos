package repository;

import com.conta.bancaria.correntista.servicos.adapter.dto.UsuarioDto;
import com.conta.bancaria.correntista.servicos.framework.repository.CadastroRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CadastroRepositoryTest {

    @InjectMocks
    private CadastroRepository cadastroRepository;


    @Mock
    private File file;

    @Test
    void getByNome() throws IOException {
        String nome = "Ronaldo";
        UsuarioDto result = cadastroRepository.getByNome(nome);

        assertEquals(222434531L, result.getId());
        assertEquals("Ronaldo", result.getNome());
    }

    @Test
    void getByIdExistingId() throws IOException {
        Long id = 222434532L;

        UsuarioDto result = cadastroRepository.getById(id);

        assertEquals(222434532L, result.getId());
        assertEquals("Messi", result.getNome());
    }
}
