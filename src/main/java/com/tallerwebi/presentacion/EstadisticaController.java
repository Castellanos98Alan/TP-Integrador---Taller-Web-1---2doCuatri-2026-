package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.WrapperEstadistica;
import com.tallerwebi.dominio.excepcion.EstadisticasNoGuardadas;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EstadisticaController {

  private final ServicioEstadistica servicioEstadistica;

  @Autowired
  public EstadisticaController(ServicioEstadistica servicioEstadistica) {
    this.servicioEstadistica = servicioEstadistica;
  }

  @PostMapping("/partido/{id}/guardar-estadisticas")
  public String guardarEstadisticas(
    @PathVariable("id") Long idPartido,
    @ModelAttribute("formulario") WrapperEstadistica wrapperEstadistica,
    HttpServletRequest request,
    RedirectAttributes redirectAttributes
  ) {
    HttpSession session = request.getSession();
    Usuario usuarioLogueado = (Usuario) session.getAttribute("USUARIO");

    if (usuarioLogueado == null) {
      return "redirect:/login?error=Debes+iniciar+sesion+para+realizar+esta+accion";
    }

    try {
      servicioEstadistica.guardarEstadisticasYEnviarARevision(
        idPartido,
        wrapperEstadistica.getEstadisticas()
      );

      redirectAttributes.addFlashAttribute(
        "exito",
        "Las estadisticas se guardaron correctamente, se enviaron al capitan rival para su revision."
      );
      return "redirect:/partido/" + idPartido + "/sala-de-espera";
    } catch (EstadisticasNoGuardadas e) {
      redirectAttributes.addFlashAttribute("error", "No se pudieron guardar las estadisticas.");
      return "redirect:/partido/" + idPartido + "/cargar-estadisticas";
    }
  }
}
