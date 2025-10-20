package com.acougue.erp.controller;

import com.acougue.erp.model.*;
import com.acougue.erp.service.VendaService;
import com.acougue.erp.service.BalancaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/vendas")
@CrossOrigin(origins = "*")
public class VendaController {

    @Autowired
    private VendaService vendaService;

    @PostMapping("/iniciar")
    public Venda iniciarVenda() {
        return vendaService.iniciarVenda();
    }

    @PostMapping("/{vendaId}/adicionar-item-balanca")
    public Map<String, Object> adicionarItemComBalanca(
            @PathVariable Long vendaId,
            @RequestBody Map<String, Object> request) {

        try {
            Long produtoId = Long.valueOf(request.get("produtoId").toString());
            String tipoBalanca = request.get("tipoBalanca").toString();
            BigDecimal quantidadePersonalizada = request.get("quantidadePersonalizada") != null ?
                    new BigDecimal(request.get("quantidadePersonalizada").toString()) : null;

            BalancaService.TipoBalanca tipo = BalancaService.TipoBalanca.valueOf(tipoBalanca.toUpperCase());

            Venda venda = new Venda();
            venda.setId(vendaId);

            ItemVenda item;
            if (quantidadePersonalizada != null) {
                item = vendaService.adicionarItemComBalancaEQuantidade(venda, produtoId, tipo, quantidadePersonalizada);
            } else {
                item = vendaService.adicionarItemComBalanca(venda, produtoId, tipo);
            }

            Map<String, Object> resultado = new HashMap<>();
            resultado.put("sucesso", true);
            resultado.put("item", Map.of(
                    "produto", item.getProduto().getNome(),
                    "quantidade", item.getQuantidade(),
                    "precoUnitario", item.getPrecoUnitario(),
                    "subtotal", item.getSubtotal()
            ));
            resultado.put("venda", Map.of(
                    "total", venda.getTotalVenda(),
                    "quantidadeItens", venda.getItens().size()
            ));
            resultado.put("mensagem", "Item adicionado com leitura da balança");

            return resultado;

        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("sucesso", false);
            erro.put("erro", e.getMessage());
            return erro;
        }
    }

    @PostMapping("/{vendaId}/adicionar-item")
    public Venda adicionarItem(
            @PathVariable Long vendaId,
            @RequestBody Map<String, Object> request) {

        Long produtoId = Long.valueOf(request.get("produtoId").toString());
        BigDecimal quantidade = new BigDecimal(request.get("quantidade").toString());

        Venda venda = vendaService.salvarVenda(new Venda());
        return vendaService.adicionarItem(venda, produtoId, quantidade);
    }

    @PostMapping("/{vendaId}/finalizar")
    public Venda finalizarVenda(
            @PathVariable Long vendaId,
            @RequestBody Map<String, Object> request) {

        FormaPagamento formaPagamento = FormaPagamento.valueOf(
                request.get("formaPagamento").toString());
        BigDecimal valorPago = new BigDecimal(request.get("valorPago").toString());

        Venda venda = vendaService.salvarVenda(new Venda());
        return vendaService.finalizarVenda(venda, formaPagamento, valorPago);
    }

    @PostMapping("/venda-rapida-balanca")
    public Map<String, Object> vendaRapidaComBalanca() {
        return vendaService.processarVendaRapidaComBalanca();
    }

    @GetMapping("/teste-venda-balanca")
    public Map<String, Object> testeVendaBalanca() {
        return vendaService.processarVendaRapidaComBalanca();
    }

    @GetMapping("/teste-balanca-simples")
    public Map<String, Object> testeBalancaSimples() {
        try {
            var peso = balancaService.lerPeso(BalancaService.TipoBalanca.FILIZOLA);

            Map<String, Object> resultado = new HashMap<>();
            resultado.put("sucesso", true);
            resultado.put("peso", peso);
            resultado.put("mensagem", "Teste de balança realizado com sucesso");
            return resultado;

        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("sucesso", false);
            erro.put("erro", e.getMessage());
            return erro;
        }
    }

    @PostMapping("/{vendaId}/cancelar")
    public Map<String, Object> cancelarVenda(@PathVariable Long vendaId) {
        try {
            Venda vendaCancelada = vendaService.cancelarVenda(vendaId);

            Map<String, Object> resultado = new HashMap<>();
            resultado.put("sucesso", true);
            resultado.put("venda", Map.of(
                    "id", vendaCancelada.getId(),
                    "status", vendaCancelada.getStatus()
            ));
            resultado.put("mensagem", "Venda cancelada com sucesso");
            return resultado;

        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("sucesso", false);
            erro.put("erro", e.getMessage());
            return erro;
        }
    }

    @GetMapping("/verificar-estoque")
    public Map<String, Object> verificarEstoque(
            @RequestParam Long produtoId,
            @RequestParam BigDecimal quantidade) {

        try {
            boolean estoqueSuficiente = vendaService.verificarEstoqueSuficiente(produtoId, quantidade);

            Map<String, Object> resultado = new HashMap<>();
            resultado.put("sucesso", true);
            resultado.put("produtoId", produtoId);
            resultado.put("quantidadeSolicitada", quantidade);
            resultado.put("estoqueSuficiente", estoqueSuficiente);
            resultado.put("mensagem", estoqueSuficiente ? "Estoque suficiente" : "Estoque insuficiente");
            return resultado;

        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("sucesso", false);
            erro.put("erro", e.getMessage());
            return erro;
        }
    }

    @GetMapping("/teste-venda-rapida")
    public Map<String, Object> testeVendaRapida() {
        Map<String, Object> resultado = new HashMap<>();

        try {
            Venda venda = vendaService.iniciarVenda();
            venda = vendaService.adicionarItem(venda, 1L, new BigDecimal("2.5"));
            venda = vendaService.adicionarItem(venda, 2L, new BigDecimal("1.0"));
            venda = vendaService.finalizarVenda(venda, FormaPagamento.DINHEIRO, new BigDecimal("200.00"));

            resultado.put("sucesso", true);
            resultado.put("venda", venda);
            resultado.put("mensagem", "Venda teste realizada com sucesso!");

        } catch (Exception e) {
            resultado.put("sucesso", false);
            resultado.put("erro", e.getMessage());
        }

        return resultado;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
                "status", "OK",
                "modulo", "Vendas",
                "balancaIntegrada", "true",
                "mensagem", "Módulo de vendas operacional com integração de balanças"
        );
    }

    @Autowired
    private BalancaService balancaService;
}