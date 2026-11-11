package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Cancha;
import com.tallerwebi.dominio.servicio.ServicioHome;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

  private final ServicioHome servicioHome;

  @Autowired
  public HomeController(ServicioHome servicioHome) {
    this.servicioHome = servicioHome;
  }

  @RequestMapping(path = { "/", "/home" }, method = RequestMethod.GET)
  public ModelAndView irAHome(HttpServletRequest request) {
    Map<String, Object> model = new ModelMap();

    List<Cancha> canchasCarrusel = servicioHome.obtenerCanchasParaHome(4);
    model.put("canchasDestacadas", canchasCarrusel);

    return new ModelAndView("home", model);
  }
}
