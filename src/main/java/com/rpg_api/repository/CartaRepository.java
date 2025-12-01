package com.rpg_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rpg_api.model.Carta;

@Repository
public interface CartaRepository extends JpaRepository<Carta, Integer> {
  Carta findByNome(String nome);
}
