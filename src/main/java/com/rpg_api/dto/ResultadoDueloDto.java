package com.rpg_api.dto;

import com.rpg_api.model.Carta;
import lombok.Data;

@Data
public class ResultadoDueloDto {
    public Carta vencedor;
    public String tipoVitoria;
    DetalhesDueloDto detalhes;
}
