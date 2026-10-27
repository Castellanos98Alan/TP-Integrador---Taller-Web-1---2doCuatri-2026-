package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.Equipo;
import com.tallerwebi.dominio.InscripcionPartido;
import com.tallerwebi.dominio.Partido;
import com.tallerwebi.dominio.repositorio.RepositorioEquipo;
import java.time.LocalDateTime;
import java.util.List;
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

    inscripciones.sort((i1, i2) ->
      Integer.compare(i2.getJugador().getPuntos(), i1.getJugador().getPuntos())
    );

    Equipo equipoLocal = obtenerOCrearEquipoLocal(partido);
    Equipo equipoVisitante = obtenerOCrearEquipoVisitante(partido);

    for (int i = 0; i < inscripciones.size(); i++) {
      InscripcionPartido inscripcion = inscripciones.get(i);

      if (i % 4 == 0 || i % 4 == 3) {
        inscripcion.setEquipo(equipoLocal);
      } else {
        inscripcion.setEquipo(equipoVisitante);
      }
    }
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
