package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Notificacion;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.servicio.ServicioLogin;
import com.tallerwebi.dominio.servicio.ServicioNotificacion;
import com.tallerwebi.dominio.servicio.ServicioSolicitudAmistad;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class SolicitudAmistadControllerTest {

  private SolicitudAmistadController solicitudAmistadController;

  private ServicioSolicitudAmistad servicioSolicitudAmistadMock;
  private ServicioLogin servicioLoginMock;
  private ServicioNotificacion servicioNotificacionMock;

  @BeforeEach
  public void init() {
    servicioSolicitudAmistadMock = mock(ServicioSolicitudAmistad.class);

    servicioLoginMock = mock(ServicioLogin.class);

    servicioNotificacionMock = mock(ServicioNotificacion.class);

    solicitudAmistadController =
      new SolicitudAmistadController(
        servicioSolicitudAmistadMock,
        servicioLoginMock,
        servicioNotificacionMock
      );
  }

  @Test
  public void aceptarSolicitudDebeLlamarAlServicioYVolverALaVistaAnterior() {
    // Given
    Integer idSolicitud = 1;
    String volverA = "/ranking";

    // When
    ModelAndView modelAndView = solicitudAmistadController.aceptarSolicitud(idSolicitud, volverA);

    // Then
    verify(servicioSolicitudAmistadMock, times(1)).aceptarSolicitud(idSolicitud);

    assertThat(modelAndView.getViewName(), equalTo("redirect:/ranking"));
  }

  @Test
  public void rechazarSolicitudDebeLlamarAlServicioYVolverALaVistaAnterior() {
    // Given

    Integer idSolicitud = 1;
    String volverA = "/perfil";

    // When

    ModelAndView modelAndView = solicitudAmistadController.rechazarSolicitud(idSolicitud, volverA);

    // Then

    verify(servicioSolicitudAmistadMock, times(1)).rechazarSolicitud(idSolicitud);

    assertThat(modelAndView.getViewName(), equalTo("redirect:/perfil"));
  }

  @Test
  public void enviarSolicitudDebeLlamarAlServicio() {
    // Given

    Usuario matias = new Usuario();
    matias.setEmail("matias@gmail.com");

    Usuario ivana = new Usuario();
    ivana.setEmail("ivana@gmail.com");

    Long idIvana = 2L;

    HttpServletRequest requestMock = mock(HttpServletRequest.class);

    HttpSession sessionMock = mock(HttpSession.class);

    when(requestMock.getSession()).thenReturn(sessionMock);

    when(sessionMock.getAttribute("USUARIO")).thenReturn(matias);

    when(servicioLoginMock.buscarUsuarioPorId(idIvana)).thenReturn(ivana);

    // When

    ModelAndView modelAndView = solicitudAmistadController.enviarSolicitud(idIvana, requestMock);

    // Then

    verify(servicioLoginMock, times(1)).buscarUsuarioPorId(idIvana);

    verify(servicioSolicitudAmistadMock, times(1)).enviarSolicitud(matias, ivana);

    //assertThat(modelAndView.getViewName(), equalTo("redirect:/perfil?id=" + idIvana));
    assertThat(modelAndView.getViewName(), equalTo("redirect:/perfil/2?solicitudEnviada=true"));
  }

  @Test
  public void verNotificacionesDebeRetornarLasNotificacionesDelUsuario() {
    // Given

    Long idUsuario = 1L;

    Usuario usuario = new Usuario();
    usuario.setId(idUsuario);

    Notificacion notificacion1 = new Notificacion();
    Notificacion notificacion2 = new Notificacion();

    List<Notificacion> notificaciones = new ArrayList<>();

    notificaciones.add(notificacion1);
    notificaciones.add(notificacion2);

    HttpSession sessionMock = mock(HttpSession.class);

    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuario);

    when(servicioNotificacionMock.obtenerNotificaciones(idUsuario)).thenReturn(notificaciones);

    // When

    ModelAndView modelAndView = solicitudAmistadController.verNotificaciones(sessionMock);

    // Then

    verify(servicioNotificacionMock, times(1)).obtenerNotificaciones(idUsuario);

    assertThat(modelAndView.getViewName(), equalTo("notificaciones"));

    assertThat(modelAndView.getModel().get("notificaciones"), equalTo(notificaciones));

    assertThat(notificaciones.size(), equalTo(2));
  }

  @Test
  public void verNotificacionesSinNotificacionesDebeRetornarUnaListaVacia() {
    // Given

    Long idUsuario = 1L;

    Usuario usuario = new Usuario();
    usuario.setId(idUsuario);

    List<Notificacion> notificaciones = new ArrayList<>();

    HttpSession sessionMock = mock(HttpSession.class);

    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuario);

    when(servicioNotificacionMock.obtenerNotificaciones(idUsuario)).thenReturn(notificaciones);

    // When

    ModelAndView modelAndView = solicitudAmistadController.verNotificaciones(sessionMock);

    // Then

    verify(servicioNotificacionMock, times(1)).obtenerNotificaciones(idUsuario);

    assertThat(modelAndView.getViewName(), equalTo("notificaciones"));

    assertThat(
      ((List<Notificacion>) modelAndView.getModel().get("notificaciones")).size(),
      equalTo(0)
    );
  }
}
