package com.conta.bancaria.correntista.servicos.core.service;

import com.conta.bancaria.correntista.servicos.adapter.dto.UsuarioDto;
import com.conta.bancaria.correntista.servicos.framework.repository.CadastroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CadastroService {


    private final CadastroRepository cadastroRepository;

    public UsuarioDto getByNome(String nome) throws IOException {
        return cadastroRepository.getByNome(nome);
    }

    public UsuarioDto getById(Long id) throws IOException {
        return cadastroRepository.getById(id);
    }
}
