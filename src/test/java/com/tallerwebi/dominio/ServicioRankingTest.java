package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioRankingTest {

  private ServicioRanking servicioRanking;
  private RepositorioJugador repositorioJugadorMock;

  @BeforeEach
  public void init() {
    this.repositorioJugadorMock = mock(RepositorioJugador.class);
    this.servicioRanking = new ServicioRankingImp(this.repositorioJugadorMock);
  }

  @Test
  public void obtenerRankingDeberiaOrdenarJugadoresPorPuntosDeMayorAMenor() {
    // preparacion
    Jugador juan = new Jugador();
    juan.setNombre("Juan");
    juan.setPuntos(1000);

    Jugador pedro = new Jugador();
    pedro.setNombre("Pedro");
    pedro.setPuntos(1500);

    Jugador lucas = new Jugador();
    lucas.setNombre("Lucas");
    lucas.setPuntos(800);

    List<Jugador> rankingEsperado = new ArrayList<>();
    rankingEsperado.add(pedro);
    rankingEsperado.add(juan);

    when(repositorioJugadorMock.buscarRanking()).thenReturn(rankingEsperado);

    List<Jugador> ranking = servicioRanking.obtenerRanking();

    assertThat(ranking.get(0).getNombre(), equalTo("Pedro"));
    assertThat(ranking.get(1).getNombre(), equalTo("Juan"));
  }

  @Test
  public void obtenerRankingSinJugadoresDeberiaDevolverUnaListaVacia() {
    // preparacion
    List<Jugador> jugadores = new ArrayList<>();

    when(this.repositorioJugadorMock.buscarTodos()).thenReturn(jugadores);

    // ejecucion
    List<Jugador> ranking = this.servicioRanking.obtenerRanking();

    // validacion
    assertThat(ranking.size(), equalTo(0));

    verify(this.repositorioJugadorMock, times(1)).buscarRanking();
  }

  @Test
  public void dadoQueUnJugadorTiene55PuntosDebeTenerNivelA() {
    Jugador jugador = new Jugador();
    jugador.setPuntos(75);

    String nivel = servicioRanking.calcularNivel(75);

    assertThat(nivel, equalTo("A"));
  }

  @Test
  public void alSumarPuntosDebeActualizarElNivelDelJugador() {
    Jugador jugador = new Jugador();
    jugador.setPuntos(55);

    jugador.setPuntos(90);
    String nivel = servicioRanking.calcularNivel(jugador.getPuntos());

    assertThat(nivel, equalTo("S"));
  }
}
