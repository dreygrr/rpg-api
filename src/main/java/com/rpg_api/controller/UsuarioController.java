package com.rpg_api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rpg_api.model.Carta;
import com.rpg_api.service.CartaService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {
  private final CartaService cartaService;

  public UsuarioController(CartaService cartaService) {
    this.cartaService = cartaService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getMethodName(@PathVariable int id) {
    return ResponseEntity.ok("eita@@@ " + id);
  }
  
  @GetMapping("/{id}/cartas")
  public ResponseEntity<?> getCartasById(@PathVariable int id) {
    try {
      System.out.println("AQUI" + id);
      List<Carta> cartas = cartaService.getCartasByUsuarioId(id);

      return ResponseEntity.ok(cartas);  
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
