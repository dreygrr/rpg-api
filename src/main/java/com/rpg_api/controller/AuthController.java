package com.rpg_api.controller;

import com.rpg_api.dto.AuthResponseDto;
import com.rpg_api.dto.CadastroDto;
import com.rpg_api.dto.LoginDto;
import com.rpg_api.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrar(@RequestBody CadastroDto cadastroDto) {
        try {
            // Validações básicas
            if (cadastroDto.getEmail() == null || cadastroDto.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email é obrigatório");
            }
            if (cadastroDto.getNome() == null || cadastroDto.getNome().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Nome é obrigatório");
            }
            if (cadastroDto.getSenha() == null || cadastroDto.getSenha().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Senha é obrigatória");
            }
            if (cadastroDto.getSenha().length() < 6) {
                return ResponseEntity.badRequest().body("Senha deve ter no mínimo 6 caracteres");
            }

            AuthResponseDto response = authService.cadastrar(cadastroDto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao cadastrar usuário");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        try {
            // Validações básicas
            if (loginDto.getEmail() == null || loginDto.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email é obrigatório");
            }
            if (loginDto.getSenha() == null || loginDto.getSenha().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Senha é obrigatória");
            }

            AuthResponseDto response = authService.login(loginDto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao fazer login");
        }
    }
}

