package com.rpg_api.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.rpg_api.model.Usuario;
// import com.rpg_api.repository.UsuarioRepository;

@Service
public class UsuarioService {
  // private final UsuarioRepository usuarioRepository;

  // public UsuarioService(UsuarioRepository usuarioRepository) {
  //   this.usuarioRepository = usuarioRepository;
  // }

  // public Usuario getUsuarioAutenticado() {
  //   Authentication auth = SecurityContextHolder.getContext().getAuthentication();
  //   System.out.println("Auth: " + auth);

  //   if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
  //     return null; // sem usuário logado
  //   }

  //   String email = (String) auth.getPrincipal(); // normalmente o username é o email

  //   return usuarioRepository.findByEmail(email);
  // }

  public Usuario getUsuarioAutenticado() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
      return null;
    }

    Usuario usuario = (Usuario) auth.getDetails();
    System.out.println("Email do usuário autenticado: " + usuario.getEmail());

    return usuario;
  }
}
