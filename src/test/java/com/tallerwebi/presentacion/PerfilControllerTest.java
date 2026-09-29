package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class PerfilControllerTest {

  private static final String VISTA_PERFIL = "perfil";
  private static final String VISTA_EDITAR = "editar-perfil";
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String REDIRECT_PERFIL = "redirect:/perfil";

  private static final String USUARIO = "USUARIO";
  private static final String NOMBRE = "nombre";
  private static final String POSICION = "posicion";
  private static final String ZONA = "zona";
  private static final String BIOGRAFIA = "biografia";

  @Test
  public void irAPerfilDeberiaRetornarVisitarPerfil() {
    PerfilController controlador = new PerfilController();

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);
    Usuario usuario = new Usuario();

    when(request.getSession()).thenReturn(session);
    when(session.getAttribute(USUARIO)).thenReturn(usuario);

    ModelAndView modelAndView = controlador.mostrarFormulario(request);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase(VISTA_PERFIL));
  }

  @Test
  public void irAPerfilSinSesionDeberiaRedirigirALogin() {
    PerfilController controlador = new PerfilController();

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);

    when(request.getSession()).thenReturn(session);

    ModelAndView modelAndView = controlador.mostrarFormulario(request);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase(REDIRECT_LOGIN));
  }

  @Test
  public void irAEditarPerfilDeberiaRetornarVisitarEditarPerfil() {
    PerfilController controlador = new PerfilController();

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);
    Usuario usuario = new Usuario();

    when(request.getSession()).thenReturn(session);
    when(session.getAttribute(USUARIO)).thenReturn(usuario);

    ModelAndView modelAndView = controlador.editarPerfil(request);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase(VISTA_EDITAR));
  }

  @Test
  public void irAEditarPerfilSinSesionDeberiaRedirigirALogin() {
    PerfilController controlador = new PerfilController();

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);

    when(request.getSession()).thenReturn(session);

    ModelAndView modelAndView = controlador.editarPerfil(request);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase(REDIRECT_LOGIN));
  }

  @Test
  public void guardarPerfilDeberiaGuardarDatosEnSesionYRedirigirAPerfil() {
    PerfilController controlador = new PerfilController();

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);
    Usuario usuario = new Usuario();

    when(request.getSession()).thenReturn(session);
    when(session.getAttribute(USUARIO)).thenReturn(usuario);

    ModelAndView modelAndView = controlador.guardarPerfil(
      "Santiago",
      "delantero",
      "Lafe",
      "Naci para este deporte...",
      request
    );

    verify(session).setAttribute(NOMBRE, "Santiago");
    verify(session).setAttribute(POSICION, "delantero");
    verify(session).setAttribute(ZONA, "Lafe");
    verify(session).setAttribute(BIOGRAFIA, "Naci para este deporte...");

    assertThat(modelAndView.getViewName(), equalToIgnoringCase(REDIRECT_PERFIL));
  }

  @Test
  public void guardarPerfilSinSesionDeberiaRedirigirALogin() {
    PerfilController controlador = new PerfilController();

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);

    when(request.getSession()).thenReturn(session);

    ModelAndView modelAndView = controlador.guardarPerfil(
      "Santiago",
      "delantero",
      "Lafe",
      "Naci para este deporte...",
      request
    );

    assertThat(modelAndView.getViewName(), equalToIgnoringCase(REDIRECT_LOGIN));
  }
}
