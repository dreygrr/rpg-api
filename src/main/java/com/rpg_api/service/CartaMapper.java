package com.rpg_api.service;

import java.util.List;
import java.util.Map;

import com.rpg_api.dto.CartaCreateDto;

public class CartaMapper {
  public static CartaCreateDto fromApi(Map<String, Object> api) {
    String nome = (String) api.get("name");
    
    Float strength = getFloat(api.get("strength"));
    Float hitPoints = getFloat(api.get("hit_points"));
    Float armorClass = extractArmorClass(api.get("armor_class"));
    Float speed = extractSpeed(api.get("speed"));
    
    String foto = api.get("image") != null 
    ? "https://www.dnd5eapi.co" + api.get("image")
    : null;
    
    String descricao = "A creature from D&D universe";
    
    return new CartaCreateDto(
      nome,
      strength,
      hitPoints,
      armorClass,
      speed,
      foto,
      descricao
    );
  }
  
  private static Float getFloat(Object value) {
    if (value instanceof Number num) {
      return num.floatValue();
    }
    return 0f;
  }
  
  private static Float extractArmorClass(Object armorObj) {
    if (armorObj instanceof List<?> lista && !lista.isEmpty()) {
      Object item = lista.get(0);
      if (item instanceof Map<?, ?> map) {
        Object val = map.get("value");
        if (val instanceof Number num) {
          return num.floatValue();
        }
      }
    }
    return 0f;
  }
  
  private static Float extractSpeed(Object speedObj) {
    // API retorna algo assim:
    // "speed": { "walk": "10 ft.", "swim": "40 ft." }
    if (speedObj instanceof Map<?, ?> map) {
      Object walk = map.get("walk");
      if (walk instanceof String s && s.matches("\\d+.*")) {
        // extrai número antes do espaço
        try {
          return Float.valueOf(s.split(" ")[0]);
        } catch (Exception e) {}
      }
    }
    return 0f;
  }
}
