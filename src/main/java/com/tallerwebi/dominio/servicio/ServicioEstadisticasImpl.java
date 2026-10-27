package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.DTOEstadisticaJugador;
import com.tallerwebi.dominio.Estadistica;
import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.Partido;
import com.tallerwebi.dominio.repositorio.RepositorioEstadistica;
import com.tallerwebi.dominio.repositorio.RepositorioJugador;
import com.tallerwebi.dominio.repositorio.RepositorioPartido;
import com.tallerwebi.infraestructura.RepositorioJugadorImpl;
import com.tallerwebi.infraestructura.RepositorioPartidoImpl;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServicioEstadisticasImpl implements ServicioEstadistica {

  private RepositorioEstadistica repositorioEstadisticas;
  private RepositorioPartido repositorioPartido;
  private RepositorioJugador repositorioJugador;

  public ServicioEstadisticasImpl(
    RepositorioEstadistica repositorioEstadistica,
    RepositorioPartido repositorioPartido,
    RepositorioJugador repositorioJugador
  ) {
    this.repositorioEstadisticas = repositorioEstadistica;
    this.repositorioPartido = repositorioPartido;
    this.repositorioJugador = repositorioJugador;
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
      goles += valorOZero(estadistica.getGoles());
      asistencias += valorOZero(estadistica.getAsistencias());
      amarillas += valorOZero(estadistica.getAmarillas());
      rojas += valorOZero(estadistica.getRojas());
      puntaje += valorOZero(estadistica.getPuntaje());
    }

    return new Estadistica(goles, asistencias, amarillas, rojas, puntaje, jugador, null);
  }

  private Integer valorOZero(Integer valor) {
    return valor != null ? valor : 0;
  }

  @Transactional
  @Override
  public void guardarEstadisticasYEnviarARevision(
    Long idPartido,
    List<DTOEstadisticaJugador> datosCargados
  ) {
    Partido partido = repositorioPartido.buscarPorId(idPartido);

    for (DTOEstadisticaJugador dto : datosCargados) {
      Jugador jugador = repositorioJugador.buscarPorUsuarioId(dto.getIdJugador());
      Estadistica estadistica = repositorioEstadisticas.buscarPorPartidoYJugador(partido, jugador);

      if (estadistica == null) {
        estadistica = new Estadistica();
        estadistica.setPartido(partido);
        estadistica.setJugador(jugador);
      }

      estadistica.setGoles(dto.getGoles());
      estadistica.setAmarillas(dto.getAmarillas());
      estadistica.setRojas(dto.getRojas());
      repositorioEstadisticas.guardar(estadistica);
    }

    partido.setEstadoEstadisticas(Partido.EstadoEstadisticas.EN_REVISION);
    repositorioPartido.guardar(partido);
  }
}
