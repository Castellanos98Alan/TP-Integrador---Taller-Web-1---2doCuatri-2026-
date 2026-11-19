package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.repositorio.RepositorioJugador;
import com.tallerwebi.dominio.servicio.ServicioJugador;
import com.tallerwebi.dominio.servicio.ServicioJugadorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioJugadorTest {

  private RepositorioJugador repositorioJugadorMock;
  private ServicioJugador servicioJugador;

  @BeforeEach
  public void init() {
    this.repositorioJugadorMock = mock(RepositorioJugador.class);
    this.servicioJugador = new ServicioJugadorImpl(this.repositorioJugadorMock);
  }

  @Test
  public void deberiaBuscarJugadorPorUsuarioIdExitosamente() {
    Long usuarioId = 1L;
    Jugador jugadorEsperado = new Jugador();
    jugadorEsperado.setId(10L);
    jugadorEsperado.setNombre("Tomas");

    when(repositorioJugadorMock.buscarPorUsuarioId(usuarioId)).thenReturn(jugadorEsperado);

    Jugador resultado = servicioJugador.buscarPorUsuarioId(usuarioId);

    assertThat(resultado, equalTo(jugadorEsperado));
    verify(repositorioJugadorMock, times(1)).buscarPorUsuarioId(usuarioId);
  }

  @Test
  public void deberiaRetornarNullSiNoExisteJugadorParaElUsuarioId() {
    Long usuarioId = 99L;

    when(repositorioJugadorMock.buscarPorUsuarioId(usuarioId)).thenReturn(null);

    Jugador resultado = servicioJugador.buscarPorUsuarioId(usuarioId);

    assertThat(resultado, nullValue());
    verify(repositorioJugadorMock, times(1)).buscarPorUsuarioId(usuarioId);
  }

  @Test
  public void deberiaGuardarUnJugadorDelegandoAlRepositorio() {
    Jugador jugador = new Jugador();
    jugador.setNombre("Tomas");

    servicioJugador.guardar(jugador);

    verify(repositorioJugadorMock, times(1)).guardar(jugador);
  }

  @Test
  public void deberiaModificarUnJugadorDelegandoAlRepositorio() {
    Jugador jugador = new Jugador();
    jugador.setId(10L);
    jugador.setNombre("Tomas Actualizado");

    servicioJugador.modificar(jugador);

    verify(repositorioJugadorMock, times(1)).modificar(jugador);
  }
}
