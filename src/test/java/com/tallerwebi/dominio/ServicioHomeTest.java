package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.repositorio.RepositorioCancha;
import com.tallerwebi.dominio.servicio.ServicioHome;
import com.tallerwebi.dominio.servicio.ServicioHomeImp;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioHomeTest {

  private RepositorioCancha repositorioCanchaMock;
  private ServicioHome servicioHome;

  @BeforeEach
  public void init() {
    repositorioCanchaMock = mock(RepositorioCancha.class);
    servicioHome = new ServicioHomeImp(repositorioCanchaMock);
  }

  @Test
  public void queSePuedanObtenerLasCanchasDestacadasParaLaHome() {
    // Preparación
    Integer limiteEsperado = 4;
    List<Cancha> canchasSimuladas = new ArrayList<>();
    canchasSimuladas.add(new Cancha());
    canchasSimuladas.add(new Cancha());

    when(repositorioCanchaMock.buscarDestacadas(limiteEsperado)).thenReturn(canchasSimuladas);

    // Ejecución
    List<Cancha> obtenidas = servicioHome.obtenerCanchasParaHome(limiteEsperado);

    // Validación
    assertThat(obtenidas.size(), is(equalTo(2)));
    assertThat(obtenidas, is(equalTo(canchasSimuladas)));
    verify(repositorioCanchaMock, times(1)).buscarDestacadas(limiteEsperado);
  }
}
