// src/main/java/com/acougue/erp/config/DataLoader.java
package com.acougue.erp.config;

import com.acougue.erp.model.*;
import com.acougue.erp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public void run(String... args) throws Exception {

        // 1. CRIAR USUÁRIO ADMINISTRADOR
        criarUsuarioAdmin();

        // 2. CRIAR DADOS DE TESTE
        criarDadosTesteBasico();
    }

    private void criarUsuarioAdmin() {
        // Verificar se já existe usuário admin
        if (usuarioRepository.findByUsername("admin").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword("admin123");
            admin.setNome("Proprietário do Açougue");
            admin.setEmail("proprietario@acougue.com");
            admin.setPerfil(PerfilUsuario.PROPRIETARIO);
            usuarioRepository.save(admin);
            System.out.println("=== USUÁRIO ADMIN CRIADO ===");
            System.out.println("Username: admin");
            System.out.println("Password: admin123");
            System.out.println("=============================");
        } else {
            System.out.println("Usuário admin já existe no sistema");
        }

        // Criar usuário vendedor de exemplo
        if (usuarioRepository.findByUsername("vendedor").isEmpty()) {
            Usuario vendedor = new Usuario();
            vendedor.setUsername("vendedor");
            vendedor.setPassword(passwordEncoder.encode("vendedor123")); // SENHA CRIPTOGRAFADA
            vendedor.setNome("Vendedor do Açougue");
            vendedor.setEmail("vendedor@acougue.com");
            vendedor.setPerfil(PerfilUsuario.VENDEDOR);
            usuarioRepository.save(vendedor);
            System.out.println("Usuário vendedor criado: vendedor / vendedor123");
        }
    }

    private void criarDadosTesteBasico() {
        // Criar produtos de exemplo se não existirem
        if (produtoRepository.count() == 0) {
            System.out.println("Criando produtos de exemplo...");

            Produto picanha = new Produto();
            picanha.setNome("Picanha");
            picanha.setCategoria(CategoriaCarne.BOVINA);
            picanha.setCorte("Bovino");
            picanha.setPrecoCusto(new BigDecimal("45.00"));
            picanha.setPrecoVenda(new BigDecimal("65.00"));
            picanha.setPercentualPerda(new BigDecimal("5.00"));
            picanha.setUnidadeMedida(UnidadeMedida.KG);
            picanha.setEstoqueAtual(50);
            picanha.setEstoqueMinimo(10);

            Produto alcatra = new Produto();
            alcatra.setNome("Alcatra");
            alcatra.setCategoria(CategoriaCarne.BOVINA);
            alcatra.setCorte("Bovino");
            alcatra.setPrecoCusto(new BigDecimal("35.00"));
            alcatra.setPrecoVenda(new BigDecimal("52.00"));
            alcatra.setPercentualPerda(new BigDecimal("6.00"));
            alcatra.setUnidadeMedida(UnidadeMedida.KG);
            alcatra.setEstoqueAtual(40);
            alcatra.setEstoqueMinimo(8);

            Produto linguiça = new Produto();
            linguiça.setNome("Linguiça Toscana");
            linguiça.setCategoria(CategoriaCarne.LINGUICA);
            linguiça.setPrecoCusto(new BigDecimal("18.00"));
            linguiça.setPrecoVenda(new BigDecimal("28.00"));
            linguiça.setPercentualPerda(new BigDecimal("2.00"));
            linguiça.setUnidadeMedida(UnidadeMedida.KG);
            linguiça.setEstoqueAtual(30);
            linguiça.setEstoqueMinimo(5);

            produtoRepository.saveAll(Arrays.asList(picanha, alcatra, linguiça));
            System.out.println("Produtos de exemplo criados com sucesso!");
        }

        // Criar clientes de exemplo se não existirem
        if (clienteRepository.count() == 0) {
            System.out.println("Criando clientes de exemplo...");

            Cliente cliente1 = new Cliente();
            cliente1.setNome("João Silva");
            cliente1.setTelefone("(11) 99999-9999");
            cliente1.setEmail("joao@email.com");
            cliente1.setEndereco("Rua das Carnes, 123");

            Cliente cliente2 = new Cliente();
            cliente2.setNome("Maria Santos");
            cliente2.setTelefone("(11) 88888-8888");
            cliente2.setEmail("maria@email.com");
            cliente2.setEndereco("Avenida dos Cortes, 456");

            clienteRepository.saveAll(Arrays.asList(cliente1, cliente2));
            System.out.println("Clientes de exemplo criados com sucesso!");
        }
    }
}