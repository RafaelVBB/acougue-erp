// src/main/java/com/acougue/erp/repository/ContaReceberRepository.java
package com.acougue.erp.repository;

import com.acougue.erp.model.ContaReceber;
import com.acougue.erp.model.StatusConta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ContaReceberRepository extends JpaRepository<ContaReceber, Long> {

    List<ContaReceber> findByClienteId(Long clienteId);

    List<ContaReceber> findByStatus(StatusConta status);

    List<ContaReceber> findByDataVencimentoBetween(LocalDate inicio, LocalDate fim);

    @Query("SELECT cr FROM ContaReceber cr WHERE cr.dataVencimento < :dataAtual AND cr.status = 'ABERTA'")
    List<ContaReceber> findContasVencidas(@Param("dataAtual") LocalDate dataAtual);

    @Query("SELECT SUM(cr.valor - cr.valorPago) FROM ContaReceber cr WHERE cr.status = 'ABERTA'")
    BigDecimal getTotalAReceber();

    @Query("SELECT SUM(cr.valor - cr.valorPago) FROM ContaReceber cr WHERE cr.dataVencimento < :dataAtual AND cr.status = 'ABERTA'")
    BigDecimal getTotalVencido(@Param("dataAtual") LocalDate dataAtual);

    List<ContaReceber> findByClienteIdAndStatus(Long clienteId, StatusConta status);

    // Método para contas a receber por período de vencimento
    @Query("SELECT cr FROM ContaReceber cr WHERE cr.dataVencimento BETWEEN :inicio AND :fim ORDER BY cr.dataVencimento")
    List<ContaReceber> findContasPorVencimentoPeriodo(@Param("inicio") LocalDate inicio,
                                                      @Param("fim") LocalDate fim);
}