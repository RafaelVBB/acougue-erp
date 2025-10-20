// src/main/java/com/acougue/erp/repository/VendaRepository.java
package com.acougue.erp.repository;

import com.acougue.erp.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {

    // ✅ MÉTODO ADICIONADO PARA O DASHBOARD
    List<Venda> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT v FROM Venda v WHERE v.status = 'FINALIZADA' ORDER BY v.dataHora DESC")
    List<Venda> findVendasFinalizadas();

    // Método para buscar vendas por período (para relatórios)
    @Query("SELECT v FROM Venda v WHERE v.dataHora BETWEEN :inicio AND :fim AND v.status = 'FINALIZADA'")
    List<Venda> findVendasFinalizadasPorPeriodo(@Param("inicio") LocalDateTime inicio,
                                                @Param("fim") LocalDateTime fim);

    // Método para calcular faturamento total por período
    @Query("SELECT SUM(v.totalVenda) FROM Venda v WHERE v.dataHora BETWEEN :inicio AND :fim AND v.status = 'FINALIZADA'")
    Double calcularFaturamentoPorPeriodo(@Param("inicio") LocalDateTime inicio,
                                         @Param("fim") LocalDateTime fim);

    // Método para contar quantidade de vendas por período
    @Query("SELECT COUNT(v) FROM Venda v WHERE v.dataHora BETWEEN :inicio AND :fim AND v.status = 'FINALIZADA'")
    Long countVendasPorPeriodo(@Param("inicio") LocalDateTime inicio,
                               @Param("fim") LocalDateTime fim);
}