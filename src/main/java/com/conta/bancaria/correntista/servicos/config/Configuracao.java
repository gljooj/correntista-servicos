package com.conta.bancaria.correntista.servicos.config;
import com.conta.bancaria.correntista.servicos.dto.TransferenciaResponseDto;
import com.conta.bancaria.correntista.servicos.model.Transferencia;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Configuracao {

    @Bean
    public ModelMapper getModelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.typeMap(TransferenciaResponseDto.class, Transferencia.class).addMappings(mapper -> {
            mapper.map(TransferenciaResponseDto::getIdCorrentistaOrigem, Transferencia::setIdCorrentistaOrigem);
            mapper.map(TransferenciaResponseDto::getIdCorrentistaDestino, Transferencia::setIdCorrentistaDestino);
        });

        return modelMapper;
    }
}




