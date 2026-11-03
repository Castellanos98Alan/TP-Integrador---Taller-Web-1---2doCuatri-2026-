package com.tallerwebi.dominio;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.repositorio.RepositorioEstadistica;
import com.tallerwebi.dominio.repositorio.RepositorioJugador;
import com.tallerwebi.dominio.repositorio.RepositorioPartido;
import com.tallerwebi.dominio.servicio.ServicioEstadistica;
import com.tallerwebi.dominio.servicio.ServicioEstadisticasImpl;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioEstadisticaTest {

  private RepositorioEstadistica repositorioEstadisticaMock;
  private RepositorioPartido repositorioPartidoMock;
  private RepositorioJugador repositorioJugadorMock;

  private ServicioEstadistica servicioEstadistica;

  @BeforeEach
  public void init() {
    repositorioEstadisticaMock = mock(RepositorioEstadistica.class);
    repositorioPartidoMock = mock(RepositorioPartido.class);
    repositorioJugadorMock = mock(RepositorioJugador.class);

    servicioEstadistica =
      new ServicioEstadisticasImpl(
        repositorioEstadisticaMock,
        repositorioPartidoMock,
        repositorioJugadorMock
      );
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

  @Test
  public void queSePuedanGuardarEstadisticasYEnviarARevision() {
    Long idPartido = 1L;
    Partido partidoMock = mock(Partido.class);
    Jugador jugadorMock = mock(Jugador.class);

    DTOEstadisticaJugador dto = new DTOEstadisticaJugador(10L, 2, 1, 0, 0); // idJugador, goles, amarillas, rojas
    List<DTOEstadisticaJugador> datosCargados = List.of(dto);

    when(repositorioPartidoMock.buscarPorId(idPartido)).thenReturn(partidoMock);
    when(repositorioJugadorMock.buscarPorId(dto.getIdJugador())).thenReturn(jugadorMock);
    when(repositorioEstadisticaMock.buscarPorPartidoYJugador(partidoMock, jugadorMock))
      .thenReturn(null);

    servicioEstadistica.guardarEstadisticasYEnviarARevision(idPartido, datosCargados);

    verify(repositorioPartidoMock, times(1)).buscarPorId(idPartido);
    verify(repositorioJugadorMock, times(1)).buscarPorId(dto.getIdJugador());

    verify(repositorioEstadisticaMock, times(1)).guardar(any(Estadistica.class));

    verify(partidoMock, times(1)).setEstadoEstadisticas(Partido.EstadoEstadisticas.EN_REVISION);
    verify(repositorioPartidoMock, times(1)).guardar(partidoMock);
  }

  @Test
  public void debePermitirRevisarEstadisticasAlCapitanDelEquipoRival() {
    // Given
    Usuario usuarioOrganizador = crearUsuario(1L, "organizador@test.com");
    Usuario usuarioCapitanRival = crearUsuario(2L, "capitanrival@test.com");

    Jugador jugadorOrganizador = crearJugador(10L, "Organizador", usuarioOrganizador);
    Jugador jugadorCapitanRival = crearJugador(20L, "Capitán Rival", usuarioCapitanRival);

    Equipo equipoLocal = crearEquipo(100L, "Equipo Local");
    Equipo equipoVisitante = crearEquipo(200L, "Equipo Visitante");

    Partido partido = new Partido();
    partido.setId(1L);
    partido.setEstadoEstadisticas(Partido.EstadoEstadisticas.EN_REVISION);
    partido.setOrganizador(usuarioOrganizador);
    partido.setEquipo1(equipoLocal);
    partido.setEquipo2(equipoVisitante);

    List<InscripcionPartido> inscripciones = new ArrayList<>();
    inscripciones.add(crearInscripcion(jugadorOrganizador, equipoLocal, true));
    inscripciones.add(crearInscripcion(jugadorCapitanRival, equipoVisitante, true));
    partido.setInscripciones(inscripciones);

    when(repositorioJugadorMock.buscarPorUsuarioId(usuarioOrganizador.getId()))
      .thenReturn(jugadorOrganizador);
    when(repositorioJugadorMock.buscarPorUsuarioId(usuarioCapitanRival.getId()))
      .thenReturn(jugadorCapitanRival);

    // When
    boolean puedeRevisar = servicioEstadistica.puedeRevisarEstadisticas(
      partido,
      usuarioCapitanRival
    );

    // Then
    assertThat(puedeRevisar, is(true));
  }

  @Test
  public void noDebePermitirRevisarEstadisticasAlOrganizadorDelPartido() {
    // Given
    Usuario usuarioOrganizador = crearUsuario(1L, "organizador@test.com");
    Jugador jugadorOrganizador = crearJugador(10L, "Organizador", usuarioOrganizador);

    Equipo equipoLocal = crearEquipo(100L, "Equipo Local");
    Equipo equipoVisitante = crearEquipo(200L, "Equipo Visitante");

    Partido partido = new Partido();
    partido.setId(1L);
    partido.setEstadoEstadisticas(Partido.EstadoEstadisticas.EN_REVISION);
    partido.setOrganizador(usuarioOrganizador);
    partido.setEquipo1(equipoLocal);
    partido.setEquipo2(equipoVisitante);

    List<InscripcionPartido> inscripciones = new ArrayList<>();
    inscripciones.add(crearInscripcion(jugadorOrganizador, equipoLocal, true));
    partido.setInscripciones(inscripciones);

    // When
    boolean puedeRevisar = servicioEstadistica.puedeRevisarEstadisticas(
      partido,
      usuarioOrganizador
    );

    // Then
    assertThat(puedeRevisar, is(false));
  }

  @Test
  public void noDebePermitirRevisarSiElPartidoNoEstaEnRevision() {
    // Given
    Usuario usuarioCapitanRival = crearUsuario(2L, "capitanrival@test.com");
    Partido partido = new Partido();
    partido.setId(1L);
    partido.setEstadoEstadisticas(null);

    // When
    boolean puedeRevisar = servicioEstadistica.puedeRevisarEstadisticas(
      partido,
      usuarioCapitanRival
    );

    // Then
    assertThat(puedeRevisar, is(false));
  }

  @Test
  public void noDebePermitirRevisarAUnJugadorQueNoEsCapitan() {
    // Given
    Usuario usuarioOrganizador = crearUsuario(1L, "organizador@test.com");
    Usuario usuarioJugadorComun = crearUsuario(3L, "jugadorcomun@test.com");

    Jugador jugadorOrganizador = crearJugador(10L, "Organizador", usuarioOrganizador);
    Jugador jugadorComun = crearJugador(30L, "Jugador Común", usuarioJugadorComun);

    Equipo equipoLocal = crearEquipo(100L, "Equipo Local");
    Equipo equipoVisitante = crearEquipo(200L, "Equipo Visitante");

    Partido partido = new Partido();
    partido.setId(1L);
    partido.setEstadoEstadisticas(Partido.EstadoEstadisticas.EN_REVISION);
    partido.setOrganizador(usuarioOrganizador);
    partido.setEquipo1(equipoLocal);
    partido.setEquipo2(equipoVisitante);

    List<InscripcionPartido> inscripciones = new ArrayList<>();
    inscripciones.add(crearInscripcion(jugadorOrganizador, equipoLocal, true));
    inscripciones.add(crearInscripcion(jugadorComun, equipoVisitante, false));
    partido.setInscripciones(inscripciones);

    when(repositorioJugadorMock.buscarPorUsuarioId(usuarioJugadorComun.getId()))
      .thenReturn(jugadorComun);

    // When
    boolean puedeRevisar = servicioEstadistica.puedeRevisarEstadisticas(
      partido,
      usuarioJugadorComun
    );

    // Then
    assertThat(puedeRevisar, is(false));
  }

  private Usuario crearUsuario(Long id, String email) {
    Usuario u = new Usuario();
    u.setId(id);
    u.setEmail(email);
    return u;
  }

  private Jugador crearJugador(Long id, String nombre, Usuario usuario) {
    Jugador j = new Jugador();
    j.setId(id);
    j.setNombre(nombre);
    j.setUsuario(usuario);
    return j;
  }

  private Equipo crearEquipo(Long id, String nombre) {
    Equipo e = new Equipo();
    e.setId(id);
    e.setNombre(nombre);
    return e;
  }

  private InscripcionPartido crearInscripcion(Jugador jugador, Equipo equipo, boolean esCapitan) {
    InscripcionPartido ins = new InscripcionPartido();
    ins.setJugador(jugador);
    ins.setEquipo(equipo);
    ins.setEsCapitan(esCapitan);
    return ins;
  }
}
