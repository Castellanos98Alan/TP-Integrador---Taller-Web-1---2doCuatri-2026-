package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioEstadistica {
  void registrarEstadisticaDePartido(Estadistica estadistica);

  Estadistica obtenerEstadisticasGenerales(Jugador jugador);

  List<Estadistica> obtenerEstadisticasPorJugador(Jugador jugador);

  List<Estadistica> obtenerEstadisticasPorPartido(Partido partido);
}
