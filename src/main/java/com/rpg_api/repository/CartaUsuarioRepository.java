package com.rpg_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rpg_api.model.CartaUsuario;

@Repository
public interface CartaUsuarioRepository extends JpaRepository<CartaUsuario, Integer> {
  List<CartaUsuario> findByUsuarioId(int idUsuario);
  boolean existsByUsuarioIdAndCartaId(int idUsuario, int idCarta);
}
