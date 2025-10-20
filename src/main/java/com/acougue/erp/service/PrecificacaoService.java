// src/main/java/com/acougue/erp/service/PrecificacaoService.java
package com.acougue.erp.service;

import com.acougue.erp.model.Produto;
import com.acougue.erp.model.CategoriaCarne;
import com.acougue.erp.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PrecificacaoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    // Margens padrão por categoria (em percentual)
    private final Map<CategoriaCarne, BigDecimal> margensPadrao = Map.of(
            CategoriaCarne.BOVINA, new BigDecimal("35.0"),    // 35%
            CategoriaCarne.SUINA, new BigDecimal("40.0"),     // 40%
            CategoriaCarne.FRANGO, new BigDecimal("50.0"),    // 50%
            CategoriaCarne.LINGUICA, new BigDecimal("45.0"),  // 45%
            CategoriaCarne.CARNE_MOIDA, new BigDecimal("30.0"), // 30%
            CategoriaCarne.AVES, new BigDecimal("45.0"),      // 45%
            CategoriaCarne.EMBUTIDOS, new BigDecimal("60.0"), // 60%
            CategoriaCarne.DEFUMADOS, new BigDecimal("55.0"), // 55%
            CategoriaCarne.PEIXES, new BigDecimal("50.0"),    // 50%
            CategoriaCarne.MIUDOS, new BigDecimal("80.0")     // 80%
    );

    public Produto atualizarPrecoProduto(Long produtoId, BigDecimal margemPersonalizada) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        BigDecimal margem = margemPersonalizada != null ?
                margemPersonalizada : getMargemPadraoCategoria(produto.getCategoria());

        produto.atualizarPrecoVendaAutomatico(margem);
        return produtoRepository.save(produto);
    }

    public List<Produto> atualizarPrecosCategoria(CategoriaCarne categoria, BigDecimal margem) {
        List<Produto> produtos = produtoRepository.findByCategoria(categoria);
        BigDecimal margemUsar = margem != null ? margem : getMargemPadraoCategoria(categoria);

        produtos.forEach(produto -> {
            produto.atualizarPrecoVendaAutomatico(margemUsar);
        });

        return produtoRepository.saveAll(produtos);
    }

    public List<Produto> atualizarTodosPrecos(BigDecimal margemGeral) {
        List<Produto> produtos = produtoRepository.findByAtivoTrue();

        produtos.forEach(produto -> {
            BigDecimal margem = margemGeral != null ?
                    margemGeral : getMargemPadraoCategoria(produto.getCategoria());
            produto.atualizarPrecoVendaAutomatico(margem);
        });

        return produtoRepository.saveAll(produtos);
    }

    public Map<String, Object> simularPrecificacao(Long produtoId, BigDecimal margem) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        BigDecimal margemUsar = margem != null ? margem : getMargemPadraoCategoria(produto.getCategoria());
        BigDecimal novoPreco = produto.calcularPrecoVendaComMargem(margemUsar);
        BigDecimal rentabilidadeAtual = produto.calcularRentabilidadeAtual();
        BigDecimal novaRentabilidade = novoPreco.subtract(produto.getPrecoCusto())
                .divide(produto.getPrecoCusto(), 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));

        return Map.of(
                "produto", produto.getNome(),
                "categoria", produto.getCategoria(),
                "precoCusto", produto.getPrecoCusto(),
                "precoVendaAtual", produto.getPrecoVenda(),
                "precoVendaCalculado", novoPreco,
                "margemAplicada", margemUsar,
                "percentualPerdas", produto.calcularPercentualPerdasTotal().multiply(new BigDecimal("100")),
                "rentabilidadeAtual", rentabilidadeAtual.setScale(2, java.math.RoundingMode.HALF_UP),
                "novaRentabilidade", novaRentabilidade.setScale(2, java.math.RoundingMode.HALF_UP),
                "variacao", novoPreco.subtract(produto.getPrecoVenda() != null ? produto.getPrecoVenda() : BigDecimal.ZERO)
        );
    }

    public Map<CategoriaCarne, BigDecimal> getMargensPadrao() {
        return new HashMap<>(margensPadrao);
    }

    public BigDecimal getMargemPadraoCategoria(CategoriaCarne categoria) {
        return margensPadrao.getOrDefault(categoria, new BigDecimal("40.0"));
    }

    public void atualizarMargemPadrao(CategoriaCarne categoria, BigDecimal novaMargem) {
        // Em uma implementação real, isso salvaria no banco
        margensPadrao.put(categoria, novaMargem);
    }
}