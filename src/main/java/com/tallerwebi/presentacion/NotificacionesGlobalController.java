package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Notificacion;
import com.tallerwebi.dominio.SolicitudAmistad;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.servicio.ServicioNotificacion;
import com.tallerwebi.dominio.servicio.ServicioSolicitudAmistad;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class NotificacionesGlobalController {

  private final ServicioSolicitudAmistad servicioSolicitudAmistad;
  private final ServicioNotificacion servicioNotificacion;

  public NotificacionesGlobalController(
    ServicioSolicitudAmistad servicioSolicitudAmistad,
    ServicioNotificacion servicioNotificacion
  ) {
    this.servicioSolicitudAmistad = servicioSolicitudAmistad;

    this.servicioNotificacion = servicioNotificacion;
  }

  @ModelAttribute
  public void cargarNotificaciones(HttpServletRequest request, Model model) {
    Usuario usuarioLogueado = (Usuario) request.getSession().getAttribute("USUARIO");

    if (usuarioLogueado != null) {
      List<SolicitudAmistad> solicitudesPendientes =
        servicioSolicitudAmistad.obtenerSolicitudesPendientes(usuarioLogueado);

      List<Notificacion> notificaciones = servicioNotificacion.obtenerNotificacionesNoLeidas(
        usuarioLogueado.getId()
      );

      model.addAttribute("solicitudesPendientes", solicitudesPendientes);

      model.addAttribute("notificaciones", notificaciones);

      model.addAttribute(
        "cantidadNotificaciones",
        solicitudesPendientes.size() + notificaciones.size()
      );
    } else {
      model.addAttribute("solicitudesPendientes", new ArrayList<SolicitudAmistad>());

      model.addAttribute("notificaciones", new ArrayList<Notificacion>());

      model.addAttribute("cantidadNotificaciones", 0);
    }
  }
}
