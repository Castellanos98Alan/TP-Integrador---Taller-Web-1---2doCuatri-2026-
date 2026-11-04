package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.excepcion.*;
import com.tallerwebi.dominio.servicio.ServicioBalanceoEquipo;
import com.tallerwebi.dominio.servicio.ServicioEstadistica;
import com.tallerwebi.dominio.servicio.ServicioJugador;
import com.tallerwebi.dominio.servicio.ServicioPartido;
import com.tallerwebi.dominio.servicio.ServicioPartidoFiltro;
import com.tallerwebi.dominio.servicio.ServicioSolicitudAmistad;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
//import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PartidosController {

  private static final String USUARIO_SESION = "USUARIO";

  private ServicioPartido servicioPartido;
  private ServicioPartidoFiltro servicioPartidoFiltro;
  private ServicioJugador servicioJugador;
  private ServicioSolicitudAmistad servicioSolicitudAmistad;
  private ServicioEstadistica servicioEstadistica;
  private static final String ATRIBUTO_ERROR = "error";
  private static final String REDIRECT_PARTIDO = "redirect:/partido/";
  private static final String REDIRECT_LOGIN = "redirect:/login/";

  @Autowired
  public PartidosController(
    ServicioPartido servicioPartido,
    ServicioPartidoFiltro servicioPartidoFiltro,
    ServicioJugador servicioJugador,
    ServicioSolicitudAmistad servicioSolicitudAmistad,
    ServicioEstadistica servicioEstadistica
  ) {
    this.servicioPartido = servicioPartido;
    this.servicioPartidoFiltro = servicioPartidoFiltro;
    this.servicioJugador = servicioJugador;
    this.servicioSolicitudAmistad = servicioSolicitudAmistad;
    this.servicioEstadistica = servicioEstadistica;
  }

  private static final String FILTRO_TODOS = "TODOS";
  private static final String SALA_ESPERA = "/sala-de-espera";

  @RequestMapping(path = "/partidos", method = RequestMethod.GET)
  public ModelAndView consultarPartidos(
    @RequestParam(
      value = "filtroNivel",
      required = false,
      defaultValue = FILTRO_TODOS
    ) String filtroNivel,
    @RequestParam(
      value = "filtroTipo",
      required = false,
      defaultValue = FILTRO_TODOS
    ) String filtroTipo,
    @RequestParam(
      value = "filtroCupo",
      required = false,
      defaultValue = FILTRO_TODOS
    ) String filtroCupo,
    @RequestParam(
      value = "filtroFecha",
      required = false,
      defaultValue = FILTRO_TODOS
    ) String filtroFecha,
    @RequestParam(
      value = "filtroHora",
      required = false,
      defaultValue = FILTRO_TODOS
    ) String filtroHora,
    @RequestParam(
      value = "filtroProvincia",
      required = false,
      defaultValue = FILTRO_TODOS
    ) String filtroProvincia,
    @RequestParam(
      value = "filtroLocalidad",
      required = false,
      defaultValue = FILTRO_TODOS
    ) String filtroLocalidad
  ) {
    Map<String, Object> model = new ModelMap();
    model.put(
      "listaPartidos",
      servicioPartidoFiltro.listarPartidosSegunFiltro(
        filtroNivel,
        filtroTipo,
        filtroCupo,
        filtroFecha,
        filtroHora,
        filtroProvincia,
        filtroLocalidad
      )
    );

    model.put("nivelSeleccionado", filtroNivel);
    model.put("tipoSeleccionado", filtroTipo);
    model.put("cupoSeleccionado", filtroCupo);
    model.put("fechaSeleccionada", filtroFecha);
    model.put("horaSeleccionada", filtroHora);
    model.put("provinciaSeleccionada", filtroProvincia);
    model.put("localidadSeleccionada", filtroLocalidad);

    // Datos Dinamico segun partidos
    model.put("fechasDisponibles", servicioPartidoFiltro.obtenerFechasDisponibles());
    model.put("horasDisponibles", servicioPartidoFiltro.obtenerHorasDisponibles());
    model.put("provinciasDisponibles", servicioPartidoFiltro.obtenerProvinciasDisponibles());
    model.put("localidadesDisponibles", servicioPartidoFiltro.obtenerLocalidadesDisponibles());
    model.put("cuposDisponibles", servicioPartidoFiltro.obtenerCuposDisponibles());

    return new ModelAndView("partidos", model);
  }

  @PostMapping("/partido/cancelar/{id}")
  public ModelAndView cancelarPartido(
    @PathVariable("id") Long idPartido,
    Model model,
    HttpServletRequest request
  ) {
    Usuario usuarioLogueado = (Usuario) request.getSession().getAttribute(USUARIO_SESION);

    if (usuarioLogueado == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      servicioPartido.cancelarPartido(idPartido, usuarioLogueado);
      model.addAttribute("exito", "El partido se canceló correctamente.");
    } catch (Exception e) {
      model.addAttribute(ATRIBUTO_ERROR, e.getMessage());
    }

    return new ModelAndView(REDIRECT_PARTIDO);
  }

  @RequestMapping(path = "/cancelar-partido", method = RequestMethod.GET)
  public ModelAndView cancelarCreacionFormulario() {
    return new ModelAndView("partido-cancelado");
  }

  // Con este metodo manejamos la visualizacion de la sala de espera de un partido especifico, esto mediante el ID de partido
  //que lo mandamos por la URL.
  // Utilizamos el HttpServletRequest para poder almacenar la sesion de ese momento, esto para poder traer al usuario logueado
  //de esa sesion. Validamos si el usuario logueado existe, si no existe entonces retorna un ModelAndView que contiene la vista de login
  //para que el usuario se loguee.
  //Si el usuario existe, entonces se utiliza un try catch para manejar excepciones por si el partido no existe.
  // Dentro del try utilizamos el metodo de servicioPartido para buscar el partido por su ID y que tambien nos traiga
  //su lista de confirmados inicializada. Si el partido se encuentra entonces devuelve un ModelAndView con la vista
  //de sala de espera
  @RequestMapping(path = "/partido/{id}/sala-de-espera", method = RequestMethod.GET)
  public ModelAndView verSalaDeEspera(
    @PathVariable("id") Long idPartido,
    @RequestParam(value = ATRIBUTO_ERROR, required = false) String error,
    HttpServletRequest request
  ) {
    HttpSession session = request.getSession();

    Usuario usuarioLogueado = (Usuario) session.getAttribute(USUARIO_SESION);

    if (usuarioLogueado == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    Map<String, Object> model = new ModelMap();

    if (error != null && !error.isBlank()) {
      model.put(ATRIBUTO_ERROR, error);
    }

    try {
      Partido partido = getEsParticipante(idPartido, usuarioLogueado, model);
      boolean puedeCargar = servicioEstadistica.puedeCargarEstadisticas(partido, usuarioLogueado);
      model.put("puedeCargarEstadisticas", puedeCargar);

      boolean puedeRevisar = servicioEstadistica.puedeRevisarEstadisticas(partido, usuarioLogueado);
      model.put("puedeRevisarEstadisticas", puedeRevisar);
    } catch (PartidoNoEncontrado e) {
      model.put(ATRIBUTO_ERROR, e.getMessage());
      return new ModelAndView(REDIRECT_PARTIDO, model);
    }

    return new ModelAndView("sala-de-espera", model);
  }

  private Partido getEsParticipante(
    Long idPartido,
    Usuario usuarioLogueado,
    Map<String, Object> model
  ) {
    Partido partido = servicioPartido.buscarPartidoPorId(idPartido);

    List<Usuario> confirmados = partido.getConfirmados();

    boolean esParticipante = false;

    for (Usuario confirmado : confirmados) {
      if (confirmado.getId().equals(usuarioLogueado.getId())) {
        esParticipante = true;
        break;
      }
    }
    if (
      partido.getOrganizador() != null &&
      partido.getOrganizador().getId().equals(usuarioLogueado.getId())
    ) {
      esParticipante = true;
    }

    model.put("partido", partido);
    model.put("esParticipante", esParticipante);

    if (esParticipante) {
      model.put("amigos", servicioSolicitudAmistad.obtenerAmigos(usuarioLogueado));
    }
    return partido;
  }

  // Con este metodo procesamos la accion de unirse a un partido especifico mediante una peticion POST,
  //utilizando el ID de partido que mandamos por la URL.
  // Utilizamos el HttpServletRequest para poder almacenar la sesion de ese momento, esto para poder traer al usuario logueado
  //de esa sesion. Validamos si el usuario logueado existe, si no existe entonces retorna un ModelAndView que contiene la vista de login
  //para que el usuario se loguee.
  //Si el usuario existe, entonces se utiliza un try catch para manejar excepciones por si el partido no existe.
  // Dentro del try utilizamos el metodo de servicioPartido para unirse al partido, donde mandamos el id del partido y
  //el usuario logueado.
  //Si se cumplen las validaciones entonces retorna un ModelAndView con la vista de sala de espera mandando el id del partido
  //en la URL.
  @RequestMapping(path = "/partido/{id}/unirse", method = RequestMethod.POST)
  public ModelAndView unirseAlPartido(
    @PathVariable("id") Long idPartido,
    HttpServletRequest request
  ) {
    HttpSession session = request.getSession();

    Usuario usuarioLogueado = (Usuario) session.getAttribute(USUARIO_SESION);

    if (usuarioLogueado == null) {
      return new ModelAndView("redirect:/login/?error=Debes+iniciar+sesion+para+unirte");
    }

    try {
      Jugador jugadorLogueado = servicioJugador.buscarPorUsuarioId(usuarioLogueado.getId());
      servicioPartido.unirseAPartido(idPartido, jugadorLogueado);

      return new ModelAndView(REDIRECT_PARTIDO + idPartido + "/sala-de-espera");
    } catch (
      CupoMaximoAlcanzado
      | EstadoPartidoInvalido
      | JugadorInscripto
      | DiferenciaHoraria
      | RangoJugadorIncompatibleException
      | PartidoNoEncontrado e
    ) {
      String mensajeError = e.getMessage();
      return new ModelAndView(
        REDIRECT_PARTIDO +
        idPartido +
        "/sala-de-espera?error=" +
        java.net.URLEncoder.encode(mensajeError, java.nio.charset.StandardCharsets.UTF_8)
      );
    }
  }

  @PostMapping("/partido/{id}/abandonar")
  public ModelAndView abandonarPartido(
    @PathVariable("id") Long idPartido,
    HttpServletRequest request
  ) {
    Usuario usuarioLogueado = (Usuario) request.getSession().getAttribute(USUARIO_SESION);
    if (usuarioLogueado == null) return new ModelAndView(REDIRECT_LOGIN);

    try {
      Jugador jugadorLogueado = servicioJugador.buscarPorUsuarioId(usuarioLogueado.getId());
      servicioPartido.abandonarPartido(idPartido, jugadorLogueado);
      return new ModelAndView("redirect:/partidos");
    } catch (Exception e) {
      Map<String, Object> model = new ModelMap();
      model.put(ATRIBUTO_ERROR, e.getMessage());
      return new ModelAndView(REDIRECT_PARTIDO + idPartido + SALA_ESPERA, model);
    }
  }

  @PostMapping("/partido/{id}/expulsar/{idJugador}")
  public ModelAndView expulsarJugador(
    @PathVariable("id") Long idPartido,
    @PathVariable("idJugador") Long idJugador,
    HttpServletRequest request
  ) {
    Usuario usuarioLogueado = (Usuario) request.getSession().getAttribute(USUARIO_SESION);
    if (usuarioLogueado == null) return new ModelAndView(REDIRECT_LOGIN);

    try {
      servicioPartido.expulsarJugador(idPartido, idJugador, usuarioLogueado);
      return new ModelAndView(REDIRECT_PARTIDO + idPartido + SALA_ESPERA);
    } catch (Exception e) {
      // Ahora enviamos el error al ModelMap para que aparezca en la URL
      Map<String, Object> model = new ModelMap();
      model.put(ATRIBUTO_ERROR, e.getMessage());
      return new ModelAndView(REDIRECT_PARTIDO + idPartido + SALA_ESPERA, model);
    }
  }

  @GetMapping("/partido/{id}/cargar-estadisticas")
  public ModelAndView verVistaCargarEstadisticas(
    @PathVariable("id") Long idPartido,
    HttpServletRequest request
  ) {
    HttpSession session = request.getSession();
    Usuario usuarioLogueado = (Usuario) session.getAttribute(USUARIO_SESION);

    if (usuarioLogueado == null) {
      return new ModelAndView("redirect:/login?error=Debes+iniciar+sesión");
    }

    Partido partido = servicioPartido.buscarPartidoPorId(idPartido);

    if (partido == null) {
      return new ModelAndView("redirect:/partidos?error=El+partido+no+existe");
    }

    boolean autorizado = servicioEstadistica.puedeCargarEstadisticas(partido, usuarioLogueado);

    if (!autorizado) {
      return new ModelAndView(
        REDIRECT_PARTIDO +
        idPartido +
        "/sala-de-espera?error=No+estás+autorizado+para+cargar+estadísticas+aún"
      );
    }

    WrapperEstadistica wrapperEstadistica = new WrapperEstadistica();
    if (partido.getInscripciones() != null) {
      for (int i = 0; i < partido.getInscripciones().size(); i++) {
        wrapperEstadistica.getEstadisticas().add(new DTOEstadisticaJugador());
      }
    }

    Map<String, Object> model = new ModelMap();
    model.put("partido", partido);
    model.put("formulario", wrapperEstadistica);

    return new ModelAndView("cargar-estadisticas", model);
  }

  @RequestMapping(path = "/partido/{id}/revisar-estadisticas", method = RequestMethod.GET)
  public ModelAndView verPantallaRevisionEstadisticas(
    @PathVariable("id") Long idPartido,
    HttpServletRequest request
  ) {
    Usuario usuarioLogueado = obtenerUsuarioLogueadoDesdeSesion(request);
    if (usuarioLogueado == null) {
      return new ModelAndView("redirect:/login?error=Debes+iniciar+sesion");
    }

    try {
      Partido partido = servicioPartido.buscarPartidoPorId(idPartido);

      if (!servicioEstadistica.puedeRevisarEstadisticas(partido, usuarioLogueado)) {
        String urlRedireccion =
          REDIRECT_PARTIDO +
          idPartido +
          "/sala-de-espera?error=No+estás+autorizado+para+revisar+estas+estadísticas";
        return new ModelAndView(urlRedireccion);
      }

      Map<String, Object> model = new ModelMap();
      model.put("partido", partido);

      List<Estadistica> estadisticas = servicioEstadistica.obtenerEstadisticasPorPartido(partido);

      Map<String, Integer> marcador = servicioEstadistica.calcularMarcadorPartido(
        partido,
        estadisticas
      );
      Map<String, List<Estadistica>> estadisticasSeparadas =
        servicioEstadistica.obtenerEstadisticasSeparadasPorEquipo(partido, estadisticas);

      model.put("golesLocal", marcador.get("local"));
      model.put("golesVisitante", marcador.get("visitante"));
      model.put("estadisticasLocal", estadisticasSeparadas.get("locales"));
      model.put("estadisticasVisitante", estadisticasSeparadas.get("visitantes"));

      return new ModelAndView("revisar-estadisticas", model);
    } catch (RevisionEstadisticasNoPermitida e) {
      return new ModelAndView("redirect:/partidos?error=" + "No puedes revisar estas estadisticas");
    }
  }

  private Usuario obtenerUsuarioLogueadoDesdeSesion(HttpServletRequest request) {
    if (request == null || request.getSession() == null) {
      return null;
    }
    return (Usuario) request.getSession().getAttribute("USUARIO");
  }

  @RequestMapping(path = "/partido/{id}/invitar", method = RequestMethod.POST)
  public ModelAndView invitarAmigo(
    @PathVariable("id") Long idPartido,
    @RequestParam("idJugador") Long idJugador,
    HttpServletRequest request
  ) throws InvitacionAlPartidoException {
    Usuario usuarioLogueado = (Usuario) request.getSession().getAttribute(USUARIO_SESION);

    if (usuarioLogueado == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    servicioPartido.invitarJugador(idPartido, idJugador, usuarioLogueado);

    return new ModelAndView(REDIRECT_PARTIDO + idPartido + "/sala-de-espera");
  }
}
