// src/main/java/com/acougue/erp/service/DashboardService.java
package com.acougue.erp.service;

import com.acougue.erp.model.*;
import com.acougue.erp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class DashboardService {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PerdaRepository perdaRepository;

    public Map<String, Object> getDashboardData() {
        Map<String, Object> dashboard = new HashMap<>();

        try {
            // Dados básicos
            long totalProdutos = produtoRepository.count();
            long totalClientes = clienteRepository.count();
            long totalVendas = vendaRepository.count();

            // Vendas do dia (usando método alternativo)
            LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
            LocalDateTime fimDia = LocalDate.now().atTime(LocalTime.MAX);
            List<Venda> vendasHoje = vendaRepository.findByDataHoraBetween(inicioDia, fimDia);
            BigDecimal faturamentoHoje = calcularFaturamentoTotal(vendasHoje);

            // Vendas do mês (usando método alternativo)
            LocalDateTime inicioMes = LocalDate.now().withDayOfMonth(1).atStartOfDay();
            LocalDateTime fimMes = LocalDate.now().atTime(LocalTime.MAX);
            List<Venda> vendasMes = vendaRepository.findByDataHoraBetween(inicioMes, fimMes);
            BigDecimal faturamentoMes = calcularFaturamentoTotal(vendasMes);

            // Produtos com estoque baixo
            List<Produto> produtosEstoqueBaixo = getProdutosEstoqueBaixo();

            dashboard.put("totalProdutos", totalProdutos);
            dashboard.put("totalClientes", totalClientes);
            dashboard.put("totalVendas", totalVendas);
            dashboard.put("faturamentoHoje", faturamentoHoje);
            dashboard.put("faturamentoMes", faturamentoMes);
            dashboard.put("vendasHoje", vendasHoje.size());
            dashboard.put("produtosEstoqueBaixo", produtosEstoqueBaixo);
            dashboard.put("quantidadeEstoqueBaixo", produtosEstoqueBaixo.size());
            dashboard.put("ultimaAtualizacao", LocalDateTime.now());

        } catch (Exception e) {
            dashboard.put("erro", "Erro ao carregar dashboard: " + e.getMessage());
            dashboard.put("totalProdutos", 0);
            dashboard.put("totalClientes", 0);
            dashboard.put("totalVendas", 0);
            dashboard.put("faturamentoHoje", BigDecimal.ZERO);
            dashboard.put("faturamentoMes", BigDecimal.ZERO);
            dashboard.put("vendasHoje", 0);
        }

        return dashboard;
    }

    public Map<String, Object> getMetricasVendas(LocalDate dataInicio, LocalDate dataFim) {
        Map<String, Object> metricas = new HashMap<>();

        try {
            LocalDateTime inicio = dataInicio.atStartOfDay();
            LocalDateTime fim = dataFim.atTime(LocalTime.MAX);

            List<Venda> vendasPeriodo = vendaRepository.findByDataHoraBetween(inicio, fim);

            BigDecimal faturamentoTotal = calcularFaturamentoTotal(vendasPeriodo);
            int totalVendas = vendasPeriodo.size();
            BigDecimal ticketMedio = totalVendas > 0 ?
                    faturamentoTotal.divide(BigDecimal.valueOf(totalVendas), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;

            metricas.put("periodo", dataInicio + " a " + dataFim);
            metricas.put("faturamentoTotal", faturamentoTotal);
            metricas.put("totalVendas", totalVendas);
            metricas.put("ticketMedio", ticketMedio);
            metricas.put("dias", dataInicio.until(dataFim).getDays() + 1);

        } catch (Exception e) {
            metricas.put("erro", "Erro ao calcular métricas: " + e.getMessage());
        }

        return metricas;
    }

    public Map<String, Object> getAlertas() {
        Map<String, Object> alertas = new HashMap<>();

        try {
            List<Produto> produtosEstoqueBaixo = getProdutosEstoqueBaixo();
            List<Map<String, Object>> alertasList = new ArrayList<>();

            // Alertas de estoque baixo
            for (Produto produto : produtosEstoqueBaixo) {
                Map<String, Object> alerta = new HashMap<>();
                alerta.put("tipo", "ESTOQUE_BAIXO");
                alerta.put("mensagem", "Produto " + produto.getNome() + " com estoque baixo: " + produto.getEstoqueAtual() + " unidades");
                alerta.put("produto", produto.getNome());
                alerta.put("estoqueAtual", produto.getEstoqueAtual());
                alerta.put("estoqueMinimo", produto.getEstoqueMinimo());
                alerta.put("prioridade", "ALTA");
                alertasList.add(alerta);
            }

            alertas.put("alertas", alertasList);
            alertas.put("totalAlertas", alertasList.size());
            alertas.put("alertasCriticos", produtosEstoqueBaixo.size());

        } catch (Exception e) {
            alertas.put("erro", "Erro ao carregar alertas: " + e.getMessage());
        }

        return alertas;
    }

    // Métodos auxiliares
    private BigDecimal calcularFaturamentoTotal(List<Venda> vendas) {
        return vendas.stream()
                .map(venda -> venda.getTotalVenda() != null ? venda.getTotalVenda() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<Produto> getProdutosEstoqueBaixo() {
        List<Produto> produtos = produtoRepository.findByAtivoTrue();
        return produtos.stream()
                .filter(p -> p.getEstoqueAtual() <= p.getEstoqueMinimo())
                .toList();
    }
}