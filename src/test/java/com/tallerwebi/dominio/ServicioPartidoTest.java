package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Enumeration.TipoDeNotificacion;
import com.tallerwebi.dominio.excepcion.CanchaNoDisponibleException;
import com.tallerwebi.dominio.excepcion.CanchaNoEncontradaException;
import com.tallerwebi.dominio.excepcion.CupoInvalidoException;
import com.tallerwebi.dominio.excepcion.FechaPartidoInvalidaException;
import com.tallerwebi.dominio.excepcion.InvitacionAlPartidoException;
import com.tallerwebi.dominio.excepcion.RangoJugadorIncompatibleException;
import com.tallerwebi.dominio.repositorio.RepositorioCancha;
import com.tallerwebi.dominio.repositorio.RepositorioPartido;
import com.tallerwebi.dominio.repositorio.RepositorioUsuario;
import com.tallerwebi.dominio.servicio.*;
import com.tallerwebi.presentacion.DatosCrearPartido;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioPartidoTest {

  private ServicioPartido servicioPartido;

  private ServicioJugador servicioJugadorMock;

  private RepositorioPartido repositorioPartidoMock;

  private RepositorioCancha repositorioCanchaMock;

  private RepositorioUsuario repositorioUsuarioMock;

  private ServicioBalanceoEquipo servicioBalanceoEquipoMock;

  private ServicioNotificacion servicioNotificacionMock;

  private ServicioSolicitudAmistad servicioSolicitudAmistadMock;
  private ServicioInvitacionPartido servicioInvitacionPartidoMock;

  private static final String FILTRO_TODOS = "TODOS";

  @BeforeEach
  public void init() {
    repositorioPartidoMock = mock(RepositorioPartido.class);

    repositorioUsuarioMock = mock(RepositorioUsuario.class);

    repositorioCanchaMock = mock(RepositorioCancha.class);

    servicioJugadorMock = mock(ServicioJugador.class);

    servicioInvitacionPartidoMock = mock(ServicioInvitacionPartido.class);

    servicioBalanceoEquipoMock = mock(ServicioBalanceoEquipo.class);

    servicioNotificacionMock = mock(ServicioNotificacion.class);

    servicioSolicitudAmistadMock = mock(ServicioSolicitudAmistad.class);

    servicioPartido =
      new ServicioPartidoImpl(
        repositorioPartidoMock,
        repositorioCanchaMock,
        repositorioUsuarioMock,
        servicioJugadorMock,
        servicioBalanceoEquipoMock,
        servicioNotificacionMock,
        servicioSolicitudAmistadMock,
        servicioInvitacionPartidoMock
      );
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

    Usuario noOrganizador = new Usuario();
    noOrganizador.setId(2L);

    Partido partido = new Partido();
    partido.setId(10L);
    partido.setOrganizador(organizador);
    partido.setEstado(Partido.EstadoPartido.ACTIVO);

    when(repositorioPartidoMock.buscarPorId(10L)).thenReturn(partido);

    Exception exception = assertThrows(
      RuntimeException.class,
      () -> servicioPartido.cancelarPartido(10L, noOrganizador)
    );

    assertEquals("No estas autorizado para cancelar este partido.", exception.getMessage());

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
      () -> servicioPartido.cancelarPartido(10L, organizador)
    );

    assertEquals("No se puede cancelar un partido que ya finalizo.", exception.getMessage());

    verify(repositorioPartidoMock, never()).modificar(any());
  }

  @Test
  public void queUnUsuarioPuedaUnirseAPartidoExitosamente() {
    Long partidoId = 1L;

    Partido partido = new Partido();

    partido.setId(partidoId);
    partido.setCupoMaximo(10);
    partido.setInscripciones(new ArrayList<>());

    partido.setEstado(Partido.EstadoPartido.ACTIVO);

    Usuario usuario = new Usuario();

    usuario.setId(100L);
    usuario.setEmail("test@unlam.edu.ar");

    Jugador jugador = new Jugador();
    partido.setNivel("B");
    jugador.setNivel("INTERMEDIO");

    jugador.setId(1L);
    jugador.setUsuario(usuario);

    when(repositorioPartidoMock.buscarPorId(partidoId)).thenReturn(partido);

    when(repositorioPartidoMock.buscarPartidosPorJugador(jugador.getId()))
      .thenReturn(new ArrayList<>());

    servicioPartido.unirseAPartido(partidoId, jugador);

    assertThat(partido.getInscripciones(), hasSize(1));

    assertEquals(jugador, partido.getInscripciones().get(0).getJugador());

    assertEquals(1, partido.getCantJugadores());

    verify(repositorioPartidoMock, times(1)).modificar(partido);
  }

  @Test
  public void queLanceExcepcionSiElPartidoNoExisteAlQuererUnirse() {
    Long partidoId = 99L;

    Jugador jugador = new Jugador();

    when(repositorioPartidoMock.buscarPorId(partidoId)).thenReturn(null);

    Exception exception = assertThrows(
      RuntimeException.class,
      () -> servicioPartido.unirseAPartido(partidoId, jugador)
    );

    assertEquals("El partido" + partidoId + " no existe.", exception.getMessage());
  }

  @Test
  public void queLanceExcepcionSiElPartidoEstaCancelado() {
    Long partidoId = 1L;

    Partido partido = new Partido();

    partido.setId(partidoId);

    partido.setEstado(Partido.EstadoPartido.CANCELADO);

    Jugador jugador = new Jugador();

    when(repositorioPartidoMock.buscarPorId(partidoId)).thenReturn(partido);

    Exception exception = assertThrows(
      RuntimeException.class,
      () -> servicioPartido.unirseAPartido(partidoId, jugador)
    );

    assertEquals("No se puede unir a un partido finalizado o cancelado.", exception.getMessage());
  }

  @Test
  public void queLanceExcepcionSiElPartidoEstaFinalizado() {
    Long partidoId = 1L;

    Partido partido = new Partido();

    partido.setId(partidoId);

    partido.setEstado(Partido.EstadoPartido.FINALIZADO);

    Jugador jugador = new Jugador();

    when(repositorioPartidoMock.buscarPorId(partidoId)).thenReturn(partido);

    Exception exception = assertThrows(
      RuntimeException.class,
      () -> servicioPartido.unirseAPartido(partidoId, jugador)
    );

    assertEquals("No se puede unir a un partido finalizado o cancelado.", exception.getMessage());
  }

  @Test
  public void queLanceExcepcionSiElUsuarioYaEstaUnido() {
    Long partidoId = 1L;

    Partido partido = new Partido();

    partido.setId(partidoId);
    partido.setCupoMaximo(10);

    partido.setEstado(Partido.EstadoPartido.ACTIVO);

    partido.setInscripciones(new ArrayList<>());

    Jugador jugador = new Jugador();

    jugador.setId(100L);
    partido.setNivel("B");
    jugador.setNivel("INTERMEDIO");

    InscripcionPartido inscripcionExistente = new InscripcionPartido();

    inscripcionExistente.setPartido(partido);

    inscripcionExistente.setJugador(jugador);

    partido.getInscripciones().add(inscripcionExistente);

    when(repositorioPartidoMock.buscarPorId(partidoId)).thenReturn(partido);

    Exception exception = assertThrows(
      RuntimeException.class,
      () -> servicioPartido.unirseAPartido(partidoId, jugador)
    );

    assertEquals("Ya estas anotado en la sala de espera de este partido.", exception.getMessage());
  }

  @Test
  public void queLanceExcepcionSiSeAlcanzoElCupoMaximo() {
    Long partidoId = 1L;

    Partido partido = new Partido();

    partido.setId(partidoId);
    partido.setCupoMaximo(2);

    partido.setEstado(Partido.EstadoPartido.ACTIVO);

    partido.setInscripciones(new ArrayList<>());

    Jugador jugador1 = new Jugador();

    jugador1.setId(101L);

    Jugador jugador2 = new Jugador();

    jugador2.setId(102L);

    InscripcionPartido inscripcion1 = new InscripcionPartido();

    inscripcion1.setPartido(partido);
    inscripcion1.setJugador(jugador1);

    partido.getInscripciones().add(inscripcion1);

    InscripcionPartido inscripcion2 = new InscripcionPartido();

    inscripcion2.setPartido(partido);
    inscripcion2.setJugador(jugador2);

    partido.getInscripciones().add(inscripcion2);

    Jugador jugadorNuevo = new Jugador();

    jugadorNuevo.setId(103L);
    partido.setNivel("B");
    jugadorNuevo.setNivel("INTERMEDIO");

    when(repositorioPartidoMock.buscarPorId(partidoId)).thenReturn(partido);

    Exception exception = assertThrows(
      RuntimeException.class,
      () -> servicioPartido.unirseAPartido(partidoId, jugadorNuevo)
    );

    assertEquals("Cupo maximo alcanzado.", exception.getMessage());

    verify(servicioBalanceoEquipoMock, never()).balancearEquipos(any(Partido.class));
  }

  @Test
  public void quePermitaUnirseSiHayMasDeTresHorasDeDiferencia() {
    Long partidoIdNuevo = 1L;

    Long partidoIdExistente = 2L;

    Partido partidoNuevo = new Partido();

    partidoNuevo.setId(partidoIdNuevo);

    partidoNuevo.setCupoMaximo(10);

    partidoNuevo.setEstado(Partido.EstadoPartido.ACTIVO);

    partidoNuevo.setFecha(LocalDateTime.of(2026, 6, 6, 19, 0));

    partidoNuevo.setInscripciones(new ArrayList<>());

    Partido partidoExistente = new Partido();

    partidoExistente.setId(partidoIdExistente);

    partidoExistente.setFecha(LocalDateTime.of(2026, 6, 6, 15, 0));

    Usuario usuario = new Usuario();

    usuario.setId(100L);

    Jugador jugador = new Jugador();

    jugador.setId(50L);
    jugador.setUsuario(usuario);
    partidoNuevo.setNivel("B");
    jugador.setNivel("INTERMEDIO");

    when(repositorioPartidoMock.buscarPorId(partidoIdNuevo)).thenReturn(partidoNuevo);

    when(repositorioPartidoMock.buscarPartidosPorJugador(jugador.getId()))
      .thenReturn(List.of(partidoExistente));

    servicioPartido.unirseAPartido(partidoIdNuevo, jugador);

    assertThat(partidoNuevo.getInscripciones(), hasSize(1));

    assertEquals(jugador, partidoNuevo.getInscripciones().get(0).getJugador());

    verify(repositorioPartidoMock, times(1)).modificar(partidoNuevo);
  }

  @Test
  public void queNoPermitaUnirseSiHayMenosDeTresHorasDeDiferencia() {
    Long partidoIdNuevo = 1L;

    Partido partidoNuevo = new Partido();

    partidoNuevo.setId(partidoIdNuevo);

    partidoNuevo.setCupoMaximo(10);

    partidoNuevo.setEstado(Partido.EstadoPartido.ACTIVO);

    partidoNuevo.setFecha(LocalDateTime.of(2026, 6, 6, 17, 0));

    partidoNuevo.setInscripciones(new ArrayList<>());

    Partido partidoExistente = new Partido();

    partidoExistente.setFecha(LocalDateTime.of(2026, 6, 6, 15, 0));

    Jugador jugador = new Jugador();

    jugador.setId(50L);
    partidoNuevo.setNivel("B");
    jugador.setNivel("INTERMEDIO");

    when(repositorioPartidoMock.buscarPorId(partidoIdNuevo)).thenReturn(partidoNuevo);

    when(repositorioPartidoMock.buscarPartidosPorJugador(jugador.getId()))
      .thenReturn(List.of(partidoExistente));

    Exception exception = assertThrows(
      RuntimeException.class,
      () -> servicioPartido.unirseAPartido(partidoIdNuevo, jugador)
    );

    assertTrue(exception.getMessage().contains("No podes unirte"));

    verify(repositorioPartidoMock, never()).modificar(partidoNuevo);
  }

  @Test
  public void queSePuedaBuscarUnPartidoPorIdExitosamente() {
    Long partidoId = 1L;

    Partido partido = new Partido();

    partido.setId(partidoId);

    when(repositorioPartidoMock.buscarPorId(partidoId)).thenReturn(partido);

    Partido encontrado = servicioPartido.buscarPartidoPorId(partidoId);

    assertNotNull(encontrado);

    assertEquals(partidoId, encontrado.getId());
  }

  @Test
  public void deberiaGuardarElPartidoAlCrearPartido() {
    Cancha cancha = new Cancha();

    cancha.setId(1L);

    when(repositorioCanchaMock.buscarPorNombre("Cancha Los Leones")).thenReturn(cancha);

    when(repositorioPartidoMock.buscarPartidosEnRango(any(), any(), any()))
      .thenReturn(new ArrayList<>());

    DatosCrearPartido datos = new DatosCrearPartido(
      "Cancha Los Leones",
      LocalDateTime.of(2026, 10, 20, 18, 30),
      10,
      "INTERMEDIO"
    );

    servicioPartido.crearPartido(datos, new Usuario());

    verify(repositorioPartidoMock).guardar(any(Partido.class));
  }

  @Test
  public void noDeberiaCrearElPartidoSiLaCanchaIngresadaNoExiste() {
    DatosCrearPartido datos = new DatosCrearPartido(
      "Inexistente",
      LocalDateTime.of(2026, 10, 20, 18, 30),
      10,
      "INTERMEDIO"
    );

    Exception exception = assertThrows(
      CanchaNoEncontradaException.class,
      () -> servicioPartido.crearPartido(datos, new Usuario())
    );

    assertEquals("La cancha ingresada no existe.", exception.getMessage());

    verify(repositorioPartidoMock, never()).guardar(any());
  }

  @Test
  public void noDeberiaCrearElPartidoSiElCupoEsCeroONegativo() {
    DatosCrearPartido datos = new DatosCrearPartido(
      "Cancha Los Leones",
      LocalDateTime.of(2026, 10, 20, 18, 30),
      0,
      "INTERMEDIO"
    );

    Exception exception = assertThrows(
      CupoInvalidoException.class,
      () -> servicioPartido.crearPartido(datos, new Usuario())
    );

    assertEquals("El cupo debe ser mayor a cero.", exception.getMessage());

    verify(repositorioPartidoMock, never()).guardar(any());
  }

  @Test
  public void noDeberiaCrearElPartidoSiLaFechaEstaVacia() {
    DatosCrearPartido datos = new DatosCrearPartido("Cancha Los Leones", null, 10, "INTERMEDIO");

    Exception exception = assertThrows(
      FechaPartidoInvalidaException.class,
      () -> servicioPartido.crearPartido(datos, new Usuario())
    );

    assertEquals("Debe ingresar una fecha y hora.", exception.getMessage());

    verify(repositorioPartidoMock, never()).guardar(any());
  }

  @Test
  public void noDeberiaCrearPartidoSiLaCanchaEstaOcupada() {
    Cancha cancha = new Cancha();
    cancha.setId(1L);

    Partido partidoExistente = new Partido();

    when(repositorioCanchaMock.buscarPorNombre("Cancha Los Leones")).thenReturn(cancha);

    when(repositorioPartidoMock.buscarPartidosEnRango(any(), any(), any()))
      .thenReturn(List.of(partidoExistente));

    DatosCrearPartido datos = new DatosCrearPartido(
      "Cancha Los Leones",
      LocalDateTime.of(2026, 10, 20, 18, 30),
      10,
      "INTERMEDIO"
    );

    Exception exception = assertThrows(
      CanchaNoDisponibleException.class,
      () -> servicioPartido.crearPartido(datos, new Usuario())
    );

    assertEquals(
      "La cancha no está disponible en esa franja horaria (los partidos duran 1 hora).",
      exception.getMessage()
    );

    verify(repositorioPartidoMock, never()).guardar(any());
  }

  @Test
  public void deberiaBalancearYAsignarCapitanesCuandoSeCompletaElCupo() {
    Long partidoId = 1L;

    Partido partido = new Partido();

    partido.setId(partidoId);

    partido.setCupoMaximo(2);

    partido.setEstado(Partido.EstadoPartido.ACTIVO);

    partido.setInscripciones(new ArrayList<>());

    Jugador jugadorExistente = new Jugador();

    jugadorExistente.setId(10L);

    InscripcionPartido inscripcionExistente = new InscripcionPartido(partido, jugadorExistente);

    partido.getInscripciones().add(inscripcionExistente);

    partido.setCantJugadores(1);

    Jugador jugadorNuevo = new Jugador();

    jugadorNuevo.setId(20L);
    partido.setNivel("B");
    jugadorExistente.setNivel("INTERMEDIO");
    jugadorNuevo.setNivel("INTERMEDIO");

    when(repositorioPartidoMock.buscarPorId(partidoId)).thenReturn(partido);

    when(repositorioPartidoMock.buscarPartidosPorJugador(jugadorNuevo.getId()))
      .thenReturn(new ArrayList<>());

    servicioPartido.unirseAPartido(partidoId, jugadorNuevo);

    assertEquals(2, partido.getCantJugadores());

    verify(servicioBalanceoEquipoMock, times(1)).balancearEquipos(partido);

    verify(servicioBalanceoEquipoMock, times(1)).asignarCapitanes(partido);

    verify(repositorioPartidoMock, times(1)).modificar(partido);
  }

  @Test
  public void noDeberiaBalancearSiElCupoTodaviaNoEstaCompleto() {
    Long partidoId = 1L;

    Partido partido = new Partido();

    partido.setId(partidoId);

    partido.setCupoMaximo(10);

    partido.setEstado(Partido.EstadoPartido.ACTIVO);

    partido.setInscripciones(new ArrayList<>());

    for (int i = 0; i < 8; i++) {
      Jugador jugador = new Jugador();
      jugador.setNivel("INTERMEDIO");
      jugador.setId((long) i);

      InscripcionPartido inscripcion = new InscripcionPartido(partido, jugador);

      partido.getInscripciones().add(inscripcion);
    }

    partido.setCantJugadores(8);

    Jugador nuevoJugador = new Jugador();
    partido.setNivel("B");
    nuevoJugador.setNivel("INTERMEDIO");
    nuevoJugador.setId(100L);

    when(repositorioPartidoMock.buscarPorId(partidoId)).thenReturn(partido);

    when(repositorioPartidoMock.buscarPartidosPorJugador(nuevoJugador.getId()))
      .thenReturn(new ArrayList<>());

    servicioPartido.unirseAPartido(partidoId, nuevoJugador);

    assertEquals(9, partido.getCantJugadores());

    verify(servicioBalanceoEquipoMock, never()).balancearEquipos(any(Partido.class));

    verify(servicioBalanceoEquipoMock, never()).asignarCapitanes(any(Partido.class));

    verify(repositorioPartidoMock, times(1)).modificar(partido);
  }

  //Axel
  @Test
  public void noDeberiaCrearElPartidoSiLaCanchaYaTieneUnPartidoEnEseHorario() {
    // Given
    LocalDateTime fecha = LocalDateTime.of(2026, 10, 20, 18, 0);
    Cancha cancha = new Cancha();
    cancha.setId(1L);
    when(repositorioCanchaMock.buscarPorNombre("Cancha Los Leones")).thenReturn(cancha);
    when(repositorioPartidoMock.buscarPartidosEnRango(1L, fecha.minusHours(1), fecha.plusHours(1)))
      .thenReturn(List.of(new Partido()));
    DatosCrearPartido datos = new DatosCrearPartido("Cancha Los Leones", fecha, 10, "B");

    // When
    Exception exception = assertThrows(
      RuntimeException.class,
      () -> servicioPartido.crearPartido(datos, new Usuario())
    );

    // Then
    assertEquals(
      "La cancha no está disponible en esa franja horaria (los partidos duran 1 hora).",
      exception.getMessage()
    );
    verify(repositorioPartidoMock, never()).guardar(any());
  }

  //Axel Sprint 4
  @Test
  public void noDeberiaPermitirUnirseAUnPartidoDeRangoIncompatible() {
    // Given
    Long partidoId = 1L;

    Partido partido = new Partido();
    partido.setId(partidoId);
    partido.setNivel("C");
    partido.setCupoMaximo(10);
    partido.setEstado(Partido.EstadoPartido.ACTIVO);
    partido.setInscripciones(new ArrayList<>());

    Jugador jugador = new Jugador();
    jugador.setId(100L);
    jugador.setNivel("INTERMEDIO");

    when(repositorioPartidoMock.buscarPorId(partidoId)).thenReturn(partido);

    // When
    RangoJugadorIncompatibleException exception = assertThrows(
      RangoJugadorIncompatibleException.class,
      () -> servicioPartido.unirseAPartido(partidoId, jugador)
    );

    // Then
    assertEquals(
      "No podes unirte a este partido porque no corresponde a tu rango.",
      exception.getMessage()
    );

    verify(repositorioPartidoMock, never()).modificar(any());
  }

  @Test
  public void deberiaPermitirUnirseAUnPartidoDeRangoCompatible() {
    // Given
    Long partidoId = 1L;

    Partido partido = new Partido();
    partido.setId(partidoId);
    partido.setNivel("b");
    partido.setCupoMaximo(10);
    partido.setEstado(Partido.EstadoPartido.ACTIVO);
    partido.setInscripciones(new ArrayList<>());

    Jugador jugador = new Jugador();
    jugador.setId(100L);
    jugador.setNivel("INTERMEDIO");

    when(repositorioPartidoMock.buscarPorId(partidoId)).thenReturn(partido);

    when(repositorioPartidoMock.buscarPartidosPorJugador(jugador.getId()))
      .thenReturn(new ArrayList<>());

    // When
    servicioPartido.unirseAPartido(partidoId, jugador);

    // Then
    assertEquals(1, partido.getCantJugadores());
    assertEquals(1, partido.getInscripciones().size());

    verify(repositorioPartidoMock, times(1)).modificar(partido);

    verify(repositorioPartidoMock)
      .buscarPartidosEnRango(
        1L,
        LocalDateTime.of(2026, 10, 20, 17, 0),
        LocalDateTime.of(2026, 10, 20, 19, 0)
      );
  }

  @Test
  public void alInvitarUnJugadorDebeLlamarAlServicioDeInvitacion()
    throws InvitacionAlPartidoException {
    // Given

    Long idPartido = 1L;
    Long idJugador = 2L;

    Usuario usuarioQueInvita = new Usuario();

    // When

    servicioPartido.invitarJugador(idPartido, idJugador, usuarioQueInvita);

    // Then

    verify(servicioInvitacionPartidoMock, times(1))
      .invitarJugador(idPartido, idJugador, usuarioQueInvita);
  }

  @Test
  public void dadoQueUnPartidoTieneJugadoresCuandoSeCancelaDebeNotificarlos() {
    // Given

    Usuario organizador = new Usuario();
    organizador.setId(1L);

    Usuario usuario1 = new Usuario();
    usuario1.setId(2L);

    Usuario usuario2 = new Usuario();
    usuario2.setId(3L);

    Jugador jugador1 = new Jugador();
    jugador1.setUsuario(usuario1);

    Jugador jugador2 = new Jugador();
    jugador2.setUsuario(usuario2);

    Partido partido = new Partido();
    partido.setId(10L);
    partido.setOrganizador(organizador);
    partido.setEstado(Partido.EstadoPartido.ACTIVO);

    InscripcionPartido inscripcion1 = new InscripcionPartido(partido, jugador1);

    InscripcionPartido inscripcion2 = new InscripcionPartido(partido, jugador2);

    partido.getInscripciones().add(inscripcion1);

    partido.getInscripciones().add(inscripcion2);

    when(repositorioPartidoMock.buscarPorId(10L)).thenReturn(partido);

    // When

    servicioPartido.cancelarPartido(10L, organizador);

    // Then

    assertThat(partido.getEstado(), equalTo(Partido.EstadoPartido.CANCELADO));

    verify(servicioNotificacionMock).notificarCancelacion(usuario1, partido);

    verify(servicioNotificacionMock).notificarCancelacion(usuario2, partido);
  }

  @Test
  public void alCancelarUnPartidoSinJugadoresNoDebeEnviarNotificaciones() {
    // Given

    Usuario organizador = new Usuario();
    organizador.setId(1L);

    Partido partido = new Partido();
    partido.setId(10L);
    partido.setOrganizador(organizador);
    partido.setEstado(Partido.EstadoPartido.ACTIVO);

    partido.setInscripciones(new ArrayList<>());

    when(repositorioPartidoMock.buscarPorId(10L)).thenReturn(partido);

    // When

    servicioPartido.cancelarPartido(10L, organizador);

    // Then

    verify(servicioNotificacionMock, never())
      .notificarCancelacion(any(Usuario.class), any(Partido.class));
  }
}
