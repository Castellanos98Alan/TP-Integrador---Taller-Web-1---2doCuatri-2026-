package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Cancha;
import com.tallerwebi.dominio.Notificacion;
import com.tallerwebi.dominio.SolicitudAmistad;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.servicio.ServicioHome;
import com.tallerwebi.dominio.servicio.ServicioNotificacion;
import com.tallerwebi.dominio.servicio.ServicioSolicitudAmistad;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class HomeControllerTest {

  private ServicioHome servicioHomeMock;
  private ServicioNotificacion servicioNotificacionMock;
  private ServicioSolicitudAmistad servicioSolicitudAmistadMock;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;
  private HomeController homeController;

  @BeforeEach
  public void init() {
    servicioHomeMock = mock(ServicioHome.class);
    servicioNotificacionMock = mock(ServicioNotificacion.class);
    servicioSolicitudAmistadMock = mock(ServicioSolicitudAmistad.class); // Instanciar el mock

    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);

    when(requestMock.getSession()).thenReturn(sessionMock);

    homeController =
      new HomeController(servicioHomeMock, servicioNotificacionMock, servicioSolicitudAmistadMock);
  }

  @Test
  public void queAlIrALaHomeSeMuestreLaVistaCorrectaYSeCarguenLasCanchasDestacadas() {
    // Preparación
    List<Cancha> canchasSimuladas = new ArrayList<>();
    canchasSimuladas.add(new Cancha());
    canchasSimuladas.add(new Cancha());

    when(servicioHomeMock.obtenerCanchasParaHome(4)).thenReturn(canchasSimuladas);

    // Ejecución
    ModelAndView mav = homeController.irAHome(requestMock);

    // Validación de la vista
    assertThat(mav.getViewName(), is(equalTo("home")));

    // Validación del modelo
    Map<String, Object> modelo = mav.getModel();
    assertThat(modelo.get("canchasDestacadas"), is(notNullValue()));
    assertThat(modelo.get("canchasDestacadas"), is(equalTo(canchasSimuladas)));

    verify(servicioHomeMock, times(1)).obtenerCanchasParaHome(4);
  }

  @Test
  public void queAlIrALaHomeConUsuarioLogueadoSeCarguenLasNotificacionesYSolicitudes() {
    // Preparación
    Usuario usuarioMock = new Usuario();
    usuarioMock.setId(1L);

    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);

    List<Notificacion> notificaciones = new ArrayList<>();
    notificaciones.add(new Notificacion());
    when(servicioNotificacionMock.obtenerNotificacionesNoLeidas(1L)).thenReturn(notificaciones);

    List<SolicitudAmistad> solicitudes = new ArrayList<>();
    solicitudes.add(new SolicitudAmistad());
    when(servicioSolicitudAmistadMock.obtenerSolicitudesPendientes(usuarioMock))
      .thenReturn(solicitudes);

    // Ejecución
    ModelAndView mav = homeController.irAHome(requestMock);

    // Validación
    Map<String, Object> modelo = mav.getModel();
    assertThat(modelo.get("cantidadNotificaciones"), is(equalTo(1)));
    assertThat(modelo.get("notificaciones"), is(equalTo(notificaciones)));
    assertThat(modelo.get("solicitudesPendientes"), is(equalTo(solicitudes)));
  }
}
