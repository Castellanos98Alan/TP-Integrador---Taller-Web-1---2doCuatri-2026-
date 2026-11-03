package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Enumeration.TipoDeNotificacion;
import com.tallerwebi.dominio.repositorio.RepositorioNotificacion;
import com.tallerwebi.dominio.repositorio.RepositorioPartido;
import com.tallerwebi.dominio.repositorio.RepositorioUsuario;
import com.tallerwebi.dominio.servicio.ServicioNotificacion;
import com.tallerwebi.dominio.servicio.ServicioNotificationImpl;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioNotificacionTest {

  private ServicioNotificacion servicioNotificacion;

  private RepositorioNotificacion repositorioNotificacionMock;
  private RepositorioPartido repositorioPartidoMock;
  private RepositorioUsuario repositorioUsuarioMock;

  @BeforeEach
  public void init() {
    this.repositorioNotificacionMock = mock(RepositorioNotificacion.class);

    this.repositorioPartidoMock = mock(RepositorioPartido.class);

    this.repositorioUsuarioMock = mock(RepositorioUsuario.class);

    this.servicioNotificacion =
      new ServicioNotificationImpl(
        this.repositorioNotificacionMock,
        this.repositorioPartidoMock,
        this.repositorioUsuarioMock
      );
  }

  @Test
  public void alNotificarUnaInvitacionDebeGuardarUnaNotificacion() {
    // Given

    Usuario usuario = new Usuario();
    Partido partido = new Partido();

    // When

    servicioNotificacion.notificarInvitacion(usuario, partido);

    // Then

    verify(this.repositorioNotificacionMock, times(1)).guardar(any(Notificacion.class));
  }

  @Test
  public void alNotificarFormularioCompletadoDebeGuardarUnaNotificacion() {
    // Given

    Usuario capitan = new Usuario();
    Partido partido = new Partido();

    // When

    servicioNotificacion.notificarFormularioCompletado(capitan, partido);

    // Then

    verify(this.repositorioNotificacionMock, times(1)).guardar(any(Notificacion.class));
  }

  @Test
  public void alNotificarUnaCancelacionDebeGuardarUnaNotificacion() {
    // Given

    Usuario usuario = new Usuario();
    Partido partido = new Partido();

    // When

    servicioNotificacion.notificarCancelacion(usuario, partido);

    // Then

    verify(this.repositorioNotificacionMock, times(1)).guardar(any(Notificacion.class));
  }

  @Test
  public void obtenerNotificacionesDebeRetornarLasNotificacionesDelUsuario() {
    // Given

    Long idUsuario = 1L;

    Notificacion notificacion1 = new Notificacion();
    Notificacion notificacion2 = new Notificacion();

    List<Notificacion> notificaciones = new ArrayList<>();

    notificaciones.add(notificacion1);
    notificaciones.add(notificacion2);

    when(repositorioNotificacionMock.buscarPorUsuario(idUsuario)).thenReturn(notificaciones);

    // When

    List<Notificacion> notificacionesBuscadas = servicioNotificacion.obtenerNotificaciones(
      idUsuario
    );

    // Then

    assertThat(notificacionesBuscadas.size(), equalTo(2));

    verify(this.repositorioNotificacionMock, times(1)).buscarPorUsuario(idUsuario);
  }

  @Test
  public void alMarcarComoLeidaDebeCambiarElEstadoDeLaNotificacion() {
    // Given

    Long idNotificacion = 1L;

    Notificacion notificacion = new Notificacion();

    notificacion.setLeida(false);

    when(repositorioNotificacionMock.buscarPorID(idNotificacion)).thenReturn(notificacion);

    // When

    servicioNotificacion.marcarComoLeida(idNotificacion);

    // Then

    assertThat(notificacion.getLeida(), equalTo(true));

    verify(this.repositorioNotificacionMock, times(1)).buscarPorID(idNotificacion);
  }
}
