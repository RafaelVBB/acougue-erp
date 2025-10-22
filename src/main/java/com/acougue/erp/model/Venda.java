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

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

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
        if (itens != null && !itens.isEmpty()) {
            this.totalVenda = itens.stream()
                    .map(item -> item.getSubtotal() != null ? item.getSubtotal() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            this.totalVenda = BigDecimal.ZERO;
        }
    }

    public void calcularTroco() {
        if (valorPago != null && totalVenda != null) {
            this.troco = valorPago.subtract(totalVenda);
        } else {
            this.troco = BigDecimal.ZERO;
        }
    }

    public void adicionarItem(ItemVenda item) {
        if (item != null) {
            item.setVenda(this);
            if (this.itens == null) {
                this.itens = new ArrayList<>();
            }
            this.itens.add(item);
            calcularTotal();
        }
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<ItemVenda> getItens() {
        return itens;
    }

    public void setItens(List<ItemVenda> itens) {
        this.itens = itens;
        calcularTotal();
    }

    public BigDecimal getTotalVenda() {
        return totalVenda != null ? totalVenda : BigDecimal.ZERO;
    }

    public void setTotalVenda(BigDecimal totalVenda) {
        this.totalVenda = totalVenda;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public BigDecimal getValorPago() {
        return valorPago != null ? valorPago : BigDecimal.ZERO;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
        calcularTroco();
    }

    public BigDecimal getTroco() {
        return troco != null ? troco : BigDecimal.ZERO;
    }

    public void setTroco(BigDecimal troco) {
        this.troco = troco;
    }

    public StatusVenda getStatus() {
        return status != null ? status : StatusVenda.ABERTA;
    }

    public void setStatus(StatusVenda status) {
        this.status = status;
    }
}