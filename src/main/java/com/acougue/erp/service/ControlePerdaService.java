// src/main/java/com/acougue/erp/service/ControlePerdaService.java
package com.acougue.erp.service;

import com.acougue.erp.model.*;
import com.acougue.erp.repository.ControlePerdaRepository;
import com.acougue.erp.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ControlePerdaService {

    @Autowired
    private ControlePerdaRepository controlePerdaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    public ControlePerda registrarPerda(Long produtoId, BigDecimal quantidade, TipoPerda tipoPerda, String motivo) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        ControlePerda perda = new ControlePerda(produto, quantidade, tipoPerda, motivo);

        // Atualizar estoque
        produto.setEstoqueAtual(produto.getEstoqueAtual() - quantidade.intValue());
        produtoRepository.save(produto);

        return controlePerdaRepository.save(perda);
    }

    public ControlePerda registrarPerdaComImpacto(Long produtoId, BigDecimal quantidade, TipoPerda tipoPerda, String motivo) {
        ControlePerda perda = registrarPerda(produtoId, quantidade, tipoPerda, motivo);
        perda.aplicarImpactoPrecificacao();
        return controlePerdaRepository.save(perda);
    }

    public List<ControlePerda> buscarPerdasPorPeriodo(LocalDate inicio, LocalDate fim) {
        return controlePerdaRepository.findByDataRegistroBetween(inicio, fim);
    }

    public BigDecimal calcularTotalPerdasPeriodo(LocalDate inicio, LocalDate fim) {
        BigDecimal total = controlePerdaRepository.sumValorPerdaPorPeriodo(inicio, fim);
        return total != null ? total : BigDecimal.ZERO;
    }

    public List<ControlePerda> buscarPerdasPorTipo(TipoPerda tipoPerda) {
        return controlePerdaRepository.findByTipoPerda(tipoPerda);
    }

    public Optional<ControlePerda> findById(Long id) {
        return controlePerdaRepository.findById(id);
    }

    public void deleteById(Long id) {
        controlePerdaRepository.deleteById(id);
    }
}