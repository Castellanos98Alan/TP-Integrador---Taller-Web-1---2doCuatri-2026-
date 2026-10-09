package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioPartido;
import com.tallerwebi.dominio.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CrearPartidoController {

  private ServicioPartido servicioPartido;

  @Autowired
  public CrearPartidoController(ServicioPartido servicioPartido) {
    this.servicioPartido = servicioPartido;
  }

  @GetMapping("/crearPartido")
  public ModelAndView irACrearPartido() {
    ModelAndView model = new ModelAndView("crearPartido");
    model.getModelMap().put("datosPartido", new DatosCrearPartido());
    return model;
  }

  @PostMapping("/crearPartido")
  public ModelAndView crearPartido(
    @ModelAttribute("datosPartido") DatosCrearPartido datosPartido,
    HttpSession session
  ) {
    Usuario usuario = (Usuario) session.getAttribute("USUARIO");
    if (usuario == null) {
      return new ModelAndView("redirect:/login");
    }

    if (datosPartido.getCancha() == null || datosPartido.getCancha().isEmpty()) {
      ModelAndView model = new ModelAndView("crearPartido");
      model.getModelMap().put("error", "Debe ingresar una cancha");
      return model;
    }

    try {
      servicioPartido.crearPartido(datosPartido, usuario);
    } catch (RuntimeException e) {
      ModelAndView model = new ModelAndView("crearPartido");
      model.getModelMap().put("datosPartido", datosPartido);
      model.getModelMap().put("error", e.getMessage());
      return model;
    }

    return new ModelAndView("redirect:/partidos");
  }
}
