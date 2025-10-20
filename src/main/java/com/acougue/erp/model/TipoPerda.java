// src/main/java/com/acougue/erp/model/TipoPerda.java
package com.acougue.erp.model;

public enum TipoPerda {
    VALIDADE("Perda por Validade Vencida"),
    PROCESSAMENTO("Perda no Processamento"),
    QUEBRA("Quebra/Manuseio"),
    ARMazenAMENTO("Problema de Armazenamento"),
    OUTROS("Outros Motivos");

    private final String descricao;

    TipoPerda(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}