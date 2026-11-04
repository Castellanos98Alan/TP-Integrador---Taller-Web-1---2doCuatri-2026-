package com.tallerwebi.presentacion;

import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Notificacion;
import com.tallerwebi.dominio.SolicitudAmistad;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.servicio.ServicioNotificacion;
import com.tallerwebi.dominio.servicio.ServicioSolicitudAmistad;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

public class NotificacionesGlobalControllerTest {

  private NotificacionesGlobalController notificacionesGlobalController;

  private ServicioSolicitudAmistad servicioSolicitudAmistadMock;
  private ServicioNotificacion servicioNotificacionMock;

  private HttpServletRequest requestMock;
  private HttpSession sessionMock;
  private Model modelMock;

  @BeforeEach
  public void init() {
    this.servicioSolicitudAmistadMock = mock(ServicioSolicitudAmistad.class);

    this.servicioNotificacionMock = mock(ServicioNotificacion.class);

    this.requestMock = mock(HttpServletRequest.class);

    this.sessionMock = mock(HttpSession.class);

    this.modelMock = mock(Model.class);

    this.notificacionesGlobalController =
      new NotificacionesGlobalController(
        this.servicioSolicitudAmistadMock,
        this.servicioNotificacionMock
      );
  }

  @Test
  public void dadoQueHayUnUsuarioLogueadoDebeCargarSolicitudesYNotificaciones() {
    // Given

    Usuario usuario = new Usuario();
    usuario.setId(1L);

    SolicitudAmistad solicitud1 = new SolicitudAmistad();

    SolicitudAmistad solicitud2 = new SolicitudAmistad();

    Notificacion notificacion1 = new Notificacion();

    Notificacion notificacion2 = new Notificacion();

    Notificacion notificacion3 = new Notificacion();

    List<SolicitudAmistad> solicitudes = new ArrayList<>();

    solicitudes.add(solicitud1);
    solicitudes.add(solicitud2);

    List<Notificacion> notificaciones = new ArrayList<>();

    notificaciones.add(notificacion1);
    notificaciones.add(notificacion2);
    notificaciones.add(notificacion3);

    when(requestMock.getSession()).thenReturn(sessionMock);

    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuario);

    when(servicioSolicitudAmistadMock.obtenerSolicitudesPendientes(usuario))
      .thenReturn(solicitudes);

    when(servicioNotificacionMock.obtenerNotificacionesNoLeidas(usuario.getId()))
      .thenReturn(notificaciones);

    // When

    notificacionesGlobalController.cargarNotificaciones(requestMock, modelMock);

    // Then

    verify(modelMock, times(1)).addAttribute("solicitudesPendientes", solicitudes);

    verify(modelMock, times(1)).addAttribute("notificaciones", notificaciones);

    verify(modelMock, times(1)).addAttribute("cantidadNotificaciones", 5);
  }

  @Test
  public void dadoQueNoHayUsuarioLogueadoDebeCargarCantidadNotificacionesEnCero() {
    // Given

    when(requestMock.getSession()).thenReturn(sessionMock);

    when(sessionMock.getAttribute("USUARIO")).thenReturn(null);

    // When

    notificacionesGlobalController.cargarNotificaciones(requestMock, modelMock);

    // Then

    verify(modelMock, times(1)).addAttribute("cantidadNotificaciones", 0);

    verify(servicioSolicitudAmistadMock, never()).obtenerSolicitudesPendientes(any(Usuario.class));

    verify(servicioNotificacionMock, never()).obtenerNotificacionesNoLeidas(anyLong());
  }
}
