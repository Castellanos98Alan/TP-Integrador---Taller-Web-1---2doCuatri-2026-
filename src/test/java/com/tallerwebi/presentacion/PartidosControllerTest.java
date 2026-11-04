package com.tallerwebi.presentacion;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Estadistica;
import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.Partido;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.CupoMaximoAlcanzado;
import com.tallerwebi.dominio.excepcion.RevisionEstadisticasNoPermitida;
import com.tallerwebi.dominio.servicio.ServicioBalanceoEquipo;
import com.tallerwebi.dominio.servicio.ServicioEstadistica;
import com.tallerwebi.dominio.servicio.ServicioJugador;
import com.tallerwebi.dominio.servicio.ServicioPartido;
import com.tallerwebi.dominio.servicio.ServicioPartidoFiltro;
import com.tallerwebi.dominio.servicio.ServicioSolicitudAmistad;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

public class PartidosControllerTest {

  private PartidosController partidosController;
  private ServicioPartido servicioPartidoMock;
  private ServicioPartidoFiltro servicioPartidoFiltroMock;
  private ServicioJugador servicioJugadorMock;
  private ServicioBalanceoEquipo servicioBalanceoEquipoMock;
  private ServicioSolicitudAmistad servicioSolicitudAmistadMock;
  private ServicioEstadistica servicioEstadisticaMock;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;
  private Model modelMock;

  @BeforeEach
  public void init() {
    servicioPartidoMock = mock(ServicioPartido.class);
    servicioPartidoFiltroMock = mock(ServicioPartidoFiltro.class);
    servicioJugadorMock = mock(ServicioJugador.class);
    servicioSolicitudAmistadMock = mock(ServicioSolicitudAmistad.class);
    servicioEstadisticaMock = mock(ServicioEstadistica.class);

    partidosController =
      new PartidosController(
        servicioPartidoMock,
        servicioPartidoFiltroMock,
        servicioJugadorMock,
        servicioSolicitudAmistadMock,
        servicioEstadisticaMock
      );

    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);
    modelMock = mock(Model.class);
  }

  public static final String FILTRO_TODOS = "TODOS";

  @Test
  public void deberiaRetornarLaVistaPartidosYElModeloConLaListaDePartidos() {
    List<Partido> listaMock = new ArrayList<>();
    when(
      servicioPartidoFiltroMock.listarPartidosSegunFiltro(
        FILTRO_TODOS,
        FILTRO_TODOS,
        FILTRO_TODOS,
        FILTRO_TODOS,
        FILTRO_TODOS,
        FILTRO_TODOS,
        FILTRO_TODOS
      )
    )
      .thenReturn(listaMock);

    // Ejecución
    ModelAndView mav = whenUsuarioConsultaPartidos(
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS
    );

    // Validación
    thenLaVistaEs("partidos", mav);
    thenModeloKeyEs(listaMock, mav);
  }

  @Test
  public void queElControladorCargueLasProvinciasYLocalidadesAlMostrarLaVista() {
    // Preparación
    when(servicioPartidoFiltroMock.obtenerProvinciasDisponibles())
      .thenReturn(Arrays.asList("Buenos Aires"));
    when(servicioPartidoFiltroMock.obtenerLocalidadesDisponibles())
      .thenReturn(Arrays.asList("La Matanza"));

    // Ejecución (Ajustá los parámetros según la firma actual de tu método en el controlador)
    ModelAndView mav = whenUsuarioConsultaPartidos(
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      "Buenos Aires",
      "La Matanza"
    );

    // Validación
    ModelMap modelo = mav.getModelMap();
    assertThat(modelo.get("provinciasDisponibles"), notNullValue());
    assertThat(modelo.get("localidadesDisponibles"), notNullValue());
    assertThat(modelo.get("provinciaSeleccionada"), equalTo("Buenos Aires"));
    assertThat(modelo.get("localidadSeleccionada"), equalTo("La Matanza"));

    verify(servicioPartidoFiltroMock, times(1)).obtenerProvinciasDisponibles();
    verify(servicioPartidoFiltroMock, times(1)).obtenerLocalidadesDisponibles();
  }

  @Test
  public void queAlCancelarUnPartidoExitosamenteRedirijaALaVistaDePartidos() {
    Long idPartido = 5L;
    Usuario usuarioLogueado = new Usuario();
    usuarioLogueado.setId(1L);

    when(requestMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioLogueado);

    ModelAndView modelAndView = partidosController.cancelarPartido(
      idPartido,
      modelMock,
      requestMock
    );

    verify(servicioPartidoMock, times(1)).cancelarPartido(idPartido, usuarioLogueado);
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/partido/"));
  }

  @Test
  public void SiNoHayUsuarioLogueadoAlIntentarCancelarRedirijaAlLogin() {
    Long idPartido = 5L;

    when(requestMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getAttribute("USUARIO")).thenReturn(null);

    ModelAndView modelAndView = partidosController.cancelarPartido(
      idPartido,
      modelMock,
      requestMock
    );

    verify(servicioPartidoMock, never()).cancelarPartido(anyLong(), any());
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));
  }

  @Test
  public void unirseAlPartidoSinSesionDeberiaRedirigirAlLogin() {
    when(requestMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getAttribute("USUARIO")).thenReturn(null);

    ModelAndView modelAndView = partidosController.unirseAlPartido(1L, requestMock);

    assertThat(
      modelAndView.getViewName(),
      equalToIgnoringCase("redirect:/login?error=Debes+iniciar+sesion+para+unirte")
    );
  }

  @Test
  public void unirseAlPartidoConSesionActivaDeberiaRedirigirASalaDeEspera() {
    Usuario usuario = new Usuario();
    usuario.setId(1L);
    Jugador jugador = new Jugador();
    jugador.setId(1L);

    when(requestMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuario);

    // Configuración correcta del mock para que devuelva un jugador válido
    when(servicioJugadorMock.buscarPorUsuarioId(usuario.getId())).thenReturn(jugador);

    ModelAndView modelAndView = partidosController.unirseAlPartido(1L, requestMock);

    verify(servicioPartidoMock, times(1)).unirseAPartido(1L, jugador);
    assertThat(
      modelAndView.getViewName(),
      equalToIgnoringCase("redirect:/partido/1/sala-de-espera")
    );
  }

  @Test
  public void unirseAlPartidoConExcepcionDeberiaLanzarError() {
    Usuario usuario = new Usuario();
    usuario.setId(1L);
    Jugador jugador = new Jugador();
    jugador.setId(1L);

    when(requestMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuario);
    when(servicioJugadorMock.buscarPorUsuarioId(usuario.getId())).thenReturn(jugador);

    // Configuración correcta del mock para que devuelva un jugador válido
    when(servicioJugadorMock.buscarPorUsuarioId(usuario.getId())).thenReturn(jugador);

    // Simulamos que el servicio lanza la excepción de negocio cuando intenta unirse
    doThrow(new CupoMaximoAlcanzado("Cupo maximo alcanzado"))
      .when(servicioPartidoMock)
      .unirseAPartido(1L, jugador);

    ModelAndView modelAndView = partidosController.unirseAlPartido(1L, requestMock);

    verify(servicioPartidoMock, times(1)).unirseAPartido(1L, jugador);

    // Verificamos que ahora el controlador redirige a la sala de espera con el error en la URL
    assertThat(
      modelAndView.getViewName(),
      equalToIgnoringCase("redirect:/partido/1/sala-de-espera?error=Cupo+maximo+alcanzado")
    );
  }

  private void thenLaVistaEs(String vistaEsperada, ModelAndView mav) {
    assertThat(vistaEsperada, equalTo(mav.getViewName()));
  }

  private void thenModeloKeyEs(List<Partido> listaMock, ModelAndView mav) {
    assertThat(mav.getModel().get("listaPartidos"), equalTo(listaMock));
  }

  private ModelAndView whenUsuarioConsultaPartidos(
    String filtroNivel,
    String filtroTipo,
    String cupoMaximo,
    String fecha,
    String filtroHora,
    String filtroPartido,
    String filtroLocalidad
  ) {
    return partidosController.consultarPartidos(
      filtroNivel,
      filtroTipo,
      cupoMaximo,
      fecha,
      filtroHora,
      filtroPartido,
      filtroLocalidad
    );
  }

  @Test
  public void abandonarPartidoSinSesionRedirigeALogin() {
    when(requestMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getAttribute("USUARIO")).thenReturn(null);

    ModelAndView mav = partidosController.abandonarPartido(1L, requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/login"));
    verify(servicioPartidoMock, never()).abandonarPartido(anyLong(), any());
  }

  @Test
  public void abandonarPartidoConExitoRedirigeAPartidos() {
    Usuario usuario = new Usuario();
    usuario.setId(1L);
    Jugador jugador = new Jugador();

    when(requestMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuario);
    when(servicioJugadorMock.buscarPorUsuarioId(usuario.getId())).thenReturn(jugador);

    ModelAndView mav = partidosController.abandonarPartido(1L, requestMock);

    verify(servicioPartidoMock, times(1)).abandonarPartido(1L, jugador);
    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/partidos"));
  }

  @Test
  public void abandonarPartidoConErrorRedirigeASalaDeEspera() {
    Usuario usuario = new Usuario();
    usuario.setId(1L);
    Jugador jugador = new Jugador();

    when(requestMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuario);
    when(servicioJugadorMock.buscarPorUsuarioId(usuario.getId())).thenReturn(jugador);

    doThrow(new RuntimeException("No estás inscripto en este partido."))
      .when(servicioPartidoMock)
      .abandonarPartido(1L, jugador);

    ModelAndView mav = partidosController.abandonarPartido(1L, requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/partido/1/sala-de-espera"));
    assertThat(mav.getModel().get("error"), notNullValue());
  }

  @Test
  public void expulsarJugadorSinSesionRedirigeALogin() {
    when(requestMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getAttribute("USUARIO")).thenReturn(null);

    ModelAndView mav = partidosController.expulsarJugador(1L, 2L, requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/login"));
    verify(servicioPartidoMock, never()).expulsarJugador(anyLong(), anyLong(), any());
  }

  @Test
  public void expulsarJugadorConExitoRedirigeASalaDeEspera() {
    Usuario organizador = new Usuario();
    organizador.setId(1L);

    when(requestMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getAttribute("USUARIO")).thenReturn(organizador);

    ModelAndView mav = partidosController.expulsarJugador(1L, 2L, requestMock);

    verify(servicioPartidoMock, times(1)).expulsarJugador(1L, 2L, organizador);
    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/partido/1/sala-de-espera"));
  }

  @Test
  public void expulsarJugadorConErrorGuardaErrorEnModelYRedirige() {
    Long idPartido = 1L;
    Long idJugador = 2L;
    Usuario usuarioLogueado = new Usuario();

    when(requestMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioLogueado);

    doThrow(new RuntimeException("Error al expulsar"))
      .when(servicioPartidoMock)
      .expulsarJugador(idPartido, idJugador, usuarioLogueado);

    ModelAndView mav = partidosController.expulsarJugador(idPartido, idJugador, requestMock);

    // Verificamos que el error ahora viaja en el Model y no en la sesión
    assertEquals("redirect:/partido/" + idPartido + "/sala-de-espera", mav.getViewName());
    assertEquals("Error al expulsar", mav.getModel().get("error"));
  }

  // =========================================================================
// TESTS PARA verVistaCargarEstadisticas
// =========================================================================

  @Test
  public void verVistaCargarEstadisticasExitosoDeberiaRetornarVistaCargarEstadisticasYModelo() {
    // PREPARACIÓN
    Long idPartido = 1L;
    Usuario usuarioLogueado = mock(Usuario.class);
    Partido partidoMock = mock(Partido.class);

    when(requestMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioLogueado);
    when(servicioPartidoMock.buscarPartidoPorId(idPartido)).thenReturn(partidoMock);
    when(servicioEstadisticaMock.puedeCargarEstadisticas(partidoMock, usuarioLogueado)).thenReturn(true);

    // EJECUCIÓN
    ModelAndView modelAndView = partidosController.verVistaCargarEstadisticas(idPartido, requestMock);

    // VERIFICACIÓN
    assertEquals("cargar-estadisticas", modelAndView.getViewName());
    assertEquals(partidoMock, modelAndView.getModel().get("partido"));
    assertNotNull(modelAndView.getModel().get("formulario"));
    verify(servicioEstadisticaMock, times(1)).puedeCargarEstadisticas(partidoMock, usuarioLogueado);
  }

  @Test
  public void verVistaCargarEstadisticasCuandoUsuarioNoEstaAutorizadoDeberiaRedirigirASalaDeEspera() {
    // PREPARACIÓN
    Long idPartido = 1L;
    Usuario usuarioLogueado = mock(Usuario.class);
    Partido partidoMock = mock(Partido.class);

    when(requestMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioLogueado);
    when(servicioPartidoMock.buscarPartidoPorId(idPartido)).thenReturn(partidoMock);
    when(servicioEstadisticaMock.puedeCargarEstadisticas(partidoMock, usuarioLogueado)).thenReturn(false);

    // EJECUCIÓN
    ModelAndView modelAndView = partidosController.verVistaCargarEstadisticas(idPartido, requestMock);

    // VERIFICACIÓN
    String viewNameEsperado = "redirect:/partido/" + idPartido + "/sala-de-espera?error=No+estás+autorizado+para+cargar+estadísticas+aún";
    assertEquals(viewNameEsperado, modelAndView.getViewName());
  }


  @Test
  public void verPantallaRevisionEstadisticasExitosoDeberiaRetornarVistaRevisarEstadisticasYDatosSeparados() {
    // PREPARACIÓN
    Long idPartido = 1L;
    Usuario usuarioLogueado = mock(Usuario.class);
    Partido partidoMock = mock(Partido.class);
    List<Estadistica> estadisticasMock = List.of(mock(Estadistica.class));

    Map<String, Integer> marcadorMock = Map.of("local", 2, "visitante", 1);
    Map<String, List<Estadistica>> estadisticasSeparadasMock = Map.of(
            "locales", List.of(mock(Estadistica.class)),
            "visitantes", List.of(mock(Estadistica.class))
    );

    when(requestMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioLogueado);
    when(servicioPartidoMock.buscarPartidoPorId(idPartido)).thenReturn(partidoMock);
    when(servicioEstadisticaMock.puedeRevisarEstadisticas(partidoMock, usuarioLogueado)).thenReturn(true);
    when(servicioEstadisticaMock.obtenerEstadisticasPorPartido(partidoMock)).thenReturn(estadisticasMock);
    when(servicioEstadisticaMock.calcularMarcadorPartido(partidoMock, estadisticasMock)).thenReturn(marcadorMock);
    when(servicioEstadisticaMock.obtenerEstadisticasSeparadasPorEquipo(partidoMock, estadisticasMock)).thenReturn(estadisticasSeparadasMock);

    // EJECUCIÓN
    ModelAndView modelAndView = partidosController.verPantallaRevisionEstadisticas(idPartido, requestMock);

    // VERIFICACIÓN
    assertEquals("revisar-estadisticas", modelAndView.getViewName());
    assertEquals(partidoMock, modelAndView.getModel().get("partido"));
    assertEquals(2, modelAndView.getModel().get("golesLocal"));
    assertEquals(1, modelAndView.getModel().get("golesVisitante"));
    assertNotNull(modelAndView.getModel().get("estadisticasLocal"));
    assertNotNull(modelAndView.getModel().get("estadisticasVisitante"));
  }

  @Test
  public void verPantallaRevisionEstadisticasQuandoLanzaExcepcionRevisionNoPermitidaDeberiaRedirigirAPartidosConError() throws Exception {
    // PREPARACIÓN
    Long idPartido = 1L;
    Usuario usuarioLogueado = mock(Usuario.class);

    when(requestMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioLogueado);
    when(servicioPartidoMock.buscarPartidoPorId(idPartido)).thenThrow(new RevisionEstadisticasNoPermitida("Error de revisión"));

    // EJECUCIÓN
    ModelAndView modelAndView = partidosController.verPantallaRevisionEstadisticas(idPartido, requestMock);

    // VERIFICACIÓN
    assertEquals("redirect:/partidos?error=No puedes revisar estas estadisticas", modelAndView.getViewName());
  }
}
