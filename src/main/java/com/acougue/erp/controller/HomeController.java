// src/main/java/com/acougue/erp/controller/HomeController.java
package com.acougue.erp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of(
                "message", "Sistema ERP para Açougue - API Online!",
                "status", "OK",
                "modulos", "Produtos, Vendas, PDV"
        );
    }
}