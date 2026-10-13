package com.tallerwebi.dominio;

import java.util.List;

public class ServicioEstadisticasImpl implements ServicioEstadistica {

  private RepositorioEstadistica repositorioEstadisticas;

  public ServicioEstadisticasImpl(RepositorioEstadistica repositorioEstadistica) {
    this.repositorioEstadisticas = repositorioEstadistica;
  }

  @Override
  public List<Estadistica> obtenerEstadisticasPorJugador(Jugador jugador) {
    return repositorioEstadisticas.buscarTodasLasEstadisticasPorJugador(jugador);
  }

  @Override
  public List<Estadistica> obtenerEstadisticasPorPartido(Partido partido) {
    return repositorioEstadisticas.buscarTodasLasEstadisticasPorPartido(partido);
  }

  @Override
  public void registrarEstadisticaDePartido(Estadistica estadistica) {
    repositorioEstadisticas.guardar(estadistica);
  }

  @Override
  public Estadistica obtenerEstadisticasGenerales(Jugador jugador) {
    List<Estadistica> estadisticas = repositorioEstadisticas.buscarTodasLasEstadisticasPorJugador(
      jugador
    );

    Integer goles = 0;
    Integer asistencias = 0;
    Integer amarillas = 0;
    Integer rojas = 0;
    Integer puntaje = 0;

    for (Estadistica estadistica : estadisticas) {
      goles += estadistica.getGoles();

      asistencias += estadistica.getAsistencias();

      amarillas += estadistica.getAmarillas();

      rojas += estadistica.getRojas();

      puntaje += estadistica.getPuntaje();
    }

    return new Estadistica(goles, asistencias, amarillas, rojas, puntaje, jugador, null);
  }
}
