// src/main/java/com/acougue/erp/model/Perda.java
package com.acougue.erp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "perdas")
public class Perda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    private Double quantidade;

    @Column(name = "valor_estimado")
    private BigDecimal valorEstimado;

    private String motivo;

    @Column(name = "data_registro")
    private LocalDate dataRegistro;

    @PrePersist
    protected void onCreate() {
        dataRegistro = LocalDate.now();
    }
}