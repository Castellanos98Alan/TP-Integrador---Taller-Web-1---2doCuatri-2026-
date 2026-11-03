package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.Partido;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.CupoMaximoAlcanzado;
import com.tallerwebi.dominio.servicio.ServicioBalanceoEquipo;
import com.tallerwebi.dominio.servicio.ServicioEstadistica;
import com.tallerwebi.dominio.servicio.ServicioJugador;
import com.tallerwebi.dominio.servicio.ServicioPartido;
import com.tallerwebi.dominio.servicio.ServicioSolicitudAmistad;
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
  private ServicioSolicitudAmistad servicioSolicitudAmistadMock;
  private ServicioEstadistica servicioEstadisticaMock;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;
  private Model modelMock;

  @BeforeEach
  public void init() {
    servicioPartidoMock = mock(ServicioPartido.class);
    servicioJugadorMock = mock(ServicioJugador.class);
    servicioSolicitudAmistadMock = mock(ServicioSolicitudAmistad.class);
    servicioEstadisticaMock = mock(ServicioEstadistica.class);

    partidosController =
      new PartidosController(
        servicioPartidoMock,
        servicioJugadorMock,
        servicioSolicitudAmistadMock,
        servicioEstadisticaMock
      );

    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);
    modelMock = mock(Model.class);
  }

  public static final String FILTRO_TODOS = "TODOS";

  //  @Test
  //  public void deberiaRetornarLaVistaPartidosYElModeloConLaListaDePartidos() {
  //    List<Partido> listaMock = new ArrayList<>();
  //    when(
  //      servicioPartidoMock.listarPartidosSegunFiltro(
  //        FILTRO_TODOS,
  //        FILTRO_TODOS,
  //        FILTRO_TODOS,
  //        FILTRO_TODOS,
  //        FILTRO_TODOS,
  //        FILTRO_TODOS,
  //        FILTRO_TODOS
  //      )
  //    )
  //      .thenReturn(listaMock);
  //
  //    // Ejecución
  //    ModelAndView mav = whenUsuarioConsultaPartidos(
  //      FILTRO_TODOS,
  //      FILTRO_TODOS,
  //      FILTRO_TODOS,
  //      FILTRO_TODOS,
  //      FILTRO_TODOS,
  //      FILTRO_TODOS,
  //      FILTRO_TODOS
  //    );
  //
  //    // Validación
  //    thenLaVistaEs("partidos", mav);
  //    thenModeloKeyEs(listaMock, mav);
  //  }
  //
  //  @Test
  //  public void queElControladorCargueLasProvinciasYLocalidadesAlMostrarLaVista() {
  //    // Preparación
  //    when(servicioPartidoMock.obtenerProvinciasDisponibles())
  //      .thenReturn(Arrays.asList("Buenos Aires"));
  //    when(servicioPartidoMock.obtenerLocalidadesDisponibles())
  //      .thenReturn(Arrays.asList("La Matanza"));
  //
  //    // Ejecución (Ajustá los parámetros según la firma actual de tu método en el controlador)
  //    ModelAndView mav = whenUsuarioConsultaPartidos(
  //      FILTRO_TODOS,
  //      FILTRO_TODOS,
  //      FILTRO_TODOS,
  //      FILTRO_TODOS,
  //      FILTRO_TODOS,
  //      "Buenos Aires",
  //      "La Matanza"
  //    );
  //
  //    // Validación
  //    ModelMap modelo = mav.getModelMap();
  //    assertThat(modelo.get("provinciasDisponibles"), notNullValue());
  //    assertThat(modelo.get("localidadesDisponibles"), notNullValue());
  //    assertThat(modelo.get("provinciaSeleccionada"), equalTo("Buenos Aires"));
  //    assertThat(modelo.get("localidadSeleccionada"), equalTo("La Matanza"));
  //
  //    verify(servicioPartidoMock, times(1)).obtenerProvinciasDisponibles();
  //    verify(servicioPartidoMock, times(1)).obtenerLocalidadesDisponibles();
  //  }

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
    Jugador jugador = servicioJugadorMock.buscarPorUsuarioId(usuario.getId());

    when(requestMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuario);

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
  //  private ModelAndView whenUsuarioConsultaPartidos(
  //    String filtroNivel,
  //    String filtroTipo,
  //    String cupoMaximo,
  //    String fecha,
  //    String filtroHora,
  //    String filtroPartido,
  //    String filtroLocalidad
  //  ) {
  //    return partidosController.consultarPartidos(
  //      filtroNivel,
  //      filtroTipo,
  //      cupoMaximo,
  //      fecha,
  //      filtroHora,
  //      filtroPartido,
  //      filtroLocalidad
  //    );
  //  }
}
