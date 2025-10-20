// src/main/java/com/acougue/erp/controller/TestController.java
package com.acougue.erp.controller;

import com.acougue.erp.repository.ProdutoRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final ProdutoRepository produtoRepository;

    public TestController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @GetMapping("/test-repo")
    public String testRepo() {
        long count = produtoRepository.count();
        return "Produtos no banco: " + count;
    }
}