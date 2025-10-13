package com.rpg_api.model;

import lombok.Data;

import java.util.List;

@Data
public class Deck {
    private String Nome;
    private List<Carta> Cartas;
}
