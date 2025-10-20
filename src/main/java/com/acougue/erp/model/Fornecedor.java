// src/main/java/com/acougue/erp/model/Fornecedor.java
package com.acougue.erp.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fornecedores")
public class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String cnpj;
    private String telefone;
    private String email;
    private String endereco;

    @Enumerated(EnumType.STRING)
    private TipoFornecedor tipo;

    private boolean ativo = true;

    @Column(name = "data_cadastro")
    private LocalDate dataCadastro;

    @OneToMany(mappedBy = "fornecedor", cascade = CascadeType.ALL)
    private List<Compra> compras = new ArrayList<>();

    public Fornecedor() {
        this.dataCadastro = LocalDate.now();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public TipoFornecedor getTipo() { return tipo; }
    public void setTipo(TipoFornecedor tipo) { this.tipo = tipo; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public LocalDate getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDate dataCadastro) { this.dataCadastro = dataCadastro; }
    public List<Compra> getCompras() { return compras; }
    public void setCompras(List<Compra> compras) { this.compras = compras; }
}