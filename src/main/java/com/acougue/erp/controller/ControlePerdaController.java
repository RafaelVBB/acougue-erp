// src/main/java/com/acougue/erp/controller/ControlePerdaController.java
package com.acougue.erp.controller;

import com.acougue.erp.model.ControlePerda;
import com.acougue.erp.model.TipoPerda;
import com.acougue.erp.service.ControlePerdaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/controle-perdas")
@CrossOrigin(origins = "*")
public class ControlePerdaController {

    @Autowired
    private ControlePerdaService controlePerdaService;

    @PostMapping("/registrar")
    public ControlePerda registrarPerda(@RequestBody Map<String, Object> request) {
        Long produtoId = Long.valueOf(request.get("produtoId").toString());
        BigDecimal quantidade = new BigDecimal(request.get("quantidade").toString());
        TipoPerda tipoPerda = TipoPerda.valueOf(request.get("tipoPerda").toString());
        String motivo = request.get("motivo").toString();
        Boolean impacto = Boolean.valueOf(request.getOrDefault("impactoPrecificacao", "false").toString());

        if (impacto) {
            return controlePerdaService.registrarPerdaComImpacto(produtoId, quantidade, tipoPerda, motivo);
        } else {
            return controlePerdaService.registrarPerda(produtoId, quantidade, tipoPerda, motivo);
        }
    }

    @GetMapping("/periodo")
    public List<ControlePerda> getPerdasPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return controlePerdaService.buscarPerdasPorPeriodo(inicio, fim);
    }

    @GetMapping("/total-periodo")
    public Map<String, Object> getTotalPerdasPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        BigDecimal total = controlePerdaService.calcularTotalPerdasPeriodo(inicio, fim);
        return Map.of(
                "periodoInicio", inicio,
                "periodoFim", fim,
                "totalPerdas", total,
                "quantidadeRegistros", controlePerdaService.buscarPerdasPorPeriodo(inicio, fim).size()
        );
    }

    @GetMapping("/tipo/{tipoPerda}")
    public List<ControlePerda> getPerdasPorTipo(@PathVariable TipoPerda tipoPerda) {
        return controlePerdaService.buscarPerdasPorTipo(tipoPerda);
    }

    @GetMapping("/teste-perda")
    public Map<String, Object> testeRegistroPerda() {
        try {
            // Teste: Registrar perda de 1kg de Picanha por validade
            ControlePerda perda = controlePerdaService.registrarPerdaComImpacto(
                    1L,
                    new BigDecimal("1.0"),
                    TipoPerda.VALIDADE,
                    "Teste - Produto vencido"
            );

            return Map.of(
                    "sucesso", true,
                    "mensagem", "Perda registrada com sucesso!",
                    "perda", perda,
                    "impactoPrecificacao", perda.isImpactoPrecificacao()
            );

        } catch (Exception e) {
            return Map.of(
                    "sucesso", false,
                    "erro", e.getMessage()
            );
        }
    }
}