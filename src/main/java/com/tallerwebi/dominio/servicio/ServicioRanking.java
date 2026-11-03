package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.Jugador;
import java.util.List;

public interface ServicioRanking {
  List<Jugador> obtenerRanking();

  String calcularNivel(Integer puntos);
  List<Jugador> obtenerRankingFiltrado(
    String nivel,
    String posicion,
    String rango,
    String vista,
    Long usuarioId
  );
}
