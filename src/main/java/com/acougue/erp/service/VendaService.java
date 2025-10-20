package com.acougue.erp.service;

import com.acougue.erp.model.*;
import com.acougue.erp.repository.ProdutoRepository;
import com.acougue.erp.repository.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class VendaService {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private BalancaService balancaService;

    public Venda iniciarVenda() {
        return new Venda();
    }

    public Venda adicionarItem(Venda venda, Long produtoId, BigDecimal quantidade) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        ItemVenda item = new ItemVenda(produto, quantidade);
        venda.adicionarItem(item);

        return venda;
    }

    public ItemVenda adicionarItemComBalanca(Venda venda, Long produtoId, BalancaService.TipoBalanca tipoBalanca) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        BigDecimal peso = balancaService.lerPesoComProduto(tipoBalanca, produto.getNome());

        ItemVenda item = new ItemVenda(produto, peso);
        venda.adicionarItem(item);

        return item;
    }

    public ItemVenda adicionarItemComBalancaEQuantidade(Venda venda, Long produtoId, BalancaService.TipoBalanca tipoBalanca, BigDecimal quantidadePersonalizada) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        BigDecimal quantidade;
        if (quantidadePersonalizada != null && quantidadePersonalizada.compareTo(BigDecimal.ZERO) > 0) {
            quantidade = quantidadePersonalizada;
        } else {
            quantidade = balancaService.lerPesoComProduto(tipoBalanca, produto.getNome());
        }

        ItemVenda item = new ItemVenda(produto, quantidade);
        venda.adicionarItem(item);

        return item;
    }

    public Venda finalizarVenda(Venda venda, FormaPagamento formaPagamento, BigDecimal valorPago) {
        venda.setFormaPagamento(formaPagamento);
        venda.setValorPago(valorPago);
        venda.setStatus(StatusVenda.FINALIZADA);

        return vendaRepository.save(venda);
    }

    public Venda salvarVenda(Venda venda) {
        return vendaRepository.save(venda);
    }

    public Map<String, Object> processarVendaRapidaComBalanca() {
        try {
            Venda venda = iniciarVenda();

            ItemVenda item1 = adicionarItemComBalanca(venda, 1L, BalancaService.TipoBalanca.FILIZOLA);
            ItemVenda item2 = adicionarItemComBalanca(venda, 2L, BalancaService.TipoBalanca.TOLEDO);

            venda = vendaRepository.save(venda);

            BigDecimal total = venda.getTotalVenda();
            venda = finalizarVenda(venda, FormaPagamento.DINHEIRO, total.add(new BigDecimal("10.00")));

            java.util.List<Map<String, Object>> itensFormatados = venda.getItens().stream()
                    .map(item -> {
                        Map<String, Object> itemMap = new HashMap<>();
                        itemMap.put("produto", item.getProduto().getNome());
                        itemMap.put("quantidade", item.getQuantidade());
                        itemMap.put("precoUnitario", item.getPrecoUnitario());
                        itemMap.put("subtotal", item.getSubtotal());
                        return itemMap;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> resultado = new HashMap<>();
            resultado.put("sucesso", true);
            resultado.put("venda", Map.of(
                    "id", venda.getId(),
                    "total", venda.getTotalVenda(),
                    "troco", venda.getTroco(),
                    "formaPagamento", venda.getFormaPagamento().toString()
            ));
            resultado.put("itens", itensFormatados);
            resultado.put("mensagem", "Venda com balança processada com sucesso!");

            return resultado;

        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("sucesso", false);
            erro.put("erro", e.getMessage());
            return erro;
        }
    }

    public boolean verificarEstoqueSuficiente(Long produtoId, BigDecimal quantidade) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        return produto.getEstoqueAtual() >= quantidade.intValue();
    }

    public void atualizarEstoqueAposVenda(Venda venda) {
        for (ItemVenda item : venda.getItens()) {
            Produto produto = item.getProduto();
            int quantidadeVendida = item.getQuantidade().intValue();

            if (produto.getEstoqueAtual() >= quantidadeVendida) {
                produto.setEstoqueAtual(produto.getEstoqueAtual() - quantidadeVendida);
                produtoRepository.save(produto);
            } else {
                throw new RuntimeException("Estoque insuficiente para: " + produto.getNome());
            }
        }
    }

    public Venda cancelarVenda(Long vendaId) {
        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));

        for (ItemVenda item : venda.getItens()) {
            Produto produto = item.getProduto();
            int quantidade = item.getQuantidade().intValue();
            produto.setEstoqueAtual(produto.getEstoqueAtual() + quantidade);
            produtoRepository.save(produto);
        }

        venda.setStatus(StatusVenda.CANCELADA);
        return vendaRepository.save(venda);
    }
}