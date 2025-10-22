// src/main/java/com/acougue/erp/service/AuthService.java
package com.acougue.erp.service;

import com.acougue.erp.model.Usuario;
import com.acougue.erp.repository.UsuarioRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class AuthService {

    @Value("${jwt.secret:mySecretKeyForAcougueERP2024SecurityApplication}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}") // 24 horas
    private int jwtExpirationMs;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateJwtToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            System.err.println("Token JWT inválido: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("Token JWT expirado: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("Token JWT não suportado: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Claims JWT vazios: " + e.getMessage());
        }
        return false;
    }

    public String authenticateUser(String username, String password) {
        try {
            System.out.println("=== 🚀 INICIANDO LOGIN ===");
            System.out.println("📧 Username: '" + username + "'");
            System.out.println("🔑 Password: '" + password + "'");

            // Buscar usuário no banco
            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        System.out.println("❌ USUÁRIO NÃO ENCONTRADO!");
                        return new RuntimeException("Usuário não encontrado");
                    });

            System.out.println("✅ Usuário encontrado: " + usuario.getUsername());
            System.out.println("🔐 Senha no banco: " + usuario.getPassword());

            // VERIFICAÇÃO SIMPLES - SEM CRIPTOGRAFIA
            if (!usuario.getPassword().equals(password)) {
                System.out.println("❌ SENHA NÃO CONFERE!");
                System.out.println("Esperava: '" + password + "'");
                System.out.println("Recebeu: '" + usuario.getPassword() + "'");
                throw new RuntimeException("Senha incorreta");
            }

            System.out.println("✅ SENHA CONFERIU!");

            // Atualizar último login
            usuario.setUltimoLogin(LocalDateTime.now());
            usuarioRepository.save(usuario);

            String token = generateJwtToken(username);
            System.out.println("🎫 Token gerado com sucesso!");
            System.out.println("=== 🎉 LOGIN BEM-SUCEDIDO ===");

            return token;

        } catch (Exception e) {
            System.out.println("=== 💥 ERRO NO LOGIN ===");
            System.out.println("Erro: " + e.getMessage());
            throw new RuntimeException("Credenciais inválidas");
        }
    }

    public Usuario registerUser(Usuario usuario) {
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new RuntimeException("Username já está em uso!");
        }

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("Email já está em uso!");
        }

        return usuarioRepository.save(usuario);
    }
}