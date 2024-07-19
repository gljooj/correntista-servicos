package com.conta.bancaria.correntista.servicos.adapter.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDto {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String status;

    public UsuarioDto(long l, String joão) {
    }
}
