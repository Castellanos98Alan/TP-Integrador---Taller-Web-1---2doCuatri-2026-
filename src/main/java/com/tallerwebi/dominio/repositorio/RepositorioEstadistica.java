package com.tallerwebi.dominio.repositorio;

import com.tallerwebi.dominio.Estadistica;
import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.Partido;
import java.util.List;

public interface RepositorioEstadistica {
  public void guardar(Estadistica estadistica);

  public void modificar(Estadistica estadistica);

  public List<Estadistica> buscarTodasLasEstadisticasPorJugador(Jugador jugador);

  public List<Estadistica> buscarTodasLasEstadisticasPorPartido(Partido partido);

  List<Estadistica> buscarPorJugadorYCondicion(
    Jugador jugador,
    Estadistica.CondicionJugador condicion
  );
}
