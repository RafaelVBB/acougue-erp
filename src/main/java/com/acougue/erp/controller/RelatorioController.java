// src/main/java/com/acougue/erp/controller/RelatorioController.java
package com.acougue.erp.controller;

import com.acougue.erp.service.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/relatorios")
@CrossOrigin(origins = "*")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    @GetMapping("/rentabilidade")
    public Map<String, Object> getRelatorioRentabilidade(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return relatorioService.gerarRelatorioRentabilidade(inicio, fim);
    }

    @GetMapping("/clientes")
    public Map<String, Object> getRelatorioClientes(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return relatorioService.gerarRelatorioClientes(inicio, fim);
    }

    @GetMapping("/previsao-vendas")
    public Map<String, Object> getPrevisaoVendas(@RequestParam(defaultValue = "6") int meses) {
        return relatorioService.gerarPrevisaoVendas(meses);
    }

    @GetMapping("/teste-relatorios")
    public Map<String, Object> testeRelatorios() {
        try {
            LocalDate inicio = LocalDate.now().minusMonths(1);
            LocalDate fim = LocalDate.now();

            Map<String, Object> rentabilidade = relatorioService.gerarRelatorioRentabilidade(inicio, fim);
            Map<String, Object> clientes = relatorioService.gerarRelatorioClientes(inicio, fim);
            Map<String, Object> previsao = relatorioService.gerarPrevisaoVendas(3);

            return Map.of(
                    "sucesso", true,
                    "relatorios", Map.of(
                            "rentabilidade", rentabilidade,
                            "clientes", clientes,
                            "previsao", previsao
                    ),
                    "mensagem", "Relatórios gerados com sucesso!"
            );

        } catch (Exception e) {
            return Map.of(
                    "sucesso", false,
                    "erro", e.getMessage()
            );
        }
    }
}