package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class PerfilControllerTest {

  @Test
  public void irAPerfilDeberiaRetornarVisitarPerfil() {
    PerfilController controlador = new PerfilController();

    ModelAndView modelAndView = controlador.mostrarFormulario();

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("perfil"));
  }

  @Test
  public void irAEditarPerfilDeberiaRetornarVisitarEditarPerfil() {
    PerfilController controlador = new PerfilController(); // Instanciamos un controlador

    ModelAndView modelAndView = controlador.editarPerfil(); // Nos devuelve editarPerfil

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("editar-perfil")); // Compara
  }

  @Test
  public void guardarPerfilDeberiaRedirigirAlPerfil() {
    PerfilController controlador = new PerfilController();

    ModelAndView modelAndView = controlador.guardarPerfil(
      "Santiago",
      "delantero",
      "Lafe",
      "Naci para este deporte..."
    );

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/perfil"));
  }
}
