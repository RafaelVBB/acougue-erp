// src/main/java/com/acougue/erp/controller/AuthController.java
package com.acougue.erp.controller;

import com.acougue.erp.model.Usuario;
import com.acougue.erp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            String jwt = authService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());

            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("type", "Bearer");
            response.put("username", loginRequest.getUsername());
            response.put("message", "Login realizado com sucesso!");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Credenciais inválidas"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Usuario usuario) {
        try {
            Usuario novoUsuario = authService.registerUser(usuario);
            return ResponseEntity.ok(Map.of("message", "Usuário registrado com sucesso!", "id", novoUsuario.getId()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Classe interna para request de login
    public static class LoginRequest {
        private String username;
        private String password;

        // Getters e Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}