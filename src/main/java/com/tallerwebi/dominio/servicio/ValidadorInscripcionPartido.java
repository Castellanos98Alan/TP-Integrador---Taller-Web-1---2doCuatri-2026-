package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.Partido;
import com.tallerwebi.dominio.Partido.EstadoPartido;
import com.tallerwebi.dominio.excepcion.CupoMaximoAlcanzado;
import com.tallerwebi.dominio.excepcion.DiferenciaHoraria;
import com.tallerwebi.dominio.excepcion.EstadoPartidoInvalido;
import com.tallerwebi.dominio.excepcion.JugadorInscripto;
import com.tallerwebi.dominio.excepcion.RangoJugadorIncompatibleException;
import com.tallerwebi.dominio.repositorio.RepositorioPartido;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Service;

/* Valida las condiciones necesarias para inscribir un jugador en un partido.*/
@Service
public class ValidadorInscripcionPartido {

  private static final int HORAS_MINIMAS_ENTRE_PARTIDOS = 3;

  private final RepositorioPartido repositorioPartido;

  public ValidadorInscripcionPartido(RepositorioPartido repositorioPartido) {
    this.repositorioPartido = repositorioPartido;
  }

  public void validarInscripcion(Partido partido, Jugador jugador) {
    validarEstadoDelPartido(partido);
    validarRangoJugador(partido, jugador);
    validarCuposYDisponibilidad(partido, jugador);
    validarHorarioParaInscripcion(partido, jugador);
  }

  private void validarEstadoDelPartido(Partido partido) {
    if (
      partido.getEstado() != EstadoPartido.PENDIENTE && partido.getEstado() != EstadoPartido.ACTIVO
    ) {
      throw new EstadoPartidoInvalido("No se puede unir a un partido finalizado o cancelado.");
    }
  }

  private void validarRangoJugador(Partido partido, Jugador jugador) {
    String categoriaJugador = obtenerCategoriaRango(jugador.getNivel());
    String categoriaPartido = obtenerCategoriaRango(partido.getNivel());

    if (categoriaJugador == null || categoriaPartido == null) {
      throw new RangoJugadorIncompatibleException(
        "El rango del jugador o del partido no es válido."
      );
    }

    if (!categoriaJugador.equals(categoriaPartido)) {
      throw new RangoJugadorIncompatibleException(
        "No podes unirte a este partido porque no corresponde a tu rango."
      );
    }
  }

  private String obtenerCategoriaRango(String nivel) {
    if (nivel == null) {
      return null;
    }

    String rango = nivel.trim().toUpperCase(Locale.ROOT);

    if (esRangoAvanzado(rango)) {
      return "AVANZADO";
    }

    if (esRangoIntermedio(rango)) {
      return "INTERMEDIO";
    }

    if (esRangoBajo(rango)) {
      return "BAJO";
    }

    return null;
  }

  private boolean esRangoAvanzado(String rango) {
    return Set.of("SS", "S", "AVANZADO").contains(rango);
  }

  private boolean esRangoIntermedio(String rango) {
    return Set.of("A", "B", "INTERMEDIO").contains(rango);
  }

  private boolean esRangoBajo(String rango) {
    return Set.of("C", "D", "BAJO", "PRINCIPIANTE").contains(rango);
  }

  private void validarCuposYDisponibilidad(Partido partido, Jugador jugador) {
    if (partido.getInscripciones() == null) {
      partido.setInscripciones(new ArrayList<>());
    }

    boolean inscripto = partido
      .getInscripciones()
      .stream()
      .anyMatch(i -> i.getJugador().getId().equals(jugador.getId()));

    if (inscripto) {
      throw new JugadorInscripto("Ya estas anotado en la sala de espera de este partido.");
    }

    if (
      partido.getCupoMaximo() != null &&
      partido.getInscripciones().size() >= partido.getCupoMaximo()
    ) {
      throw new CupoMaximoAlcanzado("Cupo maximo alcanzado.");
    }
  }

  private void validarDiferenciaHoraria(
    LocalDateTime fechaExistente,
    LocalDateTime fechaNuevoPartido
  ) {
    if (fechaExistente == null) {
      return;
    }

    long horasDeDiferencia = ChronoUnit.HOURS.between(fechaExistente, fechaNuevoPartido);

    if (Math.abs(horasDeDiferencia) < HORAS_MINIMAS_ENTRE_PARTIDOS) {
      throw new DiferenciaHoraria(
        "No podes unirte, es necesario que haya al menos 3 horas de diferencia (" +
        fechaExistente.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) +
        ")."
      );
    }
  }

  private void validarHorarioParaInscripcion(Partido partidoNuevo, Jugador jugador) {
    List<Partido> partidosUsuario = repositorioPartido.buscarPartidosPorJugador(jugador.getId());

    LocalDateTime fechaNuevoPartido = partidoNuevo.getFecha();

    if (fechaNuevoPartido == null) {
      return;
    }

    for (Partido partidoExistente : partidosUsuario) {
      validarDiferenciaHoraria(partidoExistente.getFecha(), fechaNuevoPartido);
    }
  }
}
