// src/main/java/com/acougue/erp/controller/PrecificacaoController.java
package com.acougue.erp.controller;

import com.acougue.erp.model.CategoriaCarne;
import com.acougue.erp.model.Produto;
import com.acougue.erp.service.PrecificacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/precificacao")
@CrossOrigin(origins = "*")
public class PrecificacaoController {

    @Autowired
    private PrecificacaoService precificacaoService;

    @PostMapping("/produto/{produtoId}")
    public Produto atualizarPrecoProduto(
            @PathVariable Long produtoId,
            @RequestParam(required = false) BigDecimal margem) {
        return precificacaoService.atualizarPrecoProduto(produtoId, margem);
    }

    @PostMapping("/categoria/{categoria}")
    public List<Produto> atualizarPrecosCategoria(
            @PathVariable CategoriaCarne categoria,
            @RequestParam(required = false) BigDecimal margem) {
        return precificacaoService.atualizarPrecosCategoria(categoria, margem);
    }

    @PostMapping("/todos")
    public List<Produto> atualizarTodosPrecos(@RequestParam(required = false) BigDecimal margem) {
        return precificacaoService.atualizarTodosPrecos(margem);
    }

    @GetMapping("/simular/{produtoId}")
    public Map<String, Object> simularPrecificacao(
            @PathVariable Long produtoId,
            @RequestParam(required = false) BigDecimal margem) {
        return precificacaoService.simularPrecificacao(produtoId, margem);
    }

    @GetMapping("/margens-padrao")
    public Map<CategoriaCarne, BigDecimal> getMargensPadrao() {
        return precificacaoService.getMargensPadrao();
    }

    @GetMapping("/teste-precificacao")
    public Map<String, Object> testePrecificacao() {
        try {
            // Simular precificação para a Picanha (ID 1)
            Map<String, Object> simulacao = precificacaoService.simularPrecificacao(1L, null);

            // Aplicar a precificação
            Produto produtoAtualizado = precificacaoService.atualizarPrecoProduto(1L, null);

            return Map.of(
                    "sucesso", true,
                    "simulacao", simulacao,
                    "produtoAtualizado", Map.of(
                            "nome", produtoAtualizado.getNome(),
                            "novoPreco", produtoAtualizado.getPrecoVenda(),
                            "margemAplicada", precificacaoService.getMargemPadraoCategoria(produtoAtualizado.getCategoria())
                    ),
                    "mensagem", "Precificação aplicada com sucesso!"
            );

        } catch (Exception e) {
            return Map.of(
                    "sucesso", false,
                    "erro", e.getMessage()
            );
        }
    }
}