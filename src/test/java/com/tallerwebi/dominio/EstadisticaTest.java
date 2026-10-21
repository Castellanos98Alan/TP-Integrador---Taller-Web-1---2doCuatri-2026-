package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

public class EstadisticaTest {

  @Test
  public void queSePuedaCrearEstadisticaConConstructorParametrizado() {
    Jugador jugador = new Jugador();
    Partido partido = new Partido();

    Estadistica estadistica = new Estadistica(2, 1, 0, 0, 8, jugador, partido);

    assertThat(estadistica.getGoles(), equalTo(2));
    assertThat(estadistica.getAsistencias(), equalTo(1));
    assertThat(estadistica.getAmarillas(), equalTo(0));
    assertThat(estadistica.getRojas(), equalTo(0));
    assertThat(estadistica.getPuntaje(), equalTo(8));
    assertThat(estadistica.getJugador(), equalTo(jugador));
    assertThat(estadistica.getPartido(), equalTo(partido));
  }

  @Test
  public void queSePuedanAsignarYObtenerValoresConSettersYGetters() {
    Estadistica estadistica = new Estadistica();
    Jugador jugador = new Jugador();
    Partido partido = new Partido();

    estadistica.setId(1);
    estadistica.setGoles(3);
    estadistica.setAsistencias(2);
    estadistica.setAmarillas(1);
    estadistica.setRojas(1);
    estadistica.setPuntaje(10);
    estadistica.setJugador(jugador);
    estadistica.setPartido(partido);

    assertThat(estadistica.getId(), equalTo(1));
    assertThat(estadistica.getGoles(), equalTo(3));
    assertThat(estadistica.getAsistencias(), equalTo(2));
    assertThat(estadistica.getAmarillas(), equalTo(1));
    assertThat(estadistica.getRojas(), equalTo(1));
    assertThat(estadistica.getPuntaje(), equalTo(10));
    assertThat(estadistica.getJugador(), equalTo(jugador));
    assertThat(estadistica.getPartido(), equalTo(partido));
  }
}
