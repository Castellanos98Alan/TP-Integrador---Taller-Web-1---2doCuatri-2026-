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
    // When
    ModelAndView mav = whenSePideIrACrearPartido();

    // Then
    thenLaVistaEs(mav, "crearPartido");
  }

  @Test
  public void queAlCrearPartidoSinCanchaVuelvaALaVistaConError() {
    // Given
    givenExisteUnUsuarioEnSesion();
    DatosCrearPartido datosSinCancha = givenDatosCrearPartidoConCancha("");

    // When
    ModelAndView mav = whenSeIntentaCrearPartido(datosSinCancha);

    // Then
    thenLaVistaEsIgnorandoMayusculas(mav, "crearPartido");
    thenElMensajeDeErrorEs(mav, "Debe ingresar una cancha");
  }

  @Test
  public void queAlCrearPartidoExitosamenteRedirijaAPartidos() {
    // Given
    givenExisteUnUsuarioEnSesion();
    DatosCrearPartido datosValidos = givenDatosCrearPartidoConCancha("Cancha 5");

    // When
    ModelAndView mav = whenSeIntentaCrearPartido(datosValidos);

    // Then
    thenLaVistaEs(mav, "redirect:/partidos");
  }


  

  // Métodos auxiliares: Given

  private void givenExisteUnUsuarioEnSesion() {
    Usuario usuario = new Usuario();
    session.setAttribute("USUARIO", usuario);
  }

  private DatosCrearPartido givenDatosCrearPartidoConCancha(String nombreCancha) {
    DatosCrearPartido datos = new DatosCrearPartido();
    datos.setCancha(nombreCancha);
    return datos;
  }

  // Métodos auxiliares: When

  private ModelAndView whenSePideIrACrearPartido() {
    return controlador.irACrearPartido();
  }

  private ModelAndView whenSeIntentaCrearPartido(DatosCrearPartido datos) {
    return controlador.crearPartido(datos, session);
  }


  // Métodos auxiliares: Then

  private void thenLaVistaEs(ModelAndView mav, String vistaEsperada) {
    assertThat(mav.getViewName(), equalTo(vistaEsperada));
  }

  private void thenLaVistaEsIgnorandoMayusculas(ModelAndView mav, String vistaEsperada) {
    assertThat(mav.getViewName(), equalToIgnoringCase(vistaEsperada));
  }

  private void thenElMensajeDeErrorEs(ModelAndView mav, String mensajeEsperado) {
    assertThat(mav.getModel().get("error").toString(), equalTo(mensajeEsperado));
  }
}