package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.Estadistica;
import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.Partido;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.servicio.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class PerfilControllerTest {

  private static final String VISTA_EDITAR = "editar-perfil";
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String REDIRECT_PERFIL = "redirect:/perfil";
  private static final String USUARIO = "USUARIO";

  // Dependencias del controller (si agregás una, la sumás acá y en setUp)
  private ServicioLogin servicioLogin;
  private ServicioJugador servicioJugador;
  private ServicioRanking servicioRanking;
  private ServicioEstadistica servicioEstadistica;
  private ServicioSolicitudAmistad servicioSolicitudAmistad;
  private ServicioPartido servicioPartido;

  private PerfilController controlador;

  // Contexto web y datos comunes
  private HttpServletRequest request;
  private HttpSession session;
  private Usuario usuario;
  private Jugador jugador;

  @BeforeEach
  public void setUp() {
    servicioLogin = mock(ServicioLogin.class);
    servicioJugador = mock(ServicioJugador.class);
    servicioRanking = mock(ServicioRanking.class);
    servicioEstadistica = mock(ServicioEstadistica.class);
    servicioSolicitudAmistad = mock(ServicioSolicitudAmistad.class);
    servicioPartido = mock(ServicioPartido.class);

    controlador =
      new PerfilController(
        servicioLogin,
        servicioJugador,
        servicioRanking,
        servicioEstadistica,
        servicioSolicitudAmistad,
        servicioPartido
      );

    request = mock(HttpServletRequest.class);
    session = mock(HttpSession.class);
    when(request.getSession()).thenReturn(session);

    usuario = new Usuario();
    jugador = new Jugador();
  }

  // ---------- Helpers ----------

  private void iniciarSesion() {
    when(session.getAttribute(USUARIO)).thenReturn(usuario);
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

  private void configurarEstadisticasVacias(Jugador jugador) {
    when(servicioEstadistica.obtenerEstadisticasGenerales(jugador))
      .thenReturn(crearEstadisticas(0, 0, 0, 0, 0, jugador));
  }

  // ---------- Tests: perfil ----------

  @Test
  public void irAPerfilDeberiaRetornarVistaPerfil() {
    usuario.setId(1L);
    jugador.setPuntos(0);
    jugador.setNivel("D");
    jugador.setUsuario(usuario);

    iniciarSesion();
    when(servicioJugador.buscarPorUsuarioId(usuario.getId())).thenReturn(jugador);
    when(servicioRanking.calcularNivel(0)).thenReturn("D");
    configurarEstadisticasVacias(jugador);

    ModelAndView modelAndView = controlador.mostrarFormulario(request);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("PERFIL"));
    assertEquals(0, modelAndView.getModel().get("puntos"));
    assertEquals("D", modelAndView.getModel().get("nivel"));
  }

  @Test
  public void irAPerfilDeberiaMostrarPuntosYRangoDelJugador() {
    jugador.setPuntos(50);

    iniciarSesion();
    when(servicioJugador.buscarPorUsuarioId(usuario.getId())).thenReturn(jugador);
    when(servicioRanking.calcularNivel(50)).thenReturn("A");
    configurarEstadisticasVacias(jugador);

    ModelAndView modelAndView = controlador.mostrarFormulario(request);

    assertEquals(50, modelAndView.getModel().get("puntos"));
    assertEquals("A", modelAndView.getModel().get("nivel"));
    verify(servicioRanking).calcularNivel(50);
  }

  @Test
  public void irAPerfilDeberiaMostrarEstadisticasDelJugador() {
    jugador.setPuntos(80);
    jugador.setPartidosJugados(10);
    jugador.setPartidosGanados(6);
    jugador.setPartidosPerdidos(3);

    iniciarSesion();
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
    ModelAndView modelAndView = controlador.mostrarFormulario(request);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase(REDIRECT_LOGIN));
  }

  @Test
  public void irAPerfilDeberiaMostrarHistorialDePartidos() {
    Partido partido = mock(Partido.class);
    Estadistica estadistica = crearEstadisticas(2, 1, 1, 0, 8, jugador);
    estadistica.setPartido(partido);

    iniciarSesion();
    when(servicioJugador.buscarPorUsuarioId(usuario.getId())).thenReturn(jugador);
    when(servicioRanking.calcularNivel(0)).thenReturn("D");
    configurarEstadisticasVacias(jugador);
    when(servicioEstadistica.obtenerEstadisticasPorJugador(jugador))
      .thenReturn(List.of(estadistica));
    when(servicioPartido.buscarPartidosPorJugador(jugador.getId())).thenReturn(List.of(partido));
    when(partido.getId()).thenReturn(1L);

    ModelAndView modelAndView = controlador.mostrarFormulario(request);

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> historial = (List<Map<String, Object>>) modelAndView
      .getModel()
      .get("historialPartidos");

    assertEquals(1, historial.size());
    assertEquals(2, historial.get(0).get("goles"));
    assertEquals(1, historial.get(0).get("asistencias"));
    assertEquals(1, historial.get(0).get("amarillas"));
    assertEquals(0, historial.get(0).get("rojas"));
    assertEquals(8, historial.get(0).get("puntaje"));
  }

  // ---------- Tests: editar perfil ----------

  @Test
  public void irAEditarPerfilDeberiaRetornarVistaEditarPerfil() {
    iniciarSesion();

    ModelAndView modelAndView = controlador.editarPerfil(request);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase(VISTA_EDITAR));
  }

  @Test
  public void irAEditarPerfilSinSesionDeberiaRedirigirALogin() {
    ModelAndView modelAndView = controlador.editarPerfil(request);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase(REDIRECT_LOGIN));
  }

  // ---------- Tests: guardar perfil ----------

  @Test
  public void guardarPerfilDeberiaActualizarUsuarioYJugador() {
    iniciarSesion();
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
    iniciarSesion();
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
    verify(servicioJugador).guardar(any(Jugador.class));
    assertThat(modelAndView.getViewName(), equalToIgnoringCase(REDIRECT_PERFIL));
  }

  // ---------- Tests: foto ----------

  @Test
  public void guardarFotoDeberiaGuardarLaRutaDeLaImagen() throws Exception {
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

    iniciarSesion();
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
