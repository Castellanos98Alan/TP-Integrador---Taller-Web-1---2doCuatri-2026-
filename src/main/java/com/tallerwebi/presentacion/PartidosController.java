package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Partido;
import com.tallerwebi.dominio.ServicioPartido;
import com.tallerwebi.dominio.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
//import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PartidosController {

  private ServicioPartido servicioPartido;

  @Autowired
  public PartidosController(ServicioPartido servicioPartido) {
    this.servicioPartido = servicioPartido;
  }

  private static final String FILTRO_TODOS = "TODOS";

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
      value = "filtroDistancia",
      required = false,
      defaultValue = FILTRO_TODOS
    ) String filtroDistancia
  ) {
    Map<String, Object> model = new ModelMap();
    model.put(
      "listaPartidos",
      servicioPartido.listarPartidosSegunFiltro(
        filtroNivel,
        filtroTipo,
        filtroCupo,
        filtroFecha,
        filtroHora,
        filtroDistancia
      )
    );
    return new ModelAndView("partidos", model);
  }

  @PostMapping("/partido/cancelar/{id}")
  public ModelAndView cancelarPartido(
    @PathVariable("id") Long idPartido,
    Model model,
    HttpServletRequest request
  ) {
    Usuario usuarioLogueado = (Usuario) request.getSession().getAttribute("USUARIO");

    if (usuarioLogueado == null) {
      return new ModelAndView("redirect:/login");
    }

    try {
      servicioPartido.cancelarPartido(idPartido, usuarioLogueado);
      model.addAttribute("exito", "El partido se canceló correctamente.");
    } catch (Exception e) {
      model.addAttribute("error", e.getMessage());
    }

    return new ModelAndView("redirect:/partidos");
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
    HttpServletRequest request
  ) {
    HttpSession session = request.getSession();
    Usuario usuarioLogueado = (Usuario) session.getAttribute("USUARIO");

    if (usuarioLogueado == null) {
      return new ModelAndView("redirect:/login?error=Debes+iniciar+sesion+para+unirte");
    }

    Map<String, Object> model = new ModelMap();
    try {
      Partido partido = servicioPartido.buscarPartidoPorId(idPartido);
      model.put("partido", partido);
    } catch (Exception e) {
      model.put("error", e.getMessage());
      return new ModelAndView("redirect:/partidos", model);
    }

    return new ModelAndView("sala-de-espera", model);
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

    Usuario usuarioLogueado = (Usuario) session.getAttribute("USUARIO");

    if (usuarioLogueado == null) {
      return new ModelAndView("redirect:/login?error=Debes+iniciar+sesion+para+unirte");
    }

    try {
      servicioPartido.unirseAPartido(idPartido, usuarioLogueado);
      return new ModelAndView("redirect:/partido/" + idPartido + "/sala-de-espera");
    } catch (Exception e) {
      Map<String, Object> model = new ModelMap();
      model.put("error", e.getMessage());
      return new ModelAndView("redirect:/partido/" + idPartido + "/sala-de-espera", model);
    }
  }
}
