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
  private RepositorioUsuario repositorioUsuarioMock;

  @BeforeEach
  public void init() {
    this.repositorioUsuarioMock = mock(RepositorioUsuario.class);
    this.servicioRanking = new ServicioRankingImp(this.repositorioUsuarioMock);
  }

  @Test
  public void obtenerRankingDeberiaOrdenarJugadoresPorPuntosDeMayorAMenor() {
    // preparacion
    Usuario juan = new Usuario();
    juan.setNombre("Juan");
    juan.setPuntos(1000);

    Usuario pedro = new Usuario();
    pedro.setNombre("Pedro");
    pedro.setPuntos(1500);

    Usuario lucas = new Usuario();
    lucas.setNombre("Lucas");
    lucas.setPuntos(800);

    List<Usuario> jugadores = new ArrayList<>();
    jugadores.add(juan);
    jugadores.add(pedro);
    jugadores.add(lucas);

    when(this.repositorioUsuarioMock.buscarTodos()).thenReturn(jugadores);

    // ejecucion
    List<Usuario> ranking = this.servicioRanking.obtenerRanking();

    // validacion
    assertThat(ranking.get(0).getNombre(), equalTo("Pedro"));
    assertThat(ranking.get(0).getPuntos(), equalTo(1500));

    assertThat(ranking.get(1).getNombre(), equalTo("Juan"));
    assertThat(ranking.get(1).getPuntos(), equalTo(1000));

    assertThat(ranking.get(2).getNombre(), equalTo("Lucas"));
    assertThat(ranking.get(2).getPuntos(), equalTo(800));

    verify(this.repositorioUsuarioMock, times(1)).buscarTodos();
  }

  @Test
  public void obtenerRankingSinJugadoresDeberiaDevolverUnaListaVacia() {
    // preparacion
    List<Usuario> jugadores = new ArrayList<>();

    when(this.repositorioUsuarioMock.buscarTodos()).thenReturn(jugadores);

    // ejecucion
    List<Usuario> ranking = this.servicioRanking.obtenerRanking();

    // validacion
    assertThat(ranking.size(), equalTo(0));

    verify(this.repositorioUsuarioMock, times(1)).buscarTodos();
  }
}
