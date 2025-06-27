package com.conta.bancaria.correntista.servicos.core.exception.validacao;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErroDeFormularioDto {

    private String campo;
    private String erro;
    public ErroDeFormularioDto(String campo, String erro) {
        this.campo = campo;
        this.erro = erro;
    }


}
