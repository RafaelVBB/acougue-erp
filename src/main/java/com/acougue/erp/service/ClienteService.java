package com.acougue.erp.service;

import com.acougue.erp.model.Cliente;
import com.acougue.erp.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public List<Cliente> findByAtivos() {
        return clienteRepository.findByAtivoTrue();
    }

    public Optional<Cliente> findById(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente save(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public void deleteById(Long id) {
        clienteRepository.deleteById(id);
    }

    public List<Cliente> findByNome(String nome) {
        return clienteRepository.findByNomeContainingIgnoreCase(nome);
    }

    public List<Cliente> findClientesComDivida() {
        return clienteRepository.findClientesComDivida();
    }

    public List<Cliente> findClientesComLimiteEstourado() {
        return clienteRepository.findClientesComLimiteEstourado();
    }

    public boolean verificarLimiteCredito(Long clienteId, BigDecimal valorCompra) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            return cliente.podeComprarFiado(valorCompra);
        }
        return false;
    }

    public Cliente adicionarDivida(Long clienteId, BigDecimal valor) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        cliente.adicionarDivida(valor);
        return clienteRepository.save(cliente);
    }

    public Cliente quitarDivida(Long clienteId, BigDecimal valor) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        cliente.quitarDivida(valor);
        return clienteRepository.save(cliente);
    }

    public Map<String, Object> getResumoFiado() {
        BigDecimal totalDividas = clienteRepository.getTotalDividas();
        Long clientesComDivida = clienteRepository.countClientesComDivida();
        List<Cliente> clientesLimiteEstourado = clienteRepository.findClientesComLimiteEstourado();

        return Map.of(
                "totalDividas", totalDividas != null ? totalDividas : BigDecimal.ZERO,
                "quantidadeClientesComDivida", clientesComDivida != null ? clientesComDivida : 0,
                "clientesLimiteEstourado", clientesLimiteEstourado.size(),
                "totalClientesAtivos", clienteRepository.findByAtivoTrue().size()
        );
    }
}