package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.RepositorioPartido;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioPartidoTest {

  private ServicioPartido servicioPartido;
  private RepositorioPartido repositorioPartidoMock;

  @BeforeEach
  public void init() {
    repositorioPartidoMock = mock(RepositorioPartido.class);
    servicioPartido = new ServicioPartidoImpl(repositorioPartidoMock);
  }

  @Test
  public void queUnOrganizadorPuedaCancelarSuPartidoActivoExitosamente() {
    Usuario organizador = new Usuario();
    organizador.setId(1L);

    Partido partido = new Partido();
    partido.setId(10L);
    partido.setOrganizador(organizador);
    partido.setEstado(Partido.EstadoPartido.ACTIVO);

    when(repositorioPartidoMock.buscarPorId(10L)).thenReturn(partido);

    servicioPartido.cancelarPartido(10L, organizador);

    assertEquals(Partido.EstadoPartido.CANCELADO, partido.getEstado());
    verify(repositorioPartidoMock, times(1)).modificar(partido);
  }

  @Test
  public void queUnUsuarioQueNoEsElOrganizadorNoPuedaCancelarElPartido() {
    Usuario organizador = new Usuario();
    organizador.setId(1L);

    Usuario intruso = new Usuario();
    intruso.setId(2L);

    Partido partido = new Partido();
    partido.setId(10L);
    partido.setOrganizador(organizador);
    partido.setEstado(Partido.EstadoPartido.ACTIVO);

    when(repositorioPartidoMock.buscarPorId(10L)).thenReturn(partido);

    Exception exception = assertThrows(
      RuntimeException.class,
      () -> {
        servicioPartido.cancelarPartido(10L, intruso);
      }
    );

    assertEquals("No estás autorizado para cancelar este partido.", exception.getMessage());
    verify(repositorioPartidoMock, never()).modificar(any());
  }

  @Test
  public void queNoSePuedaCancelarUnPartidoQueYaFinalizo() {
    Usuario organizador = new Usuario();
    organizador.setId(1L);

    Partido partido = new Partido();
    partido.setId(10L);
    partido.setOrganizador(organizador);
    partido.setEstado(Partido.EstadoPartido.FINALIZADO);

    when(repositorioPartidoMock.buscarPorId(10L)).thenReturn(partido);

    Exception exception = assertThrows(
      RuntimeException.class,
      () -> {
        servicioPartido.cancelarPartido(10L, organizador);
      }
    );

    assertEquals("No se puede cancelar un partido que ya finalizó.", exception.getMessage());
    verify(repositorioPartidoMock, never()).modificar(any());
  }

  @Test
  public void deberiaFiltrarPartidosPorNivelPrincipiante() {
    // Ejecución
    List<Partido> resultado = servicioPartido.listarPartidosSegunFiltro(
      "PRINCIPIANTE",
      "TODOS",
      "TODOS",
      "TODOS",
      "TODOS",
      "TODOS"
    );
    // Validación

    assertThat(resultado.size(), greaterThan(0));
    Partido partido = resultado.get(0);
    assertThat(partido.getNivel(), equalTo("PRINCIPIANTE"));
  }

  @Test
  public void deberiaFiltrarPartidosPrivados() {
    // Ejecución
    List<Partido> resultado = servicioPartido.listarPartidosSegunFiltro(
      "TODOS",
      "PRIVADO",
      "TODOS",
      "TODOS",
      "TODOS",
      "TODOS"
    );
    // Validación
    assertThat("La lista no debería estar vacía", resultado.size(), greaterThan(0));
    Partido partido = resultado.get(0);
    assertThat(partido.getEsPrivado(), equalTo(true));
  }

  @Test
  public void deberiaFiltrarPartidosPublicos() {
    // Ejecución
    List<Partido> resultado = servicioPartido.listarPartidosSegunFiltro(
      "TODOS",
      "PUBLICO",
      "TODOS",
      "TODOS",
      "TODOS",
      "TODOS"
    );
    // Validación
    assertThat("La lista no debería estar vacía", resultado.size(), greaterThan(0));
    Partido partido = resultado.get(0);
    assertThat(partido.getEsPrivado(), equalTo(false));
  }

  @Test
  public void deberiaFiltrarPartidosPorCupo22() {
    // Ejecución
    List<Partido> resultado = servicioPartido.listarPartidosSegunFiltro(
      "TODOS",
      "TODOS",
      "22",
      "TODOS",
      "TODOS",
      "TODOS"
    );
    // Validación
    assertThat("La lista no debería estar vacía", resultado.size(), greaterThan(0));
    Partido partido = resultado.get(0);
    assertThat(partido.getCupoMaximo(), equalTo(22));
  }

  @Test
  public void deberiaFiltrarPartidosPorFecha() {
    // Ejecución
    List<Partido> resultado = servicioPartido.listarPartidosSegunFiltro(
      "TODOS",
      "TODOS",
      "TODOS",
      "2026-10-02",
      "TODOS",
      "TODOS"
    );
    // Validación
    assertThat("La lista no debería estar vacía", resultado.size(), greaterThan(0));
    Partido partido = resultado.get(0);
    assertThat(partido.getFecha().toLocalDate().toString(), equalTo("2026-10-02"));
  }

  @Test
  public void deberiaFiltrarPartidosPorHora() {
    // Ejecución
    List<Partido> resultado = servicioPartido.listarPartidosSegunFiltro(
      "TODOS",
      "TODOS",
      "TODOS",
      "TODOS",
      "14:30",
      "TODOS"
    );
    // Validación
    assertThat("La lista no debería estar vacía", resultado.size(), greaterThan(0));
    Partido partido = resultado.get(0);
    assertThat(partido.getFecha().toLocalTime().toString(), equalTo("14:30"));
  }

  @Test
  public void deberiaFiltrarPartidosPorDistanciaKm() {
    // Ejecución
    List<Partido> resultado = servicioPartido.listarPartidosSegunFiltro(
      "TODOS",
      "TODOS",
      "TODOS",
      "TODOS",
      "TODOS",
      "5"
    );
    // Validación
    assertThat("La lista no debería estar vacía", resultado.size(), greaterThan(0));
    Partido partido = resultado.get(0);
    assertThat(partido.getDistanciaKm().toString(), equalTo("5"));
  }
}
