package com.acougue.erp.controller;

import com.acougue.erp.service.BalancaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/balanca")
@CrossOrigin(origins = "*")
public class BalancaController {

    @Autowired
    private BalancaService balancaService;

    @PostMapping("/ler-peso/{tipo}")
    public Map<String, Object> lerPeso(@PathVariable String tipo) {
        try {
            BalancaService.TipoBalanca tipoBalanca = BalancaService.TipoBalanca.valueOf(tipo.toUpperCase());
            BigDecimal peso = balancaService.lerPeso(tipoBalanca);

            Map<String, Object> resultado = new HashMap<>();
            resultado.put("sucesso", true);
            resultado.put("tipoBalanca", tipoBalanca.toString());
            resultado.put("peso", peso);
            resultado.put("unidade", "kg");
            resultado.put("mensagem", "Peso lido com sucesso");
            return resultado;

        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("sucesso", false);
            erro.put("erro", "Tipo de balança inválido: " + tipo);
            return erro;
        }
    }

    @PostMapping("/ler-peso-produto/{tipo}")
    public Map<String, Object> lerPesoComProduto(
            @PathVariable String tipo,
            @RequestParam String produto) {
        try {
            BalancaService.TipoBalanca tipoBalanca = BalancaService.TipoBalanca.valueOf(tipo.toUpperCase());
            BigDecimal peso = balancaService.lerPesoComProduto(tipoBalanca, produto);

            Map<String, Object> resultado = new HashMap<>();
            resultado.put("sucesso", true);
            resultado.put("tipoBalanca", tipoBalanca.toString());
            resultado.put("produto", produto);
            resultado.put("peso", peso);
            resultado.put("unidade", "kg");
            resultado.put("mensagem", "Peso do produto lido com sucesso");
            return resultado;

        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("sucesso", false);
            erro.put("erro", e.getMessage());
            return erro;
        }
    }

    @GetMapping("/testar-conexao/{tipo}")
    public Map<String, Object> testarConexao(@PathVariable String tipo) {
        try {
            BalancaService.TipoBalanca tipoBalanca = BalancaService.TipoBalanca.valueOf(tipo.toUpperCase());
            return balancaService.testarConexao(tipoBalanca);

        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("sucesso", false);
            erro.put("erro", "Tipo de balança inválido: " + tipo);
            return erro;
        }
    }

    @GetMapping("/teste-integracao")
    public Map<String, Object> testeIntegracao() {
        try {
            Map<String, Object> testeFilizola = balancaService.testarConexao(BalancaService.TipoBalanca.FILIZOLA);
            Map<String, Object> testeToledo = balancaService.testarConexao(BalancaService.TipoBalanca.TOLEDO);

            BigDecimal pesoFilizola = balancaService.lerPesoComProduto(BalancaService.TipoBalanca.FILIZOLA, "Picanha");
            BigDecimal pesoToledo = balancaService.lerPesoComProduto(BalancaService.TipoBalanca.TOLEDO, "Alcatra");

            Map<String, Object> resultado = new HashMap<>();
            resultado.put("sucesso", true);
            resultado.put("testeFilizola", testeFilizola);
            resultado.put("testeToledo", testeToledo);
            resultado.put("pesoFilizola", pesoFilizola);
            resultado.put("pesoToledo", pesoToledo);
            resultado.put("mensagem", "Teste de integração com balanças realizado com sucesso");
            return resultado;

        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("sucesso", false);
            erro.put("erro", e.getMessage());
            return erro;
        }
    }
}