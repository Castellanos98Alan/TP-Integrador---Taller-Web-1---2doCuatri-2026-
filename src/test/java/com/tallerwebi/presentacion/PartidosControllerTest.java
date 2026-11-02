package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.Partido;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.servicio.ServicioBalanceoEquipo;
import com.tallerwebi.dominio.servicio.ServicioJugador;
import com.tallerwebi.dominio.servicio.ServicioPartido;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

public class PartidosControllerTest {

  private PartidosController partidosController;
  private ServicioPartido servicioPartidoMock;
  private ServicioJugador servicioJugadorMock;
  private ServicioBalanceoEquipo servicioBalanceoEquipoMock;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;
  private Model modelMock;

  @BeforeEach
  public void init() {
    servicioPartidoMock = mock(ServicioPartido.class);
    servicioJugadorMock = mock(ServicioJugador.class);
    partidosController = new PartidosController(servicioPartidoMock, servicioJugadorMock);

    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);
    modelMock = mock(Model.class);
  }

  public static final String FILTRO_TODOS = "TODOS";

  @Test
  public void deberiaRetornarLaVistaPartidosYElModeloConLaListaDePartidos() {
    List<Partido> listaMock = new ArrayList<>();
    when(
      servicioPartidoMock.listarPartidosSegunFiltro(
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
    when(servicioPartidoMock.obtenerProvinciasDisponibles())
      .thenReturn(Arrays.asList("Buenos Aires"));
    when(servicioPartidoMock.obtenerLocalidadesDisponibles())
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

    verify(servicioPartidoMock, times(1)).obtenerProvinciasDisponibles();
    verify(servicioPartidoMock, times(1)).obtenerLocalidadesDisponibles();
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
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/partidos"));
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

    // Configuración correcta del mock para que devuelva un jugador válido
    when(servicioJugadorMock.buscarPorUsuarioId(usuario.getId())).thenReturn(jugador);

    doThrow(new RuntimeException("Cupo maximo alcanzado"))
      .when(servicioPartidoMock)
      .unirseAPartido(1L, jugador);

    ModelAndView modelAndView = partidosController.unirseAlPartido(1L, requestMock);

    verify(servicioPartidoMock, times(1)).unirseAPartido(1L, jugador);
    assertThat(
      modelAndView.getViewName(),
      equalToIgnoringCase("redirect:/partido/1/sala-de-espera")
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
  public void expulsarJugadorConErrorGuardaErrorEnSesionYRedirige() {
    Usuario noOrganizador = new Usuario();
    noOrganizador.setId(1L);

    when(requestMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getAttribute("USUARIO")).thenReturn(noOrganizador);

    doThrow(new RuntimeException("Solo el organizador puede expulsar jugadores."))
      .when(servicioPartidoMock)
      .expulsarJugador(1L, 2L, noOrganizador);

    ModelAndView mav = partidosController.expulsarJugador(1L, 2L, requestMock);

    verify(sessionMock, times(1)).setAttribute(eq("error"), anyString());
    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/partido/1/sala-de-espera"));
  }
}
