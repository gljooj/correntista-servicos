package com.conta.bancaria.correntista.servicos.framework.repository;

import com.conta.bancaria.correntista.servicos.core.domain.model.Correntista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CorrentistaRepository extends JpaRepository<Correntista, Long> {

    @Query(value = "SELECT c FROM Correntista c WHERE c.idUsuario = :idUsuario")
    Correntista findByIdUsuario(Long idUsuario);




}
