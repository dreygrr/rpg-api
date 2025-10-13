package com.rpg_api.model;

import lombok.Data;

@Data
public class Carta {
    private int id;
    private String nome;
    private float dano;
    private float vida;
    private float defesa;
    private float velocidade;
    private String foto;
    private String descricao = "";

    public Carta() {}

    public Carta(int id, String nome, float dano, float vida, float defesa, float velocidade, String raridade, String[] habilidades, String foto, String descricao) {
        this.id = id;
        this.nome = nome;
        this.dano = dano;
        this.vida = vida;
        this.defesa = defesa;
        this.velocidade = velocidade;
        this.foto = foto;
        this.descricao = descricao;
    }
}
