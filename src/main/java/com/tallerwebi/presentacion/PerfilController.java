package com.tallerwebi.presentacion;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PerfilController {

  @GetMapping("/perfil")
  public ModelAndView mostrarFormulario() {
    return new ModelAndView("perfil");
  }

  @GetMapping("/editar-perfil")
  public ModelAndView editarPerfil() {
    return new ModelAndView("editar-perfil");
  }

  @PostMapping("/guardar-perfil")
  public ModelAndView guardarPerfil(
    @RequestParam("nombre") String nombre,
    @RequestParam("posicion") String posicion,
    @RequestParam("zona") String zona,
    @RequestParam("biografia") String biografia
  ) {
    ModelAndView modelAndView = new ModelAndView("perfil");

    modelAndView.addObject("nombre", nombre);
    modelAndView.addObject("posicion", posicion);
    modelAndView.addObject("zona", zona);
    modelAndView.addObject("biografia", biografia);

    return modelAndView;
  }
}
