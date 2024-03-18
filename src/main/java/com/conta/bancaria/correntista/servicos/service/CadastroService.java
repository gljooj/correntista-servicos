package com.conta.bancaria.correntista.servicos.service;

import com.conta.bancaria.correntista.servicos.dto.UsuarioDto;
import com.conta.bancaria.correntista.servicos.repository.CadastroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CadastroService {


    @Autowired
    private final CadastroRepository cadastroRepository;

    public UsuarioDto getByNome(String nome) throws IOException {
        return cadastroRepository.getByNome(nome);
    }

    public UsuarioDto getById(Long id) throws IOException {
        return cadastroRepository.getById(id);
    }
}
