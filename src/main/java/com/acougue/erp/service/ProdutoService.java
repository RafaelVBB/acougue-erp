// src/main/java/com/acougue/erp/service/ProdutoService.java
package com.acougue.erp.service;

import com.acougue.erp.model.Produto;
import com.acougue.erp.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    // Use constructor injection instead of @Autowired
    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<Produto> getAllProdutos() {
        return produtoRepository.findByAtivoTrue();
    }

    public Produto getProdutoById(Long id) {
        return produtoRepository.findById(id).orElse(null);
    }
}