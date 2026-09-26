package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioPartido;
import com.tallerwebi.dominio.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PartidosController {

  private final ServicioPartido servicioPartido;

  @Autowired
  public PartidosController(ServicioPartido servicioPartido) {
    this.servicioPartido = servicioPartido;
  }

  @GetMapping("/partidos")
  public ModelAndView mostrarFormulario() {
    return new ModelAndView("partidos");
  }

  @PostMapping("/partido/cancelar/{id}")
  public ModelAndView cancelarPartido(@PathVariable("id") Long idPartido, HttpServletRequest request) {
    ModelMap modelo = new ModelMap();

    Usuario usuarioLogueado = (Usuario) request.getSession().getAttribute("usuario");

    if (usuarioLogueado == null) {
      return new ModelAndView("redirect:/login");
    }

    try {
      servicioPartido.cancelarPartido(idPartido, usuarioLogueado);
      modelo.put("exito", "El partido se canceló correctamente.");
    } catch (Exception e) {
      modelo.put("error", e.getMessage());
    }

    return new ModelAndView("redirect:/partidos", modelo);
  }
}
