package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.WrapperEstadistica;
import com.tallerwebi.dominio.servicio.ServicioEstadistica;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class EstadisticaController {

  private final ServicioEstadistica servicioEstadistica;

  @Autowired
  public EstadisticaController(ServicioEstadistica servicioEstadistica) {
    this.servicioEstadistica = servicioEstadistica;
  }

  @PostMapping("/partido/{id}/guardar-estadisticas")
  public ModelAndView guardarEstadisticas(
    @PathVariable("id") Long idPartido,
    @ModelAttribute WrapperEstadistica wrapperEstadistica,
    HttpServletRequest request,
    Model modelo
  ) {
    HttpSession session = request.getSession();
    Usuario usuarioLogueado = (Usuario) session.getAttribute("USUARIO");

    if (usuarioLogueado == null) {
      return new ModelAndView(
        "redirect:/login?error=Debes+iniciar+sesion+para+realizar+esta+accion"
      );
    }

    try {
      servicioEstadistica.guardarEstadisticasYEnviarARevision(
        idPartido,
        wrapperEstadistica.getEstadisticas()
      );

      modelo.addAttribute(
        "exito",
        "Las estadisticas se guardaron correctamente y estan en revision."
      );
      return new ModelAndView(
        "redirect:/partido/" + idPartido + "/sala-de-espera",
        (ModelMap) modelo
      );
    } catch (Exception e) {
      modelo.addAttribute("error", e.getMessage());
      return new ModelAndView(
        "redirect:/partido/" + idPartido + "/estadisticas",
        (ModelMap) modelo
      );
    }
  }
}
