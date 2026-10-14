package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioRanking {
  List<Jugador> obtenerRanking();

  List<Jugador> obtenerRankingFiltradoPorNivel(String nivel);

  List<Jugador> obtenerRankingFiltradoPorPosicion(String posicion);

  List<Jugador> obtenerRankingFiltradoPorNivelYPosicion(String nivel, String posicion);

  String calcularNivel(Integer puntos);
}
