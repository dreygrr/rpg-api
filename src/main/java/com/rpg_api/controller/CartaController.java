package com.rpg_api.controller;

import com.rpg_api.dto.DueloDto;
import com.rpg_api.dto.ResultadoDueloDto;
import com.rpg_api.model.Carta;
import com.rpg_api.service.CartaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cartas")
@CrossOrigin(origins = "*")
public class CartaController {
    private final CartaService cartaService;

    public CartaController(CartaService cartaService) {
        this.cartaService = cartaService;
    }

    @GetMapping("/aleatorias")
    public ResponseEntity<List<Carta>> getCartasAleatorias(@RequestParam(defaultValue = "3") int qtd) {
        try {
            List<Carta> cartas = cartaService.gerarCartasAleatorias(qtd);
            return ResponseEntity.ok(cartas);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/duelo")
    public ResponseEntity<?> duelarCartas(@RequestBody DueloDto dueloDto) {
        try {
            ResultadoDueloDto resultado = cartaService.duelarCartas(dueloDto.idCartaJogador, dueloDto.idCartaInimigo);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}