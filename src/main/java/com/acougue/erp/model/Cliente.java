package com.acougue.erp.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 20)
    private String telefone;

    @Column(length = 100)
    private String email;

    @Column(name = "limite_credito", precision = 10, scale = 2)
    private BigDecimal limiteCredito = BigDecimal.ZERO;

    @Column(name = "saldo_devedor", precision = 10, scale = 2)
    private BigDecimal saldoDevedor = BigDecimal.ZERO;

    @Column(name = "data_cadastro")
    private LocalDate dataCadastro;

    @Column(name = "ultima_compra")
    private LocalDate ultimaCompra;

    private boolean ativo = true;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ContaReceber> contasReceber = new ArrayList<>();

    public Cliente() {
        this.dataCadastro = LocalDate.now();
    }

    public Cliente(String nome, String telefone, BigDecimal limiteCredito) {
        this();
        this.nome = nome;
        this.telefone = telefone;
        this.limiteCredito = limiteCredito;
    }

    public boolean podeComprarFiado(BigDecimal valorCompra) {
        if (!ativo || limiteCredito == null) return false;
        BigDecimal novoSaldo = saldoDevedor.add(valorCompra);
        return novoSaldo.compareTo(limiteCredito) <= 0;
    }

    public void adicionarDivida(BigDecimal valor) {
        this.saldoDevedor = saldoDevedor.add(valor);
        this.ultimaCompra = LocalDate.now();
    }

    public void quitarDivida(BigDecimal valor) {
        this.saldoDevedor = saldoDevedor.subtract(valor).max(BigDecimal.ZERO);
    }

    public BigDecimal getCreditoDisponivel() {
        return limiteCredito.subtract(saldoDevedor).max(BigDecimal.ZERO);
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public BigDecimal getLimiteCredito() { return limiteCredito; }
    public void setLimiteCredito(BigDecimal limiteCredito) { this.limiteCredito = limiteCredito; }
    public BigDecimal getSaldoDevedor() { return saldoDevedor; }
    public void setSaldoDevedor(BigDecimal saldoDevedor) { this.saldoDevedor = saldoDevedor; }
    public LocalDate getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDate dataCadastro) { this.dataCadastro = dataCadastro; }
    public LocalDate getUltimaCompra() { return ultimaCompra; }
    public void setUltimaCompra(LocalDate ultimaCompra) { this.ultimaCompra = ultimaCompra; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public List<ContaReceber> getContasReceber() { return contasReceber; }
    public void setContasReceber(List<ContaReceber> contasReceber) { this.contasReceber = contasReceber; }

    public void setCpf(String s) {
    }

    public void setEndereco(String s) {

    }
}