package com.conta.bancaria.correntista.servicos.framework.config;

import com.conta.bancaria.correntista.servicos.adapter.dto.TransferenciaResponseDto;
import com.conta.bancaria.correntista.servicos.core.domain.model.Transferencia;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Configuracao {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        Converter<Transferencia, TransferenciaResponseDto> transferenciaToDtoConverter = context -> {
            Transferencia source = context.getSource();

            return new TransferenciaResponseDto(
                    source.getId(),
                    source.getIdCorrentistaOrigem(),
                    source.getIdCorrentistaDestino(),
                    source.getValor(),
                    source.getStatusTransacao(),
                    source.getStatusBacen()
            );
        };

        modelMapper.createTypeMap(Transferencia.class, TransferenciaResponseDto.class)
                .setConverter(transferenciaToDtoConverter);

        return modelMapper;
    }
}