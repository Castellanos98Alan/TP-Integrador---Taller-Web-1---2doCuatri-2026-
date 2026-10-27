package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.SolicitudAmistadInvalida;
import com.tallerwebi.dominio.servicio.ServicioLogin;
import com.tallerwebi.dominio.servicio.ServicioSolicitudAmistad;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
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
  public ModelAndView aceptarSolicitud(
    @RequestParam("idSolicitud") Integer idSolicitud,
    @RequestParam("volverA") String volverA
  ) {
    servicioSolicitudAmistad.aceptarSolicitud(idSolicitud);

    return new ModelAndView("redirect:" + volverA);
  }

  // RECHAZAR SOLICITUD

  @RequestMapping(path = "/amistad/rechazar", method = RequestMethod.POST)
  public ModelAndView rechazarSolicitud(
    @RequestParam("idSolicitud") Integer idSolicitud,
    @RequestParam("volverA") String volverA
  ) {
    servicioSolicitudAmistad.rechazarSolicitud(idSolicitud);

    return new ModelAndView("redirect:" + volverA);
  }

  // ENVIAR SOLICITUD

  @PostMapping("/amistad/enviar")
  public ModelAndView enviarSolicitud(
    @RequestParam("idUsuario") Long idUsuario,
    HttpServletRequest request
  ) {
    Usuario usuarioLogueado = (Usuario) request.getSession().getAttribute("USUARIO");

    Usuario receptor = servicioLogin.buscarUsuarioPorId(idUsuario);

    try {
      servicioSolicitudAmistad.enviarSolicitud(usuarioLogueado, receptor);

      return new ModelAndView("redirect:/perfil/" + idUsuario + "?solicitudEnviada=true");
    } catch (SolicitudAmistadInvalida e) {
      return new ModelAndView("redirect:/perfil/" + idUsuario + "?errorAmistad=true");
    }
  }
}
