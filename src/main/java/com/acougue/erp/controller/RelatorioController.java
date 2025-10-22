// src/main/java/com/acougue/erp/controller/RelatorioController.java
package com.acougue.erp.controller;

import com.acougue.erp.service.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    @GetMapping("/basico")
    public Map<String, Object> getRelatorioBasico() {
        return relatorioService.getRelatorioBasico();
    }

    @GetMapping("/produtos-mais-vendidos")
    public Map<String, Object> getProdutosMaisVendidos() {
        return relatorioService.getProdutosMaisVendidos();
    }

    @GetMapping("/clientes")
    public Map<String, Object> getRelatorioClientes() {
        return relatorioService.getRelatorioClientes();
    }

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboard() {
        return relatorioService.getDashboard();
    }
}