package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.servicio.ServicioLogin;
import com.tallerwebi.dominio.servicio.ServicioSolicitudAmistad;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class SolicitudAmistadControllerTest {

  private SolicitudAmistadController solicitudAmistadController;

  private ServicioSolicitudAmistad servicioSolicitudAmistadMock;
  private ServicioLogin servicioLoginMock;

  @BeforeEach
  public void init() {
    servicioSolicitudAmistadMock = mock(ServicioSolicitudAmistad.class);

    servicioLoginMock = mock(ServicioLogin.class);

    solicitudAmistadController =
      new SolicitudAmistadController(servicioSolicitudAmistadMock, servicioLoginMock);
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
}
