// src/main/java/com/acougue/erp/model/Venda.java
package com.acougue.erp.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vendas")
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_hora")
    private LocalDateTime dataHora;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ItemVenda> itens = new ArrayList<>();

    @Column(name = "total_venda", precision = 10, scale = 2)
    private BigDecimal totalVenda;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento")
    private FormaPagamento formaPagamento;

    @Column(name = "valor_pago", precision = 10, scale = 2)
    private BigDecimal valorPago;

    @Column(name = "troco", precision = 10, scale = 2)
    private BigDecimal troco;

    @Enumerated(EnumType.STRING)
    private StatusVenda status;

    // Construtores
    public Venda() {
        this.dataHora = LocalDateTime.now();
        this.status = StatusVenda.ABERTA;
        this.totalVenda = BigDecimal.ZERO;
    }

    // Métodos
    public void calcularTotal() {
        this.totalVenda = itens.stream()
                .map(ItemVenda::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void calcularTroco() {
        if (valorPago != null && totalVenda != null) {
            this.troco = valorPago.subtract(totalVenda);
        }
    }

    public void adicionarItem(ItemVenda item) {
        item.setVenda(this);
        this.itens.add(item);
        calcularTotal();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public List<ItemVenda> getItens() { return itens; }
    public void setItens(List<ItemVenda> itens) { this.itens = itens; }
    public BigDecimal getTotalVenda() { return totalVenda; }
    public void setTotalVenda(BigDecimal totalVenda) { this.totalVenda = totalVenda; }
    public FormaPagamento getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(FormaPagamento formaPagamento) { this.formaPagamento = formaPagamento; }
    public BigDecimal getValorPago() { return valorPago; }
    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
        calcularTroco();
    }
    public BigDecimal getTroco() { return troco; }
    public void setTroco(BigDecimal troco) { this.troco = troco; }
    public StatusVenda getStatus() { return status; }
    public void setStatus(StatusVenda status) { this.status = status; }
}