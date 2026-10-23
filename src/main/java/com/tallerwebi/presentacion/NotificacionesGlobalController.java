package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.SolicitudAmistad;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.servicio.ServicioSolicitudAmistad;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class NotificacionesGlobalController {

  private final ServicioSolicitudAmistad servicioSolicitudAmistad;

  public NotificacionesGlobalController(ServicioSolicitudAmistad servicioSolicitudAmistad) {
    this.servicioSolicitudAmistad = servicioSolicitudAmistad;
  }

  @ModelAttribute
  public void cargarSolicitudesPendientes(HttpServletRequest request, Model model) {
    Usuario usuarioLogueado = (Usuario) request.getSession().getAttribute("USUARIO");

    if (usuarioLogueado != null) {
      List<SolicitudAmistad> solicitudes = servicioSolicitudAmistad.obtenerSolicitudesPendientes(
        usuarioLogueado
      );

      model.addAttribute("solicitudesPendientes", solicitudes);
    } else {
      model.addAttribute("solicitudesPendientes", Collections.emptyList());
    }
  }
}
