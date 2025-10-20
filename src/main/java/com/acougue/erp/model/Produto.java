package com.acougue.erp.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaCarne categoria;

    @Column(length = 50)
    private String corte;

    @Column(name = "preco_custo", precision = 10, scale = 2)
    private BigDecimal precoCusto;

    @Column(name = "preco_venda", precision = 10, scale = 2)
    private BigDecimal precoVenda;

    @Column(name = "percentual_perda", precision = 5, scale = 2)
    private BigDecimal percentualPerda = BigDecimal.ZERO;

    @Column(name = "percentual_quebra", precision = 5, scale = 2)
    private BigDecimal percentualQuebra = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidade_medida", nullable = false)
    private UnidadeMedida unidadeMedida = UnidadeMedida.KG;

    @Column(name = "estoque_atual")
    private Integer estoqueAtual = 0;

    @Column(name = "estoque_minimo")
    private Integer estoqueMinimo = 0;

    private boolean ativo = true;

    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    // Construtores
    public Produto() {}

    public Produto(String nome, CategoriaCarne categoria, BigDecimal precoVenda, UnidadeMedida unidadeMedida) {
        this.nome = nome;
        this.categoria = categoria;
        this.precoVenda = precoVenda;
        this.unidadeMedida = unidadeMedida;
    }

    @PrePersist
    protected void onCreate() {
        dataCadastro = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public CategoriaCarne getCategoria() { return categoria; }
    public void setCategoria(CategoriaCarne categoria) { this.categoria = categoria; }
    public String getCorte() { return corte; }
    public void setCorte(String corte) { this.corte = corte; }
    public BigDecimal getPrecoCusto() { return precoCusto; }
    public void setPrecoCusto(BigDecimal precoCusto) { this.precoCusto = precoCusto; }
    public BigDecimal getPrecoVenda() { return precoVenda; }
    public void setPrecoVenda(BigDecimal precoVenda) { this.precoVenda = precoVenda; }
    public BigDecimal getPercentualPerda() { return percentualPerda; }
    public void setPercentualPerda(BigDecimal percentualPerda) { this.percentualPerda = percentualPerda; }
    public BigDecimal getPercentualQuebra() { return percentualQuebra; }
    public void setPercentualQuebra(BigDecimal percentualQuebra) { this.percentualQuebra = percentualQuebra; }
    public UnidadeMedida getUnidadeMedida() { return unidadeMedida; }
    public void setUnidadeMedida(UnidadeMedida unidadeMedida) { this.unidadeMedida = unidadeMedida; }
    public Integer getEstoqueAtual() { return estoqueAtual; }
    public void setEstoqueAtual(Integer estoqueAtual) { this.estoqueAtual = estoqueAtual; }
    public Integer getEstoqueMinimo() { return estoqueMinimo; }
    public void setEstoqueMinimo(Integer estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

    // Método para calcular preço de venda considerando perdas e margem
    public BigDecimal calcularPrecoVendaComMargem(BigDecimal margemLucro) {
        if (precoCusto == null) {
            return precoVenda != null ? precoVenda : BigDecimal.ZERO;
        }

        // Fórmula: PreçoVenda = PreçoCusto / (1 - %Perdas) * (1 + %Margem)
        BigDecimal percentualPerdasTotal = calcularPercentualPerdasTotal();
        BigDecimal fatorPerdas = BigDecimal.ONE.subtract(percentualPerdasTotal);

        // Evitar divisão por zero
        if (fatorPerdas.compareTo(BigDecimal.ZERO) <= 0) {
            fatorPerdas = new BigDecimal("0.01"); // Mínimo 1%
        }

        BigDecimal custoAjustado = precoCusto.divide(fatorPerdas, 4, java.math.RoundingMode.HALF_UP);
        BigDecimal fatorMargem = BigDecimal.ONE.add(margemLucro.divide(new BigDecimal("100")));

        return custoAjustado.multiply(fatorMargem).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    // Calcular percentual total de perdas
    public BigDecimal calcularPercentualPerdasTotal() {
        BigDecimal perda = percentualPerda != null ? percentualPerda : BigDecimal.ZERO;
        BigDecimal quebra = percentualQuebra != null ? percentualQuebra : BigDecimal.ZERO;
        return perda.add(quebra).divide(new BigDecimal("100"));
    }

    // Método para atualizar preço automaticamente
    public void atualizarPrecoVendaAutomatico(BigDecimal margemLucro) {
        BigDecimal novoPreco = calcularPrecoVendaComMargem(margemLucro);
        if (novoPreco.compareTo(BigDecimal.ZERO) > 0) {
            this.precoVenda = novoPreco;
        }
    }

    // Método para calcular rentabilidade atual
    public BigDecimal calcularRentabilidadeAtual() {
        if (precoCusto == null || precoVenda == null || precoCusto.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return precoVenda.subtract(precoCusto)
                .divide(precoCusto, 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }
}