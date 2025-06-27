package com.conta.bancaria.correntista.servicos.adapter.mapper;

import com.conta.bancaria.correntista.servicos.adapter.dto.CorrentistaDto;
import com.conta.bancaria.correntista.servicos.core.domain.model.Correntista;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CorrentistaMapper {
    Correntista fromDto(CorrentistaDto dto);

    CorrentistaDto toDto(Correntista correntista);
}
