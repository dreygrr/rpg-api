package com.rpg_api.dto;

public class DetalhesDueloDto {
    public DetalhesDueloDto(float danoJogador, float resJogador, float danoInimigo, float resInimigo) {
        this.danoJogador = danoJogador;
        this.resJogador = resJogador;
        this.danoInimigo = danoInimigo;
        this.resInimigo = resInimigo;
    }

    float danoJogador;
    float resJogador;

    float danoInimigo;
    float resInimigo;
}
