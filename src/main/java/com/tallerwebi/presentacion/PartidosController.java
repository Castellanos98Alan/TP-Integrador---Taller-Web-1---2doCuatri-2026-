package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioPartido;
import com.tallerwebi.dominio.Usuario;
import jakarta.servlet.http.HttpServletRequest;
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
}
