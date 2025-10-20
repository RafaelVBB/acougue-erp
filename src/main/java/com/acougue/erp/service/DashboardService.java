// src/main/java/com/acougue/erp/service/DashboardService.java
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
public class DashboardService {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ContaReceberRepository contaReceberRepository;

    @Autowired
    private ControlePerdaRepository controlePerdaRepository;
// ADICIONE estes métodos ao DashboardService.java

    public Map<String, Object> getMetricasAvancadas() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioMes = YearMonth.now().atDay(1);
        LocalDate fimMes = YearMonth.now().atEndOfMonth();

        // KPIs Avançados
        BigDecimal faturamentoMes = calcularFaturamentoMes(inicioMes, fimMes);
        BigDecimal custoMes = calcularCustoMes(inicioMes, fimMes);
        BigDecimal lucroMes = faturamentoMes.subtract(custoMes);

        // Métricas de Eficiência
        BigDecimal ticketMedio = calcularTicketMedio(inicioMes, fimMes);
        BigDecimal giroEstoque = calcularGiroEstoque();
        BigDecimal margemLucro = faturamentoMes.compareTo(BigDecimal.ZERO) > 0 ?
                lucroMes.divide(faturamentoMes, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")) :
                BigDecimal.ZERO;

        return Map.of(
                "periodo", Map.of("inicio", inicioMes.toString(), "fim", fimMes.toString()),
                "kpis", Map.of(
                        "faturamentoMes", faturamentoMes,
                        "custoMes", custoMes,
                        "lucroMes", lucroMes,
                        "margemLucro", margemLucro,
                        "ticketMedio", ticketMedio,
                        "giroEstoque", giroEstoque
                ),
                "alertas", getAlertasAvancados(),
                "metas", calcularProgressoMetas()
        );
    }

    private BigDecimal calcularCustoMes(LocalDate inicio, LocalDate fim) {
        // Implementar cálculo de custos (compras + perdas + despesas)
        return BigDecimal.valueOf(15000.00); // Exemplo
    }

    private BigDecimal calcularTicketMedio(LocalDate inicio, LocalDate fim) {
        List<Venda> vendas = vendaRepository.findByDataHoraBetween(
                inicio.atStartOfDay(),
                fim.atTime(23, 59, 59)
        );

        if (vendas.isEmpty()) return BigDecimal.ZERO;

        BigDecimal totalVendas = vendas.stream()
                .map(Venda::getTotalVenda)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalVendas.divide(BigDecimal.valueOf(vendas.size()), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularGiroEstoque() {
        // Implementar cálculo de giro de estoque
        return BigDecimal.valueOf(2.5); // Exemplo
    }

    private Map<String, Object> getAlertasAvancados() {
        List<Map<String, Object>> alertas = new ArrayList<>();

        // Alertas de Margem
        List<Produto> produtosBaixaMargem = produtoRepository.findByAtivoTrue().stream()
                .filter(p -> p.calcularRentabilidadeAtual().compareTo(new BigDecimal("10")) < 0)
                .collect(Collectors.toList());

        if (!produtosBaixaMargem.isEmpty()) {
            alertas.add(Map.of(
                    "tipo", "MARGEM_BAIXA",
                    "severidade", "ALTA",
                    "mensagem", produtosBaixaMargem.size() + " produtos com margem abaixo de 10%",
                    "produtos", produtosBaixaMargem.stream()
                            .map(p -> p.getNome() + " (" + p.calcularRentabilidadeAtual() + "%)")
                            .collect(Collectors.toList())
            ));
        }

        return Map.of("alertas", alertas, "total", alertas.size());
    }

    private Map<String, Object> calcularProgressoMetas() {
        return Map.of(
                "faturamento", Map.of("meta", 50000, "atingido", 35000, "percentual", 70),
                "clientes", Map.of("meta", 100, "atingido", 85, "percentual", 85),
                "lucro", Map.of("meta", 12000, "atingido", 8500, "percentual", 71)
        );
    }
    public Map<String, Object> getDashboardData() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioMes = YearMonth.now().atDay(1);
        LocalDate fimMes = YearMonth.now().atEndOfMonth();

        // Métricas principais
        BigDecimal faturamentoMes = calcularFaturamentoMes(inicioMes, fimMes);
        BigDecimal totalAReceber = contaReceberRepository.getTotalAReceber();
        BigDecimal totalPerdasMes = calcularPerdasMes(inicioMes, fimMes);
        Long clientesAtivos = (long) clienteRepository.findByAtivoTrue().size();

        // Produtos mais vendidos
        List<Map<String, Object>> produtosMaisVendidos = getProdutosMaisVendidos();

        // Alertas
        List<Map<String, Object>> alertas = getAlertas();

        // Evolução do faturamento (últimos 6 meses)
        List<Map<String, Object>> evolucaoFaturamento = getEvolucaoFaturamento();

        return Map.of(
                "periodo", Map.of(
                        "inicio", inicioMes.toString(),
                        "fim", fimMes.toString()
                ),
                "metricas", Map.of(
                        "faturamentoMes", faturamentoMes != null ? faturamentoMes : BigDecimal.ZERO,
                        "totalAReceber", totalAReceber != null ? totalAReceber : BigDecimal.ZERO,
                        "totalPerdasMes", totalPerdasMes,
                        "clientesAtivos", clientesAtivos,
                        "lucroEstimado", calcularLucroEstimado(faturamentoMes, totalPerdasMes)
                ),
                "produtosMaisVendidos", produtosMaisVendidos,
                "alertas", alertas,
                "evolucaoFaturamento", evolucaoFaturamento,
                "resumoCategorias", getResumoPorCategoria()
        );
    }

    private BigDecimal calcularFaturamentoMes(LocalDate inicio, LocalDate fim) {
        List<Venda> vendas = vendaRepository.findByDataHoraBetween(
                inicio.atStartOfDay(),
                fim.atTime(23, 59, 59)
        );

        return vendas.stream()
                .map(Venda::getTotalVenda)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calcularPerdasMes(LocalDate inicio, LocalDate fim) {
        BigDecimal totalPerdas = controlePerdaRepository.sumValorPerdaPorPeriodo(inicio, fim);
        return totalPerdas != null ? totalPerdas : BigDecimal.ZERO;
    }

    private BigDecimal calcularLucroEstimado(BigDecimal faturamento, BigDecimal perdas) {
        // Estimativa simplificada: 25% de margem líquida
        BigDecimal margemLiquida = new BigDecimal("0.25");
        return faturamento.multiply(margemLiquida).subtract(perdas).max(BigDecimal.ZERO);
    }

    private List<Map<String, Object>> getProdutosMaisVendidos() {
        List<Produto> produtos = produtoRepository.findByAtivoTrue();

        return produtos.stream()
                .sorted((p1, p2) -> Integer.compare(p2.getEstoqueAtual(), p1.getEstoqueAtual()))
                .limit(5)
                .map(produto -> {
                    Map<String, Object> produtoMap = new HashMap<>();
                    produtoMap.put("nome", produto.getNome());
                    produtoMap.put("categoria", produto.getCategoria().toString());
                    produtoMap.put("precoVenda", produto.getPrecoVenda());
                    produtoMap.put("estoque", produto.getEstoqueAtual());
                    produtoMap.put("rentabilidade", produto.calcularRentabilidadeAtual());
                    return produtoMap;
                })
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> getAlertas() {
        List<Map<String, Object>> alertas = new ArrayList<>();

        // Alertas de estoque baixo
        List<Produto> produtosEstoqueBaixo = produtoRepository.findProdutosComEstoqueBaixo();
        if (!produtosEstoqueBaixo.isEmpty()) {
            Map<String, Object> alertaEstoque = new HashMap<>();
            alertaEstoque.put("tipo", "ESTOQUE_BAIXO");
            alertaEstoque.put("severidade", "ALTA");
            alertaEstoque.put("mensagem", produtosEstoqueBaixo.size() + " produtos com estoque baixo");

            List<String> detalhes = produtosEstoqueBaixo.stream()
                    .map(p -> p.getNome() + " (" + p.getEstoqueAtual() + " " + p.getUnidadeMedida() + ")")
                    .collect(Collectors.toList());
            alertaEstoque.put("detalhes", detalhes);

            alertas.add(alertaEstoque);
        }

        // Alertas de contas vencidas
        List<ContaReceber> contasVencidas = contaReceberRepository.findContasVencidas(LocalDate.now());
        if (!contasVencidas.isEmpty()) {
            Map<String, Object> alertaContas = new HashMap<>();
            alertaContas.put("tipo", "CONTAS_VENCIDAS");
            alertaContas.put("severidade", "MEDIA");
            alertaContas.put("mensagem", contasVencidas.size() + " contas vencidas");

            BigDecimal valorTotal = contasVencidas.stream()
                    .map(ContaReceber::getSaldoDevedor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            alertaContas.put("valorTotal", valorTotal);

            alertas.add(alertaContas);
        }

        // Alertas de limite estourado
        List<Cliente> clientesLimiteEstourado = clienteRepository.findClientesComLimiteEstourado();
        if (!clientesLimiteEstourado.isEmpty()) {
            Map<String, Object> alertaLimite = new HashMap<>();
            alertaLimite.put("tipo", "LIMITE_ESTOURADO");
            alertaLimite.put("severidade", "MEDIA");
            alertaLimite.put("mensagem", clientesLimiteEstourado.size() + " clientes com limite estourado");

            alertas.add(alertaLimite);
        }

        return alertas;
    }

    private List<Map<String, Object>> getEvolucaoFaturamento() {
        List<Map<String, Object>> evolucao = new ArrayList<>();

        for (int i = 5; i >= 0; i--) {
            YearMonth mes = YearMonth.now().minusMonths(i);
            LocalDate inicio = mes.atDay(1);
            LocalDate fim = mes.atEndOfMonth();

            BigDecimal faturamento = calcularFaturamentoMes(inicio, fim);

            Map<String, Object> mesMap = new HashMap<>();
            mesMap.put("mes", mes.getMonth().toString());
            mesMap.put("ano", mes.getYear());
            mesMap.put("faturamento", faturamento != null ? faturamento : BigDecimal.ZERO);

            evolucao.add(mesMap);
        }

        return evolucao;
    }

    private Map<String, Object> getResumoPorCategoria() {
        List<Produto> produtos = produtoRepository.findByAtivoTrue();

        Map<CategoriaCarne, List<Produto>> produtosPorCategoria = produtos.stream()
                .collect(Collectors.groupingBy(Produto::getCategoria));

        Map<String, Object> resumo = new HashMap<>();

        for (Map.Entry<CategoriaCarne, List<Produto>> entry : produtosPorCategoria.entrySet()) {
            CategoriaCarne categoria = entry.getKey();
            List<Produto> produtosCategoria = entry.getValue();

            BigDecimal valorEstoque = produtosCategoria.stream()
                    .map(p -> p.getPrecoVenda() != null ? p.getPrecoVenda() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> categoriaMap = new HashMap<>();
            categoriaMap.put("quantidadeProdutos", produtosCategoria.size());
            categoriaMap.put("valorEstoque", valorEstoque);
            categoriaMap.put("estoqueTotal", produtosCategoria.stream()
                    .mapToInt(Produto::getEstoqueAtual)
                    .sum());

            resumo.put(categoria.toString(), categoriaMap);
        }

        return resumo;
    }

    public Map<String, Object> getRelatorioVendas(LocalDate inicio, LocalDate fim) {
        List<Venda> vendas = vendaRepository.findByDataHoraBetween(
                inicio.atStartOfDay(),
                fim.atTime(23, 59, 59)
        );

        BigDecimal totalVendas = vendas.stream()
                .map(Venda::getTotalVenda)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long quantidadeVendas = vendas.size();

        // Vendas por forma de pagamento
        Map<FormaPagamento, BigDecimal> vendasPorFormaPagamento = new HashMap<>();
        for (Venda venda : vendas) {
            if (venda.getFormaPagamento() != null) {
                FormaPagamento forma = venda.getFormaPagamento();
                BigDecimal total = vendasPorFormaPagamento.getOrDefault(forma, BigDecimal.ZERO);
                vendasPorFormaPagamento.put(forma, total.add(venda.getTotalVenda() != null ? venda.getTotalVenda() : BigDecimal.ZERO));
            }
        }

        List<Map<String, Object>> vendasList = vendas.stream()
                .map(v -> {
                    Map<String, Object> vendaMap = new HashMap<>();
                    vendaMap.put("id", v.getId());
                    vendaMap.put("data", v.getDataHora().toString());
                    vendaMap.put("total", v.getTotalVenda());
                    vendaMap.put("formaPagamento", v.getFormaPagamento() != null ? v.getFormaPagamento().toString() : "N/A");
                    vendaMap.put("quantidadeItens", v.getItens().size());
                    return vendaMap;
                })
                .collect(Collectors.toList());

        Map<String, Object> relatorio = new HashMap<>();
        relatorio.put("periodo", Map.of("inicio", inicio.toString(), "fim", fim.toString()));
        relatorio.put("totalVendas", totalVendas);
        relatorio.put("quantidadeVendas", quantidadeVendas);
        relatorio.put("ticketMedio", quantidadeVendas > 0 ?
                totalVendas.divide(BigDecimal.valueOf(quantidadeVendas), 2, java.math.RoundingMode.HALF_UP) :
                BigDecimal.ZERO);
        relatorio.put("vendasPorFormaPagamento", vendasPorFormaPagamento);
        relatorio.put("vendas", vendasList);

        return relatorio;
    }
}