package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.servicio.ServicioLogin;
import com.tallerwebi.dominio.servicio.ServicioSolicitudAmistad;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SolicitudAmistadController {

  private final ServicioSolicitudAmistad servicioSolicitudAmistad;
  private final ServicioLogin servicioLogin;

  public SolicitudAmistadController(
    ServicioSolicitudAmistad servicioSolicitudAmistad,
    ServicioLogin servicioLogin
  ) {
    this.servicioSolicitudAmistad = servicioSolicitudAmistad;
    this.servicioLogin = servicioLogin;
  }

  // ACEPTAR LA SOLUCITUD

  @RequestMapping(path = "/amistad/aceptar", method = RequestMethod.POST)
  public ModelAndView aceptarSolicitud(@RequestParam("idSolicitud") Integer idSolicitud) {
    servicioSolicitudAmistad.aceptarSolicitud(idSolicitud);

    return new ModelAndView("redirect:/home");
  }

  // RECHAZAR SOLICITUD

  @RequestMapping(path = "/amistad/rechazar", method = RequestMethod.POST)
  public ModelAndView rechazarSolicitud(@RequestParam("idSolicitud") Integer idSolicitud) {
    servicioSolicitudAmistad.rechazarSolicitud(idSolicitud);

    return new ModelAndView("redirect:/home");
  }

  // ENVIAR SOLICITUD

  @RequestMapping(path = "/amistad/enviar", method = RequestMethod.POST)
  public ModelAndView enviarSolicitud(
    @RequestParam("idUsuario") Long idUsuario,
    HttpServletRequest request
  ) {
    Usuario usuarioLogueado = (Usuario) request.getSession().getAttribute("USUARIO");

    Usuario receptor = servicioLogin.buscarUsuarioPorId(idUsuario);

    servicioSolicitudAmistad.enviarSolicitud(usuarioLogueado, receptor);

    return new ModelAndView("redirect:/perfil?id=" + idUsuario);
  }
}
