package com.acougue.erp.service;

import com.acougue.erp.config.BalancaConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class BalancaService {

    @Autowired
    private BalancaConfig balancaConfig;

    private Random random = new Random();

    public enum TipoBalanca {
        FILIZOLA,
        TOLEDO
    }

    public BigDecimal lerPeso(TipoBalanca tipoBalanca) {
        simularDelay();
        double peso = 0.1 + (4.9 * random.nextDouble());
        return BigDecimal.valueOf(peso).setScale(3, java.math.RoundingMode.HALF_UP);
    }

    public BigDecimal lerPesoComProduto(TipoBalanca tipoBalanca, String produto) {
        simularDelay();

        double pesoBase;
        switch (produto.toLowerCase()) {
            case "picanha":
            case "alcatra":
                pesoBase = 0.5 + (2.0 * random.nextDouble());
                break;
            case "linguiça":
                pesoBase = 0.3 + (1.0 * random.nextDouble());
                break;
            case "frango":
                pesoBase = 0.8 + (3.0 * random.nextDouble());
                break;
            default:
                pesoBase = 0.1 + (2.0 * random.nextDouble());
        }

        return BigDecimal.valueOf(pesoBase).setScale(3, java.math.RoundingMode.HALF_UP);
    }

    public Map<String, Object> testarConexao(TipoBalanca tipoBalanca) {
        try {
            String porta = tipoBalanca == TipoBalanca.FILIZOLA ?
                    balancaConfig.getFilizola().getPorta() :
                    balancaConfig.getToledo().getPorta();

            boolean conectado = random.nextDouble() > 0.2;

            Map<String, Object> resultado = new HashMap<>();
            resultado.put("sucesso", conectado);
            resultado.put("tipoBalanca", tipoBalanca.toString());
            resultado.put("porta", porta);
            resultado.put("mensagem", conectado ? "Balança conectada com sucesso" : "Falha na conexão com a balança");

            return resultado;

        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("sucesso", false);
            erro.put("erro", e.getMessage());
            return erro;
        }
    }

    private void simularDelay() {
        try {
            Thread.sleep(1000 + random.nextInt(1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}