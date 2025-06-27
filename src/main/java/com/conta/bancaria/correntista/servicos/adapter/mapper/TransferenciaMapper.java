package com.conta.bancaria.correntista.servicos.adapter.mapper;

import com.conta.bancaria.correntista.servicos.adapter.dto.TransferenciaDto;
import com.conta.bancaria.correntista.servicos.adapter.dto.TransferenciaResponseDto;
import com.conta.bancaria.correntista.servicos.core.domain.model.Transferencia;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransferenciaMapper{
    Transferencia fromDto(TransferenciaDto dto);
    TransferenciaDto toDto(Transferencia transferencia);
    @org.mapstruct.BeanMapping(ignoreUnmappedSourceProperties = "dataTransferencia")
    TransferenciaResponseDto toResponseDto(Transferencia transferencia);
}
