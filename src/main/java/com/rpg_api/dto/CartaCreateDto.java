package com.rpg_api.dto;

public record CartaCreateDto(
  String nome,
  float dano,
  float vida,
  float defesa,
  float velocidade,
  String foto,
  String descricao
) {}
