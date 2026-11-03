package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.*;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

public interface ServicioEstadistica {
  void registrarEstadisticaDePartido(Estadistica estadistica);

  Estadistica obtenerEstadisticasGenerales(Jugador jugador);

  List<Estadistica> obtenerEstadisticasPorJugador(Jugador jugador);

  List<Estadistica> obtenerEstadisticasPorPartido(Partido partido);

  boolean puedeCargarEstadisticas(Partido partido, Usuario usuarioOrganizador);

  @Transactional
  void guardarEstadisticasYEnviarARevision(
    Long idPartido,
    List<DTOEstadisticaJugador> datosCargados
  );

  boolean puedeRevisarEstadisticas(Partido partido, Usuario usuario);

  Map<String, Integer> calcularMarcadorPartido(Partido partido, List<Estadistica> estadisticas);

  Map<String, List<Estadistica>> obtenerEstadisticasSeparadasPorEquipo(
    Partido partido,
    List<Estadistica> estadisticas
  );
}
