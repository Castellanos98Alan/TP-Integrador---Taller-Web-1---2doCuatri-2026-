package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.ServicioJugador;
import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.ServicioRanking;
import com.tallerwebi.dominio.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PerfilController {

  private final ServicioLogin servicioLogin;
  private final ServicioJugador servicioJugador;
  private final ServicioRanking servicioRanking;
  private static final String FOTO = "foto";

  @Autowired
  public PerfilController(
    ServicioLogin servicioLogin,
    ServicioJugador servicioJugador,
    ServicioRanking servicioRanking
  ) {
    this.servicioLogin = servicioLogin;
    this.servicioJugador = servicioJugador;
    this.servicioRanking = servicioRanking;
  }

  @GetMapping("/perfil")
  public ModelAndView mostrarFormulario(HttpServletRequest request) {
    HttpSession session = request.getSession();
    Usuario usuario = (Usuario) session.getAttribute("USUARIO");

    if (usuario == null) {
      return new ModelAndView("redirect:/login");
    }

    Jugador jugador = servicioJugador.buscarPorUsuarioId(usuario.getId());
    ModelAndView modelAndView = new ModelAndView("perfil");

    modelAndView.addObject("email", usuario.getEmail());
    modelAndView.addObject("rol", usuario.getRol());
    modelAndView.addObject("activo", usuario.getActivo());
    modelAndView.addObject("nickname", usuario.getNickname());
    modelAndView.addObject("nombreJugador", jugador != null ? jugador.getNombre() : null);
    modelAndView.addObject("biografia", usuario.getBiografia());
    modelAndView.addObject("zona", usuario.getZona());

    modelAndView.addObject("nombreJugador", jugador != null ? jugador.getNombre() : null);

    modelAndView.addObject("posicion1", jugador != null ? jugador.getPosicion1() : null);

    modelAndView.addObject("posicion2", jugador != null ? jugador.getPosicion2() : null);

    modelAndView.addObject("puntos", jugador != null ? jugador.getPuntos() : 0);

    modelAndView.addObject(
      "nivel",
      jugador != null ? servicioRanking.calcularNivel(jugador.getPuntos()) : "D"
    );

    modelAndView.addObject(FOTO, session.getAttribute(FOTO));

    return modelAndView;
  }

  @GetMapping("/editar-perfil")
  public ModelAndView editarPerfil(HttpServletRequest request) {
    HttpSession session = request.getSession();
    Usuario usuario = (Usuario) session.getAttribute("USUARIO");

    if (usuario == null) {
      return new ModelAndView("redirect:/login");
    }

    Jugador jugador = servicioJugador.buscarPorUsuarioId(usuario.getId());

    ModelAndView modelAndView = new ModelAndView("editar-perfil");

    modelAndView.addObject("nickname", usuario.getNickname());
    modelAndView.addObject("zona", usuario.getZona());
    modelAndView.addObject("biografia", usuario.getBiografia());
    modelAndView.addObject("posicion1", jugador != null ? jugador.getPosicion1() : null);
    modelAndView.addObject("posicion2", jugador != null ? jugador.getPosicion2() : null);

    modelAndView.addObject(FOTO, session.getAttribute(FOTO));

    return modelAndView;
  }

  @PostMapping("/guardar-perfil")
  public ModelAndView guardarPerfil(
    @RequestParam("nickname") String nickname,
    @RequestParam(value = "posicion1", required = false) String posicion,
    @RequestParam(value = "posicion2", required = false) String posicion2,
    @RequestParam("zona") String zona,
    @RequestParam("biografia") String biografia,
    HttpServletRequest request
  ) {
    HttpSession session = request.getSession();
    Usuario usuario = (Usuario) session.getAttribute("USUARIO");

    Jugador jugador = servicioJugador.buscarPorUsuarioId(usuario.getId());

    if (jugador == null) {
      jugador = new Jugador(nickname, posicion, posicion2, "D");

      jugador.setUsuario(usuario);
      servicioJugador.guardar(jugador);
    } else {
      jugador.setPosicion1(posicion.isEmpty() ? null : posicion);

      jugador.setPosicion2(posicion.isEmpty() ? null : posicion2);

      servicioJugador.modificar(jugador);
    }

    return new ModelAndView("redirect:/perfil");
  }
}
