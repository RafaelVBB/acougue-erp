// src/main/java/com/acougue/erp/repository/ProdutoRepository.java
package com.acougue.erp.repository;

import com.acougue.erp.model.Produto;
import com.acougue.erp.model.CategoriaCarne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByAtivoTrue();

    List<Produto> findByCategoria(CategoriaCarne categoria);

    List<Produto> findByNomeContainingIgnoreCase(String nome);

    @Query("SELECT p FROM Produto p WHERE p.estoqueAtual <= p.estoqueMinimo AND p.ativo = true")
    List<Produto> findProdutosComEstoqueBaixo();

    @Query("SELECT p FROM Produto p WHERE p.categoria = :categoria AND p.ativo = true ORDER BY p.nome")
    List<Produto> findByCategoriaAtivos(@Param("categoria") CategoriaCarne categoria);

    Optional<Produto> findByNomeAndCategoria(String nome, CategoriaCarne categoria);

    // Método para produtos mais vendidos (simplificado - baseado em estoque)
    @Query("SELECT p FROM Produto p WHERE p.ativo = true ORDER BY p.estoqueAtual ASC")
    List<Produto> findProdutosMaisVendidos();

    // Método para calcular valor total do estoque
    @Query("SELECT SUM(p.precoVenda * p.estoqueAtual) FROM Produto p WHERE p.ativo = true")
    Double calcularValorTotalEstoque();
}