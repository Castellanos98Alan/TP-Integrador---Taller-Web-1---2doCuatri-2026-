package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.excepcion.InvitacionAlPartidoException;
import com.tallerwebi.dominio.repositorio.RepositorioPartido;
import com.tallerwebi.dominio.repositorio.RepositorioUsuario;
import com.tallerwebi.dominio.servicio.ServicioInvitacionPartido;
import com.tallerwebi.dominio.servicio.ServicioInvitacionPartidoImpl;
import com.tallerwebi.dominio.servicio.ServicioNotificacion;
import com.tallerwebi.dominio.servicio.ServicioSolicitudAmistad;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioInvitacionPartidoTest {

  private ServicioInvitacionPartido servicioInvitacionPartido;

  private RepositorioPartido repositorioPartidoMock;
  private RepositorioUsuario repositorioUsuarioMock;
  private ServicioNotificacion servicioNotificacionMock;
  private ServicioSolicitudAmistad servicioSolicitudAmistadMock;

  @BeforeEach
  public void init() {
    this.repositorioPartidoMock = mock(RepositorioPartido.class);

    this.repositorioUsuarioMock = mock(RepositorioUsuario.class);

    this.servicioNotificacionMock = mock(ServicioNotificacion.class);

    this.servicioSolicitudAmistadMock = mock(ServicioSolicitudAmistad.class);

    this.servicioInvitacionPartido =
      new ServicioInvitacionPartidoImpl(
        this.repositorioPartidoMock,
        this.repositorioUsuarioMock,
        this.servicioNotificacionMock,
        this.servicioSolicitudAmistadMock
      );
  }

  @Test
  public void dadoQueParticipoEnUnPartidoPendientePuedoInvitarAUnAmigo()
    throws InvitacionAlPartidoException {
    // Given

    Long idPartido = 1L;
    Long idAmigo = 2L;



    Usuario juan = givenDadoQueTengoUnUsuario(1L);
    Usuario pedro = givenDadoQueTengoUnUsuario(idAmigo);

    Partido partido = givenDadoQueTengoUnPartidoPendiente();
    partido.setId(idPartido);

    givenDadoQueTengoUnJugadorConfirmado(partido, juan);

    when(repositorioPartidoMock.buscarPorId(idPartido)).thenReturn(partido);

    when(repositorioUsuarioMock.buscarPorId(idAmigo)).thenReturn(pedro);

    when(servicioSolicitudAmistadMock.sonAmigos(juan, pedro)).thenReturn(true);

    // When

    servicioInvitacionPartido.invitarJugador(idPartido, idAmigo, juan);

    // Then

    verify(repositorioPartidoMock, times(1)).buscarPorId(idPartido);

    verify(repositorioUsuarioMock, times(1)).buscarPorId(idAmigo);

    verify(servicioSolicitudAmistadMock, times(1)).sonAmigos(juan, pedro);

    verify(servicioNotificacionMock, times(1)).notificarInvitacion(pedro, partido);

    verify(servicioNotificacionMock, times(1)).existeInvitacionParaPartido(idAmigo, idPartido);
  }

  @Test
  public void dadoQueNoParticipoEnUnPartidoNoPuedoInvitar() throws InvitacionAlPartidoException {
    // Given

    Long idPartido = 1L;
    Long idAmigo = 2L;

    Usuario juan = givenDadoQueTengoUnUsuario(1L);
    Usuario pedro = givenDadoQueTengoUnUsuario(idAmigo);

    Partido partido = givenDadoQueTengoUnPartidoPendiente();

    when(repositorioPartidoMock.buscarPorId(idPartido)).thenReturn(partido);

    when(repositorioUsuarioMock.buscarPorId(idAmigo)).thenReturn(pedro);

    // When

    try {
      servicioInvitacionPartido.invitarJugador(idPartido, idAmigo, juan);

      fail("Se esperaba una InvitacionAlPartidoException");
    } catch (InvitacionAlPartidoException e) {
      assertThat(e.getMessage(), equalTo("Debes participar en el partido para invitar"));
    }

    // Then

    verify(servicioNotificacionMock, never())
      .notificarInvitacion(any(Usuario.class), any(Partido.class));
  }

  @Test
  public void dadoQueUnUsuarioNoEsMiAmigoNoPuedoInvitarlo() throws InvitacionAlPartidoException {
    // Given

    Long idPartido = 1L;
    Long idAmigo = 3L;

    Usuario juan = givenDadoQueTengoUnUsuario(1L);
    Usuario martin = givenDadoQueTengoUnUsuario(idAmigo);

    Partido partido = givenDadoQueTengoUnPartidoPendiente();

    givenDadoQueTengoUnJugadorConfirmado(partido, juan);

    when(repositorioPartidoMock.buscarPorId(idPartido)).thenReturn(partido);

    when(repositorioUsuarioMock.buscarPorId(idAmigo)).thenReturn(martin);

    when(servicioSolicitudAmistadMock.sonAmigos(juan, martin)).thenReturn(false);

    // When

    try {
      servicioInvitacionPartido.invitarJugador(idPartido, idAmigo, juan);

      fail("Se esperaba una InvitacionAlPartidoException");
    } catch (InvitacionAlPartidoException e) {
      assertThat(e.getMessage(), equalTo("Solo puedes invitar a tus amigos"));
    }

    // Then

    verify(servicioSolicitudAmistadMock, times(1)).sonAmigos(juan, martin);

    verify(servicioNotificacionMock, never())
      .notificarInvitacion(any(Usuario.class), any(Partido.class));
  }

  @Test
  public void dadoQueMiAmigoYaEstaEnElPartidoNoPuedoInvitarlo()
    throws InvitacionAlPartidoException {
    // Given

    Long idPartido = 1L;
    Long idAmigo = 2L;

    Usuario juan = givenDadoQueTengoUnUsuario(1L);
    Usuario pedro = givenDadoQueTengoUnUsuario(idAmigo);

    Partido partido = givenDadoQueTengoUnPartidoPendiente();

    givenDadoQueTengoUnJugadorConfirmado(partido, juan);
    givenDadoQueTengoUnJugadorConfirmado(partido, pedro);

    when(repositorioPartidoMock.buscarPorId(idPartido)).thenReturn(partido);

    when(repositorioUsuarioMock.buscarPorId(idAmigo)).thenReturn(pedro);

    when(servicioSolicitudAmistadMock.sonAmigos(juan, pedro)).thenReturn(true);

    // When

    try {
      servicioInvitacionPartido.invitarJugador(idPartido, idAmigo, juan);

      fail("Se esperaba una InvitacionAlPartidoException");
    } catch (InvitacionAlPartidoException e) {
      assertThat(e.getMessage(), equalTo("Este amigo ya está en el partido"));
    }

    // Then

    verify(servicioNotificacionMock, never())
      .notificarInvitacion(any(Usuario.class), any(Partido.class));
  }

  @Test
  public void dadoQueElPartidoEstaCanceladoNoPuedoInvitar() throws InvitacionAlPartidoException {
    // Given

    Long idPartido = 1L;
    Long idAmigo = 2L;

    Usuario juan = givenDadoQueTengoUnUsuario(1L);
    Usuario pedro = givenDadoQueTengoUnUsuario(idAmigo);

    Partido partido = givenDadoQueTengoUnPartidoPendiente();

    partido.setEstado(Partido.EstadoPartido.CANCELADO);

    givenDadoQueTengoUnJugadorConfirmado(partido, juan);

    when(repositorioPartidoMock.buscarPorId(idPartido)).thenReturn(partido);

    when(repositorioUsuarioMock.buscarPorId(idAmigo)).thenReturn(pedro);

    // When

    try {
      servicioInvitacionPartido.invitarJugador(idPartido, idAmigo, juan);

      fail("Se esperaba una InvitacionAlPartidoException");
    } catch (InvitacionAlPartidoException e) {
      assertThat(e.getMessage(), equalTo("El partido no admite invitaciones"));
    }

    // Then

    verify(servicioNotificacionMock, never())
      .notificarInvitacion(any(Usuario.class), any(Partido.class));
  }

  @Test
  public void dadoQueElPartidoEstaActivoNoPuedoInvitar() throws InvitacionAlPartidoException {
    // Given

    Long idPartido = 1L;
    Long idAmigo = 2L;

    Usuario juan = givenDadoQueTengoUnUsuario(1L);
    Usuario pedro = givenDadoQueTengoUnUsuario(idAmigo);

    Partido partido = givenDadoQueTengoUnPartidoPendiente();

    partido.setEstado(Partido.EstadoPartido.ACTIVO);

    givenDadoQueTengoUnJugadorConfirmado(partido, juan);

    when(repositorioPartidoMock.buscarPorId(idPartido)).thenReturn(partido);

    when(repositorioUsuarioMock.buscarPorId(idAmigo)).thenReturn(pedro);

    // When

    try {
      servicioInvitacionPartido.invitarJugador(idPartido, idAmigo, juan);

      fail("Se esperaba una InvitacionAlPartidoException");
    } catch (InvitacionAlPartidoException e) {
      assertThat(e.getMessage(), equalTo("El partido no admite invitaciones"));
    }

    // Then

    verify(servicioNotificacionMock, never())
      .notificarInvitacion(any(Usuario.class), any(Partido.class));
  }

  @Test
  public void dadoQueElUsuarioQueInvitaEsNuloNoDebeEnviarInvitacion()
    throws InvitacionAlPartidoException {
    // Given

    Long idPartido = 1L;
    Long idAmigo = 2L;

    Usuario pedro = givenDadoQueTengoUnUsuario(idAmigo);

    Partido partido = givenDadoQueTengoUnPartidoPendiente();

    when(repositorioPartidoMock.buscarPorId(idPartido)).thenReturn(partido);

    when(repositorioUsuarioMock.buscarPorId(idAmigo)).thenReturn(pedro);

    // When

    try {
      servicioInvitacionPartido.invitarJugador(idPartido, idAmigo, null);

      fail("Se esperaba una InvitacionAlPartidoException");
    } catch (InvitacionAlPartidoException e) {
      assertThat(e.getMessage(), equalTo("No se puede realizar la invitación"));
    }

    // Then

    verify(servicioNotificacionMock, never())
      .notificarInvitacion(any(Usuario.class), any(Partido.class));
  }

  private Usuario givenDadoQueTengoUnUsuario(Long id) {
    Usuario usuario = new Usuario();
    usuario.setId(id);

    return usuario;
  }

  private Partido givenDadoQueTengoUnPartidoPendiente() {
    Partido partido = new Partido();

    partido.setEstado(Partido.EstadoPartido.PENDIENTE);

    return partido;
  }

  private void givenDadoQueTengoUnJugadorConfirmado(Partido partido, Usuario usuario) {
    Jugador jugador = new Jugador();

    jugador.setUsuario(usuario);

    InscripcionPartido inscripcion = new InscripcionPartido(partido, jugador);

    partido.getInscripciones().add(inscripcion);
  }

  @Test
  public void dadoQueUnAmigoYaFueInvitadoNoDebeRecibirOtraInvitacion()
          throws InvitacionAlPartidoException {

    // Given

    Long idPartido = 1L;
    Long idAmigo = 2L;

    Usuario juan = givenDadoQueTengoUnUsuario(1L);
    Usuario pedro = givenDadoQueTengoUnUsuario(idAmigo);

    Partido partido = givenDadoQueTengoUnPartidoPendiente();
    partido.setId(idPartido);

    givenDadoQueTengoUnJugadorConfirmado(partido, juan);

    when(repositorioPartidoMock.buscarPorId(idPartido))
            .thenReturn(partido);

    when(repositorioUsuarioMock.buscarPorId(idAmigo))
            .thenReturn(pedro);

    when(servicioSolicitudAmistadMock.sonAmigos(juan, pedro))
            .thenReturn(true);

    when(servicioNotificacionMock.existeInvitacionParaPartido(
            idAmigo,
            idPartido
    )).thenReturn(true);

    // When

    try {

      servicioInvitacionPartido.invitarJugador(
              idPartido,
              idAmigo,
              juan
      );

      fail("Se esperaba una InvitacionAlPartidoException");

    } catch (InvitacionAlPartidoException e) {

      assertThat(
              e.getMessage(),
              equalTo("Este amigo ya fue invitado a este partido")
      );
    }

    // Then

    verify(servicioNotificacionMock, never())
            .notificarInvitacion(
                    any(Usuario.class),
                    any(Partido.class)
            );
  }
}
