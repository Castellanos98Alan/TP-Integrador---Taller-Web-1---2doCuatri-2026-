package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.repositorio.RepositorioPartido;
import com.tallerwebi.dominio.servicio.ServicioPartidoFiltro;
import com.tallerwebi.dominio.servicio.ServicioPartidoFiltroImp;
import com.tallerwebi.dominio.servicio.ServicioPartidoImpl;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioPartidoFiltroTest {

  private static final String FILTRO_TODOS = "TODOS";

  private RepositorioPartido repositorioPartidoMock;
  private ServicioPartidoFiltro servicioPartidoFiltro;

  @BeforeEach
  public void init() {
    repositorioPartidoMock = mock(RepositorioPartido.class);

    servicioPartidoFiltro = new ServicioPartidoFiltroImp(repositorioPartidoMock);
  }

  @Test
  public void deberiaDelegarLaBusquedaAlRepositorioConLosFiltrosCorrectos() {
    List<Partido> listaFalsa = List.of(new Partido(), new Partido());

    when(
      repositorioPartidoMock.buscarPartidosPorFiltro(
        "PRINCIPIANTE",
        FILTRO_TODOS,
        "22",
        FILTRO_TODOS,
        FILTRO_TODOS,
        FILTRO_TODOS,
        FILTRO_TODOS
      )
    )
      .thenReturn(listaFalsa);

    List<Partido> resultado = servicioPartidoFiltro.listarPartidosSegunFiltro(
      "PRINCIPIANTE",
      FILTRO_TODOS,
      "22",
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS
    );

    assertEquals(listaFalsa, resultado);

    verify(repositorioPartidoMock)
      .buscarPartidosPorFiltro(
        "PRINCIPIANTE",
        FILTRO_TODOS,
        "22",
        FILTRO_TODOS,
        FILTRO_TODOS,
        FILTRO_TODOS,
        FILTRO_TODOS
      );
  }

  @Test
  public void queSePuedanObtenerProvinciasYLocalidadesDisponiblesSinRepetir() {
    Partido partido1 = new Partido();

    Cancha cancha1 = new Cancha();
    cancha1.setProvincia("Buenos Aires");
    cancha1.setLocalidad("La Matanza");

    partido1.setCancha(cancha1);

    Partido partido2 = new Partido();

    Cancha cancha2 = new Cancha();
    cancha2.setProvincia("Buenos Aires");
    cancha2.setLocalidad("Moron");

    partido2.setCancha(cancha2);

    List<Partido> partidosSimulados = Arrays.asList(partido1, partido2);

    when(
      repositorioPartidoMock.buscarPartidosPorFiltro(
        anyString(),
        anyString(),
        anyString(),
        anyString(),
        anyString(),
        anyString(),
        anyString()
      )
    )
      .thenReturn(partidosSimulados);

    List<String> provincias = servicioPartidoFiltro.obtenerProvinciasDisponibles();

    List<String> localidades = servicioPartidoFiltro.obtenerLocalidadesDisponibles();

    assertThat(provincias.size(), equalTo(1));

    assertTrue(provincias.contains("Buenos Aires"));

    assertThat(localidades.size(), equalTo(2));

    assertTrue(localidades.contains("La Matanza"));

    assertTrue(localidades.contains("Moron"));
  }

  @Test
  public void queNoFalleAlObtenerProvinciasYLocalidadesSiLaCanchaOAtributosSonNulos() {
    Partido partidoSinCancha = new Partido();

    partidoSinCancha.setCancha(null);

    Partido partidoSinUbicacion = new Partido();

    Cancha canchaVacia = new Cancha();

    canchaVacia.setProvincia(null);
    canchaVacia.setLocalidad(null);

    partidoSinUbicacion.setCancha(canchaVacia);

    List<Partido> partidosIncompletos = Arrays.asList(partidoSinCancha, partidoSinUbicacion);

    when(
      repositorioPartidoMock.buscarPartidosPorFiltro(
        anyString(),
        anyString(),
        anyString(),
        anyString(),
        anyString(),
        anyString(),
        anyString()
      )
    )
      .thenReturn(partidosIncompletos);

    List<String> provincias = servicioPartidoFiltro.obtenerProvinciasDisponibles();

    List<String> localidades = servicioPartidoFiltro.obtenerLocalidadesDisponibles();

    assertTrue(provincias.isEmpty());

    assertTrue(localidades.isEmpty());
  }

  @Test
  public void deberiaObtenerFechasDisponiblesSinRepetir() {
    Partido partido1 = new Partido();

    partido1.setFecha(LocalDateTime.of(2026, 10, 20, 18, 0));

    Partido partido2 = new Partido();

    partido2.setFecha(LocalDateTime.of(2026, 10, 20, 20, 0));

    Partido partido3 = new Partido();

    partido3.setFecha(LocalDateTime.of(2026, 10, 21, 18, 0));

    when(
      repositorioPartidoMock.buscarPartidosPorFiltro(
        anyString(),
        anyString(),
        anyString(),
        anyString(),
        anyString(),
        anyString(),
        anyString()
      )
    )
      .thenReturn(List.of(partido1, partido2, partido3));

    List<String> fechas = servicioPartidoFiltro.obtenerFechasDisponibles();

    assertEquals(2, fechas.size());

    assertTrue(fechas.contains("2026-10-20"));

    assertTrue(fechas.contains("2026-10-21"));
  }

  @Test
  public void deberiaObtenerHorasDisponiblesSinRepetir() {
    Partido partido1 = new Partido();

    partido1.setFecha(LocalDateTime.of(2026, 10, 20, 18, 30));

    Partido partido2 = new Partido();

    partido2.setFecha(LocalDateTime.of(2026, 10, 21, 18, 30));

    Partido partido3 = new Partido();

    partido3.setFecha(LocalDateTime.of(2026, 10, 21, 20, 0));

    when(
      repositorioPartidoMock.buscarPartidosPorFiltro(
        anyString(),
        anyString(),
        anyString(),
        anyString(),
        anyString(),
        anyString(),
        anyString()
      )
    )
      .thenReturn(List.of(partido1, partido2, partido3));

    List<String> horas = servicioPartidoFiltro.obtenerHorasDisponibles();

    assertEquals(2, horas.size());

    assertTrue(horas.contains("18:30"));

    assertTrue(horas.contains("20:00"));
  }

  @Test
  public void deberiaObtenerCuposDisponiblesSinRepetir() {
    Partido partido1 = new Partido();

    partido1.setCupoMaximo(10);

    Partido partido2 = new Partido();

    partido2.setCupoMaximo(10);

    Partido partido3 = new Partido();

    partido3.setCupoMaximo(14);

    when(
      repositorioPartidoMock.buscarPartidosPorFiltro(
        anyString(),
        anyString(),
        anyString(),
        anyString(),
        anyString(),
        anyString(),
        anyString()
      )
    )
      .thenReturn(List.of(partido1, partido2, partido3));

    List<Integer> cupos = servicioPartidoFiltro.obtenerCuposDisponibles();

    assertEquals(2, cupos.size());

    assertTrue(cupos.contains(10));

    assertTrue(cupos.contains(14));
  }
}
