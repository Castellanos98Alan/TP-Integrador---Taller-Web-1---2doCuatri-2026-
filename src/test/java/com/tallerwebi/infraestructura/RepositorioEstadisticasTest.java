package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.tallerwebi.dominio.*;
import com.tallerwebi.infraestructura.config.HibernateInfraestructuraTestConfig;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { HibernateInfraestructuraTestConfig.class })
public class RepositorioEstadisticasTest {

  @Autowired
  private SessionFactory sessionFactory;

  private RepositorioEstadistica repositorioEstadistica;
  private RepositorioPartido repositorioPartido;
  private RepositorioJugador repositorioJugador;

  @BeforeEach
  public void init() {
    repositorioEstadistica = new RepositorioEstadisticaImpl(sessionFactory);
    repositorioPartido = new RepositorioPartidoImpl(sessionFactory);
    repositorioJugador = new RepositorioJugadorImpl(sessionFactory);
  }

  @Transactional
  @Rollback
  @Test
  public void dadoQueTengo3PartidosConSusEstadisticasMeDebeRetornarTodasLasEstadisticasDeUnPartido() {
    //Given
    Jugador jugador1 = givenDadoQueTengoUnJugador("Pedro", "Delantero", "Mediocampista", "B");
    Jugador jugador2 = givenDadoQueTengoUnJugador("Ivan", "Delantero", "Mediocampista", "A");
    Jugador jugador3 = givenDadoQueTengoUnJugador("Juan", "Delantero", "Mediocampista", "B");

    Partido partido1 = givenDadoQueTengoUnPartido("B", false, LocalDateTime.of(2026, 10, 9, 23, 0));
    Partido partido2 = givenDadoQueTengoUnPartido("A", false, LocalDateTime.of(2026, 10, 9, 22, 0));
    Partido partido3 = givenDadoQueTengoUnPartido(
      "C",
      false,
      LocalDateTime.of(2026, 10, 10, 23, 0)
    );

    Estadistica estadisticaPartido1 = givenDadoQueTengoSusEstadisticas(
      1,
      1,
      0,
      0,
      1,
      jugador2,
      partido1
    );
    Estadistica estadisticaPartido2 = givenDadoQueTengoSusEstadisticas(
      3,
      0,
      1,
      0,
      5,
      jugador3,
      partido1
    );
    Estadistica estadisticaPartido3 = givenDadoQueTengoSusEstadisticas(
      1,
      1,
      0,
      0,
      3,
      jugador1,
      partido2
    );
    Estadistica estadisticaPartido4 = givenDadoQueTengoSusEstadisticas(
      1,
      1,
      0,
      0,
      3,
      jugador2,
      partido3
    );

    //When

    List<Estadistica> estadisticaBuscada =
      repositorioEstadistica.buscarTodasLasEstadisticasPorPartido(partido1);

    //That

    assertThat(estadisticaBuscada.size(), equalTo(2));
  }

  @Transactional
  @Rollback
  @Test
  public void dadoQueTengo3PartidosConSusEstadisticasMeDebeRetornarLasEstadisticasTotalesDeXJugador() {
    //Given
    Jugador jugador1 = givenDadoQueTengoUnJugador("Pedro", "Delantero", "Mediocampista", "B");
    Jugador jugador2 = givenDadoQueTengoUnJugador("Ivan", "Delantero", "Mediocampista", "A");
    Jugador jugador3 = givenDadoQueTengoUnJugador("Juan", "Delantero", "Mediocampista", "B");

    Partido partido1 = givenDadoQueTengoUnPartido("B", false, LocalDateTime.of(2026, 10, 9, 23, 0));
    Partido partido2 = givenDadoQueTengoUnPartido("A", false, LocalDateTime.of(2026, 10, 9, 22, 0));
    Partido partido3 = givenDadoQueTengoUnPartido(
      "C",
      false,
      LocalDateTime.of(2026, 10, 10, 23, 0)
    );

    Estadistica estadisticaPartido1 = givenDadoQueTengoSusEstadisticas(
      1,
      1,
      0,
      0,
      1,
      jugador2,
      partido1
    );
    Estadistica estadisticaPartido2 = givenDadoQueTengoSusEstadisticas(
      3,
      0,
      1,
      0,
      5,
      jugador3,
      partido1
    );
    Estadistica estadisticaPartido3 = givenDadoQueTengoSusEstadisticas(
      1,
      1,
      0,
      0,
      3,
      jugador1,
      partido2
    );
    Estadistica estadisticaPartido4 = givenDadoQueTengoSusEstadisticas(
      1,
      1,
      0,
      0,
      3,
      jugador2,
      partido3
    );

    //When

    List<Estadistica> estadisticas1 = repositorioEstadistica.buscarTodasLasEstadisticasPorJugador(
      jugador2
    );
    List<Estadistica> estadisticas2 = repositorioEstadistica.buscarTodasLasEstadisticasPorJugador(
      jugador3
    );
    List<Estadistica> estadisticas3 = repositorioEstadistica.buscarTodasLasEstadisticasPorJugador(
      jugador1
    );

    //That

    assertThat(estadisticas1.size(), equalTo(2));
    assertThat(estadisticas2.size(), equalTo(1));
    assertThat(estadisticas3.size(), equalTo(1));
  }

  @Transactional
  @Rollback
  @Test
  public void dadoQueTengo1PartidoQueTodaviaNoTieneEstadisticasRetornaVacio() {
    //Given

    Partido partido1 = givenDadoQueTengoUnPartido("B", false, LocalDateTime.of(2026, 10, 9, 23, 0));

    //When

    List<Estadistica> estadisticaBuscada =
      repositorioEstadistica.buscarTodasLasEstadisticasPorPartido(partido1);

    //That

    assertThat(estadisticaBuscada.size(), equalTo(0));
  }

  @Transactional
  @Rollback
  @Test
  public void dadoQueTengo1JuadorQueTodaviaNoTieneEstadisticasRetornaVacio() {
    //Given

    Jugador jugador1 = givenDadoQueTengoUnJugador("Pedro", "Delantero", "Mediocampista", "B");

    //When

    List<Estadistica> estadisticaBuscada =
      repositorioEstadistica.buscarTodasLasEstadisticasPorJugador(jugador1);

    //That

    assertThat(estadisticaBuscada.size(), equalTo(0));
  }

  @Transactional
  @Rollback
  @Test
  public void dadoQueTengoUnaEstadisticaDeUnJugadorDebeRetornarSusGolesCorrectamente() {
    //given

    Jugador jugador1 = givenDadoQueTengoUnJugador("Pedro", "Delantero", "Arquero", "C");
    Partido partido1 = givenDadoQueTengoUnPartido(
      "C",
      false,
      LocalDateTime.of(2026, 10, 11, 17, 0)
    );

    Estadistica estadistica = givenDadoQueTengoSusEstadisticas(3, 0, 0, 1, 0, jugador1, partido1);

    //when

    List<Estadistica> estadisticaBuscada =
      repositorioEstadistica.buscarTodasLasEstadisticasPorJugador(jugador1);

    //that

    assertThat(estadisticaBuscada.getFirst().getGoles(), equalTo(3));
  }

  @Transactional
  @Rollback
  @Test
  public void dadoQueTengoEstadisticasDeDistintosPartidosDebeRetornarSoloLasDelPartidoBuscado() {
    //Given
    Jugador jugador1 = givenDadoQueTengoUnJugador("Pedro", "Delantero", "Mediocampista", "B");
    Jugador jugador2 = givenDadoQueTengoUnJugador("Ivan", "Delantero", "Mediocampista", "A");
    Jugador jugador3 = givenDadoQueTengoUnJugador("Juan", "Delantero", "Mediocampista", "B");

    Partido partido1 = givenDadoQueTengoUnPartido("B", false, LocalDateTime.of(2026, 10, 9, 23, 0));
    Partido partido2 = givenDadoQueTengoUnPartido("A", false, LocalDateTime.of(2026, 10, 9, 22, 0));
    Partido partido3 = givenDadoQueTengoUnPartido(
      "C",
      false,
      LocalDateTime.of(2026, 10, 10, 23, 0)
    );

    Estadistica estadisticaPartido1 = givenDadoQueTengoSusEstadisticas(
      1,
      1,
      0,
      0,
      1,
      jugador2,
      partido1
    );
    Estadistica estadisticaPartido2 = givenDadoQueTengoSusEstadisticas(
      3,
      0,
      1,
      0,
      5,
      jugador3,
      partido1
    );
    Estadistica estadisticaPartido3 = givenDadoQueTengoSusEstadisticas(
      1,
      1,
      0,
      0,
      3,
      jugador1,
      partido2
    );
    Estadistica estadisticaPartido4 = givenDadoQueTengoSusEstadisticas(
      1,
      1,
      0,
      0,
      3,
      jugador2,
      partido3
    );

    // when

    List<Estadistica> estadisticaBuscada =
      repositorioEstadistica.buscarTodasLasEstadisticasPorPartido(partido2);

    // THAT

    assertThat(estadisticaBuscada.getFirst().getJugador(), equalTo(jugador1));
  }

  @Transactional
  @Rollback
  @Test
  public void dadoQueTengoJugadoresDeDistintosNivelesDebeRetornarSoloLosDelNivelBuscado() {
    // Given

    Jugador jugador1 = givenDadoQueTengoUnJugador("Pedro", "Delantero", "Mediocampista", "B");
    Jugador jugador2 = givenDadoQueTengoUnJugador("Ivan", "Delantero", "Mediocampista", "A");
    Jugador jugador3 = givenDadoQueTengoUnJugador("Juan", "Delantero", "Mediocampista", "B");

    Partido partido1 = givenDadoQueTengoUnPartido("B", false, LocalDateTime.of(2026, 10, 9, 23, 0));
    Partido partido2 = givenDadoQueTengoUnPartido("A", false, LocalDateTime.of(2026, 10, 9, 22, 0));
    Partido partido3 = givenDadoQueTengoUnPartido(
      "C",
      false,
      LocalDateTime.of(2026, 10, 10, 23, 0)
    );

    Estadistica estadisticaPartido1 = givenDadoQueTengoSusEstadisticas(
      1,
      1,
      0,
      0,
      1,
      jugador2,
      partido1
    );
    Estadistica estadisticaPartido2 = givenDadoQueTengoSusEstadisticas(
      3,
      0,
      1,
      0,
      5,
      jugador3,
      partido1
    );
    Estadistica estadisticaPartido3 = givenDadoQueTengoSusEstadisticas(
      1,
      1,
      0,
      0,
      3,
      jugador1,
      partido2
    );
    Estadistica estadisticaPartido4 = givenDadoQueTengoSusEstadisticas(
      1,
      1,
      0,
      0,
      3,
      jugador2,
      partido3
    );

    // when

    List<Jugador> jugadoresFiltrados = repositorioJugador.buscarRankingPorNivel("B");

    // then

    assertThat(jugadoresFiltrados.size(), equalTo(2));
  }

  @Transactional
  @Rollback
  @Test
  public void dadoQueTengoJugadoresDeDistintosPosicionesDebeRetornarSoloLosDeLaProsicion1() {
    Jugador jugador1 = givenDadoQueTengoUnJugador("Pedro", "Delantero", "Mediocampista", "B");
    Jugador jugador2 = givenDadoQueTengoUnJugador("Ivan", "Defensor", "Mediocampista", "A");
    Jugador jugador3 = givenDadoQueTengoUnJugador("Juan", "Arquero", "Mediocampista", "B");

    Partido partido1 = givenDadoQueTengoUnPartido("B", false, LocalDateTime.of(2026, 10, 9, 23, 0));
    Partido partido2 = givenDadoQueTengoUnPartido("A", false, LocalDateTime.of(2026, 10, 9, 22, 0));
    Partido partido3 = givenDadoQueTengoUnPartido(
      "C",
      false,
      LocalDateTime.of(2026, 10, 10, 23, 0)
    );

    Estadistica estadisticaPartido1 = givenDadoQueTengoSusEstadisticas(
      1,
      1,
      0,
      0,
      1,
      jugador2,
      partido1
    );
    Estadistica estadisticaPartido2 = givenDadoQueTengoSusEstadisticas(
      3,
      0,
      1,
      0,
      5,
      jugador3,
      partido1
    );
    Estadistica estadisticaPartido3 = givenDadoQueTengoSusEstadisticas(
      1,
      1,
      0,
      0,
      3,
      jugador1,
      partido2
    );
    Estadistica estadisticaPartido4 = givenDadoQueTengoSusEstadisticas(
      1,
      1,
      0,
      0,
      3,
      jugador2,
      partido3
    );

    //when

    List<Jugador> jugadoresFiltrados = repositorioJugador.buscarRankingPorPosicion("Arquero");

    // then

    assertThat(jugadoresFiltrados.size(), equalTo(1));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaGuardarUnaEstadisticaDeJugadorLocal() {
    // Given
    Jugador jugador = givenDadoQueTengoUnJugador("Pedro", "Delantero", "Mediocampista", "B");

    Partido partido = givenDadoQueTengoUnPartido("B", false, LocalDateTime.of(2026, 10, 20, 20, 0));

    Estadistica estadistica = givenDadoQueTengoSusEstadisticas(2, 1, 0, 0, 8, jugador, partido);

    estadistica.setCondicion(Estadistica.CondicionJugador.LOCAL);

    // When

    List<Estadistica> estadisticasEncontradas =
      repositorioEstadistica.buscarTodasLasEstadisticasPorJugador(jugador);

    // Then
    assertThat(estadisticasEncontradas.size(), equalTo(1));

    assertThat(
      estadisticasEncontradas.getFirst().getCondicion(),
      equalTo(Estadistica.CondicionJugador.LOCAL)
    );
  }

  private Estadistica givenDadoQueTengoSusEstadisticas(
    Integer goles,
    Integer asistencias,
    Integer amarillas,
    Integer rojas,
    Integer puntaje,
    Jugador jugador,
    Partido partido
  ) {
    Estadistica estadisticaDelPartido = new Estadistica(
      goles,
      asistencias,
      amarillas,
      rojas,
      puntaje,
      jugador,
      partido
    );

    repositorioEstadistica.guardar(estadisticaDelPartido);

    return estadisticaDelPartido;
  }

  private Jugador givenDadoQueTengoUnJugador(
    String nombre,
    String posicion1,
    String posicion2,
    String nivel
  ) {
    Jugador jugador = new Jugador(nombre, posicion1, posicion2, nivel);
    repositorioJugador.guardar(jugador);

    return jugador;
  }

  private Partido givenDadoQueTengoUnPartido(
    String nivel,
    boolean esPrivado,
    LocalDateTime fechaDelPartido
  ) {
    Partido partido1 = new Partido(nivel, esPrivado, fechaDelPartido);
    repositorioPartido.guardar(partido1);

    return partido1;
  }
}
