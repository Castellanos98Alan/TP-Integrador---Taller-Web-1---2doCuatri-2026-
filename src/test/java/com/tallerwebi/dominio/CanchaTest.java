package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

public class CanchaTest {

  @Test
  public void queSePuedaCrearCanchaConConstructorParametrizado() {
    // Ejecución
    Cancha cancha = new Cancha(1L, "Los Leones");

    // Validación
    assertThat(cancha.getId(), equalTo(1L));
    assertThat(cancha.getNombre(), equalTo("Los Leones"));
  }

  @Test
  public void queSePuedanAsignarYObtenerValoresConSettersYGetters() {
    // Preparación
    Cancha cancha = new Cancha();

    // Ejecución
    cancha.setId(2L);
    cancha.setNombre("El Monumentalito");

    // Validación
    assertThat(cancha.getId(), equalTo(2L));
    assertThat(cancha.getNombre(), equalTo("El Monumentalito"));
  }
}
