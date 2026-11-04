package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.servicio.ServicioNotificacion;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class NotificacionController {

  private final ServicioNotificacion servicioNotificacion;

  public NotificacionController(ServicioNotificacion servicioNotificacion) {
    this.servicioNotificacion = servicioNotificacion;
  }

  @RequestMapping(path = "/notificacion/marcar-leida", method = RequestMethod.POST)
  public ModelAndView marcarComoLeida(
    @RequestParam("idNotificacion") Long idNotificacion,
    @RequestParam("volverA") String volverA
  ) {
    servicioNotificacion.marcarComoLeida(idNotificacion);

    return new ModelAndView("redirect:" + volverA);
  }
}
