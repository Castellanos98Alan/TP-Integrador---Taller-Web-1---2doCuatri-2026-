package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioPartidos;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
//import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PartidosController {

  private ServicioPartidos servicioPartidos;

  @Autowired
  public PartidosController(ServicioPartidos servicioPartidos) {
    this.servicioPartidos = servicioPartidos;
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
      servicioPartidos.listarPartidosSegunFiltro(
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
}
