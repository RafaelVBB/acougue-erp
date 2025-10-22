// src/main/java/com/acougue/erp/repository/PerdaRepository.java
package com.acougue.erp.repository;

import com.acougue.erp.model.Perda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PerdaRepository extends JpaRepository<Perda, Long> {
    List<Perda> findByDataRegistroBetween(LocalDate dataInicio, LocalDate dataFim);
}