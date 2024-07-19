package com.conta.bancaria.correntista.servicos.framework.repository;

import com.conta.bancaria.correntista.servicos.core.domain.model.Transferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferenciaRepository extends JpaRepository<Transferencia, Long> {


    @Query(value = "SELECT c FROM Transferencia c WHERE c.idCorrentistaOrigem = :idCorrentistaOrigem")
    List<Transferencia> findByCorrentistaId(Long idCorrentistaOrigem);


}
