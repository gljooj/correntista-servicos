package com.conta.bancaria.correntista.servicos.repository;

import com.conta.bancaria.correntista.servicos.dto.UsuarioDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.List;

@Repository
public class CadastroRepository {

    public UsuarioDto getByNome(String nome) throws java.io.IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        File booksJsonfile = new File("src/main/resources/usuarios.json");

        List<UsuarioDto> usuarios = objectMapper.readValue(booksJsonfile, new TypeReference<List<UsuarioDto>>() {});

        UsuarioDto find_usuario = null;
        for(UsuarioDto usuario : usuarios){
            if(usuario.getNome().equals(nome)){
                find_usuario = usuario;
            }
        }
        return find_usuario;
    }

    public UsuarioDto getById(Long id) throws java.io.IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        File booksJsonfile = new File("src/main/resources/usuarios.json");

        List<UsuarioDto> usuarios = objectMapper.readValue(booksJsonfile, new TypeReference<List<UsuarioDto>>() {});

        UsuarioDto find_usuario = null;
        for(UsuarioDto usuario : usuarios){
            if(usuario.getId().equals(id)){
                find_usuario = usuario;
            }
        }
        return find_usuario;
    }
}
