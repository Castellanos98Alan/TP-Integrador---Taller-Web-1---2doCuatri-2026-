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
    Usuario usuarioLogueado = (Usuario) request.getSession().getAttribute("usuario");

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

  @RequestMapping(path = "/partido/{id}/sala-de-espera", method = RequestMethod.GET)
  public ModelAndView verSalaDeEspera(
    @PathVariable("id") Long idPartido,
    HttpServletRequest request
  ) {
    HttpSession session = request.getSession();
    Usuario usuarioLogueado = null;

    java.util.Enumeration<String> attributeNames = session.getAttributeNames();
    while (attributeNames.hasMoreElements()) {
      String key = attributeNames.nextElement();
      Object value = session.getAttribute(key);
      if (value instanceof Usuario) {
        usuarioLogueado = (Usuario) value;
        break;
      }
    }

    if (usuarioLogueado == null) {
      usuarioLogueado = new Usuario();
      usuarioLogueado.setId(1L);
      usuarioLogueado.setEmail("test@unlam.edu.ar");
      session.setAttribute("usuario", usuarioLogueado);
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
