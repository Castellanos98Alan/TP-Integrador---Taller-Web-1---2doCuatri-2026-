package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.ServicioRanking;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
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
    @RequestParam(value = "posicion", required = false) String posicion
  ) {
    Map<String, Object> modelo = new ModelMap();

    List<Jugador> ranking;

    if (nivel != null && !nivel.isEmpty() && posicion != null && !posicion.isEmpty()) {
      ranking = servicioRanking.obtenerRankingFiltradoPorNivelYPosicion(nivel, posicion);
    } else if (nivel != null && !nivel.isEmpty()) {
      ranking = servicioRanking.obtenerRankingFiltradoPorNivel(nivel);
    } else if (posicion != null && !posicion.isEmpty()) {
      ranking = servicioRanking.obtenerRankingFiltradoPorPosicion(posicion);
    } else {
      ranking = servicioRanking.obtenerRanking();
    }

    modelo.put("ranking", ranking);
    modelo.put("nivelSeleccionado", nivel);
    modelo.put("posicionSeleccionada", posicion);

    return new ModelAndView("ranking", modelo);
  }
}
