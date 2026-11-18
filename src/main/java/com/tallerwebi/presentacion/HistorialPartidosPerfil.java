package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.Enumeration.EstadoPartido;
import com.tallerwebi.dominio.servicio.ServicioEstadistica;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

@Component
public class HistorialPartidosPerfil {

  private final ServicioEstadistica servicioEstadistica;

  public HistorialPartidosPerfil(ServicioEstadistica servicioEstadistica) {
    this.servicioEstadistica = servicioEstadistica;
  }

  public List<Map<String, Object>> armarHistorialPartidos(
    List<Partido> partidosJugador,
    List<Estadistica> estadisticasJugador,
    Jugador jugador
  ) {
    List<Map<String, Object>> historialPartidos = new ArrayList<>();

    for (Partido partido : partidosJugador) {
      if (partido.getEstado() == EstadoPartido.FINALIZADO) {
        historialPartidos.add(construirDatosPartido(partido, estadisticasJugador, jugador));
      }
    }

    return historialPartidos;
  }

  private Map<String, Object> construirDatosPartido(
    Partido partido,
    List<Estadistica> estadisticasJugador,
    Jugador jugador
  ) {
    Map<String, Object> datosPartido = new ModelMap();

    datosPartido.put("idPartido", partido.getId());
    datosPartido.put("fecha", partido.getFecha());

    datosPartido.put("goles", 0);
    datosPartido.put("asistencias", 0);
    datosPartido.put("amarillas", 0);
    datosPartido.put("rojas", 0);
    datosPartido.put("puntaje", 0);

    // Obtener las estadísticas de todos los jugadores del partido.
    List<Estadistica> estadisticasPartido = servicioEstadistica.obtenerEstadisticasPorPartido(
      partido
    );

    // Mantener el mismo cálculo de marcador que utiliza el resumen.
    Map<String, Integer> marcador = servicioEstadistica.calcularMarcadorPartido(
      partido,
      estadisticasPartido
    );

    int golesLocal = marcador.getOrDefault("local", 0);
    int golesVisitante = marcador.getOrDefault("visitante", 0);

    datosPartido.put("resultado", golesLocal + " - " + golesVisitante);

    // Identificar el equipo al que pertenece el jugador.
    boolean esLocal = perteneceAlEquipo(partido, jugador, partido.getEquipo1());

    boolean esVisitante = perteneceAlEquipo(partido, jugador, partido.getEquipo2());

    if (esLocal) {
      datosPartido.put("condicion", obtenerCondicion(golesLocal, golesVisitante));
    } else if (esVisitante) {
      datosPartido.put("condicion", obtenerCondicion(golesVisitante, golesLocal));
    } else {
      datosPartido.put("condicion", "Sin resultado");
    }

    // Recuperar las estadísticas individuales del jugador.
    Estadistica estadistica = buscarEstadisticaDePartido(partido.getId(), estadisticasJugador);

    if (estadistica != null) {
      datosPartido.put("goles", valorONullACero(estadistica.getGoles()));
      datosPartido.put("asistencias", valorONullACero(estadistica.getAsistencias()));
      datosPartido.put("amarillas", valorONullACero(estadistica.getAmarillas()));
      datosPartido.put("rojas", valorONullACero(estadistica.getRojas()));
      datosPartido.put("puntaje", valorONullACero(estadistica.getPuntaje()));
    }

    return datosPartido;
  }

  private boolean perteneceAlEquipo(Partido partido, Jugador jugador, Equipo equipo) {
    if (
      partido.getInscripciones() == null ||
      jugador == null ||
      jugador.getId() == null ||
      equipo == null ||
      equipo.getId() == null
    ) {
      return false;
    }

    return partido
      .getInscripciones()
      .stream()
      .anyMatch(inscripcion ->
        inscripcion.getJugador() != null &&
        inscripcion.getJugador().getId() != null &&
        inscripcion.getJugador().getId().equals(jugador.getId()) &&
        inscripcion.getEquipo() != null &&
        inscripcion.getEquipo().getId() != null &&
        inscripcion.getEquipo().getId().equals(equipo.getId())
      );
  }

  private String obtenerCondicion(int golesPropios, int golesRivales) {
    int comparacion = Integer.compare(golesPropios, golesRivales);

    if (comparacion > 0) {
      return "Victoria";
    }

    if (comparacion < 0) {
      return "Derrota";
    }

    return "Empate";
  }

  private Estadistica buscarEstadisticaDePartido(
    Long idPartido,
    List<Estadistica> estadisticasJugador
  ) {
    return estadisticasJugador
      .stream()
      .filter(estadistica ->
        estadistica.getPartido() != null && estadistica.getPartido().getId().equals(idPartido)
      )
      .findFirst()
      .orElse(null);
  }

  private int valorONullACero(Integer valor) {
    return valor != null ? valor : 0;
  }
}
