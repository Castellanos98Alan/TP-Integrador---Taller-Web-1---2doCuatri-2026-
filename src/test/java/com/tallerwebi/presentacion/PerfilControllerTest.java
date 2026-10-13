package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.ServicioJugador;
import com.tallerwebi.dominio.ServicioLogin;
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
  private static final String REDIRECT_EDITAR = "redirect:/editar-perfil";
  private static final String USUARIO = "USUARIO";

  @Test
  public void irAPerfilDeberiaRetornarVisitarPerfil() {
    ServicioLogin servicioLogin = mock(ServicioLogin.class);
    ServicioJugador servicioJugador = mock(ServicioJugador.class);
    PerfilController controlador = new PerfilController(servicioLogin, servicioJugador);

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
    ServicioLogin servicioLogin = mock(ServicioLogin.class);
    ServicioJugador servicioJugador = mock(ServicioJugador.class);
    PerfilController controlador = new PerfilController(servicioLogin, servicioJugador);

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);

    when(request.getSession()).thenReturn(session);

    ModelAndView modelAndView = controlador.mostrarFormulario(request);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase(REDIRECT_LOGIN));
  }

  @Test
  public void irAEditarPerfilDeberiaRetornarVisitarEditarPerfil() {
    ServicioLogin servicioLogin = mock(ServicioLogin.class);
    ServicioJugador servicioJugador = mock(ServicioJugador.class);
    PerfilController controlador = new PerfilController(servicioLogin, servicioJugador);

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
    ServicioLogin servicioLogin = mock(ServicioLogin.class);
    ServicioJugador servicioJugador = mock(ServicioJugador.class);
    PerfilController controlador = new PerfilController(servicioLogin, servicioJugador);

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);

    when(request.getSession()).thenReturn(session);

    ModelAndView modelAndView = controlador.editarPerfil(request);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase(REDIRECT_LOGIN));
  }

  @Test
  public void guardarPerfilDeberiaActualizarUsuarioYJugador() {
    ServicioLogin servicioLogin = mock(ServicioLogin.class);
    ServicioJugador servicioJugador = mock(ServicioJugador.class);
    PerfilController controlador = new PerfilController(servicioLogin, servicioJugador);

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);
    Usuario usuario = new Usuario();
    Jugador jugador = new Jugador();

    when(request.getSession()).thenReturn(session);
    when(session.getAttribute(USUARIO)).thenReturn(usuario);
    when(servicioJugador.buscarPorUsuarioId(usuario.getId())).thenReturn(jugador);

    ModelAndView modelAndView = controlador.guardarPerfil(
      "Santiago",
      "delantero",
      "arquero",
      "Lafe",
      "Naci para este deporte",
      request
    );

    verify(servicioLogin).modificar(usuario);
    verify(servicioJugador).modificar(jugador);

    assertThat(usuario.getNickname(), equalToIgnoringCase("Santiago"));
    assertThat(usuario.getZona(), equalToIgnoringCase("Lafe"));
    assertThat(usuario.getBiografia(), equalToIgnoringCase("Naci para este deporte"));

    assertThat(jugador.getPosicion1(), equalToIgnoringCase("delantero"));
    assertThat(jugador.getPosicion2(), equalToIgnoringCase("arquero"));

    assertThat(modelAndView.getViewName(), equalToIgnoringCase(REDIRECT_PERFIL));
  }

  @Test
  public void guardarPerfilSinSesionDeberiaRedirigirALogin() {
    ServicioLogin servicioLogin = mock(ServicioLogin.class);
    ServicioJugador servicioJugador = mock(ServicioJugador.class);
    PerfilController controlador = new PerfilController(servicioLogin, servicioJugador);

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);

    when(request.getSession()).thenReturn(session);

    ModelAndView modelAndView = controlador.guardarPerfil(
      "Santiago",
      "delantero",
      "arquero",
      "Lafe",
      "Naci para este deporte",
      request
    );

    assertThat(modelAndView.getViewName(), equalToIgnoringCase(REDIRECT_LOGIN));
  }

  @Test
  public void guardarPerfilSinJugadorDeberiaRedirigirAEditarPerfil() {
    ServicioLogin servicioLogin = mock(ServicioLogin.class);
    ServicioJugador servicioJugador = mock(ServicioJugador.class);
    PerfilController controlador = new PerfilController(servicioLogin, servicioJugador);

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);
    Usuario usuario = new Usuario();

    when(request.getSession()).thenReturn(session);
    when(session.getAttribute(USUARIO)).thenReturn(usuario);
    when(servicioJugador.buscarPorUsuarioId(usuario.getId())).thenReturn(null);

    ModelAndView modelAndView = controlador.guardarPerfil(
      "Santiago",
      "delantero",
      "arquero",
      "Lafe",
      "Naci para este deporte",
      request
    );

    verify(servicioLogin).modificar(usuario);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase(REDIRECT_EDITAR));
  }
}
