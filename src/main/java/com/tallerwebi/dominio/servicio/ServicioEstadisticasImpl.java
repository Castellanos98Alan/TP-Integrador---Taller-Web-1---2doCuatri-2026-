package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.Enumeration.CondicionJugador;
import com.tallerwebi.dominio.Enumeration.EstadoEstadisticas;
import com.tallerwebi.dominio.Enumeration.EstadoPartido;
import com.tallerwebi.dominio.excepcion.ConfirmarEstadisticasNoPermitida;
import com.tallerwebi.dominio.excepcion.EstadisticasNoGuardadas;
import com.tallerwebi.dominio.excepcion.PartidoNoEncontrado;
import com.tallerwebi.dominio.excepcion.RechazarEstadisticasNoPermitida;
import com.tallerwebi.dominio.repositorio.RepositorioEstadistica;
import com.tallerwebi.dominio.repositorio.RepositorioJugador;
import com.tallerwebi.dominio.repositorio.RepositorioPartido;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServicioEstadisticasImpl implements ServicioEstadistica {

  private RepositorioEstadistica repositorioEstadisticas;
  private RepositorioPartido repositorioPartido;
  private RepositorioJugador repositorioJugador;
  private ServicioNotificacion servicioNotificacion;
  private final ValidadorEstadisticas validadorEstadisticas = new ValidadorEstadisticas();

  @Autowired
  private HttpServletRequest request;

  public ServicioEstadisticasImpl(
    RepositorioEstadistica repositorioEstadistica,
    RepositorioPartido repositorioPartido,
    RepositorioJugador repositorioJugador,
    ServicioNotificacion servicioNotificacion
  ) {
    this.repositorioEstadisticas = repositorioEstadistica;
    this.repositorioPartido = repositorioPartido;
    this.repositorioJugador = repositorioJugador;
    this.servicioNotificacion = servicioNotificacion;
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

  @Override
  public boolean puedeCargarEstadisticas(Partido partido, Usuario usuarioLogueado) {
    if (partido == null || partido.getOrganizador() == null || usuarioLogueado == null) {
      return false;
    }

    boolean esOrganizador = partido.getOrganizador().getId().equals(usuarioLogueado.getId());

    boolean partidoActivo = partido.getEstado() == EstadoPartido.ACTIVO;

    boolean estadoEstadisticasValido =
      partido.getEstadoEstadisticas() == EstadoEstadisticas.PENDIENTE_DE_CARGA ||
      partido.getEstadoEstadisticas() == EstadoEstadisticas.RECHAZADA;

    return esOrganizador && partidoActivo && estadoEstadisticasValido;
  }

  @Transactional
  @Override
  public void guardarEstadisticasYEnviarARevision(
    Long idPartido,
    List<DTOEstadisticaJugador> datosCargados
  ) {
    validadorEstadisticas.validarLista(datosCargados);

    Partido partido = repositorioPartido.buscarPorId(idPartido);

    guardarOActualizarEstadisticas(partido, datosCargados);

    partido.setEstadoEstadisticas(EstadoEstadisticas.EN_REVISION);
    repositorioPartido.guardar(partido);

    servicioNotificacion.notificarFormularioCompletado(partido.getOrganizador(), partido);
  }

  private void guardarOActualizarEstadisticas(
    Partido partido,
    List<DTOEstadisticaJugador> datosCargados
  ) {
    for (DTOEstadisticaJugador dto : datosCargados) {
      if (dto != null && dto.getIdJugador() != null) {
        Jugador jugador = repositorioJugador.buscarPorId(dto.getIdJugador());

        if (jugador != null) {
          Estadistica estadistica = repositorioEstadisticas.buscarPorPartidoYJugador(
            partido,
            jugador
          );

          if (estadistica == null) {
            estadistica = new Estadistica();
            estadistica.setPartido(partido);
            estadistica.setJugador(jugador);
          }

          estadistica.setGoles(dto.getGoles());
          estadistica.setAmarillas(dto.getAmarillas());
          estadistica.setRojas(dto.getRojas());
          estadistica.setAsistencias(dto.getAsistencias());
          repositorioEstadisticas.guardar(estadistica);
        }
      }
    }
  }

  private Jugador buscarCapitanDelEquipo(Partido partido, Equipo equipo) {
    if (partido.getInscripciones() == null || equipo == null || equipo.getId() == null) {
      return null;
    }
    return partido
      .getInscripciones()
      .stream()
      .filter(i ->
        i.getEquipo() != null &&
        i.getEquipo().getId() != null &&
        i.getEquipo().getId().equals(equipo.getId())
      )
      .filter(i -> i.isEsCapitan())
      .map(InscripcionPartido::getJugador)
      .findFirst()
      .orElse(null);
  }

  @Override
  public boolean puedeRevisarEstadisticas(Partido partido, Usuario usuarioLogueado) {
    boolean puedeRevisar = false;

    boolean partidoValidoEnRevision =
      partido != null && partido.getEstadoEstadisticas() == EstadoEstadisticas.EN_REVISION;
    boolean usuarioValido = usuarioLogueado != null;

    if (partidoValidoEnRevision && usuarioValido) {
      Jugador jugadorLogueado = repositorioJugador.buscarPorUsuarioId(usuarioLogueado.getId());

      if (jugadorLogueado != null) {
        Jugador capitanRival = obtenerCapitanRival(partido);

        puedeRevisar =
          capitanRival != null &&
          capitanRival.getId() != null &&
          capitanRival.getId().equals(jugadorLogueado.getId());
      }
    }

    return puedeRevisar;
  }

  private Jugador obtenerCapitanRival(Partido partido) {
    Equipo equipoOrganizador = obtenerEquipoDelOrganizador(partido);
    Equipo equipoRival = obtenerEquipoRival(partido, equipoOrganizador);
    return buscarCapitanDelEquipo(partido, equipoRival);
  }

  private Equipo obtenerEquipoDelOrganizador(Partido partido) {
    if (partido.getInscripciones() == null || partido.getOrganizador() == null) {
      return null;
    }

    Jugador jugadorOrganizador = repositorioJugador.buscarPorUsuarioId(
      partido.getOrganizador().getId()
    );
    if (jugadorOrganizador == null) {
      return null;
    }

    return partido
      .getInscripciones()
      .stream()
      .filter(i ->
        i.getJugador() != null && i.getJugador().getId().equals(jugadorOrganizador.getId())
      )
      .map(InscripcionPartido::getEquipo)
      .findFirst()
      .orElse(null);
  }

  private Equipo obtenerEquipoRival(Partido partido, Equipo equipoOrganizador) {
    if (equipoOrganizador == null) {
      return null;
    }

    if (
      partido.getEquipo1() != null && partido.getEquipo1().getId().equals(equipoOrganizador.getId())
    ) {
      return partido.getEquipo2();
    } else if (
      partido.getEquipo2() != null && partido.getEquipo2().getId().equals(equipoOrganizador.getId())
    ) {
      return partido.getEquipo1();
    }

    return null;
  }

  @Override
  public Map<String, Integer> calcularMarcadorPartido(
    Partido partido,
    List<Estadistica> estadisticas
  ) {
    int golesLocal = 0;
    int golesVisitante = 0;

    if (partido.getEquipo1() != null && partido.getEquipo2() != null && estadisticas != null) {
      for (Estadistica est : estadisticas) {
        if (est.getJugador() != null && est.getGoles() != null) {
          boolean esDelLocal = partido
            .getInscripciones()
            .stream()
            .anyMatch(ins ->
              ins.getJugador().getId().equals(est.getJugador().getId()) &&
              ins.getEquipo() != null &&
              ins.getEquipo().getId().equals(partido.getEquipo1().getId())
            );

          if (esDelLocal) {
            golesLocal += est.getGoles();
          } else {
            golesVisitante += est.getGoles();
          }
        }
      }
    }

    Map<String, Integer> marcador = new HashMap<>();
    marcador.put("local", golesLocal);
    marcador.put("visitante", golesVisitante);
    return marcador;
  }

  @Override
  public Map<String, List<Estadistica>> obtenerEstadisticasSeparadasPorEquipo(
    Partido partido,
    List<Estadistica> estadisticas
  ) {
    List<Estadistica> locales = new ArrayList<>();
    List<Estadistica> visitantes = new ArrayList<>();

    if (partido.getEquipo1() != null && partido.getEquipo2() != null && estadisticas != null) {
      for (Estadistica est : estadisticas) {
        if (est.getJugador() != null) {
          boolean esDelLocal = partido
            .getInscripciones()
            .stream()
            .anyMatch(ins ->
              ins.getJugador().getId().equals(est.getJugador().getId()) &&
              ins.getEquipo() != null &&
              ins.getEquipo().getId().equals(partido.getEquipo1().getId())
            );

          if (esDelLocal) {
            locales.add(est);
          } else {
            visitantes.add(est);
          }
        }
      }
    }

    Map<String, List<Estadistica>> resultado = new HashMap<>();
    resultado.put("locales", locales);
    resultado.put("visitantes", visitantes);
    return resultado;
  }

  @Transactional
  @Override
  public void confirmarEstadisticas(Long idPartido, Usuario usuarioLogueado)
    throws ConfirmarEstadisticasNoPermitida {
    Partido partido = repositorioPartido.buscarPorId(idPartido);

    if (partido == null) {
      throw new PartidoNoEncontrado("El partido no existe.");
    }

    if (!puedeRevisarEstadisticas(partido, usuarioLogueado)) {
      throw new ConfirmarEstadisticasNoPermitida(
        "No estas autorizado para confirmar estas estadisticas."
      );
    }

    aplicarPuntajesYActualizarJugadores(partido);

    partido.setEstadoEstadisticas(EstadoEstadisticas.CONFIRMADA);
    partido.setEstado(EstadoPartido.FINALIZADO);

    repositorioPartido.guardar(partido);

    servicioNotificacion.notificarFormularioCompletado(partido.getOrganizador(), partido);
  }

  private void aplicarPuntajesYActualizarJugadores(Partido partido) {
    List<Estadistica> estadisticas = repositorioEstadisticas.buscarTodasLasEstadisticasPorPartido(
      partido
    );
    if (estadisticas == null || estadisticas.isEmpty()) {
      return;
    }

    Map<String, Integer> marcador = calcularMarcadorPartido(partido, estadisticas);
    int golesLocal = marcador.get("local");
    int golesVisitante = marcador.get("visitante");

    for (Estadistica est : estadisticas) {
      if (est.getJugador() != null) {
        procesarEstadisticaJugador(partido, est, golesLocal, golesVisitante);
      }
    }
  }

  private void procesarEstadisticaJugador(
    Partido partido,
    Estadistica est,
    int golesLocal,
    int golesVisitante
  ) {
    Jugador jugador = est.getJugador();
    boolean esLocal = esJugadorDelEquipoLocal(partido, jugador);
    est.setCondicion(esLocal ? CondicionJugador.LOCAL : CondicionJugador.VISITANTE);

    int golesEquipo = esLocal ? golesLocal : golesVisitante;
    int golesRival = esLocal ? golesVisitante : golesLocal;

    int puntosResultado = calcularPuntosYActualizarHistorial(jugador, golesEquipo, golesRival);
    int puntosRendimiento =
      (valorOZero(est.getGoles()) * 2) +
      (valorOZero(est.getAsistencias()) * 1) -
      (valorOZero(est.getAmarillas()) * 1) -
      (valorOZero(est.getRojas()) * 3);

    int puntajeTotalPartido = puntosResultado + puntosRendimiento;

    est.setPuntaje(puntajeTotalPartido);
    repositorioEstadisticas.guardar(est);

    jugador.setPartidosJugados(valorOZero(jugador.getPartidosJugados()) + 1);
    jugador.setPuntos(Math.max(0, valorOZero(jugador.getPuntos()) + puntajeTotalPartido));
    repositorioJugador.modificar(jugador);
  }

  private int calcularPuntosYActualizarHistorial(Jugador jugador, int golesEquipo, int golesRival) {
    if (golesEquipo > golesRival) {
      jugador.setPartidosGanados(valorOZero(jugador.getPartidosGanados()) + 1);
      return 4;
    }
    if (golesEquipo < golesRival) {
      jugador.setPartidosPerdidos(valorOZero(jugador.getPartidosPerdidos()) + 1);
      return -1;
    }
    return 2;
  }

  private boolean esJugadorDelEquipoLocal(Partido partido, Jugador jugador) {
    if (partido.getInscripciones() == null || partido.getEquipo1() == null || jugador == null) {
      return false;
    }

    return partido
      .getInscripciones()
      .stream()
      .anyMatch(ins ->
        ins.getJugador() != null &&
        ins.getJugador().getId().equals(jugador.getId()) &&
        ins.getEquipo() != null &&
        ins.getEquipo().getId().equals(partido.getEquipo1().getId())
      );
  }

  @Transactional
  @Override
  public void rechazarEstadisticas(Long idPartido, Usuario usuarioLogueado)
    throws RechazarEstadisticasNoPermitida {
    Partido partido = repositorioPartido.buscarPorId(idPartido);

    if (partido == null) {
      throw new PartidoNoEncontrado("El partido no existe.");
    }

    if (!puedeRevisarEstadisticas(partido, usuarioLogueado)) {
      throw new RechazarEstadisticasNoPermitida(
        "No estas autorizado para rechazar estas estadisticas."
      );
    }

    partido.setEstadoEstadisticas(EstadoEstadisticas.RECHAZADA);

    repositorioPartido.guardar(partido);

    servicioNotificacion.notificarFormularioCompletado(partido.getOrganizador(), partido);
  }
}
