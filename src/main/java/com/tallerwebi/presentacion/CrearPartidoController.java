package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.ExcepcionNegocio;
import com.tallerwebi.dominio.servicio.ServicioPartido;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
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
  public ModelAndView irACrearPartido(HttpServletRequest request) {
    if (request.getSession().getAttribute("USUARIO") == null) {
      return new ModelAndView("redirect:/login?error=Debes+iniciar+sesion+para+crear+un+partido");
    }

    ModelAndView model = new ModelAndView("crearPartido");

    DatosCrearPartido datos = new DatosCrearPartido();
    datos.setCodigoDeAcceso(servicioPartido.generarCodigoAcceso());

    model.getModelMap().put("datosPartido", datos);
    model.getModelMap().put("localidades", servicioPartido.listarLocalidades());
    model.getModelMap().put("canchas", servicioPartido.listarCanchas());
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
    String error;
    if (datosPartido.getCancha() == null || datosPartido.getCancha().isEmpty()) {
      error = "Debe ingresar una cancha";
    } else if (
      datosPartido.getFechaHora() != null &&
      datosPartido.getFechaHora().isBefore(LocalDateTime.now())
    ) {
      error = "La fecha y hora no pueden ser anteriores a la actual";
    } else {
      try {
        servicioPartido.crearPartido(datosPartido, usuario);
        return new ModelAndView("redirect:/partidos");
      } catch (ExcepcionNegocio e) {
        error = e.getMessage();
      }
    }
    ModelAndView model = new ModelAndView("crearPartido");
    model.getModelMap().put("datosPartido", datosPartido);
    model.getModelMap().put("localidades", servicioPartido.listarLocalidades());
    model.getModelMap().put("canchas", servicioPartido.listarCanchas());
    model.getModelMap().put("error", error);
    return model;
  }
}
