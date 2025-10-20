// src/main/java/com/acougue/erp/model/ControlePerda.java
package com.acougue.erp.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "controle_perdas")
public class ControlePerda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(name = "data_registro", nullable = false)
    private LocalDate dataRegistro;

    @Column(name = "quantidade_perda", precision = 10, scale = 3)
    private BigDecimal quantidadePerda;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_perda", nullable = false)
    private TipoPerda tipoPerda;

    @Column(length = 500)
    private String motivo;

    @Column(name = "valor_perda", precision = 10, scale = 2)
    private BigDecimal valorPerda;

    @Column(name = "impacto_precificacao")
    private boolean impactoPrecificacao = false;

    // Construtores
    public ControlePerda() {
        this.dataRegistro = LocalDate.now();
    }

    public ControlePerda(Produto produto, BigDecimal quantidadePerda, TipoPerda tipoPerda, String motivo) {
        this();
        this.produto = produto;
        this.quantidadePerda = quantidadePerda;
        this.tipoPerda = tipoPerda;
        this.motivo = motivo;
        calcularValorPerda();
    }

    // Métodos
    public void calcularValorPerda() {
        if (produto != null && produto.getPrecoCusto() != null && quantidadePerda != null) {
            this.valorPerda = produto.getPrecoCusto().multiply(quantidadePerda);
        }
    }

    public void aplicarImpactoPrecificacao() {
        if (!impactoPrecificacao && produto != null) {
            // Atualiza percentual de perda do produto
            BigDecimal percentualAtual = produto.getPercentualPerda() != null ?
                    produto.getPercentualPerda() : BigDecimal.ZERO;

            // Calcula novo percentual (simplificado)
            BigDecimal novaPerda = percentualAtual.add(BigDecimal.valueOf(0.5)); // +0.5%
            produto.setPercentualPerda(novaPerda.min(BigDecimal.valueOf(20))); // Máximo 20%

            this.impactoPrecificacao = true;
        }
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }
    public LocalDate getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(LocalDate dataRegistro) { this.dataRegistro = dataRegistro; }
    public BigDecimal getQuantidadePerda() { return quantidadePerda; }
    public void setQuantidadePerda(BigDecimal quantidadePerda) {
        this.quantidadePerda = quantidadePerda;
        calcularValorPerda();
    }
    public TipoPerda getTipoPerda() { return tipoPerda; }
    public void setTipoPerda(TipoPerda tipoPerda) { this.tipoPerda = tipoPerda; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public BigDecimal getValorPerda() { return valorPerda; }
    public void setValorPerda(BigDecimal valorPerda) { this.valorPerda = valorPerda; }
    public boolean isImpactoPrecificacao() { return impactoPrecificacao; }
    public void setImpactoPrecificacao(boolean impactoPrecificacao) { this.impactoPrecificacao = impactoPrecificacao; }
}