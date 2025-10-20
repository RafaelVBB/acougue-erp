package com.acougue.erp.controller;

import com.acougue.erp.model.Cliente;
import com.acougue.erp.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping("/")
    public String home() {
        return "API de Clientes - Açougue ERP";
    }

    @GetMapping
    public List<Cliente> getAllClientes() {
        return clienteService.findByAtivos();
    }

    @GetMapping("/{id}")
    public Optional<Cliente> getClienteById(@PathVariable Long id) {
        return clienteService.findById(id);
    }

    @PostMapping
    public Cliente createCliente(@RequestBody Cliente cliente) {
        return clienteService.save(cliente);
    }

    @PutMapping("/{id}")
    public Cliente updateCliente(@PathVariable Long id, @RequestBody Cliente clienteDetails) {
        Cliente cliente = clienteService.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        cliente.setNome(clienteDetails.getNome());
        cliente.setTelefone(clienteDetails.getTelefone());
        cliente.setEmail(clienteDetails.getEmail());
        cliente.setLimiteCredito(clienteDetails.getLimiteCredito());
        cliente.setAtivo(clienteDetails.isAtivo());

        return clienteService.save(cliente);
    }

    @DeleteMapping("/{id}")
    public void deleteCliente(@PathVariable Long id) {
        clienteService.deleteById(id);
    }

    @GetMapping("/buscar")
    public List<Cliente> searchClientes(@RequestParam String nome) {
        return clienteService.findByNome(nome);
    }

    @GetMapping("/com-divida")
    public List<Cliente> getClientesComDivida() {
        return clienteService.findClientesComDivida();
    }

    @GetMapping("/limite-estourado")
    public List<Cliente> getClientesComLimiteEstourado() {
        return clienteService.findClientesComLimiteEstourado();
    }

    @GetMapping("/{id}/verificar-limite")
    public Map<String, Object> verificarLimiteCredito(
            @PathVariable Long id,
            @RequestParam BigDecimal valorCompra) {
        boolean podeComprar = clienteService.verificarLimiteCredito(id, valorCompra);
        Optional<Cliente> clienteOpt = clienteService.findById(id);

        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            return Map.of(
                    "podeComprar", podeComprar,
                    "cliente", cliente.getNome(),
                    "limiteCredito", cliente.getLimiteCredito(),
                    "saldoDevedor", cliente.getSaldoDevedor(),
                    "creditoDisponivel", cliente.getCreditoDisponivel(),
                    "valorCompra", valorCompra
            );
        }

        return Map.of("podeComprar", false, "erro", "Cliente não encontrado");
    }

    @PostMapping("/{id}/adicionar-divida")
    public Cliente adicionarDivida(@PathVariable Long id, @RequestParam BigDecimal valor) {
        return clienteService.adicionarDivida(id, valor);
    }

    @PostMapping("/{id}/quitar-divida")
    public Cliente quitarDivida(@PathVariable Long id, @RequestParam BigDecimal valor) {
        return clienteService.quitarDivida(id, valor);
    }

    @GetMapping("/resumo-fiado")
    public Map<String, Object> getResumoFiado() {
        return clienteService.getResumoFiado();
    }

    @GetMapping("/teste-cliente")
    public Map<String, Object> testeCliente() {
        try {
            Cliente cliente = new Cliente("João Silva", "(11) 99999-9999", new BigDecimal("500.00"));
            cliente.setEmail("joao@email.com");
            Cliente clienteSalvo = clienteService.save(cliente);

            Cliente clienteComDivida = clienteService.adicionarDivida(clienteSalvo.getId(), new BigDecimal("150.00"));

            return Map.of(
                    "sucesso", true,
                    "clienteCriado", clienteSalvo,
                    "clienteComDivida", Map.of(
                            "nome", clienteComDivida.getNome(),
                            "saldoDevedor", clienteComDivida.getSaldoDevedor(),
                            "creditoDisponivel", clienteComDivida.getCreditoDisponivel()
                    ),
                    "resumoFiado", clienteService.getResumoFiado()
            );

        } catch (Exception e) {
            return Map.of(
                    "sucesso", false,
                    "erro", e.getMessage()
            );
        }
    }
}