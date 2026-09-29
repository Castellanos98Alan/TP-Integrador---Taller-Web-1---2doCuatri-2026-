package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.ServicioRanking;
import com.tallerwebi.dominio.Usuario;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class RankingControllerTest {

  private RankingController rankingController;
  private ServicioRanking servicioRankingMock;

  @BeforeEach
  public void init() {
    this.servicioRankingMock = mock(ServicioRanking.class);
    this.rankingController = new RankingController(this.servicioRankingMock);
  }

  @Test
  public void mostrarRankingDeberiaMostrarLaVistaRankingConLosJugadores() {
    // preparacion
    Usuario pedro = new Usuario();
    pedro.setNombre("Pedro");
    pedro.setPuntos(1500);

    Usuario juan = new Usuario();
    juan.setNombre("Juan");
    juan.setPuntos(1000);

    List<Usuario> rankingEsperado = new ArrayList<>();
    rankingEsperado.add(pedro);
    rankingEsperado.add(juan);

    when(this.servicioRankingMock.obtenerRanking()).thenReturn(rankingEsperado);

    // ejecucion
    ModelAndView modelAndView = this.rankingController.mostrarRanking();

    // validacion
    assertThat(modelAndView.getViewName(), equalTo("ranking"));
    assertThat(modelAndView.getModel().get("ranking"), equalTo(rankingEsperado));

    verify(this.servicioRankingMock, times(1)).obtenerRanking();
  }
}
