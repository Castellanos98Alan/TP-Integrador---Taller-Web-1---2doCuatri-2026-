package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
//import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

//import com.tallerwebi.dominio.Cancha;
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
import org.springframework.ui.Model; // <-- Importante importar est
import org.springframework.web.servlet.ModelAndView;

public class PartidosControllerTest {

  // ServicioPartidos servicioPartidos = mock(ServicioPartidos.class);
  //PartidosController partidosController = new PartidosController(servicioPartidos);

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
    modelMock = mock(Model.class); // <-- Lo inicializamos
  }

  public static final String FILTRO_TODOS = "TODOS";
  private static final String FILTRO_PUBLICO = "PUBLICO";
  private static final String NIVEL_PRINCIPIANTE = "PRINCIPIANTE";
  private static final String NIVEL_INTERMEDIO = "INTERMEDIO";
  private static final String NIVEL_AVANZADO = "AVANZADO";
  private static final String ATRIBUTO_LISTA = "listaPartidos";

  @Test
  public void deberiaRetornarLaVistaPartidosYElModeloConLaListaDePartidos() {
    // Preparación
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

    // Ejecución
    ModelAndView mav = partidosController.consultarPartidos(
      "TODOS",
      "TODOS",
      null,
      "TODOS",
      "TODOS",
      "TODOS"
    );

    // Validación
    assertThat(mav.getViewName(), equalTo("partidos"));
    thenLaVistaEs("partidos", mav);
    thenModeloKeyEs(listaMock, mav);
  }

  /* 
  @Test
  public void deberiaRetornarLaVistaPartidosYElModeloConLaListaDePartidos() {
    // Preparación
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

    // Ejecución
    ModelAndView mav = partidosController.consultarPartidos(
      "TODOS",
      "TODOS",
      null,
      "TODOS",
      "TODOS",
      "TODOS"
    );
    //validación
    assertThat(mav.getViewName(), equalTo("partidos"));
    assertThat(mav.getModel().get("listaPartidos"), equalTo(listaMock));
  }
*/
  @Test
  public void deberiaMostrarPartidosPublicosCuandoElFiltroEsPublico() {
    // Preparación
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

    // Ejecución
    ModelAndView mav = whenUsuarioConsultaPartidos(
      FILTRO_TODOS,
      FILTRO_PUBLICO,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS
    );

    // Validación
    thenElModeloContienePartidosPrivados(mav);
    thenElModeloContieneLaListaDePartidos(mav);
    thenLaVistaEs("partidos", mav);
  }

  @Test
  public void deberiaMostrarPartidosPrincipiantesCuandoSeFiltraPorNivel() {
    // Preparación
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

    // Ejecución
    ModelAndView mav = whenUsuarioConsultaPartidos(
      NIVEL_PRINCIPIANTE,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS
    );

    // Validación

    thenElNivelEs(NIVEL_PRINCIPIANTE, mav);
  }

  @Test
  public void deberiaMostrarPartidosIntermedioCuandoSeFiltraPorNivel() {
    // Preparación
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

    // Ejecución
    ModelAndView mav = whenUsuarioConsultaPartidos(
      NIVEL_INTERMEDIO,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS
    );

    // Validación

    thenElNivelEs(NIVEL_INTERMEDIO, mav);
  }

  @Test
  public void deberiaMostrarPartidosAvanzadosYPublicoCuandoSeFiltraPorNivel() {
    // Preparación
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

    // Ejecución
    ModelAndView mav = whenUsuarioConsultaPartidos(
      NIVEL_AVANZADO,
      FILTRO_PUBLICO,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS
    );

    // Validación

    thenElNivelEs(NIVEL_AVANZADO, mav);
  }

  @Test
  public void deberiaMostrarPartidosDe10JugadoresCuandoSeFiltraPorEsaCantidad() {
    // Preparación
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
    // Ejecución
    ModelAndView mav = partidosController.consultarPartidos(
      FILTRO_TODOS,
      FILTRO_TODOS,
      cupoMaximoString,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS
    );

    // Validación
    thenLosCuposSon(cupoMaximoString, mav);
  }

  @Test
  public void deberiaMostrarPartidosDe22JugadoresCuandoSeFiltraPorEsaCantidad() {
    // Preparación
    ArrayList<Partido> partidos10Jugadores = new ArrayList<>();
    Partido p = new Partido();
    p.setcupoMaximo(22);
    partidos10Jugadores.add(p);
    String cupoMaximoString = "22";
    // Asumimos que el servicio ahora recibe (nivel, tipo, cupos, fecha, distancia)
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

    // Ejecución
    ModelAndView mav = partidosController.consultarPartidos(
      FILTRO_TODOS,
      FILTRO_TODOS,
      cupoMaximoString,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS
    );

    // Validación
    thenLosCuposSon(cupoMaximoString, mav);
  }

  @Test
  public void deberiaMostrarPartidosCercanosCuandoSeFiltraPorDistanciaDe5Km() {
    // Preparación
    ArrayList<Partido> partidosCercanos = new ArrayList<>();
    Partido p = new Partido();
    p.setDistanciaKm(3); // valor harcodeado de la distancia entre usuario y cancha.
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

    // Ejecución
    ModelAndView mav = partidosController.consultarPartidos(
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      "5"
    );

    // Validación
    thenLaDistanciaEsMenorOIgualA(distanciaKmString, mav);
  }

  @Test
  public void deberiaMostrarPartidosDeUnaFechaEspecificaCuandoSeFiltraPorFecha() {
    // Preparación
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

    // Ejecución
    ModelAndView mav = partidosController.consultarPartidos(
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      fechaBuscada,
      FILTRO_TODOS,
      FILTRO_TODOS
    );

    // Validación
    thenLaFechaCoincideCon(fechaBuscada, mav);
  }

  @Test
  public void deberiaMostrarPartidosDeUnaHoraEspecificaCuandoSeFiltraPorHora() {
    // Preparación
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

    // Ejecución
    ModelAndView mav = partidosController.consultarPartidos(
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      horaBuscada,
      FILTRO_TODOS
    );

    // Validación
    thenLaHoraCoincideCon(horaBuscada, mav);
  }

  @Test
  public void queAlCancelarUnPartidoExitosamenteRedirijaALaVistaDePartidos() {
    Long idPartido = 5L;
    Usuario usuarioLogueado = new Usuario();
    usuarioLogueado.setId(1L);

    when(requestMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getAttribute("usuario")).thenReturn(usuarioLogueado);

    // Pasamos modelMock como segundo argumento
    ModelAndView modelAndView = partidosController.cancelarPartido(
      idPartido,
      modelMock,
      requestMock
    );

    verify(servicioPartidoMock, times(1)).cancelarPartido(idPartido, usuarioLogueado);
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/partidos"));
  }

  @Test
  public void queSiNoHayUsuarioLogueadoAlIntentarCancelarRedirijaAlLogin() {
    Long idPartido = 5L;

    when(requestMock.getSession()).thenReturn(sessionMock);
    when(sessionMock.getAttribute("usuario")).thenReturn(null); // Sin sesión activa

    // Pasamos modelMock como segundo argumento
    ModelAndView modelAndView = partidosController.cancelarPartido(
      idPartido,
      modelMock,
      requestMock
    );

    verify(servicioPartidoMock, never()).cancelarPartido(anyLong(), any());
    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));
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
