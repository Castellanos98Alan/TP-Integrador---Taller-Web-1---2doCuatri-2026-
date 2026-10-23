package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.repositorio.RepositorioEstadistica;
import com.tallerwebi.dominio.servicio.ServicioEstadistica;
import com.tallerwebi.dominio.servicio.ServicioEstadisticasImpl;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioEstadisticaTest {

  private ServicioEstadistica servicioEstadistica;
  private RepositorioEstadistica repositorioEstadisticaMock;

  @BeforeEach
  public void init() {
    this.repositorioEstadisticaMock = mock(RepositorioEstadistica.class);
    this.servicioEstadistica = new ServicioEstadisticasImpl(this.repositorioEstadisticaMock);
  }

  @Test
  public void obtenerEstadisticasPorJugador() {
    //Given

    Jugador jugador1 = givenDadoQueTengoUnjugador("Juan", "Delantero", "Arquero", "C");
    Partido partido1 = givenDadoQueTengoUnPartido(
      "C",
      false,
      LocalDateTime.of(2026, 10, 11, 17, 0)
    );
    Partido partido2 = givenDadoQueTengoUnPartido(
      "C",
      false,
      LocalDateTime.of(2026, 10, 11, 17, 0)
    );

    Estadistica estadistica1 = givenDadoQueTengoUnaEstadistica(1, 1, 0, 0, 3, jugador1, partido1);
    Estadistica estadistica2 = givenDadoQueTengoUnaEstadistica(3, 1, 0, 0, 4, jugador1, partido2);

    //When

    List<Estadistica> estadisticas = new ArrayList<>();
    estadisticas.add(estadistica1);
    estadisticas.add(estadistica2);

    when(repositorioEstadisticaMock.buscarTodasLasEstadisticasPorJugador(jugador1))
      .thenReturn(estadisticas);

    List<Estadistica> estadisticasBuscadas = servicioEstadistica.obtenerEstadisticasPorJugador(
      jugador1
    );

    // Then
    assertThat(estadisticasBuscadas.size(), equalTo(2));

    assertThat(estadisticasBuscadas.get(0).getGoles(), equalTo(1));

    assertThat(estadisticasBuscadas.get(1).getGoles(), equalTo(3));
  }

  @Test
  public void obtenerEstadisticasGenerales() {
    Jugador jugador1 = givenDadoQueTengoUnjugador("Juan", "Delantero", "Arquero", "C");
    Partido partido1 = givenDadoQueTengoUnPartido(
      "C",
      false,
      LocalDateTime.of(2026, 10, 11, 17, 0)
    );
    Partido partido2 = givenDadoQueTengoUnPartido(
      "C",
      false,
      LocalDateTime.of(2026, 10, 11, 17, 0)
    );

    Estadistica estadistica1 = givenDadoQueTengoUnaEstadistica(1, 1, 0, 0, 3, jugador1, partido1);
    Estadistica estadistica2 = givenDadoQueTengoUnaEstadistica(3, 1, 0, 0, 4, jugador1, partido2);

    List<Estadistica> estadisticas = new ArrayList<>();
    estadisticas.add(estadistica1);
    estadisticas.add(estadistica2);

    // when

    when(repositorioEstadisticaMock.buscarTodasLasEstadisticasPorJugador(jugador1))
      .thenReturn(estadisticas);

    Estadistica estadisticaGenerales = servicioEstadistica.obtenerEstadisticasGenerales(jugador1);

    //Then

    assertThat(estadisticaGenerales.getGoles(), equalTo(4));
  }

  @Test
  public void obtenerEstadisticasPorPartido() {
    Jugador jugador1 = givenDadoQueTengoUnjugador("Juan", "Delantero", "Arquero", "C");
    Jugador jugador2 = givenDadoQueTengoUnjugador("Juan", "Delantero", "Arquero", "C");
    Partido partido1 = givenDadoQueTengoUnPartido(
      "C",
      false,
      LocalDateTime.of(2026, 10, 11, 17, 0)
    );
    Partido partido2 = givenDadoQueTengoUnPartido(
      "C",
      false,
      LocalDateTime.of(2026, 10, 11, 17, 0)
    );

    Estadistica estadistica1 = givenDadoQueTengoUnaEstadistica(1, 1, 0, 0, 3, jugador1, partido1);
    Estadistica estadistica2 = givenDadoQueTengoUnaEstadistica(3, 1, 0, 1, 4, jugador2, partido1);

    List<Estadistica> estadisticasPorPartido = new ArrayList<>();
    estadisticasPorPartido.add(estadistica1);
    estadisticasPorPartido.add(estadistica2);

    // when

    when(repositorioEstadisticaMock.buscarTodasLasEstadisticasPorPartido(partido1))
      .thenReturn(estadisticasPorPartido);

    List<Estadistica> estadisticasBuscadas = servicioEstadistica.obtenerEstadisticasPorPartido(
      partido1
    );

    //then

    assertThat(estadisticasBuscadas.get(1).getRojas(), equalTo(1));
  }

  @Test
  public void registrarEstadisticaDePartido() {
    // Given
    Jugador jugador1 = givenDadoQueTengoUnjugador("Juan", "Delantero", "Arquero", "C");

    Partido partido1 = givenDadoQueTengoUnPartido(
      "C",
      false,
      LocalDateTime.of(2026, 10, 11, 17, 0)
    );

    Estadistica estadistica1 = givenDadoQueTengoUnaEstadistica(1, 1, 0, 0, 3, jugador1, partido1);

    // When
    servicioEstadistica.registrarEstadisticaDePartido(estadistica1);

    // Then
    verify(this.repositorioEstadisticaMock, times(1)).guardar(estadistica1);
  }

  private Estadistica givenDadoQueTengoUnaEstadistica(
    Integer goles,
    Integer asistencias,
    Integer amarillas,
    Integer rojas,
    Integer puntaje,
    Jugador jugador,
    Partido partido
  ) {
    return new Estadistica(goles, asistencias, amarillas, rojas, puntaje, jugador, partido);
  }

  private Partido givenDadoQueTengoUnPartido(String nivel, boolean esPrivado, LocalDateTime fecha) {
    Partido partido = new Partido(nivel, esPrivado, fecha);

    return partido;
  }

  private Jugador givenDadoQueTengoUnjugador(
    String nombre,
    String posicion1,
    String posicion2,
    String nivel
  ) {
    Jugador jugador = new Jugador(nombre, posicion1, posicion2, nivel);

    return jugador;
  }
}
