package com.rpg_api.security;

import com.rpg_api.model.Usuario;
import com.rpg_api.repository.UsuarioRepository;
import com.rpg_api.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
  
  @Autowired
  private JwtUtil jwtUtil;
  
  @Autowired
  private UsuarioRepository usuarioRepository;
  
  @Override
  protected void doFilterInternal(
    @NonNull HttpServletRequest request,
    @NonNull HttpServletResponse response,
    @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    
    String authHeader = request.getHeader("Authorization");
    
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }
    
    String token = authHeader.substring(7);
    
    Long userId = jwtUtil.extractUserId(token);
    String email = jwtUtil.extractEmail(token);
    
    if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      Usuario usuario = usuarioRepository.findById(userId.intValue()).orElse(null);
      
      if (usuario != null && jwtUtil.validateToken(token, email)) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
          usuario.getEmail(),  // principal = email (simples e padrão)
          null,
          null // authorities se quiser no futuro
        );
        
        // guardamos o usuário completo aqui:
        authToken.setDetails(usuario);
        
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }
    
    filterChain.doFilter(request, response);
  }
}
