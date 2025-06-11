package com.conta.bancaria.correntista.servicos.core.service;

import com.conta.bancaria.correntista.servicos.adapter.dto.CorrentistaDto;
import com.conta.bancaria.correntista.servicos.core.domain.model.Correntista;
import com.conta.bancaria.correntista.servicos.framework.repository.CorrentistaRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CorrentistaService {

    private final ModelMapper modelMapper;
    private final CorrentistaRepository correntistaRepository;

    public BigDecimal consultaSaldoById(Long id) {
        return correntistaRepository.findById(id)
                .map(Correntista::getSaldo)
                .orElseThrow(() -> new EntityNotFoundException("Correntista com id " + id + " não encontrado."));
    }

    public Correntista getCorrentistaById(Long id) {
        return correntistaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Correntista com id " + id + " não encontrado."));
    }

    public Correntista getCorrentistaByUsuarioId(Long id) {
        Correntista correntista = correntistaRepository.findByIdUsuario(id);
        if (correntista == null) {
            throw new EntityNotFoundException("Correntista com id de usuário " + id + " não encontrado.");
        }
        return correntista;
    }

    public void save(Correntista correntista){
        correntistaRepository.save(correntista);
    }
}