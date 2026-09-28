package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PerfilController {

  private static final String NOMBRE = "nombre";
  private static final String POSICION = "posicion";
  private static final String ZONA = "zona";
  private static final String BIOGRAFIA = "biografia";
  private static final String FOTO = "foto";

  @GetMapping("/perfil")
  public ModelAndView mostrarFormulario(HttpServletRequest request) {
    HttpSession session = request.getSession();
    Usuario usuario = (Usuario) session.getAttribute("USUARIO");

    if (usuario == null) {
      return new ModelAndView("redirect:/login");
    }

    ModelAndView modelAndView = new ModelAndView("perfil");

    modelAndView.getModel().put("email", usuario.getEmail());
    modelAndView.getModel().put("rol", usuario.getRol());
    modelAndView.getModel().put("activo", usuario.getActivo());

    modelAndView.getModel().put(NOMBRE, session.getAttribute(NOMBRE));
    modelAndView.getModel().put(POSICION, session.getAttribute(POSICION));
    modelAndView.getModel().put(ZONA, session.getAttribute(ZONA));
    modelAndView.getModel().put(BIOGRAFIA, session.getAttribute(BIOGRAFIA));
    modelAndView.getModel().put(FOTO, session.getAttribute(FOTO));

    return modelAndView;
  }

  @GetMapping("/editar-perfil")
  public ModelAndView editarPerfil(HttpServletRequest request) {
    HttpSession session = request.getSession();

    if (session.getAttribute("USUARIO") == null) {
      return new ModelAndView("redirect:/login");
    }

    ModelAndView modelAndView = new ModelAndView("editar-perfil");
    modelAndView.getModel().put(FOTO, session.getAttribute(FOTO));

    return modelAndView;
  }

  @PostMapping("/guardar-perfil")
  public ModelAndView guardarPerfil(
    @RequestParam(NOMBRE) String nombre,
    @RequestParam(POSICION) String posicion,
    @RequestParam(ZONA) String zona,
    @RequestParam(BIOGRAFIA) String biografia,
    HttpServletRequest request
  ) {
    HttpSession session = request.getSession();

    if (session.getAttribute("USUARIO") == null) {
      return new ModelAndView("redirect:/login");
    }

    session.setAttribute(NOMBRE, nombre);
    session.setAttribute(POSICION, posicion);
    session.setAttribute(ZONA, zona);
    session.setAttribute(BIOGRAFIA, biografia);

    return new ModelAndView("redirect:/perfil");
  }
}
