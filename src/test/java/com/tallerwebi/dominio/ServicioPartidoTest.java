package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.repositorio.RepositorioCancha;
import com.tallerwebi.dominio.repositorio.RepositorioPartido;
import com.tallerwebi.dominio.repositorio.RepositorioUsuario;
import com.tallerwebi.dominio.servicio.ServicioPartido;
import com.tallerwebi.dominio.servicio.ServicioPartidoImpl;
import com.tallerwebi.presentacion.DatosCrearPartido;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioPartidoTest {

  private ServicioPartido servicioPartido;
  private RepositorioPartido repositorioPartidoMock;

  private RepositorioCancha repositorioCanchaMock;

  private static final String FILTRO_TODOS = "TODOS";
  // private static final String FILTRO_PRIVADO = "PRIVADO";
  // private static final String FILTRO_PUBLICO = "PUBLICO";

  private RepositorioUsuario repositorioUsuarioMock;

  @BeforeEach
  public void init() {
    repositorioPartidoMock = mock(RepositorioPartido.class);
    repositorioUsuarioMock = mock(RepositorioUsuario.class);
    repositorioCanchaMock = mock(RepositorioCancha.class);

    servicioPartido =
      new ServicioPartidoImpl(
        repositorioPartidoMock,
        repositorioCanchaMock,
        repositorioUsuarioMock
      );
  }

  @Test
  public void queUnOrganizadorPuedaCancelarSuPartidoActivoExitosamente() {
    Usuario organizador = new Usuario();
    organizador.setId(1L);

    Partido partido = new Partido();
    partido.setId(10L);
    partido.setOrganizador(organizador);
    partido.setEstado(Partido.EstadoPartido.ACTIVO);

    when(repositorioPartidoMock.buscarPorId(10L)).thenReturn(partido);

    servicioPartido.cancelarPartido(10L, organizador);

    assertEquals(Partido.EstadoPartido.CANCELADO, partido.getEstado());
    verify(repositorioPartidoMock, times(1)).modificar(partido);
  }

  @Test
  public void queUnUsuarioQueNoEsElOrganizadorNoPuedaCancelarElPartido() {
    Usuario organizador = new Usuario();
    organizador.setId(1L);

    Usuario noOrganizador = new Usuario();
    noOrganizador.setId(2L);

    Partido partido = new Partido();
    partido.setId(10L);
    partido.setOrganizador(organizador);
    partido.setEstado(Partido.EstadoPartido.ACTIVO);

    when(repositorioPartidoMock.buscarPorId(10L)).thenReturn(partido);

    Exception exception = assertThrows(
      RuntimeException.class,
      () -> {
        servicioPartido.cancelarPartido(10L, noOrganizador);
      }
    );

    assertEquals("No estas autorizado para cancelar este partido.", exception.getMessage());
    verify(repositorioPartidoMock, never()).modificar(any());
  }

  @Test
  public void queNoSePuedaCancelarUnPartidoQueYaFinalizo() {
    Usuario organizador = new Usuario();
    organizador.setId(1L);

    Partido partido = new Partido();
    partido.setId(10L);
    partido.setOrganizador(organizador);
    partido.setEstado(Partido.EstadoPartido.FINALIZADO);

    when(repositorioPartidoMock.buscarPorId(10L)).thenReturn(partido);

    Exception exception = assertThrows(
      RuntimeException.class,
      () -> {
        servicioPartido.cancelarPartido(10L, organizador);
      }
    );

    assertEquals("No se puede cancelar un partido que ya finalizo.", exception.getMessage());
    verify(repositorioPartidoMock, never()).modificar(any());
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

    List<Partido> resultado = servicioPartido.listarPartidosSegunFiltro(
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
    // Ejecución
    List<String> provincias = servicioPartido.obtenerProvinciasDisponibles();
    List<String> localidades = servicioPartido.obtenerLocalidadesDisponibles();

    // Validación
    assertThat(provincias.size(), equalTo(1)); // "Buenos Aires" no debería duplicarse
    assertThat(provincias.contains("Buenos Aires"), equalTo(true));

    assertThat(localidades.size(), equalTo(2)); // "La Matanza" y "Moron"
    assertThat(localidades.contains("La Matanza"), equalTo(true));
    assertThat(localidades.contains("Moron"), equalTo(true));
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

    // Ejecución
    List<String> provincias = servicioPartido.obtenerProvinciasDisponibles();
    List<String> localidades = servicioPartido.obtenerLocalidadesDisponibles();

    // Validación
    assertThat(provincias.isEmpty(), equalTo(true));
    assertThat(localidades.isEmpty(), equalTo(true));
  }

  @Test
  public void queUnUsuarioPuedaUnirseAPartidoExitosamente() {
    Long partidoId = 1L;
    Partido partido = new Partido();
    partido.setId(partidoId);
    partido.setCupoMaximo(10);
    partido.setInscripciones(new ArrayList<>());
    partido.setEstado(Partido.EstadoPartido.ACTIVO);

    Usuario usuario = new Usuario();
    usuario.setId(100L);
    usuario.setEmail("test@unlam.edu.ar");

    when(repositorioPartidoMock.buscarPorId(partidoId)).thenReturn(partido);

    servicioPartido.unirseAPartido(partidoId, usuario);

    assertThat(partido.getConfirmados(), hasSize(1));
    assertThat(partido.getConfirmados(), hasItem(usuario));
    assertThat(partido.getCantJugadores(), is(1));
    verify(repositorioPartidoMock, times(1)).modificar(partido);
  }

  @Test
  public void queLanceExcepcionSiElPartidoNoExisteAlQuererUnirse() {
    Long partidoIdInexistente = 99L;
    Usuario usuario = new Usuario();

    when(repositorioPartidoMock.buscarPorId(partidoIdInexistente)).thenReturn(null);

    Exception exception = assertThrows(
      RuntimeException.class,
      () -> {
        servicioPartido.unirseAPartido(partidoIdInexistente, usuario);
      }
    );

    assertEquals("El partido no existe.", exception.getMessage());
  }

  @Test
  public void queLanceExcepcionSiElPartidoEstaCanceladoOFinalizado() {
    Long partidoId = 1L;
    Partido partido = new Partido();
    partido.setId(partidoId);
    partido.setEstado(Partido.EstadoPartido.CANCELADO);

    Usuario usuario = new Usuario();

    when(repositorioPartidoMock.buscarPorId(partidoId)).thenReturn(partido);

    Exception exception = assertThrows(
      RuntimeException.class,
      () -> {
        servicioPartido.unirseAPartido(partidoId, usuario);
      }
    );

    assertEquals("No se puede unir a un partido finalizado o cancelado.", exception.getMessage());
  }

  @Test
  public void queLanceExcepcionSiElUsuarioYaEstaUnido() {
    Long partidoId = 1L;
    Partido partido = new Partido();
    partido.setId(partidoId);
    partido.setCupoMaximo(10);
    partido.setEstado(Partido.EstadoPartido.ACTIVO);
    partido.setInscripciones(new ArrayList<>());

    Usuario usuario = new Usuario();
    usuario.setId(100L);

    InscripcionPartido inscripcionExistente = new InscripcionPartido();
    inscripcionExistente.setPartido(partido);
    inscripcionExistente.setUsuario(usuario);
    partido.getInscripciones().add(inscripcionExistente);

    when(repositorioPartidoMock.buscarPorId(partidoId)).thenReturn(partido);

    Exception exception = assertThrows(
      RuntimeException.class,
      () -> {
        servicioPartido.unirseAPartido(partidoId, usuario);
      }
    );

    assertEquals("Ya estas anotado en la sala de espera de este partido.", exception.getMessage());
  }

  @Test
  public void queLanceExcepcionSiSeAlcanzoElCupoMaximo() {
    Long partidoId = 1L;
    Partido partido = new Partido();
    partido.setId(partidoId);
    partido.setCupoMaximo(2);
    partido.setEstado(Partido.EstadoPartido.ACTIVO);
    partido.setInscripciones(new ArrayList<>());

    Usuario usuario1 = new Usuario();
    usuario1.setId(101L);
    Usuario usuario2 = new Usuario();
    usuario2.setId(102L);

    InscripcionPartido inscripcion1 = new InscripcionPartido();
    inscripcion1.setPartido(partido);
    inscripcion1.setUsuario(usuario1);
    partido.getInscripciones().add(inscripcion1);

    InscripcionPartido inscripcion2 = new InscripcionPartido();
    inscripcion2.setPartido(partido);
    inscripcion2.setUsuario(usuario2);
    partido.getInscripciones().add(inscripcion2);

    Usuario usuarioNuevo = new Usuario();
    usuarioNuevo.setId(103L);

    when(repositorioPartidoMock.buscarPorId(partidoId)).thenReturn(partido);

    Exception exception = assertThrows(
      RuntimeException.class,
      () -> {
        servicioPartido.unirseAPartido(partidoId, usuarioNuevo);
      }
    );

    assertEquals("Cupo maximo alcanzado.", exception.getMessage());
  }

  @Test
  public void quePermitaUnirseSiHayMasDeTresHorasDeDiferencia() {
    Long partidoIdNuevo = 1L;
    Long partidoIdExistente = 2L;

    Partido partidoNuevo = new Partido();
    partidoNuevo.setId(partidoIdNuevo);
    partidoNuevo.setCupoMaximo(10);
    partidoNuevo.setEstado(Partido.EstadoPartido.ACTIVO);
    partidoNuevo.setFecha(LocalDateTime.of(2026, 6, 6, 19, 0));
    partidoNuevo.setInscripciones(new ArrayList<>());

    Partido partidoExistente = new Partido();
    partidoExistente.setId(partidoIdExistente);
    partidoExistente.setFecha(LocalDateTime.of(2026, 6, 6, 15, 0));

    Usuario usuario = new Usuario();
    usuario.setId(100L);

    when(repositorioPartidoMock.buscarPorId(partidoIdNuevo)).thenReturn(partidoNuevo);
    when(repositorioPartidoMock.buscarPartidosPorUsuario(usuario.getId()))
      .thenReturn(List.of(partidoExistente));

    servicioPartido.unirseAPartido(partidoIdNuevo, usuario);

    assertThat(partidoNuevo.getConfirmados(), hasSize(1));
    assertThat(partidoNuevo.getConfirmados(), hasItem(usuario));
    verify(repositorioPartidoMock, times(1)).modificar(partidoNuevo);
  }

  @Test
  public void queSePuedaBuscarUnPartidoPorIdExitosamente() {
    Long partidoId = 1L;
    Partido partido = new Partido();
    partido.setId(partidoId);

    when(repositorioPartidoMock.buscarPorId(partidoId)).thenReturn(partido);

    Partido encontrado = servicioPartido.buscarPartidoPorId(partidoId);

    assertNotNull(encontrado);
    assertEquals(partidoId, encontrado.getId());
  }

  @Test
  public void deberiaGuardarElPartidoAlCrearPartido() {
    // Given
    when(repositorioCanchaMock.buscarPorNombre("Cancha Los Leones")).thenReturn(new Cancha());
    DatosCrearPartido datos = new DatosCrearPartido(
      "Cancha Los Leones",
      LocalDateTime.of(2026, 10, 20, 18, 30),
      10,
      "INTERMEDIO"
    );

    // When
    servicioPartido.crearPartido(datos, new Usuario());

    // Then
    verify(repositorioPartidoMock).guardar(any());
  }

  @Test
  public void noDeberiaCrearElPartidoSiLaCanchaIngresadaNoExiste() {
    // Given
    DatosCrearPartido datos = new DatosCrearPartido(
      "Inexistente",
      LocalDateTime.of(2026, 10, 20, 18, 30),
      10,
      "INTERMEDIO"
    );

    // When
    Exception exception = assertThrows(
      RuntimeException.class,
      () -> servicioPartido.crearPartido(datos, new Usuario())
    );

    // Then
    assertEquals("La cancha ingresada no existe.", exception.getMessage());
    verify(repositorioPartidoMock, never()).guardar(any());
  }

  @Test
  public void noDeberiaCrearElPartidoSiElCupoEsCeroONegativo() {
    // Given
    DatosCrearPartido datos = new DatosCrearPartido(
      "Cancha Los Leones",
      LocalDateTime.of(2026, 10, 20, 18, 30),
      0,
      "INTERMEDIO"
    );

    // When
    Exception exception = assertThrows(
      RuntimeException.class,
      () -> servicioPartido.crearPartido(datos, new Usuario())
    );

    // Then
    assertEquals("El cupo debe ser mayor a cero.", exception.getMessage());
    verify(repositorioPartidoMock, never()).guardar(any());
  }

  @Test
  public void noDeberiaCrearElPartidoSiLaFechaEstaVacia() {
    // Given
    DatosCrearPartido datos = new DatosCrearPartido("Cancha Los Leones", null, 10, "INTERMEDIO");

    // When
    Exception exception = assertThrows(
      RuntimeException.class,
      () -> servicioPartido.crearPartido(datos, new Usuario())
    );

    // Then
    assertEquals("Debe ingresar una fecha y hora.", exception.getMessage());
    verify(repositorioPartidoMock, never()).guardar(any());
    //assertThat("La lista no debería estar vacía", resultado.size(), greaterThan(0));
    //Partido partido = resultado.get(0);
    //assertThat(partido.getCu
    // poMaximo(), equalTo(22));
  }

  @Test
  public void deberiaFormarDosEquiposDeCincoConDiezJugadores() {
  }

  @Test
  public void deberiaFormarDosEquiposDeSiesteConCatorceJugadores() {
  }

  @Test
  public void deberiaFormarDosEquiposDeOnceConVeintidosJugadores() {
  }

  @Test
  public void noDeberiaBalancearSiElCupoTodaviaNoEstaCompleto() {
  }

  @Test
  public void deberiaElegirLaDivisionConMenorDiferenciaDePuntos() {
  }

}
