package com.tallerwebi.dominio;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Enumeration.CondicionJugador;
import com.tallerwebi.dominio.Enumeration.EstadoEstadisticas;
import com.tallerwebi.dominio.Enumeration.EstadoPartido;
import com.tallerwebi.dominio.Enumeration.PosicionJugador;
import com.tallerwebi.dominio.excepcion.ConfirmarEstadisticasNoPermitida;
import com.tallerwebi.dominio.excepcion.PartidoNoEncontrado;
import com.tallerwebi.dominio.excepcion.RechazarEstadisticasNoPermitida;
import com.tallerwebi.dominio.repositorio.RepositorioEstadistica;
import com.tallerwebi.dominio.repositorio.RepositorioJugador;
import com.tallerwebi.dominio.repositorio.RepositorioPartido;
import com.tallerwebi.dominio.servicio.ServicioEstadistica;
import com.tallerwebi.dominio.servicio.ServicioEstadisticasImpl;
import com.tallerwebi.dominio.servicio.ServicioNotificacion;
import com.tallerwebi.dominio.servicio.ServicioRanking;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioEstadisticaTest {

  private RepositorioEstadistica repositorioEstadisticaMock;
  private RepositorioPartido repositorioPartidoMock;
  private RepositorioJugador repositorioJugadorMock;
  private ServicioNotificacion servicioNotificacionMock;
  private ServicioRanking servicioRankingMock;

  private ServicioEstadistica servicioEstadistica;

  @BeforeEach
  public void init() {
    this.repositorioEstadisticaMock = mock(RepositorioEstadistica.class);
    this.repositorioPartidoMock = mock(RepositorioPartido.class);
    this.repositorioJugadorMock = mock(RepositorioJugador.class);
    this.repositorioJugadorMock = mock(RepositorioJugador.class);
    this.servicioNotificacionMock = mock(ServicioNotificacion.class);
    this.servicioRankingMock = mock(ServicioRanking.class);

    servicioEstadistica =
      new ServicioEstadisticasImpl(
        this.repositorioEstadisticaMock,
        this.repositorioPartidoMock,
        this.repositorioJugadorMock,
        this.servicioNotificacionMock,
        this.servicioRankingMock
      );
  }

  @Test
  public void obtenerEstadisticasPorJugador() {
    //Given

    Jugador jugador1 = givenDadoQueTengoUnjugador(
      "Juan",
      PosicionJugador.DELANTERO,
      PosicionJugador.ARQUERO,
      "C"
    );
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
    Jugador jugador1 = givenDadoQueTengoUnjugador(
      "Juan",
      PosicionJugador.DELANTERO,
      PosicionJugador.ARQUERO,
      "C"
    );
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
    Jugador jugador1 = givenDadoQueTengoUnjugador(
      "Juan",
      PosicionJugador.DELANTERO,
      PosicionJugador.ARQUERO,
      "C"
    );
    Jugador jugador2 = givenDadoQueTengoUnjugador(
      "Juan",
      PosicionJugador.DELANTERO,
      PosicionJugador.ARQUERO,
      "C"
    );
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
    Jugador jugador1 = givenDadoQueTengoUnjugador(
      "Juan",
      PosicionJugador.DELANTERO,
      PosicionJugador.ARQUERO,
      "C"
    );

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
    PosicionJugador posicion1,
    PosicionJugador posicion2,
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
    Usuario organizadorMock = mock(Usuario.class);

    DTOEstadisticaJugador dto = new DTOEstadisticaJugador(10L, 2, 1, 0, 0); // idJugador, goles, amarillas, rojas
    List<DTOEstadisticaJugador> datosCargados = List.of(dto);

    when(repositorioPartidoMock.buscarPorId(idPartido)).thenReturn(partidoMock);
    when(repositorioJugadorMock.buscarPorId(dto.getIdJugador())).thenReturn(jugadorMock);
    when(repositorioEstadisticaMock.buscarPorPartidoYJugador(partidoMock, jugadorMock))
      .thenReturn(null); // Simulamos que no existe todavía (creación)
    when(partidoMock.getOrganizador()).thenReturn(organizadorMock);

    servicioEstadistica.guardarEstadisticasYEnviarARevision(idPartido, datosCargados);

    verify(repositorioPartidoMock, times(1)).buscarPorId(idPartido);
    verify(repositorioJugadorMock, times(1)).buscarPorId(dto.getIdJugador());

    verify(repositorioEstadisticaMock, times(1)).guardar(any(Estadistica.class));

    verify(partidoMock, times(1)).setEstadoEstadisticas(EstadoEstadisticas.EN_REVISION);
    verify(repositorioPartidoMock, times(1)).guardar(partidoMock);

    verify(servicioNotificacionMock, times(1))
      .notificarFormularioCompletado(organizadorMock, partidoMock);
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
    partido.setEstadoEstadisticas(EstadoEstadisticas.EN_REVISION);
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
    partido.setEstadoEstadisticas(EstadoEstadisticas.EN_REVISION);
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
    partido.setEstadoEstadisticas(EstadoEstadisticas.EN_REVISION);
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

  @Test
  public void puedeCargarEstadisticasDebeRetornarTrueParaOrganizadorConPartidoActivoYPendienteORechazada() {
    Usuario organizador = crearUsuario(1L, "org@test.com");
    Partido partido = new Partido();
    partido.setOrganizador(organizador);
    partido.setEstado(EstadoPartido.ACTIVO);
    partido.setEstadoEstadisticas(EstadoEstadisticas.PENDIENTE_DE_CARGA);

    assertThat(servicioEstadistica.puedeCargarEstadisticas(partido, organizador), is(true));

    partido.setEstadoEstadisticas(EstadoEstadisticas.RECHAZADA);
    assertThat(servicioEstadistica.puedeCargarEstadisticas(partido, organizador), is(true));
  }

  @Test
  public void puedeCargarEstadisticasDebeRetornarFalseSiDatosSonNulosONoCumplenCondiciones() {
    Usuario organizador = crearUsuario(1L, "org@test.com");
    Usuario otroUsuario = crearUsuario(2L, "otro@test.com");
    Partido partido = new Partido();
    partido.setOrganizador(organizador);
    partido.setEstado(EstadoPartido.ACTIVO);
    partido.setEstadoEstadisticas(EstadoEstadisticas.PENDIENTE_DE_CARGA);

    assertThat(servicioEstadistica.puedeCargarEstadisticas(null, organizador), is(false));
    assertThat(servicioEstadistica.puedeCargarEstadisticas(partido, null), is(false));
    assertThat(servicioEstadistica.puedeCargarEstadisticas(partido, otroUsuario), is(false));

    partido.setEstado(EstadoPartido.FINALIZADO);
    assertThat(servicioEstadistica.puedeCargarEstadisticas(partido, organizador), is(false));

    partido.setEstado(EstadoPartido.ACTIVO);
    partido.setEstadoEstadisticas(EstadoEstadisticas.CONFIRMADA);
    assertThat(servicioEstadistica.puedeCargarEstadisticas(partido, organizador), is(false));
  }

  @Test
  public void calcularMarcadorPartidoDebeSumarGolesDeLocalYVisitante() {
    Equipo local = crearEquipo(100L, "Local");
    Equipo visitante = crearEquipo(200L, "Visitante");
    Jugador jLocal = crearJugador(10L, "Local1", crearUsuario(1L, "l@test.com"));
    Jugador jVisitante = crearJugador(20L, "Vis1", crearUsuario(2L, "v@test.com"));

    Partido partido = new Partido();
    partido.setEquipo1(local);
    partido.setEquipo2(visitante);
    partido.setInscripciones(
      List.of(crearInscripcion(jLocal, local, true), crearInscripcion(jVisitante, visitante, true))
    );

    Estadistica estLocal = givenDadoQueTengoUnaEstadistica(2, 0, 0, 0, 0, jLocal, partido);
    Estadistica estVisitante = givenDadoQueTengoUnaEstadistica(1, 0, 0, 0, 0, jVisitante, partido);

    Map<String, Integer> marcador = servicioEstadistica.calcularMarcadorPartido(
      partido,
      List.of(estLocal, estVisitante)
    );

    assertThat(marcador.get("local"), equalTo(2));
    assertThat(marcador.get("visitante"), equalTo(1));
  }

  @Test
  public void obtenerEstadisticasSeparadasPorEquipoDebeAgruparLocalesYVisitantes() {
    Equipo local = crearEquipo(100L, "Local");
    Equipo visitante = crearEquipo(200L, "Visitante");
    Jugador jLocal = crearJugador(10L, "Local1", crearUsuario(1L, "l@test.com"));
    Jugador jVisitante = crearJugador(20L, "Vis1", crearUsuario(2L, "v@test.com"));

    Partido partido = new Partido();
    partido.setEquipo1(local);
    partido.setEquipo2(visitante);
    partido.setInscripciones(
      List.of(crearInscripcion(jLocal, local, true), crearInscripcion(jVisitante, visitante, true))
    );

    Estadistica estLocal = givenDadoQueTengoUnaEstadistica(1, 0, 0, 0, 0, jLocal, partido);
    Estadistica estVisitante = givenDadoQueTengoUnaEstadistica(0, 1, 0, 0, 0, jVisitante, partido);

    Map<String, List<Estadistica>> separadas =
      servicioEstadistica.obtenerEstadisticasSeparadasPorEquipo(
        partido,
        List.of(estLocal, estVisitante)
      );

    assertThat(separadas.get("locales").size(), equalTo(1));
    assertThat(separadas.get("visitantes").size(), equalTo(1));
  }

  @Test
  public void confirmarEstadisticasDebeLanzarExcepcionSiPartidoNoExiste() {
    when(repositorioPartidoMock.buscarPorId(99L)).thenReturn(null);

    assertThrows(
      PartidoNoEncontrado.class,
      () -> servicioEstadistica.confirmarEstadisticas(99L, crearUsuario(1L, "u@test.com"))
    );
  }

  @Test
  public void confirmarEstadisticasDebeLanzarExcepcionSiUsuarioNoPuedeRevisar() {
    Partido partido = new Partido();
    partido.setId(1L);
    partido.setEstadoEstadisticas(EstadoEstadisticas.PENDIENTE_DE_CARGA);

    when(repositorioPartidoMock.buscarPorId(1L)).thenReturn(partido);

    assertThrows(
      ConfirmarEstadisticasNoPermitida.class,
      () -> servicioEstadistica.confirmarEstadisticas(1L, crearUsuario(2L, "u@test.com"))
    );
  }

  @Test
  public void confirmarEstadisticasDebeActualizarPuntajesHistorialYFinalizarPartido() {
    Usuario uOrg = crearUsuario(1L, "org@test.com");
    Usuario uCapRival = crearUsuario(2L, "rival@test.com");
    Jugador jOrg = crearJugador(10L, "Org", uOrg);
    Jugador jCapRival = crearJugador(20L, "Rival", uCapRival);
    Equipo local = crearEquipo(100L, "Local");
    Equipo visitante = crearEquipo(200L, "Visitante");

    Partido partido = new Partido();
    partido.setId(1L);
    partido.setOrganizador(uOrg);
    partido.setEquipo1(local);
    partido.setEquipo2(visitante);
    partido.setEstadoEstadisticas(EstadoEstadisticas.EN_REVISION);
    partido.setInscripciones(
      List.of(crearInscripcion(jOrg, local, true), crearInscripcion(jCapRival, visitante, true))
    );

    Estadistica estLocal = givenDadoQueTengoUnaEstadistica(2, 1, 0, 0, 0, jOrg, partido);
    Estadistica estVisitante = givenDadoQueTengoUnaEstadistica(1, 0, 1, 0, 0, jCapRival, partido);

    when(repositorioPartidoMock.buscarPorId(1L)).thenReturn(partido);
    when(repositorioJugadorMock.buscarPorUsuarioId(1L)).thenReturn(jOrg);
    when(repositorioJugadorMock.buscarPorUsuarioId(2L)).thenReturn(jCapRival);
    when(repositorioEstadisticaMock.buscarTodasLasEstadisticasPorPartido(partido))
      .thenReturn(List.of(estLocal, estVisitante));
    when(servicioRankingMock.calcularNivel(anyInt())).thenReturn("B");

    servicioEstadistica.confirmarEstadisticas(1L, uCapRival);

    assertThat(partido.getEstadoEstadisticas(), equalTo(EstadoEstadisticas.CONFIRMADA));
    assertThat(partido.getEstado(), equalTo(EstadoPartido.FINALIZADO));
    assertThat(estLocal.getCondicion(), equalTo(CondicionJugador.LOCAL));
    assertThat(estVisitante.getCondicion(), equalTo(CondicionJugador.VISITANTE));
    assertThat(jOrg.getPartidosGanados(), equalTo(1));
    assertThat(jCapRival.getPartidosPerdidos(), equalTo(1));
    verify(repositorioPartidoMock, times(1)).guardar(partido);
    verify(servicioNotificacionMock, times(1)).notificarFormularioCompletado(uOrg, partido);
  }

  @Test
  public void confirmarEstadisticasDebeSumarDosPuntosPorEmpate() {
    Usuario uOrg = crearUsuario(1L, "org@test.com");
    Usuario uCapRival = crearUsuario(2L, "rival@test.com");
    Jugador jOrg = crearJugador(10L, "Org", uOrg);
    Jugador jCapRival = crearJugador(20L, "Rival", uCapRival);
    Equipo local = crearEquipo(100L, "Local");
    Equipo visitante = crearEquipo(200L, "Visitante");

    Partido partido = new Partido();
    partido.setId(1L);
    partido.setOrganizador(uOrg);
    partido.setEquipo1(local);
    partido.setEquipo2(visitante);
    partido.setEstadoEstadisticas(EstadoEstadisticas.EN_REVISION);
    partido.setInscripciones(
      List.of(crearInscripcion(jOrg, local, true), crearInscripcion(jCapRival, visitante, true))
    );

    Estadistica estLocal = givenDadoQueTengoUnaEstadistica(1, 0, 0, 0, 0, jOrg, partido);
    Estadistica estVisitante = givenDadoQueTengoUnaEstadistica(1, 0, 0, 0, 0, jCapRival, partido);

    when(repositorioPartidoMock.buscarPorId(1L)).thenReturn(partido);
    when(repositorioJugadorMock.buscarPorUsuarioId(1L)).thenReturn(jOrg);
    when(repositorioJugadorMock.buscarPorUsuarioId(2L)).thenReturn(jCapRival);
    when(repositorioEstadisticaMock.buscarTodasLasEstadisticasPorPartido(partido))
      .thenReturn(List.of(estLocal, estVisitante));
    when(servicioRankingMock.calcularNivel(anyInt())).thenReturn("C");

    servicioEstadistica.confirmarEstadisticas(1L, uCapRival);

    assertThat(estLocal.getPuntaje(), equalTo(4)); // 2 por empate + 2 por 1 gol
    assertThat(jOrg.getPartidosJugados(), equalTo(1));
  }

  @Test
  public void rechazarEstadisticasDebeLanzarExcepcionSiPartidoNoExiste() {
    when(repositorioPartidoMock.buscarPorId(99L)).thenReturn(null);

    assertThrows(
      PartidoNoEncontrado.class,
      () -> servicioEstadistica.rechazarEstadisticas(99L, crearUsuario(1L, "u@test.com"))
    );
  }

  @Test
  public void rechazarEstadisticasDebeLanzarExcepcionSiUsuarioNoPuedeRevisar() {
    Partido partido = new Partido();
    partido.setId(1L);
    partido.setEstadoEstadisticas(EstadoEstadisticas.PENDIENTE_DE_CARGA);

    when(repositorioPartidoMock.buscarPorId(1L)).thenReturn(partido);

    assertThrows(
      RechazarEstadisticasNoPermitida.class,
      () -> servicioEstadistica.rechazarEstadisticas(1L, crearUsuario(2L, "u@test.com"))
    );
  }

  @Test
  public void rechazarEstadisticasDebeCambiarEstadoARechazadaYNotificar() {
    Usuario uOrg = crearUsuario(1L, "org@test.com");
    Usuario uCapRival = crearUsuario(2L, "rival@test.com");
    Jugador jOrg = crearJugador(10L, "Org", uOrg);
    Jugador jCapRival = crearJugador(20L, "Rival", uCapRival);
    Equipo local = crearEquipo(100L, "Local");
    Equipo visitante = crearEquipo(200L, "Visitante");

    Partido partido = new Partido();
    partido.setId(1L);
    partido.setOrganizador(uOrg);
    partido.setEquipo1(local);
    partido.setEquipo2(visitante);
    partido.setEstadoEstadisticas(EstadoEstadisticas.EN_REVISION);
    partido.setInscripciones(
      List.of(crearInscripcion(jOrg, local, true), crearInscripcion(jCapRival, visitante, true))
    );

    when(repositorioPartidoMock.buscarPorId(1L)).thenReturn(partido);
    when(repositorioJugadorMock.buscarPorUsuarioId(1L)).thenReturn(jOrg);
    when(repositorioJugadorMock.buscarPorUsuarioId(2L)).thenReturn(jCapRival);

    servicioEstadistica.rechazarEstadisticas(1L, uCapRival);

    assertThat(partido.getEstadoEstadisticas(), equalTo(EstadoEstadisticas.RECHAZADA));
    verify(repositorioPartidoMock, times(1)).guardar(partido);
    verify(servicioNotificacionMock, times(1)).notificarFormularioCompletado(uOrg, partido);
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
