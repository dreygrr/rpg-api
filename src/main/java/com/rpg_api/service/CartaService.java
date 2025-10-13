package com.rpg_api.service;

import com.rpg_api.dto.DetalhesDueloDto;
import com.rpg_api.dto.ResultadoDueloDto;
import com.rpg_api.model.Carta;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class CartaService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final Random random = new Random();
    private int contadorId = 1;

    // Lista de cartas já geradas
    private final List<Carta> cartasGeradas = new ArrayList<>();

    // Mapa para buscar carta por id rapidamente
    private final Map<Integer, Carta> cartasPorId = new HashMap<>();

    public List<Carta> gerarCartasAleatorias(int qtd) {
        Map<?, ?> lista = restTemplate.getForObject("https://www.dnd5eapi.co/api/2014/monsters", Map.class);

        assert lista != null;
        @SuppressWarnings("unchecked")
        List<Map<String, String>> resultados = (List<Map<String, String>>) lista.get("results");

        if (resultados == null || resultados.isEmpty()) {
            throw new RuntimeException("Nenhum monstro encontrado na API D&D");
        }

        List<Carta> cartas = new ArrayList<>();

        for (int i = 0; i < qtd; i++) {
            Carta carta = null;

            // Tentar gerar carta que ainda não foi gerada
            do {
                Map<String, String> monstro = resultados.get(random.nextInt(resultados.size()));
                String index = monstro.get("index");

                @SuppressWarnings("unchecked")
                Map<String, Object> dadosMonstro = restTemplate.getForObject(
                        "https://www.dnd5eapi.co/api/2014/monsters/" + index,
                        Map.class
                );

                if (dadosMonstro == null) continue;

                // Verifica se já existe carta com mesmo nome
                Optional<Carta> existente = cartasGeradas.stream()
                        .filter(c -> c.getNome().equals(dadosMonstro.get("name")))
                        .findFirst();

                if (existente.isPresent()) {
                    carta = null; // repetida, gera novamente
                } else {
                    carta = criarCarta(dadosMonstro);
                }
            } while (carta == null);

            cartas.add(carta);
            cartasGeradas.add(carta);
            cartasPorId.put(carta.getId(), carta);
        }

        return cartas;
    }

    private Carta criarCarta(Map<String, Object> dadosMonstro) {
        Carta carta = new Carta();
        carta.setId(contadorId++);
        carta.setNome((String) dadosMonstro.get("name"));
        carta.setVida(((Number) dadosMonstro.getOrDefault("hit_points", 0)).floatValue());

        // Defesa
        Object acObj = dadosMonstro.get("armor_class");
        if (acObj instanceof List<?> list && !list.isEmpty()) {
            Object first = list.get(0);
            if (first instanceof Map<?, ?> acMap) {
                carta.setDefesa(((Number) acMap.getOrDefault("value", null)).floatValue());
            }
        } else if (acObj instanceof Number num) {
            carta.setDefesa(num.floatValue());
        } else {
            carta.setDefesa(0f);
        }

        carta.setDano(((Number) dadosMonstro.getOrDefault("strength", 0)).floatValue());

        @SuppressWarnings("unchecked")
        Map<String, Object> speed = (Map<String, Object>) dadosMonstro.get("speed");
        float velocidade = 0f;

        if (speed != null) {
            String valorVelocidade = null;
            if (speed.containsKey("fly")) valorVelocidade = (String) speed.get("fly");
            else if (speed.containsKey("walk")) valorVelocidade = (String) speed.get("walk");
            else if (speed.containsKey("swim")) valorVelocidade = (String) speed.get("swim");

            if (valorVelocidade != null) {
                valorVelocidade = valorVelocidade.replace(" ft.", "").trim();
                try { velocidade = Float.parseFloat(valorVelocidade); } catch (NumberFormatException ignored) {}
            }
        }
        carta.setVelocidade(velocidade);

        carta.setDescricao((String) dadosMonstro.getOrDefault("size", "Unknown") + " creature");
        carta.setFoto("https://www.dnd5eapi.co" + dadosMonstro.getOrDefault("image", null));

        return carta;
    }

    // Buscar carta pelo ID
    public Carta buscarCartaPorId(int id) {
        return cartasPorId.get(id);
    }

    public ResultadoDueloDto duelarCartas(int idJogador, int idInimigo) {
        Carta cartaJogador = cartasPorId.get(idJogador);
        Carta cartaInimigo = cartasPorId.get(idInimigo);

        if (cartaJogador == null || cartaInimigo == null) {
            throw new IllegalArgumentException("Uma das cartas não foi encontrada pelo ID fornecido.");
        }

        float defJogador = cartaJogador.getDefesa();
        float vidaJogador = cartaJogador.getVida();

        float defInimigo = cartaInimigo.getDefesa();
        float vidaInimigo = cartaInimigo.getVida();

        float danoJogador = cartaJogador.getDano() - defInimigo;
        float danoInimigo = cartaInimigo.getDano() - defJogador;

        float resJogador = vidaJogador - danoInimigo;
        float resInimigo = vidaInimigo - danoJogador;

        ResultadoDueloDto dto = new ResultadoDueloDto();

        dto.setVencedor(resJogador > resInimigo ? cartaJogador : resJogador == resInimigo ? null : cartaInimigo);
        dto.setTipoVitoria(resJogador == resInimigo ? "empate" : "normal");
        dto.setDetalhes(new DetalhesDueloDto(danoJogador, resJogador, danoInimigo, resInimigo));

        return dto;
    }
}
