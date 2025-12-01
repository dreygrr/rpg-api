package com.rpg_api.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.Data;

@Data
@Entity
public class Carta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String nome;
    private float dano;
    private float vida;
    private float defesa;
    private float velocidade;
    private String foto;
    private String descricao;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

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

    @PrePersist
    public void onCreate() {
        dataCriacao = LocalDateTime.now();
    }
}
