package com.rpg_api.controller;

import com.rpg_api.dto.CartaCreateDto;
import com.rpg_api.dto.DueloDto;
import com.rpg_api.dto.ResultadoDueloDto;
import com.rpg_api.model.Carta;
import com.rpg_api.model.Usuario;
import com.rpg_api.service.CartaService;
import com.rpg_api.service.UsuarioService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cartas")
@CrossOrigin(origins = "*")
public class CartaController {
    private final UsuarioService usuarioService;
    private final CartaService cartaService;

    public CartaController(CartaService cartaService, UsuarioService usuarioService) {
        this.cartaService = cartaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/{nome}")
    public ResponseEntity<?> getByNome(@PathVariable String nome) {
        Carta carta = cartaService.getByNome(nome);

        return ResponseEntity.ok(carta);
    }

    @GetMapping("/aleatorias")
    public ResponseEntity<?> getCartasAleatorias(
        @RequestParam(defaultValue = "3") int qtd,
        @RequestParam(defaultValue = "true") boolean salvar
    ) {
        try {
            System.out.println("Gerando " + qtd + " cartas aleatórias. Salvar: " + salvar);
            
            Usuario usuario = usuarioService.getUsuarioAutenticado();

            List<Carta> cartas = cartaService.gerarCartasAleatorias(qtd, salvar ? usuario : null);
            
            return ResponseEntity.ok(cartas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody CartaCreateDto carta) {
        try {
            Carta novaCarta = cartaService.addCarta(carta);
            return ResponseEntity.ok(novaCarta);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
