package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Enumeration.PosicionJugador;
import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.servicio.ServicioRanking;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RankingController {

  private ServicioRanking servicioRanking;

  @Autowired
  public RankingController(ServicioRanking servicioRanking) {
    this.servicioRanking = servicioRanking;
  }

  @RequestMapping(path = "/ranking", method = RequestMethod.GET)
  public ModelAndView mostrarRanking(
    @RequestParam(value = "nivel", required = false) String nivel,
    @RequestParam(value = "posicion", required = false) String posicionStr,
    @RequestParam(value = "rango", required = false) String rango,
    @RequestParam(value = "vista", required = false) String vista,
    HttpServletRequest request
  ) {
    Map<String, Object> modelo = new ModelMap();

    Usuario usuarioLogueado = (Usuario) request.getSession().getAttribute("USUARIO");

    Long usuarioId = null;

    if (usuarioLogueado != null) {
      usuarioId = usuarioLogueado.getId();
    }

    PosicionJugador posicionEnum = null;
    if (posicionStr != null && !posicionStr.trim().isEmpty()) {
      posicionEnum = PosicionJugador.valueOf(posicionStr.toUpperCase(Locale.ROOT));
    }

    List<Jugador> ranking = servicioRanking.obtenerRankingFiltrado(
      nivel,
      posicionEnum,
      rango,
      vista,
      usuarioId
    );

    modelo.put("ranking", ranking);

    modelo.put("nivelSeleccionado", nivel);
    modelo.put("posicionSeleccionada", posicionStr);
    modelo.put("rangoSeleccionado", rango);
    modelo.put("vistaSeleccionada", vista);

    return new ModelAndView("ranking", modelo);
  }
}
