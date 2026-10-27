package com.tallerwebi.presentacion;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.servicio.ServicioEstadistica;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class EstadisticaControllerTest {

  private MockMvc mockMvc;
  private ServicioEstadistica servicioEstadisticaMock;
  private EstadisticaController estadisticaController;

  @BeforeEach
  public void setUp() {
    servicioEstadisticaMock = Mockito.mock(ServicioEstadistica.class);
    estadisticaController = new EstadisticaController(servicioEstadisticaMock);
    mockMvc = MockMvcBuilders.standaloneSetup(estadisticaController).build();
  }

  @Test
  public void queSePuedanGuardarEstadisticasExitosamenteYRedirija() throws Exception {
    Usuario usuarioMock = new Usuario();
    usuarioMock.setId(1L);

    mockMvc
      .perform(
        post("/partido/1/guardar-estadisticas")
          .sessionAttr("USUARIO", usuarioMock)
          .param("estadisticas[0].idJugador", "10")
          .param("estadisticas[0].goles", "2")
          .param("estadisticas[0].amarillas", "1")
          .param("estadisticas[0].rojas", "0")
      )
      .andExpect(status().is3xxRedirection())
      .andExpect(
        redirectedUrl(
          "/partido/1/sala-de-espera?exito=Las+estadisticas+se+guardaron+correctamente+y+estan+en+revision."
        )
      );
  }

  @Test
  public void siElUsuarioNoEstaLogueadoDebeRedirigirAlLogin() throws Exception {
    mockMvc
      .perform(post("/partido/1/guardar-estadisticas").param("estadisticas[0].idJugador", "10"))
      .andExpect(status().is3xxRedirection())
      .andExpect(redirectedUrl("/login?error=Debes+iniciar+sesion+para+realizar+esta+accion"));
  }
}
