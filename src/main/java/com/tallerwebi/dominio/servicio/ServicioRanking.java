package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.Jugador;
import java.util.List;

public interface ServicioRanking {
  List<Jugador> obtenerRanking();

  List<Jugador> obtenerRankingFiltradoPorNivel(String nivel);

  List<Jugador> obtenerRankingFiltradoPorPosicion(String posicion);

  List<Jugador> obtenerRankingFiltradoPorNivelYPosicion(String nivel, String posicion);

  String calcularNivel(Integer puntos);
}
