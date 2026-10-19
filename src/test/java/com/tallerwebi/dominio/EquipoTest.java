package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class EquipoTest {

  @Test
  public void queSePuedaCrearEquipoConConstructorParametrizado() {
    Equipo equipo = new Equipo(1L, "Los Guerreros");

    assertThat(equipo.getId(), equalTo(1L));
    assertThat(equipo.getNombre(), equalTo("Los Guerreros"));
    assertThat(equipo.getJugadores().size(), equalTo(0)); // La lista se inicializa vacía en la clase
  }

  @Test
  public void queSePuedanAsignarYObtenerValoresConSettersYGetters() {
    Equipo equipo = new Equipo();
    List<Jugador> jugadores = new ArrayList<>();
    jugadores.add(new Jugador());

    equipo.setId(2L);
    equipo.setNombre("Los Titanes");
    equipo.setJugadores(jugadores);

    assertThat(equipo.getId(), equalTo(2L));
    assertThat(equipo.getNombre(), equalTo("Los Titanes"));
    assertThat(equipo.getJugadores().size(), equalTo(1));
    assertThat(equipo.getJugadores(), equalTo(jugadores));
  }
}
