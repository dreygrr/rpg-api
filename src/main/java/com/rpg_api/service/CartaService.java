package com.rpg_api.service;

import com.rpg_api.dto.CartaCreateDto;
import com.rpg_api.dto.DetalhesDueloDto;
import com.rpg_api.dto.ResultadoDueloDto;
import com.rpg_api.model.Carta;
import com.rpg_api.repository.CartaRepository;
import com.rpg_api.model.CartaUsuario;
import com.rpg_api.model.Usuario;
import com.rpg_api.repository.CartaUsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CartaService {
  private final RestTemplate restTemplate = new RestTemplate();
  private final Random random = new Random();
  
  private final CartaUsuarioRepository cartaUsuarioRepository;
  private final CartaRepository cartaRepository;
  
  public CartaService(CartaUsuarioRepository cartaUsuarioRepository, CartaRepository cartaRepository) {
    this.cartaUsuarioRepository = cartaUsuarioRepository;
    this.cartaRepository = cartaRepository;
  }
  
  public Carta addCarta(CartaCreateDto dto) {
    Carta carta = new Carta();

    carta.setNome(dto.nome());
    carta.setDescricao(dto.descricao());
    carta.setVida(dto.vida());
    carta.setDano(dto.dano());
    carta.setDefesa(dto.defesa());
    carta.setVelocidade(dto.velocidade());
    carta.setFoto(dto.foto());

    return cartaRepository.save(carta); // id gerado automaticamente
  }

  public List<Carta> gerarCartasAleatorias(int qtd, Usuario usuario) {
    Map<?, ?> dadosApi = restTemplate.getForObject("https://www.dnd5eapi.co/api/2014/monsters", Map.class);
    
    if (dadosApi == null) {
      throw new RuntimeException("Nenhum monstro encontrado na API D&D");
    }
    
    @SuppressWarnings("unchecked")
    List<Map<String, String>> resultadosApi = (List<Map<String, String>>) dadosApi.get("results");
    
    if (resultadosApi == null || resultadosApi.isEmpty()) {
      throw new RuntimeException("Nenhum monstro encontrado na API D&D");
    }
    
    List<Carta> cartas = new ArrayList<Carta>();
    
    for (int i = 0; i < qtd; i++) {
      Carta carta = new Carta();
      
      // Tentar gerar carta que ainda não foi gerada
      do {
        Map<String, String> cartaAtual = resultadosApi.get(random.nextInt(resultadosApi.size()));
        String indexNome = cartaAtual.get("index");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> dadosApiCartaAtual = restTemplate.getForObject(
          "https://www.dnd5eapi.co/api/2014/monsters/" + indexNome,
          Map.class
        );
        
        if (dadosApiCartaAtual == null) continue;

        String nome = (String) dadosApiCartaAtual.get("name");
        
        //carta já existe na base?
        Carta existente = cartaRepository.findByNome(nome);
        
        // se não existir no banco -> Criar nova carta
        if (existente == null) {
          CartaCreateDto dto = CartaMapper.fromApi(dadosApiCartaAtual);
          addCarta(dto);
        } else { // se existir, e ter sido adicionada há mais de 1 mês -> ATUALIZAR
          LocalDateTime umMesAtras = LocalDateTime.now().minusMonths(1);

          if (existente.getDataCriacao().isBefore(umMesAtras)) {
            System.out.println("Atualizando carta antiga: " + nome);

            CartaCreateDto dto = CartaMapper.fromApi(dadosApiCartaAtual);

            existente.setNome(dto.nome());
            existente.setDano(dto.dano());
            existente.setVida(dto.vida());
            existente.setDefesa(dto.defesa());
            existente.setVelocidade(dto.velocidade());
            existente.setFoto(dto.foto());
            existente.setDataCriacao(LocalDateTime.now()); // atualizar timestamp

            cartaRepository.save(existente);
          }
        }

        carta = cartaRepository.findByNome(dadosApiCartaAtual.get("name").toString());
      } while (carta == null);
      
      cartas.add(carta);
      
      String nome = "anônimo";
      if (usuario != null) nome = usuario.getNome();

      System.out.println("usuario " + nome + " recebeu carta: " + carta.getNome());
      
      if (usuario != null) {
        boolean jaTem = cartaUsuarioRepository.existsByUsuarioIdAndCartaId(usuario.getId().intValue(), carta.getId());

        if (!jaTem) {
          CartaUsuario cu = new CartaUsuario();
          cu.setUsuario(usuario);
          cu.setCarta(carta);

          cartaUsuarioRepository.save(cu);
          System.out.println("Usuário " + usuario.getNome() + " recebeu carta: " + carta.getNome());
        } else {
          System.out.println("Usuário " + usuario.getNome() + " já tem a carta " + carta.getNome() + ", não salvando novamente.");
        }
      }
    }
    
    return cartas;
  }
  
  public Carta getByNome(String nome) {
    return cartaRepository.findByNome(nome);
  }
  
  public ResultadoDueloDto duelarCartas(int idJogador, int idInimigo) {
    Carta cartaJogador = cartaRepository.findById(idJogador).orElse(null);
    Carta cartaInimigo = cartaRepository.findById(idInimigo).orElse(null);
    
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
  
  public List<Carta> getCartasByUsuarioId(int idUsuario) {
    List<CartaUsuario> rels = cartaUsuarioRepository.findByUsuarioId(idUsuario);
    
    return rels.stream().map(CartaUsuario::getCarta).toList();
  }
}
