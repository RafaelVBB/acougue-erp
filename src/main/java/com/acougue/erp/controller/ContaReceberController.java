package com.acougue.erp.controller;

import com.acougue.erp.model.ContaReceber;
import com.acougue.erp.model.StatusConta;
import com.acougue.erp.model.Cliente;
import com.acougue.erp.service.ContaReceberService;
import com.acougue.erp.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contas-receber")
@CrossOrigin(origins = "*")
public class ContaReceberController {

    @Autowired
    private ContaReceberService contaReceberService;

    @Autowired
    private ClienteService clienteService;

    @GetMapping("/")
    public String home() {
        return "API de Contas a Receber - Açougue ERP";
    }

    @GetMapping
    public List<ContaReceber> getAllContas() {
        return contaReceberService.findAll();
    }

    @PostMapping
    public ContaReceber criarContaReceber(@RequestBody Map<String, Object> request) {
        Long clienteId = Long.valueOf(request.get("clienteId").toString());
        Long vendaId = request.get("vendaId") != null ? Long.valueOf(request.get("vendaId").toString()) : null;
        BigDecimal valor = new BigDecimal(request.get("valor").toString());
        LocalDate dataVencimento = LocalDate.parse(request.get("dataVencimento").toString());
        String observacao = (String) request.getOrDefault("observacao", "");

        return contaReceberService.criarContaReceber(clienteId, vendaId, valor, dataVencimento, observacao);
    }

    @PostMapping("/{id}/pagar")
    public ContaReceber registrarPagamento(
            @PathVariable Long id,
            @RequestParam BigDecimal valorPago,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataPagamento) {
        return contaReceberService.registrarPagamento(id, valorPago, dataPagamento);
    }

    @GetMapping("/cliente/{clienteId}")
    public List<ContaReceber> getContasPorCliente(@PathVariable Long clienteId) {
        return contaReceberService.findByClienteId(clienteId);
    }

    @GetMapping("/status/{status}")
    public List<ContaReceber> getContasPorStatus(@PathVariable StatusConta status) {
        return contaReceberService.findByStatus(status);
    }

    @GetMapping("/vencidas")
    public List<ContaReceber> getContasVencidas() {
        return contaReceberService.findContasVencidas();
    }

    @GetMapping("/periodo")
    public List<ContaReceber> getContasPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return contaReceberService.findContasPorVencimento(inicio, fim);
    }

    @GetMapping("/resumo")
    public Map<String, Object> getResumoContasReceber() {
        return contaReceberService.getResumoContasReceber();
    }

    @PostMapping("/atualizar-status-vencidas")
    public Map<String, Object> atualizarStatusContasVencidas() {
        contaReceberService.atualizarStatusContasVencidas();
        return Map.of("mensagem", "Status das contas vencidas atualizado com sucesso");
    }

    @GetMapping("/teste-conta")
    public Map<String, Object> testeContaReceber() {
        try {
            List<Cliente> clientes = clienteService.findByAtivos();
            if (clientes.isEmpty()) {
                Cliente novoCliente = new Cliente("Cliente Teste Fiado", "(11) 99999-9999", new BigDecimal("500.00"));
                clienteService.save(novoCliente);
                clientes = clienteService.findByAtivos();
            }

            Cliente cliente = clientes.get(0);

            ContaReceber conta = contaReceberService.criarContaReceber(
                    cliente.getId(),
                    null,
                    new BigDecimal("200.00"),
                    LocalDate.now().plusDays(15),
                    "Teste - Compra fiado"
            );

            clienteService.adicionarDivida(cliente.getId(), new BigDecimal("200.00"));

            return Map.of(
                    "sucesso", true,
                    "contaCriada", Map.of(
                            "id", conta.getId(),
                            "valor", conta.getValor(),
                            "dataVencimento", conta.getDataVencimento(),
                            "status", conta.getStatus()
                    ),
                    "clienteAtualizado", Map.of(
                            "nome", cliente.getNome(),
                            "saldoDevedor", cliente.getSaldoDevedor(),
                            "creditoDisponivel", cliente.getCreditoDisponivel()
                    ),
                    "resumoContas", contaReceberService.getResumoContasReceber()
            );

        } catch (Exception e) {
            return Map.of(
                    "sucesso", false,
                    "erro", e.getMessage()
            );
        }
    }
}