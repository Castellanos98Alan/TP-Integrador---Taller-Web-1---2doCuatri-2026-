package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.Enumeration.PosicionJugador;
import com.tallerwebi.dominio.Equipo;
import com.tallerwebi.dominio.InscripcionPartido;
import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.Partido;
import com.tallerwebi.dominio.repositorio.RepositorioEquipo;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServicioBalanceoEquipoImpl implements ServicioBalanceoEquipo {

  @Autowired
  private RepositorioEquipo repositorioEquipo;

  @Override
  public void balancearEquipos(Partido partido) {
    List<InscripcionPartido> inscripciones = partido.getInscripciones();

    // Ordenamos por nivel (puntos) de mayor a menor
    inscripciones.sort((i1, i2) ->
      Integer.compare(i2.getJugador().getPuntos(), i1.getJugador().getPuntos())
    );

    Equipo equipoLocal = obtenerOCrearEquipoLocal(partido);
    Equipo equipoVisitante = obtenerOCrearEquipoVisitante(partido);

    int cantLocal = 0;
    int cantVisitante = 0;
    int ptsLocal = 0;
    int ptsVisitante = 0;
    Map<PosicionJugador, Integer> posLocal = new EnumMap<>(PosicionJugador.class);
    Map<PosicionJugador, Integer> posVisitante = new EnumMap<>(PosicionJugador.class);

    for (InscripcionPartido inscripcion : inscripciones) {
      Jugador jugador = inscripcion.getJugador();

      boolean vaAlLocal = evaluarAsignacionLocal(
        jugador,
        cantLocal,
        cantVisitante,
        ptsLocal,
        ptsVisitante,
        posLocal,
        posVisitante
      );

      if (vaAlLocal) {
        inscripcion.setEquipo(equipoLocal);
        cantLocal++;
        ptsLocal += jugador.getPuntos();
        registrarPosicion(posLocal, jugador);
      } else {
        inscripcion.setEquipo(equipoVisitante);
        cantVisitante++;
        ptsVisitante += jugador.getPuntos();
        registrarPosicion(posVisitante, jugador);
      }
    }
  }

  private void registrarPosicion(Map<PosicionJugador, Integer> conteo, Jugador jugador) {
    PosicionJugador p1 = jugador.getPosicion1();
    if (p1 != null) {
      conteo.put(p1, conteo.getOrDefault(p1, 0) + 1);
    }

    PosicionJugador p2 = jugador.getPosicion2();
    if (p2 != null) {
      conteo.put(p2, conteo.getOrDefault(p2, 0) + 1);
    }
  }

  private boolean evaluarAsignacionLocal(
    Jugador jugador,
    int cantLocal,
    int cantVis,
    int ptsLocal,
    int ptsVis,
    Map<PosicionJugador, Integer> posLocal,
    Map<PosicionJugador, Integer> posVis
  ) {
    // Regla 0: cupos parejos
    if (cantLocal < cantVis) {
      return true;
    }
    if (cantVis < cantLocal) {
      return false;
    }

    // Regla 1: Balancear  1° Posición
    PosicionJugador p1 = jugador.getPosicion1();
    if (p1 != null) {
      int count1Local = posLocal.getOrDefault(p1, 0);
      int count1Vis = posVis.getOrDefault(p1, 0);
      if (count1Local < count1Vis) {
        return true;
      }
      if (count1Vis < count1Local) {
        return false;
      }
    }

    // Regla 2: Balancear 2° Posición
    PosicionJugador p2 = jugador.getPosicion2();
    if (p2 != null) {
      int count2Local = posLocal.getOrDefault(p2, 0);
      int count2Vis = posVis.getOrDefault(p2, 0);
      if (count2Local < count2Vis) {
        return true;
      }
      if (count2Vis < count2Local) {
        return false;
      }
    }

    //Regla 3: si hay empate se asiga al jugador aquel equipo con menor puntos. 
    return ptsLocal <= ptsVis;
  }

  @Override
  public void asignarCapitanes(Partido partido) {
    List<InscripcionPartido> inscripciones = partido.getInscripciones();

    for (InscripcionPartido inscripcion : inscripciones) {
      inscripcion.setEsCapitan(false);
    }

    InscripcionPartido capitanEquipo1 = elegirCapitan(inscripciones, partido.getEquipo1());
    InscripcionPartido capitanEquipo2 = elegirCapitan(inscripciones, partido.getEquipo2());

    if (capitanEquipo1 != null) {
      capitanEquipo1.setEsCapitan(true);
    }

    if (capitanEquipo2 != null) {
      capitanEquipo2.setEsCapitan(true);
    }
  }

  private InscripcionPartido elegirCapitan(List<InscripcionPartido> inscripciones, Equipo equipo) {
    InscripcionPartido mejorCandidato = null;

    for (InscripcionPartido inscripcion : inscripciones) {
      if (
        Objects.equals(inscripcion.getEquipo(), equipo) &&
        esMejorCandidato(inscripcion, mejorCandidato)
      ) {
        mejorCandidato = inscripcion;
      }
    }

    return mejorCandidato;
  }

  private boolean esMejorCandidato(InscripcionPartido candidato, InscripcionPartido actual) {
    if (actual == null) {
      return true;
    }

    int puntosCandidato = candidato.getJugador().getPuntos();
    int puntosActual = actual.getJugador().getPuntos();

    if (puntosCandidato != puntosActual) {
      return puntosCandidato > puntosActual;
    }

    LocalDateTime fechaCandidato = candidato.getFechaInscripcion();
    LocalDateTime fechaActual = actual.getFechaInscripcion();

    if (fechaCandidato == null) {
      return false;
    }

    return fechaActual == null || fechaCandidato.isBefore(fechaActual);
  }

  private Equipo obtenerOCrearEquipoLocal(Partido partido) {
    if (partido.getEquipo1() == null) {
      Equipo nuevoEquipo = new Equipo();
      nuevoEquipo.setNombre("Equipo Local");
      repositorioEquipo.guardar(nuevoEquipo);
      partido.setEquipo1(nuevoEquipo);
    }
    return partido.getEquipo1();
  }

  private Equipo obtenerOCrearEquipoVisitante(Partido partido) {
    if (partido.getEquipo2() == null) {
      Equipo nuevoEquipo = new Equipo();
      nuevoEquipo.setNombre("Equipo Visitante");
      repositorioEquipo.guardar(nuevoEquipo);
      partido.setEquipo2(nuevoEquipo);
    }
    return partido.getEquipo2();
  }
}
