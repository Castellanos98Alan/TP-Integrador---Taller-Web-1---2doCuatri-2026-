package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.Estadistica;
import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.Partido;
import java.util.List;

public interface ServicioEstadistica {
  void registrarEstadisticaDePartido(Estadistica estadistica);

  Estadistica obtenerEstadisticasGenerales(Jugador jugador);

  List<Estadistica> obtenerEstadisticasPorJugador(Jugador jugador);

  List<Estadistica> obtenerEstadisticasPorPartido(Partido partido);
}
