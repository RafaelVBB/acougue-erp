// src/main/java/com/acougue/erp/model/RelatorioRentabilidade.java
package com.acougue.erp.model;

import java.math.BigDecimal;

public class RelatorioRentabilidade {
    private String produtoNome;
    private String categoria;
    private int quantidadeVendida;
    private int totalVendas;
    private BigDecimal valorTotalVenda = BigDecimal.ZERO;
    private BigDecimal custoTotal = BigDecimal.ZERO;
    private BigDecimal lucroTotal = BigDecimal.ZERO;
    private BigDecimal margemLucro = BigDecimal.ZERO;

    public RelatorioRentabilidade(String produtoNome, String categoria) {
        this.produtoNome = produtoNome;
        this.categoria = categoria;
    }

    // Getters e Setters
    public String getProdutoNome() { return produtoNome; }
    public void setProdutoNome(String produtoNome) { this.produtoNome = produtoNome; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public int getQuantidadeVendida() { return quantidadeVendida; }
    public void setQuantidadeVendida(int quantidadeVendida) { this.quantidadeVendida = quantidadeVendida; }

    public int getTotalVendas() { return totalVendas; }
    public void setTotalVendas(int totalVendas) { this.totalVendas = totalVendas; }

    public BigDecimal getValorTotalVenda() { return valorTotalVenda; }
    public void setValorTotalVenda(BigDecimal valorTotalVenda) { this.valorTotalVenda = valorTotalVenda; }

    public BigDecimal getCustoTotal() { return custoTotal; }
    public void setCustoTotal(BigDecimal custoTotal) { this.custoTotal = custoTotal; }

    public BigDecimal getLucroTotal() { return lucroTotal; }
    public void setLucroTotal(BigDecimal lucroTotal) { this.lucroTotal = lucroTotal; }

    public BigDecimal getMargemLucro() { return margemLucro; }
    public void setMargemLucro(BigDecimal margemLucro) { this.margemLucro = margemLucro; }

    // Métodos auxiliares
    public void addVendas(int quantidade) {
        this.totalVendas += quantidade;
    }

    public void addQuantidadeVendida(double quantidade) {
        this.quantidadeVendida += quantidade;
    }

    public void addValorTotalVenda(BigDecimal valor) {
        this.valorTotalVenda = this.valorTotalVenda.add(valor);
    }

    public void addCustoTotal(BigDecimal custo) {
        this.custoTotal = this.custoTotal.add(custo);
    }

    public void calcularMetricas() {
        this.lucroTotal = valorTotalVenda.subtract(custoTotal);
        if (custoTotal.compareTo(BigDecimal.ZERO) > 0) {
            this.margemLucro = lucroTotal.divide(custoTotal, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
    }
}