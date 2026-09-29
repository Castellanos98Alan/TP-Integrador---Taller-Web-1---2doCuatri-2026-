package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioRanking;
import com.tallerwebi.dominio.Usuario;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RankingController {

  private ServicioRanking servicioRanking;

  @Autowired
  public RankingController(ServicioRanking servicioRanking) {
    this.servicioRanking = servicioRanking;
  }

  @GetMapping("/ranking")
  public ModelAndView mostrarRanking() {
    List<Usuario> ranking = this.servicioRanking.obtenerRanking();

    Map<String, Object> modelo = new ModelMap();
    modelo.put("ranking", ranking);

    return new ModelAndView("ranking", modelo);
  }
}
