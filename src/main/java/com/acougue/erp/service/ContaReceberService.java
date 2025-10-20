package com.acougue.erp.service;

import com.acougue.erp.model.*;
import com.acougue.erp.repository.ContaReceberRepository;
import com.acougue.erp.repository.ClienteRepository;
import com.acougue.erp.repository.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class ContaReceberService {

    @Autowired
    private ContaReceberRepository contaReceberRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private VendaRepository vendaRepository;

    public List<ContaReceber> findAll() {
        return contaReceberRepository.findAll();
    }

    public ContaReceber criarContaReceber(Long clienteId, Long vendaId, BigDecimal valor, LocalDate dataVencimento, String observacao) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Venda venda = null;
        if (vendaId != null) {
            venda = vendaRepository.findById(vendaId)
                    .orElseThrow(() -> new RuntimeException("Venda não encontrada"));
        }

        ContaReceber conta = new ContaReceber(cliente, venda, valor, dataVencimento);
        conta.setObservacao(observacao);

        return contaReceberRepository.save(conta);
    }

    public ContaReceber registrarPagamento(Long contaId, BigDecimal valorPago, LocalDate dataPagamento) {
        ContaReceber conta = contaReceberRepository.findById(contaId)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        conta.registrarPagamento(valorPago, dataPagamento);
        return contaReceberRepository.save(conta);
    }

    public List<ContaReceber> findByClienteId(Long clienteId) {
        return contaReceberRepository.findByClienteId(clienteId);
    }

    public List<ContaReceber> findByStatus(StatusConta status) {
        return contaReceberRepository.findByStatus(status);
    }

    public List<ContaReceber> findContasVencidas() {
        return contaReceberRepository.findContasVencidas(LocalDate.now());
    }

    public List<ContaReceber> findContasPorVencimento(LocalDate inicio, LocalDate fim) {
        return contaReceberRepository.findByDataVencimentoBetween(inicio, fim);
    }

    public Optional<ContaReceber> findById(Long id) {
        return contaReceberRepository.findById(id);
    }

    public void deleteById(Long id) {
        contaReceberRepository.deleteById(id);
    }

    public Map<String, Object> getResumoContasReceber() {
        BigDecimal totalAReceber = contaReceberRepository.getTotalAReceber();
        BigDecimal totalVencido = contaReceberRepository.getTotalVencido(LocalDate.now());
        List<ContaReceber> contasVencidas = contaReceberRepository.findContasVencidas(LocalDate.now());

        return Map.of(
                "totalAReceber", totalAReceber != null ? totalAReceber : BigDecimal.ZERO,
                "totalVencido", totalVencido != null ? totalVencido : BigDecimal.ZERO,
                "quantidadeContasVencidas", contasVencidas.size(),
                "contasVencidas", contasVencidas.stream()
                        .map(cr -> Map.of(
                                "id", cr.getId(),
                                "cliente", cr.getCliente().getNome(),
                                "valor", cr.getValor(),
                                "dataVencimento", cr.getDataVencimento()
                        ))
                        .toList()
        );
    }

    public void atualizarStatusContasVencidas() {
        List<ContaReceber> contasVencidas = contaReceberRepository.findContasVencidas(LocalDate.now());
        for (ContaReceber conta : contasVencidas) {
            if (conta.getStatus() == StatusConta.ABERTA) {
                conta.setStatus(StatusConta.VENCIDA);
                contaReceberRepository.save(conta);
            }
        }
    }
}