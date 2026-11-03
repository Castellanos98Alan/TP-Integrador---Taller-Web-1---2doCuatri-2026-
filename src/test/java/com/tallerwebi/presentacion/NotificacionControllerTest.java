package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.servicio.ServicioNotificacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class NotificacionControllerTest {

  private NotificacionController notificacionController;
  private ServicioNotificacion servicioNotificacionMock;

  @BeforeEach
  public void init() {
    this.servicioNotificacionMock = mock(ServicioNotificacion.class);

    this.notificacionController = new NotificacionController(this.servicioNotificacionMock);
  }

  @Test
  public void alAbrirUnaNotificacionDebeMarcarlaComoLeidaYRedirigirAlPartido() {
    // Given

    Long idNotificacion = 1L;
    Long idPartido = 10L;

    // When

    String volverA = "/partido/10";
    ModelAndView modelAndView = notificacionController.marcarComoLeida(idNotificacion, volverA);

    // Then

    verify(servicioNotificacionMock, times(1)).marcarComoLeida(idNotificacion);

    assertThat(modelAndView.getViewName(), equalTo("redirect:/partido/10"));
  }

  @Test
  public void alMarcarComoLeidaDebeVolverALaUrlIndicada() {
    // Given

    Long idNotificacion = 1L;
    String volverA = "/perfil";

    // When

    ModelAndView modelAndView = notificacionController.marcarComoLeida(idNotificacion, volverA);

    // Then

    verify(servicioNotificacionMock, times(1)).marcarComoLeida(idNotificacion);

    assertThat(modelAndView.getViewName(), equalTo("redirect:/perfil"));
  }
}
