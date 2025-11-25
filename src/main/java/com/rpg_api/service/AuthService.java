package com.rpg_api.service;

import com.rpg_api.dto.AuthResponseDto;
import com.rpg_api.dto.CadastroDto;
import com.rpg_api.dto.LoginDto;
import com.rpg_api.model.Usuario;
import com.rpg_api.repository.UsuarioRepository;
import com.rpg_api.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponseDto cadastrar(CadastroDto cadastroDto) {
        // Verificar se o email já existe
        if (usuarioRepository.existsByEmail(cadastroDto.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        // Criar novo usuário
        Usuario usuario = new Usuario();
        usuario.setEmail(cadastroDto.getEmail());
        usuario.setNome(cadastroDto.getNome());
        usuario.setSenha(passwordEncoder.encode(cadastroDto.getSenha()));

        usuario = usuarioRepository.save(usuario);

        // Gerar token JWT
        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getId(), usuario.getNome());

        return new AuthResponseDto(token, usuario.getEmail(), usuario.getNome(), usuario.getId());
    }

    public AuthResponseDto login(LoginDto loginDto) {
        // Buscar usuário por email
        Usuario usuario = usuarioRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("Email ou senha inválidos"));

        // Verificar senha
        if (!passwordEncoder.matches(loginDto.getSenha(), usuario.getSenha())) {
            throw new RuntimeException("Email ou senha inválidos");
        }

        // Gerar token JWT
        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getId(), usuario.getNome());

        return new AuthResponseDto(token, usuario.getEmail(), usuario.getNome(), usuario.getId());
    }
}

