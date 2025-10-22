// src/main/java/com/acougue/erp/repository/VendaRepository.java
package com.acougue.erp.repository;

import com.acougue.erp.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {

    // Método para buscar vendas por período (usando LocalDateTime)
    List<Venda> findByDataHoraBetween(LocalDateTime dataInicio, LocalDateTime dataFim);

    // Método para buscar vendas do dia atual - CORRIGIDO
    @Query("SELECT v FROM Venda v WHERE FUNCTION('DATE', v.dataHora) = CURRENT_DATE")
    List<Venda> findVendasDoDia();

    // Método para buscar vendas do mês atual - CORRIGIDO
    @Query("SELECT v FROM Venda v WHERE FUNCTION('YEAR', v.dataHora) = FUNCTION('YEAR', CURRENT_DATE) AND FUNCTION('MONTH', v.dataHora) = FUNCTION('MONTH', CURRENT_DATE)")
    List<Venda> findVendasDoMes();
}