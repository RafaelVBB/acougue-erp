// src/main/java/com/acougue/erp/repository/ClienteRepository.java
package com.acougue.erp.repository;

import com.acougue.erp.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findByAtivoTrue();

    List<Cliente> findByNomeContainingIgnoreCase(String nome);

    @Query("SELECT c FROM Cliente c WHERE c.saldoDevedor > 0 AND c.ativo = true")
    List<Cliente> findClientesComDivida();

    @Query("SELECT c FROM Cliente c WHERE c.saldoDevedor > c.limiteCredito AND c.ativo = true")
    List<Cliente> findClientesComLimiteEstourado();

    @Query("SELECT SUM(c.saldoDevedor) FROM Cliente c WHERE c.ativo = true")
    BigDecimal getTotalDividas();

    @Query("SELECT COUNT(c) FROM Cliente c WHERE c.saldoDevedor > 0 AND c.ativo = true")
    Long countClientesComDivida();

    Optional<Cliente> findByTelefone(String telefone);

    // Método para melhores clientes (baseado em limite de crédito)
    @Query("SELECT c FROM Cliente c WHERE c.ativo = true ORDER BY c.limiteCredito DESC")
    List<Cliente> findMelhoresClientes();
}