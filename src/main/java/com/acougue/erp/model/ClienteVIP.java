// src/main/java/com/acougue/erp/model/ClienteVIP.java
package com.acougue.erp.model;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ClienteVIP {
    private Cliente cliente;
    private int totalCompras;
    private BigDecimal totalGasto;
    private BigDecimal ticketMedio;

    public ClienteVIP(Cliente cliente, int totalCompras, BigDecimal totalGasto) {
        this.cliente = cliente;
        this.totalCompras = totalCompras;
        this.totalGasto = totalGasto;
        this.ticketMedio = totalCompras > 0 ?
                totalGasto.divide(BigDecimal.valueOf(totalCompras), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
    }
}