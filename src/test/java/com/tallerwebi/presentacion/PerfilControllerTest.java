package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.Estadistica;
import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.ServicioEstadistica;
import com.tallerwebi.dominio.ServicioJugador;
import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.ServicioRanking;
import com.tallerwebi.dominio.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class PerfilControllerTest {

  private static final String VISTA_PERFIL = "perfil";
  private static final String VISTA_EDITAR = "editar-perfil";
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String REDIRECT_PERFIL = "redirect:/perfil";
  private static final String USUARIO = "USUARIO";

  private PerfilController crearControlador(
    ServicioLogin servicioLogin,
    ServicioJugador servicioJugador,
    ServicioRanking servicioRanking,
    ServicioEstadistica servicioEstadistica
  ) {
    return new PerfilController(
      servicioLogin,
      servicioJugador,
      servicioRanking,
      servicioEstadistica
    );
  }

  private Estadistica crearEstadisticas(
    Integer goles,
    Integer asistencias,
    Integer amarillas,
    Integer rojas,
    Integer puntaje,
    Jugador jugador
  ) {
    return new Estadistica(goles, asistencias, amarillas, rojas, puntaje, jugador, null);
  }

  private void configurarEstadisticasVacias(
    ServicioEstadistica servicioEstadistica,
    Jugador jugador
  ) {
    when(servicioEstadistica.obtenerEstadisticasGenerales(jugador))
      .thenReturn(crearEstadisticas(0, 0, 0, 0, 0, jugador));
  }

  @Test
  public void irAPerfilDeberiaRetornarVistaPerfil() {
    ServicioLogin servicioLogin = mock(ServicioLogin.class);
    ServicioJugador servicioJugador = mock(ServicioJugador.class);
    ServicioRanking servicioRanking = mock(ServicioRanking.class);
    ServicioEstadistica servicioEstadistica = mock(ServicioEstadistica.class);

    PerfilController controlador = crearControlador(
      servicioLogin,
      servicioJugador,
      servicioRanking,
      servicioEstadistica
      //
    );

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);
    Usuario usuario = new Usuario();

    when(request.getSession()).thenReturn(session);
    when(session.getAttribute(USUARIO)).thenReturn(usuario);

    ModelAndView modelAndView = controlador.mostrarFormulario(request);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase(VISTA_PERFIL));
    assertEquals(0, modelAndView.getModel().get("puntos"));
    assertEquals("D", modelAndView.getModel().get("nivel"));
  }

  @Test
  public void irAPerfilDeberiaMostrarPuntosYRangoDelJugador() {
    ServicioLogin servicioLogin = mock(ServicioLogin.class);
    ServicioJugador servicioJugador = mock(ServicioJugador.class);
    ServicioRanking servicioRanking = mock(ServicioRanking.class);
    ServicioEstadistica servicioEstadistica = mock(ServicioEstadistica.class);

    PerfilController controlador = crearControlador(
      servicioLogin,
      servicioJugador,
      servicioRanking,
      servicioEstadistica
    );

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);
    Usuario usuario = new Usuario();
    Jugador jugador = new Jugador();

    jugador.setPuntos(50);

    when(request.getSession()).thenReturn(session);
    when(session.getAttribute(USUARIO)).thenReturn(usuario);
    when(servicioJugador.buscarPorUsuarioId(usuario.getId())).thenReturn(jugador);
    when(servicioRanking.calcularNivel(50)).thenReturn("A");

    configurarEstadisticasVacias(servicioEstadistica, jugador);

    ModelAndView modelAndView = controlador.mostrarFormulario(request);

    assertEquals(50, modelAndView.getModel().get("puntos"));
    assertEquals("A", modelAndView.getModel().get("nivel"));

    verify(servicioRanking).calcularNivel(50);
  }

  @Test
  public void irAPerfilDeberiaMostrarEstadisticasDelJugador() {
    ServicioLogin servicioLogin = mock(ServicioLogin.class);
    ServicioJugador servicioJugador = mock(ServicioJugador.class);
    ServicioRanking servicioRanking = mock(ServicioRanking.class);
    ServicioEstadistica servicioEstadistica = mock(ServicioEstadistica.class);

    PerfilController controlador = crearControlador(
      servicioLogin,
      servicioJugador,
      servicioRanking,
      servicioEstadistica
    );

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);
    Usuario usuario = new Usuario();
    Jugador jugador = new Jugador();

    jugador.setPuntos(80);
    jugador.setPartidosJugados(10);
    jugador.setPartidosGanados(6);
    jugador.setPartidosPerdidos(3);

    when(request.getSession()).thenReturn(session);
    when(session.getAttribute(USUARIO)).thenReturn(usuario);
    when(servicioJugador.buscarPorUsuarioId(usuario.getId())).thenReturn(jugador);
    when(servicioRanking.calcularNivel(80)).thenReturn("A");

    when(servicioEstadistica.obtenerEstadisticasGenerales(jugador))
      .thenReturn(crearEstadisticas(12, 5, 2, 1, 40, jugador));

    ModelAndView modelAndView = controlador.mostrarFormulario(request);

    assertEquals(10, modelAndView.getModel().get("partidosJugados"));
    assertEquals(6, modelAndView.getModel().get("victorias"));
    assertEquals(3, modelAndView.getModel().get("derrotas"));
    assertEquals(12, modelAndView.getModel().get("goles"));
    assertEquals(5, modelAndView.getModel().get("asistencias"));
    assertEquals(2, modelAndView.getModel().get("amarillas"));
    assertEquals(1, modelAndView.getModel().get("rojas"));
    assertEquals(40, modelAndView.getModel().get("puntajeTotal"));

    verify(servicioEstadistica).obtenerEstadisticasGenerales(jugador);
  }

  @Test
  public void irAPerfilSinSesionDeberiaRedirigirALogin() {
    ServicioLogin servicioLogin = mock(ServicioLogin.class);
    ServicioJugador servicioJugador = mock(ServicioJugador.class);
    ServicioRanking servicioRanking = mock(ServicioRanking.class);
    ServicioEstadistica servicioEstadistica = mock(ServicioEstadistica.class);

    PerfilController controlador = crearControlador(
      servicioLogin,
      servicioJugador,
      servicioRanking,
      servicioEstadistica
    );

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);

    when(request.getSession()).thenReturn(session);

    ModelAndView modelAndView = controlador.mostrarFormulario(request);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase(REDIRECT_LOGIN));
  }

  @Test
  public void irAEditarPerfilDeberiaRetornarVistaEditarPerfil() {
    ServicioLogin servicioLogin = mock(ServicioLogin.class);
    ServicioJugador servicioJugador = mock(ServicioJugador.class);
    ServicioRanking servicioRanking = mock(ServicioRanking.class);
    ServicioEstadistica servicioEstadistica = mock(ServicioEstadistica.class);

    PerfilController controlador = crearControlador(
      servicioLogin,
      servicioJugador,
      servicioRanking,
      servicioEstadistica
    );

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
    ServicioRanking servicioRanking = mock(ServicioRanking.class);
    ServicioEstadistica servicioEstadistica = mock(ServicioEstadistica.class);

    PerfilController controlador = crearControlador(
      servicioLogin,
      servicioJugador,
      servicioRanking,
      servicioEstadistica
    );

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
    ServicioRanking servicioRanking = mock(ServicioRanking.class);
    ServicioEstadistica servicioEstadistica = mock(ServicioEstadistica.class);

    PerfilController controlador = crearControlador(
      servicioLogin,
      servicioJugador,
      servicioRanking,
      servicioEstadistica
    );

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
    ServicioRanking servicioRanking = mock(ServicioRanking.class);
    ServicioEstadistica servicioEstadistica = mock(ServicioEstadistica.class);

    PerfilController controlador = crearControlador(
      servicioLogin,
      servicioJugador,
      servicioRanking,
      servicioEstadistica
    );

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
  public void guardarPerfilSinJugadorDeberiaRedirigirAPerfil() {
    ServicioLogin servicioLogin = mock(ServicioLogin.class);
    ServicioJugador servicioJugador = mock(ServicioJugador.class);
    ServicioRanking servicioRanking = mock(ServicioRanking.class);
    ServicioEstadistica servicioEstadistica = mock(ServicioEstadistica.class);

    PerfilController controlador = crearControlador(
      servicioLogin,
      servicioJugador,
      servicioRanking,
      servicioEstadistica
    );

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
    verify(servicioJugador).guardar(org.mockito.ArgumentMatchers.any(Jugador.class));

    assertThat(modelAndView.getViewName(), equalToIgnoringCase(REDIRECT_PERFIL));
  }

  @Test
  public void guardarFotoDeberiaGuardarLaRutaDeLaImagen() throws Exception {
    ServicioLogin servicioLogin = mock(ServicioLogin.class);
    ServicioJugador servicioJugador = mock(ServicioJugador.class);
    ServicioRanking servicioRanking = mock(ServicioRanking.class);
    ServicioEstadistica servicioEstadistica = mock(ServicioEstadistica.class);

    PerfilController controlador = crearControlador(
      servicioLogin,
      servicioJugador,
      servicioRanking,
      servicioEstadistica
    );

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);
    Usuario usuario = new Usuario();
    Jugador jugador = new Jugador();

    // Cabecera mínima de un PNG (más de 12 bytes, como exige la validación del controller)
    byte[] png = new byte[] {
      (byte) 0x89,
      'P',
      'N',
      'G',
      0x0D,
      0x0A,
      0x1A,
      0x0A,
      0,
      0,
      0,
      0,
      0,
      0,
    };
    String fotoBase64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(png);

    when(request.getSession()).thenReturn(session);
    when(session.getAttribute(USUARIO)).thenReturn(usuario);
    when(servicioJugador.buscarPorUsuarioId(usuario.getId())).thenReturn(jugador);

    try {
      ModelAndView modelAndView = controlador.guardarFoto(fotoBase64, 50, 50, request);

      assertThat(jugador.getFoto(), org.hamcrest.Matchers.startsWith("uploads/perfiles/"));
      assertThat(jugador.getFoto(), org.hamcrest.Matchers.endsWith(".png"));
      assertEquals(50, jugador.getFotoPosicionX());
      assertEquals(50, jugador.getFotoPosicionY());

      verify(servicioJugador).modificar(jugador);

      assertThat(modelAndView.getViewName(), equalToIgnoringCase(REDIRECT_PERFIL));
    } finally {
      // El controller escribe el archivo de verdad: se borra para no dejar basura en el proyecto
      if (jugador.getFoto() != null) {
        Files.deleteIfExists(Path.of("src/main/webapp/" + jugador.getFoto()));
      }
    }
  }
}
