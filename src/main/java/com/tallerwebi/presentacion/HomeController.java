package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Cancha;
import com.tallerwebi.dominio.Notificacion;
import com.tallerwebi.dominio.SolicitudAmistad;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.servicio.ServicioHome;
import com.tallerwebi.dominio.servicio.ServicioNotificacion;
import com.tallerwebi.dominio.servicio.ServicioSolicitudAmistad; // Corregido el nombre
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
  private final ServicioNotificacion servicioNotificacion;
  private final ServicioSolicitudAmistad servicioSolicitudAmistad;

  @Autowired
  public HomeController(
    ServicioHome servicioHome,
    ServicioNotificacion servicioNotificacion,
    ServicioSolicitudAmistad servicioSolicitudAmistad
  ) {
    this.servicioHome = servicioHome;
    this.servicioNotificacion = servicioNotificacion;
    this.servicioSolicitudAmistad = servicioSolicitudAmistad;
  }

  @RequestMapping(path = { "/", "/home" }, method = RequestMethod.GET)
  public ModelAndView irAHome(HttpServletRequest request) {
    Map<String, Object> model = new ModelMap();

    List<Cancha> canchasCarrusel = servicioHome.obtenerCanchasParaHome(4);
    model.put("canchasDestacadas", canchasCarrusel);

    Usuario usuarioLogueado = (Usuario) request.getSession().getAttribute("USUARIO");

    if (usuarioLogueado != null) {
      List<Notificacion> notificaciones = servicioNotificacion.obtenerNotificacionesNoLeidas(
        usuarioLogueado.getId()
      );
      model.put("notificaciones", notificaciones);
      model.put("cantidadNotificaciones", notificaciones.size());

      List<SolicitudAmistad> solicitudes = servicioSolicitudAmistad.obtenerSolicitudesPendientes(
        usuarioLogueado
      );
      model.put("solicitudesPendientes", solicitudes);
    }

    return new ModelAndView("home", model);
  }
}
