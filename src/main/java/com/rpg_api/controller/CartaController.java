package com.rpg_api.controller;

import com.rpg_api.dto.DueloDto;
import com.rpg_api.dto.ResultadoDueloDto;
import com.rpg_api.model.Carta;
import com.rpg_api.service.CartaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Carta")
@CrossOrigin(origins = "*") // permite chamadas do front Next.js
public class CartaController {
    private final CartaService cartaService;

    public CartaController(CartaService cartaService) {
        this.cartaService = cartaService;
    }

    @GetMapping("/Random")
    public ResponseEntity<List<Carta>> getRandomCards(@RequestParam(defaultValue = "3") int qtd) {
        try {
            List<Carta> cartas = cartaService.gerarCartasAleatorias(qtd);

            return ResponseEntity.ok(cartas);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/Duelar")
    public ResponseEntity<ResultadoDueloDto> duelarCartas(@RequestBody DueloDto dueloDto) {
        try {
            return ResponseEntity.ok(cartaService.duelarCartas(dueloDto.idCartaJogador, dueloDto.idCartaInimigo));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
