package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Partido;
import com.tallerwebi.dominio.ServicioPartido;
import com.tallerwebi.dominio.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

public class PartidosControllerTest {

  private PartidosController partidosController;
  private ServicioPartido servicioPartidoMock;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;
  private Model modelMock;

  @BeforeEach
  public void init() {
    servicioPartidoMock = mock(ServicioPartido.class);
    partidosController = new PartidosController(servicioPartidoMock);

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
      FILTRO_TODOS
    );

    // Validación
    thenLaVistaEs("partidos", mav);
    thenModeloKeyEs(listaMock, mav);
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

    when(requestMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuario);

    ModelAndView modelAndView = partidosController.unirseAlPartido(1L, requestMock);

    verify(servicioPartidoMock, times(1)).unirseAPartido(1L, usuario);
    assertThat(
      modelAndView.getViewName(),
      equalToIgnoringCase("redirect:/partido/1/sala-de-espera")
    );
  }

  @Test
  public void unirseAlPartidoConExcepcionDeberiaLanzarError() {
    Usuario usuario = new Usuario();
    usuario.setId(1L);

    when(requestMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuario);

    doThrow(new RuntimeException("Cupo maximo alcanzado"))
      .when(servicioPartidoMock)
      .unirseAPartido(1L, usuario);

    ModelAndView modelAndView = partidosController.unirseAlPartido(1L, requestMock);

    verify(servicioPartidoMock, times(1)).unirseAPartido(1L, usuario);

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
    String distancia
  ) {
    return partidosController.consultarPartidos(
      filtroNivel,
      filtroTipo,
      cupoMaximo,
      fecha,
      filtroHora,
      distancia
    );
  }
}
