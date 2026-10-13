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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
  private static final String FILTRO_PUBLICO = "PUBLICO";
  private static final String NIVEL_PRINCIPIANTE = "PRINCIPIANTE";
  private static final String NIVEL_INTERMEDIO = "INTERMEDIO";
  private static final String NIVEL_AVANZADO = "AVANZADO";
  private static final String ATRIBUTO_LISTA = "listaPartidos";

  @Test
  public void deberiaRetornarLaVistaPartidosYElModeloConLaListaDePartidos() {
    List<Partido> listaMock = new ArrayList<>();
    when(
      servicioPartidoMock.listarPartidosSegunFiltro(
        "TODOS",
        "TODOS",
        null,
        "TODOS",
        "TODOS",
        "TODOS"
      )
    )
      .thenReturn(listaMock);

    ModelAndView mav = partidosController.consultarPartidos(
      "TODOS",
      "TODOS",
      null,
      "TODOS",
      "TODOS",
      "TODOS"
    );

    assertThat(mav.getViewName(), equalTo("partidos"));
    thenLaVistaEs("partidos", mav);
    thenModeloKeyEs(listaMock, mav);
  }

  @Test
  public void deberiaMostrarPartidosPublicosCuandoElFiltroEsPublico() {
    ArrayList<Partido> partidosPublicos = new ArrayList<>();
    Partido p = new Partido();
    p.setEsPrivado(false);
    partidosPublicos.add(p);

    when(
      servicioPartidoMock.listarPartidosSegunFiltro(
        FILTRO_TODOS,
        FILTRO_PUBLICO,
        FILTRO_TODOS,
        FILTRO_TODOS,
        FILTRO_TODOS,
        FILTRO_TODOS
      )
    )
      .thenReturn(partidosPublicos);

    ModelAndView mav = whenUsuarioConsultaPartidos(
      FILTRO_TODOS,
      FILTRO_PUBLICO,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS
    );

    thenElModeloContienePartidosPrivados(mav);
    thenElModeloContieneLaListaDePartidos(mav);
    thenLaVistaEs("partidos", mav);
  }

  @Test
  public void deberiaMostrarPartidosPrincipiantesCuandoSeFiltraPorNivel() {
    ArrayList<Partido> partidosPrincipiantes = new ArrayList<>();
    Partido p = new Partido();
    p.setNivel(NIVEL_PRINCIPIANTE);
    partidosPrincipiantes.add(p);

    when(
      servicioPartidoMock.listarPartidosSegunFiltro(
        NIVEL_PRINCIPIANTE,
        FILTRO_TODOS,
        FILTRO_TODOS,
        FILTRO_TODOS,
        FILTRO_TODOS,
        FILTRO_TODOS
      )
    )
      .thenReturn(partidosPrincipiantes);

    ModelAndView mav = whenUsuarioConsultaPartidos(
      NIVEL_PRINCIPIANTE,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS
    );

    thenElNivelEs(NIVEL_PRINCIPIANTE, mav);
  }

  @Test
  public void deberiaMostrarPartidosIntermedioCuandoSeFiltraPorNivel() {
    ArrayList<Partido> partidosIntermedios = new ArrayList<>();
    Partido p = new Partido();
    p.setNivel(NIVEL_INTERMEDIO);
    partidosIntermedios.add(p);

    when(
      servicioPartidoMock.listarPartidosSegunFiltro(
        NIVEL_INTERMEDIO,
        FILTRO_TODOS,
        FILTRO_TODOS,
        FILTRO_TODOS,
        FILTRO_TODOS,
        FILTRO_TODOS
      )
    )
      .thenReturn(partidosIntermedios);

    ModelAndView mav = whenUsuarioConsultaPartidos(
      NIVEL_INTERMEDIO,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS
    );

    thenElNivelEs(NIVEL_INTERMEDIO, mav);
  }

  @Test
  public void deberiaMostrarPartidosAvanzadosYPublicoCuandoSeFiltraPorNivel() {
    ArrayList<Partido> partidosAvanzados = new ArrayList<>();
    Partido p = new Partido();
    p.setNivel(NIVEL_AVANZADO);
    partidosAvanzados.add(p);

    when(
      servicioPartidoMock.listarPartidosSegunFiltro(
        NIVEL_AVANZADO,
        FILTRO_PUBLICO,
        FILTRO_TODOS,
        FILTRO_TODOS,
        FILTRO_TODOS,
        FILTRO_TODOS
      )
    )
      .thenReturn(partidosAvanzados);

    ModelAndView mav = whenUsuarioConsultaPartidos(
      NIVEL_AVANZADO,
      FILTRO_PUBLICO,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS
    );

    thenElNivelEs(NIVEL_AVANZADO, mav);
  }

  @Test
  public void deberiaMostrarPartidosDe10JugadoresCuandoSeFiltraPorEsaCantidad() {
    ArrayList<Partido> partidos10Jugadores = new ArrayList<>();
    Partido p = new Partido();
    p.setcupoMaximo(10);
    partidos10Jugadores.add(p);
    String cupoMaximoString = "10";
    when(
      servicioPartidoMock.listarPartidosSegunFiltro(
        FILTRO_TODOS,
        FILTRO_TODOS,
        cupoMaximoString,
        FILTRO_TODOS,
        FILTRO_TODOS,
        FILTRO_TODOS
      )
    )
      .thenReturn(partidos10Jugadores);

    ModelAndView mav = partidosController.consultarPartidos(
      FILTRO_TODOS,
      FILTRO_TODOS,
      cupoMaximoString,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS
    );

    thenLosCuposSon(cupoMaximoString, mav);
  }

  @Test
  public void deberiaMostrarPartidosDe22JugadoresCuandoSeFiltraPorEsaCantidad() {
    ArrayList<Partido> partidos22Jugadores = new ArrayList<>();
    Partido p = new Partido();
    p.setcupoMaximo(22);
    partidos22Jugadores.add(p);
    String cupoMaximoString = "22";

    when(
      servicioPartidoMock.listarPartidosSegunFiltro(
        FILTRO_TODOS,
        FILTRO_TODOS,
        cupoMaximoString,
        FILTRO_TODOS,
        FILTRO_TODOS,
        FILTRO_TODOS
      )
    )
      .thenReturn(partidos22Jugadores);

    ModelAndView mav = partidosController.consultarPartidos(
      FILTRO_TODOS,
      FILTRO_TODOS,
      cupoMaximoString,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS
    );

    thenLosCuposSon(cupoMaximoString, mav);
  }

  @Test
  public void deberiaMostrarPartidosCercanosCuandoSeFiltraPorDistanciaDe5Km() {
    ArrayList<Partido> partidosCercanos = new ArrayList<>();
    Partido p = new Partido();
    p.setDistanciaKm(3);
    partidosCercanos.add(p);
    String distanciaKmString = "3";

    when(
      servicioPartidoMock.listarPartidosSegunFiltro(
        FILTRO_TODOS,
        FILTRO_TODOS,
        FILTRO_TODOS,
        FILTRO_TODOS,
        FILTRO_TODOS,
        "5"
      )
    )
      .thenReturn(partidosCercanos);

    ModelAndView mav = partidosController.consultarPartidos(
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      "5"
    );

    thenLaDistanciaEsMenorOIgualA(distanciaKmString, mav);
  }

  @Test
  public void deberiaMostrarPartidosDeUnaFechaEspecificaCuandoSeFiltraPorFecha() {
    ArrayList<Partido> partidosFecha = new ArrayList<>();
    Partido p = new Partido();
    LocalDateTime fechaPartido = LocalDateTime.of(2026, 10, 2, 14, 30);
    p.setFecha(fechaPartido);
    partidosFecha.add(p);

    String fechaBuscada = "2026-10-02";
    when(
      servicioPartidoMock.listarPartidosSegunFiltro(
        FILTRO_TODOS,
        FILTRO_TODOS,
        FILTRO_TODOS,
        fechaBuscada,
        FILTRO_TODOS,
        FILTRO_TODOS
      )
    )
      .thenReturn(partidosFecha);

    ModelAndView mav = partidosController.consultarPartidos(
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      fechaBuscada,
      FILTRO_TODOS,
      FILTRO_TODOS
    );

    thenLaFechaCoincideCon(fechaBuscada, mav);
  }

  @Test
  public void deberiaMostrarPartidosDeUnaHoraEspecificaCuandoSeFiltraPorHora() {
    ArrayList<Partido> partidosHora = new ArrayList<>();
    Partido p = new Partido();
    LocalDateTime fechaPartido = LocalDateTime.of(2026, 10, 2, 14, 30);
    p.setFecha(fechaPartido);
    partidosHora.add(p);

    String horaBuscada = "14:30";
    when(
      servicioPartidoMock.listarPartidosSegunFiltro(
        FILTRO_TODOS,
        FILTRO_TODOS,
        FILTRO_TODOS,
        FILTRO_TODOS,
        horaBuscada,
        FILTRO_TODOS
      )
    )
      .thenReturn(partidosHora);

    ModelAndView mav = partidosController.consultarPartidos(
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      horaBuscada,
      FILTRO_TODOS
    );

    thenLaHoraCoincideCon(horaBuscada, mav);
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

  private void thenLaHoraCoincideCon(String horaBuscada, ModelAndView mav) {
    List<Partido> lista = (List<Partido>) mav.getModel().get(ATRIBUTO_LISTA);
    Partido partido = lista.get(0);
    LocalTime horaObtenido = partido.getFecha().toLocalTime();
    assertThat(horaBuscada, equalTo(horaObtenido.toString()));
  }

  private void thenLaFechaCoincideCon(String fechaBuscada, ModelAndView mav) {
    List<Partido> lista = (List<Partido>) mav.getModel().get(ATRIBUTO_LISTA);
    Partido partido = lista.get(0);
    LocalDate fechaObtenido = partido.getFecha().toLocalDate();
    assertThat(fechaBuscada, equalTo(fechaObtenido.toString()));
  }

  private void thenLaDistanciaEsMenorOIgualA(String distanciaKmString, ModelAndView mav) {
    List<Partido> lista = (List<Partido>) mav.getModel().get(ATRIBUTO_LISTA);
    Partido partido = lista.get(0);
    Integer distanciaObtenido = partido.getDistanciaKm();
    assertThat(distanciaKmString, equalTo(distanciaObtenido.toString()));
  }

  private void thenLosCuposSon(String cupomaximo, ModelAndView mav) {
    List<Partido> lista = (List<Partido>) mav.getModel().get(ATRIBUTO_LISTA);
    Partido partido = lista.get(0);
    Integer cupoMaximoObtenido = partido.getCupoMaximo();
    assertThat(cupomaximo, equalTo(cupoMaximoObtenido.toString()));
  }

  private void thenElModeloContienePartidosPrivados(ModelAndView mav) {
    ArrayList<Partido> listaEnModelo = (ArrayList<Partido>) mav.getModel().get(ATRIBUTO_LISTA);
    Boolean esPrivado = listaEnModelo.get(0).getEsPrivado();
    assertThat(esPrivado, equalTo(false));
  }

  private void thenElModeloContieneLaListaDePartidos(ModelAndView mav) {
    assertThat(true, equalTo(mav.getModel().containsKey(ATRIBUTO_LISTA)));
  }

  private void thenLaVistaEs(String vistaEsperada, ModelAndView mav) {
    assertThat(vistaEsperada, equalTo(mav.getViewName()));
  }

  private void thenModeloKeyEs(List<Partido> listaMock, ModelAndView mav) {
    assertThat(mav.getModel().get("listaPartidos"), equalTo(listaMock));
  }

  private void thenElNivelEs(String nivelEsperado, ModelAndView mav) {
    List<Partido> lista = (List<Partido>) mav.getModel().get(ATRIBUTO_LISTA);
    Partido partido = lista.get(0);
    String nivelCalculado = partido.getNivel();
    assertThat(nivelCalculado, equalTo(nivelEsperado));
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
