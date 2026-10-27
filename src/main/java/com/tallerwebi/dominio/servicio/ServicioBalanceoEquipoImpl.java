package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.Equipo;
import com.tallerwebi.dominio.InscripcionPartido;
import com.tallerwebi.dominio.Partido;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class ServicioBalanceoEquipoImpl implements ServicioBalanceoEquipo {

  @Override
  public void balancearEquipos(Partido partido) {
    List<InscripcionPartido> inscripciones = partido.getInscripciones();

    inscripciones.sort((i1, i2) ->
      Integer.compare(i2.getJugador().getPuntos(), i1.getJugador().getPuntos())
    );

    Equipo equipoLocal = partido.getEquipo1();
    Equipo equipoVisitante = partido.getEquipo2();

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
}
