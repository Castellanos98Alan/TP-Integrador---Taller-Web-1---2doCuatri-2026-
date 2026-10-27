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
import com.tallerwebi.dominio.servicio.ServicioJugador;
import com.tallerwebi.dominio.servicio.ServicioPartido;
import com.tallerwebi.dominio.servicio.ServicioPartidoImpl;
import com.tallerwebi.presentacion.DatosCrearPartido;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioPartidoTest {

  private ServicioPartido servicioPartido;
  private ServicioJugador servicioJugadorMock; // 1. Cambia el nombre para dejar claro que es un mock
  private RepositorioPartido repositorioPartidoMock;
  private RepositorioCancha repositorioCanchaMock;
  private RepositorioUsuario repositorioUsuarioMock;

  private static final String FILTRO_TODOS = "TODOS";

  // private static final String FILTRO_PRIVADO = "PRIVADO";
  // private static final String FILTRO_PUBLICO = "PUBLICO";

  @BeforeEach
  public void init() {
    repositorioPartidoMock = mock(RepositorioPartido.class);
    repositorioUsuarioMock = mock(RepositorioUsuario.class);
    repositorioCanchaMock = mock(RepositorioCancha.class);
    servicioJugadorMock = mock(ServicioJugador.class);

    servicioPartido =
      new ServicioPartidoImpl(
        repositorioPartidoMock,
        repositorioCanchaMock,
        repositorioUsuarioMock,
        servicioJugadorMock
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
    // 1. Configuración del escenario (Given)
    Long partidoId = 1L;
    Partido partido = new Partido();
    partido.setId(partidoId);
    partido.setCupoMaximo(10);
    partido.setInscripciones(new ArrayList<>());
    partido.setEstado(Partido.EstadoPartido.ACTIVO);

    Usuario usuarioMock = new Usuario();
    usuarioMock.setId(100L);
    usuarioMock.setEmail("test@unlam.edu.ar");

    Jugador jugadorMock = new Jugador();
    jugadorMock.setId(1L);
    jugadorMock.setUsuario(usuarioMock);

    when(repositorioPartidoMock.buscarPorId(partidoId)).thenReturn(partido);

    // 2. Ejecución (When)
    servicioPartido.unirseAPartido(partidoId, jugadorMock);

    // 3. Verificaciones (Then)
    // NOTA: Si tu lista ahora guarda Jugadores, cambia 'usuarioMock' por 'jugadorMock' aquí:
    assertThat(partido.getConfirmados(), hasSize(1));
    assertThat(partido.getCantJugadores(), is(1));
    verify(repositorioPartidoMock, times(1)).modificar(partido);
  }

  @Test
  public void queLanceExcepcionSiElPartidoNoExisteAlQuererUnirse() {
    // 1. Configuración del escenario
    Long partidoIdInexistente = 99L;

    // Instanciamos directamente el objeto vacío sin llamar a ningún servicio
    Jugador jugador = new Jugador();

    when(repositorioPartidoMock.buscarPorId(partidoIdInexistente)).thenReturn(null);

    // 2. Ejecución y Verificación
    Exception exception = assertThrows(
      RuntimeException.class,
      () -> {
        servicioPartido.unirseAPartido(partidoIdInexistente, jugador);
      }
    );

    assertEquals("El partido no existe.", exception.getMessage());
  }

  @Test
  public void queLanceExcepcionSiElPartidoEstaCanceladoOFinalizado() {
    // 1. Configuración (Usamos servicioJugadorMock o creamos el objeto directamente)
    Long partidoIdInexistente = 99L;
    Jugador jugador = new Jugador(); // No hace falta buscarlo en la BD/Mock si el partido va a fallar antes

    when(repositorioPartidoMock.buscarPorId(partidoIdInexistente)).thenReturn(null);

    // 2. Ejecución y Verificación (When/Then)
    Exception exception = assertThrows(
      RuntimeException.class,
      () -> {
        servicioPartido.unirseAPartido(partidoIdInexistente, jugador);
      }
    );

    assertEquals("El partido no existe.", exception.getMessage());
  }

  @Test
  public void queLanceExcepcionSiElUsuarioYaEstaUnido() {
    Long partidoId = 1L;
    Partido partido = new Partido();
    partido.setId(partidoId);
    partido.setCupoMaximo(10);
    partido.setEstado(Partido.EstadoPartido.ACTIVO);
    partido.setInscripciones(new ArrayList<>());

    // Instanciamos el jugador directamente
    Jugador jugador = new Jugador();
    jugador.setId(100L);

    InscripcionPartido inscripcionExistente = new InscripcionPartido();
    inscripcionExistente.setPartido(partido);
    inscripcionExistente.setJugador(jugador);
    partido.getInscripciones().add(inscripcionExistente);

    when(repositorioPartidoMock.buscarPorId(partidoId)).thenReturn(partido);

    Exception exception = assertThrows(
      RuntimeException.class,
      () -> {
        servicioPartido.unirseAPartido(partidoId, jugador);
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

    // Creamos los jugadores directamente sin llamar al servicio inexistente
    Jugador jugador1 = new Jugador();
    jugador1.setId(101L);

    Jugador jugador2 = new Jugador();
    jugador2.setId(102L);

    InscripcionPartido inscripcion1 = new InscripcionPartido();
    inscripcion1.setPartido(partido);
    inscripcion1.setJugador(jugador1);
    partido.getInscripciones().add(inscripcion1);

    InscripcionPartido inscripcion2 = new InscripcionPartido();
    inscripcion2.setPartido(partido);
    inscripcion2.setJugador(jugador2);
    partido.getInscripciones().add(inscripcion2);

    Jugador jugadorNuevo = new Jugador();
    jugadorNuevo.setId(103L);

    when(repositorioPartidoMock.buscarPorId(partidoId)).thenReturn(partido);

    Exception exception = assertThrows(
      RuntimeException.class,
      () -> {
        servicioPartido.unirseAPartido(partidoId, jugadorNuevo);
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

    Jugador jugador = new Jugador();
    jugador.setId(50L);
    jugador.setUsuario(usuario);

    when(repositorioPartidoMock.buscarPorId(partidoIdNuevo)).thenReturn(partidoNuevo);

    // NOTA: Si en tu ServicioPartidoImpl cambiaste la consulta al repositorio para usar jugador.getId()
    // debes usar 'buscarPartidosPorJugador' (o como se llame ahora en tu RepositorioPartido).
    // Dejo 'buscarPartidosPorJugador' asumiendo la refactorización lógica:
    when(repositorioPartidoMock.buscarPartidosPorJugador(jugador.getId()))
      .thenReturn(List.of(partidoExistente));

    servicioPartido.unirseAPartido(partidoIdNuevo, jugador);

    // Si partidoNuevo.getConfirmados() ahora tiene Jugadores, cambia 'usuario' por 'jugador'
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
    // 1. Configuración del escenario (Given)
    Partido partido = new Partido();
    partido.setId(1L);

    Equipo local = new Equipo();
    local.setId(1L);
    local.setNombre("Equipo Local");

    Equipo visitante = new Equipo();
    visitante.setId(2L);
    visitante.setNombre("Equipo Visitante");

    partido.setEquipo1(local);
    partido.setEquipo2(visitante);

    // Crear 10 inscripciones con jugadores y puntajes descendentes (de 100 a 10)
    List<InscripcionPartido> inscripciones = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      Jugador jugador = new Jugador();
      jugador.setId((long) i);
      jugador.setPuntos((10 - i) * 10); // Jugador 0=100pts, Jugador 1=90pts, etc.

      InscripcionPartido inscripcion = new InscripcionPartido();
      inscripcion.setPartido(partido);
      inscripcion.setJugador(jugador);

      inscripciones.add(inscripcion);
    }

    // Desordenamos la lista inicial para verificar que el método realmente la ordene por puntos
    Collections.shuffle(inscripciones);
    partido.setInscripciones(inscripciones);

    // 2. Ejecución de la lógica (When)
    servicioPartido.balancearEquipos(partido);

    // 3. Verificaciones (Then)
    List<InscripcionPartido> inscripcionesOrdenadas = partido.getInscripciones();

    // Contadores para verificar distribución
    int cantLocal = 0;
    int cantVisitante = 0;

    for (InscripcionPartido ins : inscripcionesOrdenadas) {
      if (ins.getEquipo().equals(local)) {
        cantLocal++;
      } else if (ins.getEquipo().equals(visitante)) {
        cantVisitante++;
      }
    }

    // Verificación A: Cada equipo debe tener exactamente 5 jugadores asignados
    assertThat(cantLocal, is(5));
    assertThat(cantVisitante, is(5));

    // Verificación B: El algoritmo Snake Draft debe asignar los índices correctos al equipo Local
    // Índices esperados en Local: 0 (100pts), 3 (70pts), 4 (60pts), 7 (30pts), 8 (20pts)
    assertThat(inscripcionesOrdenadas.get(0).getEquipo(), is(local));
    assertThat(inscripcionesOrdenadas.get(3).getEquipo(), is(local));
    assertThat(inscripcionesOrdenadas.get(4).getEquipo(), is(local));
    assertThat(inscripcionesOrdenadas.get(7).getEquipo(), is(local));
    assertThat(inscripcionesOrdenadas.get(8).getEquipo(), is(local));

    // Verificación C: Los índices intermedios deben ir al visitante (1, 2, 5, 6, 9)
    assertThat(inscripcionesOrdenadas.get(1).getEquipo(), is(visitante));
    assertThat(inscripcionesOrdenadas.get(2).getEquipo(), is(visitante));
    assertThat(inscripcionesOrdenadas.get(5).getEquipo(), is(visitante));
  }

  @Test
  public void deberiaFormarDosEquiposDeSieteConCatorceJugadores() {
    Partido partido = new Partido();
    partido.setId(1L);

    Equipo local = new Equipo();
    local.setId(1L);
    local.setNombre("Equipo Local");

    Equipo visitante = new Equipo();
    visitante.setId(2L);
    visitante.setNombre("Equipo Visitante");

    partido.setEquipo1(local);
    partido.setEquipo2(visitante);

    List<InscripcionPartido> inscripciones = new ArrayList<>();

    for (int i = 0; i < 14; i++) {
      Jugador jugador = new Jugador();
      jugador.setId((long) i);
      jugador.setPuntos((14 - i) * 10);

      InscripcionPartido inscripcion = new InscripcionPartido();
      inscripcion.setPartido(partido);
      inscripcion.setJugador(jugador);

      inscripciones.add(inscripcion);
    }

    Collections.shuffle(inscripciones);
    partido.setInscripciones(inscripciones);

    servicioPartido.balancearEquipos(partido);

    List<InscripcionPartido> inscripcionesOrdenadas = partido.getInscripciones();

    int cantLocal = 0;
    int cantVisitante = 0;

    for (InscripcionPartido ins : inscripcionesOrdenadas) {
      if (ins.getEquipo().equals(local)) {
        cantLocal++;
      } else if (ins.getEquipo().equals(visitante)) {
        cantVisitante++;
      }
    }

    assertThat(cantLocal, is(7));
    assertThat(cantVisitante, is(7));

    assertThat(inscripcionesOrdenadas.get(0).getEquipo(), is(local));
    assertThat(inscripcionesOrdenadas.get(3).getEquipo(), is(local));
    assertThat(inscripcionesOrdenadas.get(4).getEquipo(), is(local));
    assertThat(inscripcionesOrdenadas.get(7).getEquipo(), is(local));
    assertThat(inscripcionesOrdenadas.get(8).getEquipo(), is(local));
    assertThat(inscripcionesOrdenadas.get(11).getEquipo(), is(local));
    assertThat(inscripcionesOrdenadas.get(12).getEquipo(), is(local));

    assertThat(inscripcionesOrdenadas.get(1).getEquipo(), is(visitante));
    assertThat(inscripcionesOrdenadas.get(2).getEquipo(), is(visitante));
    assertThat(inscripcionesOrdenadas.get(5).getEquipo(), is(visitante));
    assertThat(inscripcionesOrdenadas.get(6).getEquipo(), is(visitante));
    assertThat(inscripcionesOrdenadas.get(9).getEquipo(), is(visitante));
    assertThat(inscripcionesOrdenadas.get(10).getEquipo(), is(visitante));
    assertThat(inscripcionesOrdenadas.get(13).getEquipo(), is(visitante));
  }

  @Test
  public void deberiaFormarDosEquiposDeOnceConVeintidosJugadores() {
    Partido partido = new Partido();
    partido.setId(1L);

    Equipo local = new Equipo();
    local.setId(1L);
    local.setNombre("Equipo Local");

    Equipo visitante = new Equipo();
    visitante.setId(2L);
    visitante.setNombre("Equipo Visitante");

    partido.setEquipo1(local);
    partido.setEquipo2(visitante);

    List<InscripcionPartido> inscripciones = new ArrayList<>();

    for (int i = 0; i < 22; i++) {
      Jugador jugador = new Jugador();
      jugador.setId((long) i);
      jugador.setPuntos((22 - i) * 10);

      InscripcionPartido inscripcion = new InscripcionPartido();
      inscripcion.setPartido(partido);
      inscripcion.setJugador(jugador);

      inscripciones.add(inscripcion);
    }

    Collections.shuffle(inscripciones);
    partido.setInscripciones(inscripciones);

    servicioPartido.balancearEquipos(partido);

    List<InscripcionPartido> inscripcionesOrdenadas = partido.getInscripciones();

    int cantLocal = 0;
    int cantVisitante = 0;

    for (InscripcionPartido ins : inscripcionesOrdenadas) {
      if (ins.getEquipo().equals(local)) {
        cantLocal++;
      } else if (ins.getEquipo().equals(visitante)) {
        cantVisitante++;
      }
    }

    assertThat(cantLocal, is(11));
    assertThat(cantVisitante, is(11));

    assertThat(inscripcionesOrdenadas.get(0).getEquipo(), is(local));
    assertThat(inscripcionesOrdenadas.get(3).getEquipo(), is(local));
    assertThat(inscripcionesOrdenadas.get(4).getEquipo(), is(local));
    assertThat(inscripcionesOrdenadas.get(7).getEquipo(), is(local));
    assertThat(inscripcionesOrdenadas.get(8).getEquipo(), is(local));
    assertThat(inscripcionesOrdenadas.get(11).getEquipo(), is(local));
    assertThat(inscripcionesOrdenadas.get(12).getEquipo(), is(local));
    assertThat(inscripcionesOrdenadas.get(15).getEquipo(), is(local));
    assertThat(inscripcionesOrdenadas.get(16).getEquipo(), is(local));
    assertThat(inscripcionesOrdenadas.get(19).getEquipo(), is(local));
    assertThat(inscripcionesOrdenadas.get(20).getEquipo(), is(local));

    assertThat(inscripcionesOrdenadas.get(1).getEquipo(), is(visitante));
    assertThat(inscripcionesOrdenadas.get(2).getEquipo(), is(visitante));
    assertThat(inscripcionesOrdenadas.get(5).getEquipo(), is(visitante));
    assertThat(inscripcionesOrdenadas.get(6).getEquipo(), is(visitante));
    assertThat(inscripcionesOrdenadas.get(9).getEquipo(), is(visitante));
    assertThat(inscripcionesOrdenadas.get(10).getEquipo(), is(visitante));
    assertThat(inscripcionesOrdenadas.get(13).getEquipo(), is(visitante));
    assertThat(inscripcionesOrdenadas.get(14).getEquipo(), is(visitante));
    assertThat(inscripcionesOrdenadas.get(17).getEquipo(), is(visitante));
    assertThat(inscripcionesOrdenadas.get(18).getEquipo(), is(visitante));
    assertThat(inscripcionesOrdenadas.get(21).getEquipo(), is(visitante));
  }

  @Test
  public void noDeberiaBalancearSiElCupoTodaviaNoEstaCompleto() {
    // Given
    Long partidoId = 1L;

    Partido partido = new Partido();
    partido.setId(partidoId);
    partido.setCupoMaximo(10);
    partido.setCantJugadores(8);
    partido.setInscripciones(new ArrayList<>());

    // Agregamos 8 inscripciones: todavía faltan 2 jugadores
    for (int i = 0; i < 8; i++) {
      Jugador jugador = new Jugador();
      jugador.setId((long) i);

      InscripcionPartido inscripcion = new InscripcionPartido(partido, jugador);

      partido.getInscripciones().add(inscripcion);
    }

    Usuario usuario = new Usuario();
    usuario.setId(100L);

    Jugador nuevoJugador = new Jugador();
    nuevoJugador.setId(100L);
    nuevoJugador.setUsuario(usuario);

    when(repositorioPartidoMock.buscarPorId(partidoId)).thenReturn(partido);
    when(repositorioPartidoMock.buscarPartidosPorJugador(100L)).thenReturn(new ArrayList<>());

    // Spy del servicio para comprobar si se invoca el balanceo
    ServicioPartido servicioSpy = spy(servicioPartido);

    // When
    servicioSpy.unirseAPartido(partidoId, nuevoJugador);

    // Then: el balanceo no debe ejecutarse
    verify(servicioSpy, never()).balancearEquipos(any(Partido.class));
  }

  @Test
  public void deberiaElegirLaDivisionConMenorDiferenciaDePuntos() {}
  //  @Test
  //  public void deberiaAsignarUnCapitanPorEquipo() {
  //    Partido partido = new Partido();
  //
  //    Equipo local = new Equipo();
  //    local.setId(1L);
  //
  //    Equipo visitante = new Equipo();
  //    visitante.setId(2L);
  //
  //    partido.setEquipo1(local);
  //    partido.setEquipo2(visitante);
  //
  //    Jugador jugador1 = new Jugador();
  //    jugador1.setPuntos(80);
  //
  //    Jugador jugador2 = new Jugador();
  //    jugador2.setPuntos(60);
  //
  //    Jugador jugador3 = new Jugador();
  //    jugador3.setPuntos(90);
  //
  //    Jugador jugador4 = new Jugador();
  //    jugador4.setPuntos(70);
  //
  //    InscripcionPartido ins1 = new InscripcionPartido(partido, jugador1);
  //    ins1.setEquipo(local);
  //    ins1.setEsCapitan(true);
  //
  //    InscripcionPartido ins2 = new InscripcionPartido(partido, jugador2);
  //    ins2.setEquipo(local);
  //
  //    InscripcionPartido ins3 = new InscripcionPartido(partido, jugador3);
  //    ins3.setEquipo(visitante);
  //
  //    InscripcionPartido ins4 = new InscripcionPartido(partido, jugador4);
  //    ins4.setEquipo(visitante);
  //
  //    partido.setInscripciones(new ArrayList<>(List.of(ins1, ins2, ins3, ins4)));
  //
  //    servicioPartido.asignarCapitanes(partido);
  //
  //    assertTrue(ins1.isEsCapitan());
  //    assertFalse(ins2.isEsCapitan());
  //    assertTrue(ins3.isEsCapitan());
  //    assertFalse(ins4.isEsCapitan());
  //  }
  //
  //  @Test
  //  public void deberiaElegirComoCapitanAlJugadorConMasPuntos() {
  //    Partido partido = new Partido();
  //    Equipo local = new Equipo();
  //    Equipo visitante = new Equipo();
  //
  //    partido.setEquipo1(local);
  //    partido.setEquipo2(visitante);
  //
  //    Jugador jugador1 = new Jugador();
  //    jugador1.setPuntos(40);
  //
  //    Jugador jugador2 = new Jugador();
  //    jugador2.setPuntos(80);
  //
  //    Jugador jugador3 = new Jugador();
  //    jugador3.setPuntos(60);
  //
  //    Jugador jugador4 = new Jugador();
  //    jugador4.setPuntos(90);
  //
  //    InscripcionPartido ins1 = new InscripcionPartido(partido, jugador1);
  //    ins1.setEquipo(local);
  //
  //    InscripcionPartido ins2 = new InscripcionPartido(partido, jugador2);
  //    ins2.setEquipo(local);
  //
  //    InscripcionPartido ins3 = new InscripcionPartido(partido, jugador3);
  //    ins3.setEquipo(visitante);
  //
  //    InscripcionPartido ins4 = new InscripcionPartido(partido, jugador4);
  //    ins4.setEquipo(visitante);
  //
  //    partido.setInscripciones(new ArrayList<>(List.of(ins1, ins2, ins3, ins4)));
  //
  //    servicioPartido.asignarCapitanes(partido);
  //
  //    assertFalse(ins1.isEsCapitan());
  //    assertTrue(ins2.isEsCapitan());
  //    assertFalse(ins3.isEsCapitan());
  //    assertTrue(ins4.isEsCapitan());
  //  }
  //
  //  @Test
  //  public void deberiaElegirComoCapitanAlQueSeInscribioPrimeroSiEmpatanEnPuntos() {
  //    Partido partido = new Partido();
  //    Equipo local = new Equipo();
  //    Equipo visitante = new Equipo();
  //
  //    partido.setEquipo1(local);
  //    partido.setEquipo2(visitante);
  //
  //    Jugador jugador1 = new Jugador();
  //    jugador1.setPuntos(80);
  //
  //    Jugador jugador2 = new Jugador();
  //    jugador2.setPuntos(80);
  //
  //    InscripcionPartido ins1 = new InscripcionPartido(partido, jugador1);
  //    ins1.setEquipo(local);
  //    ins1.setFechaInscripcion(LocalDateTime.of(2026, 10, 20, 10, 0));
  //
  //    InscripcionPartido ins2 = new InscripcionPartido(partido, jugador2);
  //    ins2.setEquipo(local);
  //    ins2.setFechaInscripcion(LocalDateTime.of(2026, 10, 20, 9, 0));
  //
  //    partido.setInscripciones(new ArrayList<>(List.of(ins1, ins2)));
  //
  //    servicioPartido.asignarCapitanes(partido);
  //
  //    assertFalse(ins1.isEsCapitan());
  //    assertTrue(ins2.isEsCapitan());
  //  }
}
