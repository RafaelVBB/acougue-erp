// src/main/java/com/acougue/erp/service/RelatorioService.java
package com.acougue.erp.service;

import com.acougue.erp.model.*;
import com.acougue.erp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ControlePerdaRepository controlePerdaRepository;

    @Autowired
    private ContaReceberRepository contaReceberRepository;

    // RELATÓRIO DE RENTABILIDADE
    public Map<String, Object> gerarRelatorioRentabilidade(LocalDate inicio, LocalDate fim) {
        List<Venda> vendas = vendaRepository.findByDataHoraBetween(
                inicio.atStartOfDay(),
                fim.atTime(23, 59, 59)
        );

        // Agrupar vendas por produto
        Map<Produto, RelatorioProduto> dadosProdutos = new HashMap<>();

        for (Venda venda : vendas) {
            for (ItemVenda item : venda.getItens()) {
                Produto produto = item.getProduto();
                RelatorioProduto dados = dadosProdutos.getOrDefault(produto, new RelatorioProduto(produto));

                dados.adicionarVenda(item.getQuantidade(), item.getSubtotal());
                dadosProdutos.put(produto, dados);
            }
        }

        // Calcular métricas
        List<Map<String, Object>> produtos = dadosProdutos.values().stream()
                .map(RelatorioProduto::toMap)
                .sorted((p1, p2) -> {
                    BigDecimal rent1 = (BigDecimal) p1.get("rentabilidade");
                    BigDecimal rent2 = (BigDecimal) p2.get("rentabilidade");
                    return rent2.compareTo(rent1);
                })
                .collect(Collectors.toList());

        // Métricas gerais
        BigDecimal faturamentoTotal = vendas.stream()
                .map(Venda::getTotalVenda)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal custoTotal = dadosProdutos.values().stream()
                .map(RelatorioProduto::getCustoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal lucroTotal = faturamentoTotal.subtract(custoTotal);
        BigDecimal margemLucro = faturamentoTotal.compareTo(BigDecimal.ZERO) > 0 ?
                lucroTotal.divide(faturamentoTotal, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")) :
                BigDecimal.ZERO;

        return Map.of(
                "periodo", Map.of("inicio", inicio.toString(), "fim", fim.toString()),
                "metricasGerais", Map.of(
                        "faturamentoTotal", faturamentoTotal,
                        "custoTotal", custoTotal,
                        "lucroTotal", lucroTotal,
                        "margemLucro", margemLucro,
                        "quantidadeVendas", vendas.size(),
                        "quantidadeProdutosVendidos", dadosProdutos.size()
                ),
                "produtos", produtos,
                "categorias", agruparPorCategoria(dadosProdutos.values())
        );
    }

    // RELATÓRIO DE CLIENTES
    public Map<String, Object> gerarRelatorioClientes(LocalDate inicio, LocalDate fim) {
        List<Venda> vendas = vendaRepository.findByDataHoraBetween(
                inicio.atStartOfDay(),
                fim.atTime(23, 59, 59)
        );

        // Agrupar por cliente
        Map<Cliente, RelatorioCliente> dadosClientes = new HashMap<>();

        for (Venda venda : vendas) {
            // Em produção, a venda teria cliente associado
            // Por enquanto, usaremos clientes do fiado como exemplo
            Cliente cliente = new Cliente("Cliente Avulso", "", BigDecimal.ZERO);
            RelatorioCliente dados = dadosClientes.getOrDefault(cliente, new RelatorioCliente(cliente));

            dados.adicionarCompra(venda.getTotalVenda());
            dadosClientes.put(cliente, dados);
        }

        // Adicionar clientes fiado
        List<Cliente> clientesFiado = clienteRepository.findByAtivoTrue();
        for (Cliente cliente : clientesFiado) {
            if (cliente.getSaldoDevedor().compareTo(BigDecimal.ZERO) > 0) {
                RelatorioCliente dados = dadosClientes.getOrDefault(cliente, new RelatorioCliente(cliente));
                dados.setDivida(cliente.getSaldoDevedor());
                dadosClientes.put(cliente, dados);
            }
        }

        List<Map<String, Object>> clientes = dadosClientes.values().stream()
                .map(RelatorioCliente::toMap)
                .sorted((c1, c2) -> {
                    BigDecimal valor1 = (BigDecimal) c1.get("valorTotalCompras");
                    BigDecimal valor2 = (BigDecimal) c2.get("valorTotalCompras");
                    return valor2.compareTo(valor1);
                })
                .collect(Collectors.toList());

        // Top clientes
        List<Map<String, Object>> topClientes = clientes.stream()
                .limit(10)
                .collect(Collectors.toList());

        return Map.of(
                "periodo", Map.of("inicio", inicio.toString(), "fim", fim.toString()),
                "totalClientes", clientes.size(),
                "clientesComDivida", clientesFiado.stream()
                        .filter(c -> c.getSaldoDevedor().compareTo(BigDecimal.ZERO) > 0)
                        .count(),
                "topClientes", topClientes,
                "todosClientes", clientes
        );
    }

    // RELATÓRIO DE PREVISÃO DE VENDAS
    public Map<String, Object> gerarPrevisaoVendas(int meses) {
        List<Map<String, Object>> historico = new ArrayList<>();
        List<Map<String, Object>> previsao = new ArrayList<>();

        // Histórico dos últimos 6 meses
        for (int i = 6; i >= 1; i--) {
            YearMonth mes = YearMonth.now().minusMonths(i);
            LocalDate inicio = mes.atDay(1);
            LocalDate fim = mes.atEndOfMonth();

            BigDecimal faturamento = calcularFaturamentoPeriodo(inicio, fim);

            historico.add(Map.of(
                    "mes", mes.getMonth().toString(),
                    "ano", mes.getYear(),
                    "faturamento", faturamento,
                    "tipo", "HISTORICO"
            ));
        }

        // Previsão para os próximos meses (simplificado)
        BigDecimal ultimoFaturamento = calcularFaturamentoPeriodo(
                YearMonth.now().minusMonths(1).atDay(1),
                YearMonth.now().minusMonths(1).atEndOfMonth()
        );

        for (int i = 1; i <= meses; i++) {
            YearMonth mes = YearMonth.now().plusMonths(i);

            // Previsão simples: último mês + 5% de crescimento
            BigDecimal previsaoFaturamento = ultimoFaturamento.multiply(
                    BigDecimal.ONE.add(new BigDecimal("0.05").multiply(new BigDecimal(i)))
            );

            previsao.add(Map.of(
                    "mes", mes.getMonth().toString(),
                    "ano", mes.getYear(),
                    "faturamento", previsaoFaturamento,
                    "tipo", "PREVISAO"
            ));
        }

        return Map.of(
                "historico", historico,
                "previsao", previsao,
                "crescimentoMensal", "5%",
                "observacao", "Previsão baseada em tendência histórica + crescimento estimado"
        );
    }

    // MÉTODOS AUXILIARES
    private BigDecimal calcularFaturamentoPeriodo(LocalDate inicio, LocalDate fim) {
        List<Venda> vendas = vendaRepository.findByDataHoraBetween(
                inicio.atStartOfDay(),
                fim.atTime(23, 59, 59)
        );

        return vendas.stream()
                .map(Venda::getTotalVenda)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Map<String, Object> agruparPorCategoria(Collection<RelatorioProduto> produtos) {
        Map<CategoriaCarne, RelatorioCategoria> categorias = new HashMap<>();

        for (RelatorioProduto produto : produtos) {
            CategoriaCarne categoria = produto.getProduto().getCategoria();
            RelatorioCategoria dados = categorias.getOrDefault(categoria, new RelatorioCategoria(categoria));

            dados.adicionarProduto(produto);
            categorias.put(categoria, dados);
        }

        return categorias.values().stream()
                .map(RelatorioCategoria::toMap)
                .collect(Collectors.toMap(
                        m -> m.get("categoria").toString(),
                        m -> m
                ));
    }

    // CLASSES AUXILIARES PARA RELATÓRIOS
    private static class RelatorioProduto {
        private Produto produto;
        private BigDecimal quantidadeVendida = BigDecimal.ZERO;
        private BigDecimal faturamento = BigDecimal.ZERO;

        public RelatorioProduto(Produto produto) {
            this.produto = produto;
        }

        public void adicionarVenda(BigDecimal quantidade, BigDecimal valor) {
            this.quantidadeVendida = this.quantidadeVendida.add(quantidade);
            this.faturamento = this.faturamento.add(valor);
        }

        public BigDecimal getCustoTotal() {
            if (produto.getPrecoCusto() == null) return BigDecimal.ZERO;
            return produto.getPrecoCusto().multiply(quantidadeVendida);
        }

        public BigDecimal getLucro() {
            return faturamento.subtract(getCustoTotal());
        }

        public BigDecimal getRentabilidade() {
            if (faturamento.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
            return getLucro().divide(faturamento, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
        }

        public Map<String, Object> toMap() {
            return Map.of(
                    "produto", produto.getNome(),
                    "categoria", produto.getCategoria().toString(),
                    "quantidadeVendida", quantidadeVendida,
                    "faturamento", faturamento,
                    "custoTotal", getCustoTotal(),
                    "lucro", getLucro(),
                    "rentabilidade", getRentabilidade(),
                    "margem", produto.calcularRentabilidadeAtual()
            );
        }

        public Produto getProduto() { return produto; }
    }

    private static class RelatorioCliente {
        private Cliente cliente;
        private int quantidadeCompras = 0;
        private BigDecimal valorTotalCompras = BigDecimal.ZERO;
        private BigDecimal divida = BigDecimal.ZERO;

        public RelatorioCliente(Cliente cliente) {
            this.cliente = cliente;
        }

        public void adicionarCompra(BigDecimal valor) {
            this.quantidadeCompras++;
            this.valorTotalCompras = this.valorTotalCompras.add(valor);
        }

        public void setDivida(BigDecimal divida) {
            this.divida = divida;
        }

        public Map<String, Object> toMap() {
            return Map.of(
                    "cliente", cliente.getNome(),
                    "telefone", cliente.getTelefone(),
                    "quantidadeCompras", quantidadeCompras,
                    "valorTotalCompras", valorTotalCompras,
                    "divida", divida,
                    "limiteCredito", cliente.getLimiteCredito(),
                    "ultimaCompra", cliente.getUltimaCompra() != null ? cliente.getUltimaCompra().toString() : "N/A"
            );
        }
    }

    private static class RelatorioCategoria {
        private CategoriaCarne categoria;
        private int quantidadeProdutos = 0;
        private BigDecimal faturamento = BigDecimal.ZERO;
        private BigDecimal lucro = BigDecimal.ZERO;

        public RelatorioCategoria(CategoriaCarne categoria) {
            this.categoria = categoria;
        }

        public void adicionarProduto(RelatorioProduto produto) {
            this.quantidadeProdutos++;
            this.faturamento = this.faturamento.add(produto.faturamento);
            this.lucro = this.lucro.add(produto.getLucro());
        }

        public BigDecimal getRentabilidade() {
            if (faturamento.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
            return lucro.divide(faturamento, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
        }

        public Map<String, Object> toMap() {
            return Map.of(
                    "categoria", categoria.toString(),
                    "quantidadeProdutos", quantidadeProdutos,
                    "faturamento", faturamento,
                    "lucro", lucro,
                    "rentabilidade", getRentabilidade()
            );
        }
    }
}