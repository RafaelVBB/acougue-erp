// src/main/java/com/acougue/erp/repository/ControlePerdaRepository.java
package com.acougue.erp.repository;

import com.acougue.erp.model.ControlePerda;
import com.acougue.erp.model.TipoPerda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ControlePerdaRepository extends JpaRepository<ControlePerda, Long> {

    List<ControlePerda> findByDataRegistroBetween(LocalDate inicio, LocalDate fim);

    List<ControlePerda> findByTipoPerda(TipoPerda tipoPerda);

    List<ControlePerda> findByProdutoId(Long produtoId);

    @Query("SELECT cp FROM ControlePerda cp WHERE cp.dataRegistro BETWEEN :inicio AND :fim ORDER BY cp.valorPerda DESC")
    List<ControlePerda> findTopPerdasPorPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    // ✅ MÉTODO ADICIONADO PARA O DASHBOARD
    @Query("SELECT SUM(cp.valorPerda) FROM ControlePerda cp WHERE cp.dataRegistro BETWEEN :inicio AND :fim")
    BigDecimal sumValorPerdaPorPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    // Método para calcular perdas por tipo em um período
    @Query("SELECT cp.tipoPerda, SUM(cp.valorPerda) FROM ControlePerda cp WHERE cp.dataRegistro BETWEEN :inicio AND :fim GROUP BY cp.tipoPerda")
    List<Object[]> sumValorPerdaPorTipoPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    // Método para buscar as maiores perdas
    @Query("SELECT cp FROM ControlePerda cp ORDER BY cp.valorPerda DESC LIMIT 10")
    List<ControlePerda> findTop10MaioresPerdas();

    // Método para calcular perda total por produto
    @Query("SELECT cp.produto.id, SUM(cp.valorPerda) FROM ControlePerda cp GROUP BY cp.produto.id")
    List<Object[]> sumValorPerdaPorProduto();
}