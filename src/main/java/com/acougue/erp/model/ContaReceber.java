package com.acougue.erp.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "contas_receber")
public class ContaReceber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "venda_id")
    private Venda venda;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "valor_pago", precision = 10, scale = 2)
    private BigDecimal valorPago = BigDecimal.ZERO;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    @Enumerated(EnumType.STRING)
    private StatusConta status = StatusConta.ABERTA;

    @Column(length = 500)
    private String observacao;

    public ContaReceber() {}

    public ContaReceber(Cliente cliente, Venda venda, BigDecimal valor, LocalDate dataVencimento) {
        this.cliente = cliente;
        this.venda = venda;
        this.valor = valor;
        this.dataVencimento = dataVencimento;
    }

    public void registrarPagamento(BigDecimal valorPago, LocalDate dataPagamento) {
        this.valorPago = valorPago;
        this.dataPagamento = dataPagamento;
        this.status = StatusConta.PAGA;
        cliente.quitarDivida(valorPago);
    }

    public boolean estaVencida() {
        return status == StatusConta.ABERTA && LocalDate.now().isAfter(dataVencimento);
    }

    public BigDecimal getSaldoDevedor() {
        return valor.subtract(valorPago);
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public Venda getVenda() { return venda; }
    public void setVenda(Venda venda) { this.venda = venda; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public BigDecimal getValorPago() { return valorPago; }
    public void setValorPago(BigDecimal valorPago) { this.valorPago = valorPago; }
    public LocalDate getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(LocalDate dataVencimento) { this.dataVencimento = dataVencimento; }
    public LocalDate getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDate dataPagamento) { this.dataPagamento = dataPagamento; }
    public StatusConta getStatus() { return status; }
    public void setStatus(StatusConta status) { this.status = status; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}