// src/main/java/com/acougue/erp/service/RelatorioService.java
package com.acougue.erp.service;

import com.acougue.erp.model.*;
import com.acougue.erp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class RelatorioService {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PerdaRepository perdaRepository;

    // Método básico de relatório - versão simplificada
    public Map<String, Object> getRelatorioBasico() {
        Map<String, Object> relatorio = new HashMap<>();

        try {
            // Dados básicos
            long totalProdutos = produtoRepository.count();
            long totalClientes = clienteRepository.count();
            long totalVendas = vendaRepository.count();

            relatorio.put("totalProdutos", totalProdutos);
            relatorio.put("totalClientes", totalClientes);
            relatorio.put("totalVendas", totalVendas);
            relatorio.put("dataGeracao", LocalDate.now());

        } catch (Exception e) {
            relatorio.put("erro", "Erro ao gerar relatório: " + e.getMessage());
        }

        return relatorio;
    }

    // Relatório de produtos mais vendidos - versão simplificada
    public Map<String, Object> getProdutosMaisVendidos() {
        Map<String, Object> resultado = new HashMap<>();

        try {
            List<Produto> produtos = produtoRepository.findByAtivoTrue();
            List<Map<String, Object>> produtosInfo = new ArrayList<>();

            for (Produto produto : produtos) {
                Map<String, Object> info = new HashMap<>();
                info.put("nome", produto.getNome());
                info.put("categoria", produto.getCategoria());
                info.put("precoVenda", produto.getPrecoVenda());
                info.put("estoqueAtual", produto.getEstoqueAtual());
                produtosInfo.add(info);
            }

            resultado.put("produtos", produtosInfo);
            resultado.put("total", produtosInfo.size());

        } catch (Exception e) {
            resultado.put("erro", "Erro ao buscar produtos: " + e.getMessage());
        }

        return resultado;
    }

    // Relatório de clientes - versão simplificada
    public Map<String, Object> getRelatorioClientes() {
        Map<String, Object> resultado = new HashMap<>();

        try {
            List<Cliente> clientes = clienteRepository.findAll();
            List<Map<String, Object>> clientesInfo = new ArrayList<>();

            for (Cliente cliente : clientes) {
                Map<String, Object> info = new HashMap<>();
                info.put("nome", cliente.getNome());
                info.put("telefone", cliente.getTelefone());
                info.put("email", cliente.getEmail());
                clientesInfo.add(info);
            }

            resultado.put("clientes", clientesInfo);
            resultado.put("totalClientes", clientesInfo.size());

        } catch (Exception e) {
            resultado.put("erro", "Erro ao buscar clientes: " + e.getMessage());
        }

        return resultado;
    }

    // Método para dashboard - versão simplificada
    public Map<String, Object> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();

        try {
            // Estatísticas básicas
            long totalProdutos = produtoRepository.count();
            long totalClientes = clienteRepository.count();
            long totalVendas = vendaRepository.count();

            // Produtos com estoque baixo
            List<Produto> produtos = produtoRepository.findByAtivoTrue();
            List<Map<String, Object>> estoqueBaixo = new ArrayList<>();

            for (Produto produto : produtos) {
                if (produto.getEstoqueAtual() <= produto.getEstoqueMinimo()) {
                    Map<String, Object> info = new HashMap<>();
                    info.put("nome", produto.getNome());
                    info.put("estoqueAtual", produto.getEstoqueAtual());
                    info.put("estoqueMinimo", produto.getEstoqueMinimo());
                    estoqueBaixo.add(info);
                }
            }

            dashboard.put("totalProdutos", totalProdutos);
            dashboard.put("totalClientes", totalClientes);
            dashboard.put("totalVendas", totalVendas);
            dashboard.put("produtosEstoqueBaixo", estoqueBaixo);
            dashboard.put("quantidadeEstoqueBaixo", estoqueBaixo.size());
            dashboard.put("ultimaAtualizacao", LocalDate.now());

        } catch (Exception e) {
            dashboard.put("erro", "Erro ao gerar dashboard: " + e.getMessage());
        }

        return dashboard;
    }
}