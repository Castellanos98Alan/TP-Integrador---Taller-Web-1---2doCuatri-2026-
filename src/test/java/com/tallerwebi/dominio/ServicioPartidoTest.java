package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.RepositorioPartido;
import com.tallerwebi.presentacion.DatosCrearPartido;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
      FILTRO_TODOS
    );
  }

  public void queUnUsuarioPuedaUnirseAPartidoExitosamente() {
    Long partidoId = 1L;
    Partido partido = new Partido();
    partido.setId(partidoId);
    partido.setCupoMaximo(10);
    partido.setConfirmados(new ArrayList<>());
    partido.setEstado(Partido.EstadoPartido.ACTIVO);

    Usuario usuario = new Usuario();
    usuario.setId(100L);
    usuario.setEmail("test@ejemplo.com");

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

    Usuario usuario = new Usuario();
    usuario.setId(100L);

    partido.setConfirmados(new ArrayList<>());
    partido.getConfirmados().add(usuario);

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

    Usuario usuario1 = new Usuario();
    usuario1.setId(101L);
    Usuario usuario2 = new Usuario();
    usuario2.setId(102L);

    partido.setConfirmados(new ArrayList<>());
    partido.getConfirmados().add(usuario1);
    partido.getConfirmados().add(usuario2);

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
    //assertThat(partido.getCupoMaximo(), equalTo(22));
  }
}
