package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.ServicioPartido;
import com.tallerwebi.dominio.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.ModelAndView;

public class CrearPartidoControllerTest {

  private ServicioPartido servicioPartido;
  private CrearPartidoController controlador;
  private MockHttpSession session;

  @BeforeEach
  public void init() {
    servicioPartido = mock(ServicioPartido.class);
    controlador = new CrearPartidoController(servicioPartido);
    session = new MockHttpSession();
  }

  @Test
  public void queAlPedirCrearPartidoDevuelvaLaVistaCrearPartido() {
    ModelAndView mav = controlador.irACrearPartido();
    assertThat(mav.getViewName(), equalTo("crearPartido"));
  }

  @Test
  public void queAlCrearPartidoSinCanchaVuelvaALaVistaConError() {
    Usuario usuario = new Usuario();
    session.setAttribute("USUARIO", usuario);

    DatosCrearPartido datos = new DatosCrearPartido();
    datos.setCancha("");

    ModelAndView mav = controlador.crearPartido(datos, session);

    assertThat(mav.getViewName(), equalToIgnoringCase("crearPartido"));
    assertThat(mav.getModel().get("error").toString(), equalTo("Debe ingresar una cancha"));
  }

  @Test
  public void queAlCrearPartidoExitosamenteRedirijaAPartidos() {
    Usuario usuario = new Usuario();
    session.setAttribute("USUARIO", usuario);

    DatosCrearPartido datos = new DatosCrearPartido();
    datos.setCancha("Cancha 5");

    ModelAndView mav = controlador.crearPartido(datos, session);

    assertThat(mav.getViewName(), equalTo("redirect:/partidos"));
  }
}
