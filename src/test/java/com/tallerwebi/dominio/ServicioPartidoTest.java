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
import com.tallerwebi.dominio.servicio.ServicioBalanceoEquipo;
import com.tallerwebi.dominio.servicio.ServicioJugador;
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

    private ServicioJugador servicioJugadorMock;

    private RepositorioPartido repositorioPartidoMock;

    private RepositorioCancha repositorioCanchaMock;

    private RepositorioUsuario repositorioUsuarioMock;

    private ServicioBalanceoEquipo servicioBalanceoEquipoMock;

    private static final String FILTRO_TODOS = "TODOS";

    @BeforeEach
    public void init() {

        repositorioPartidoMock = mock(RepositorioPartido.class);

        repositorioUsuarioMock = mock(RepositorioUsuario.class);

        repositorioCanchaMock = mock(RepositorioCancha.class);

        servicioJugadorMock = mock(ServicioJugador.class);

        servicioBalanceoEquipoMock = mock(ServicioBalanceoEquipo.class);

        servicioPartido = new ServicioPartidoImpl(repositorioPartidoMock, repositorioCanchaMock, repositorioUsuarioMock, servicioJugadorMock,
                servicioBalanceoEquipoMock);
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

        Exception exception = assertThrows(RuntimeException.class, () -> servicioPartido.cancelarPartido(10L, noOrganizador));

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

        Exception exception = assertThrows(RuntimeException.class, () -> servicioPartido.cancelarPartido(10L, organizador));

        assertEquals("No se puede cancelar un partido que ya finalizo.", exception.getMessage());

        verify(repositorioPartidoMock, never()).modificar(any());
    }

    @Test
    public void deberiaDelegarLaBusquedaAlRepositorioConLosFiltrosCorrectos() {

        List<Partido> listaFalsa = List.of(new Partido(), new Partido());

        when(repositorioPartidoMock.buscarPartidosPorFiltro("PRINCIPIANTE", FILTRO_TODOS, "22", FILTRO_TODOS, FILTRO_TODOS, FILTRO_TODOS,
                FILTRO_TODOS)).thenReturn(listaFalsa);

        List<Partido> resultado = servicioPartido.listarPartidosSegunFiltro("PRINCIPIANTE", FILTRO_TODOS, "22", FILTRO_TODOS, FILTRO_TODOS, FILTRO_TODOS,
                FILTRO_TODOS);

        assertEquals(listaFalsa, resultado);

        verify(repositorioPartidoMock).buscarPartidosPorFiltro("PRINCIPIANTE", FILTRO_TODOS, "22", FILTRO_TODOS, FILTRO_TODOS, FILTRO_TODOS, FILTRO_TODOS);
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

        when(repositorioPartidoMock.buscarPartidosPorFiltro(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString())).thenReturn(partidosSimulados);

        List<String> provincias = servicioPartido.obtenerProvinciasDisponibles();

        List<String> localidades = servicioPartido.obtenerLocalidadesDisponibles();

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

        when(repositorioPartidoMock.buscarPartidosPorFiltro(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString())).thenReturn(partidosIncompletos);

        List<String> provincias = servicioPartido.obtenerProvinciasDisponibles();

        List<String> localidades = servicioPartido.obtenerLocalidadesDisponibles();

        assertTrue(provincias.isEmpty());

        assertTrue(localidades.isEmpty());
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

        Jugador jugador = new Jugador();

        jugador.setId(1L);
        jugador.setUsuario(usuario);

        when(repositorioPartidoMock.buscarPorId(partidoId)).thenReturn(partido);

        when(repositorioPartidoMock.buscarPartidosPorJugador(jugador.getId())).thenReturn(new ArrayList<>());

        servicioPartido.unirseAPartido(partidoId, jugador);

        assertThat(partido.getInscripciones(), hasSize(1));

        assertEquals(jugador, partido.getInscripciones().get(0).getJugador());

        assertEquals(1, partido.getCantJugadores());

        verify(repositorioPartidoMock, times(1)).modificar(partido);
    }

    @Test
    public void queLanceExcepcionSiElPartidoNoExisteAlQuererUnirse() {

        Long partidoId = 99L;

        Jugador jugador = new Jugador();

        when(repositorioPartidoMock.buscarPorId(partidoId)).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> servicioPartido.unirseAPartido(partidoId, jugador));

        assertEquals("El partido no existe.", exception.getMessage());
    }

    @Test
    public void queLanceExcepcionSiElPartidoEstaCancelado() {

        Long partidoId = 1L;

        Partido partido = new Partido();

        partido.setId(partidoId);

        partido.setEstado(Partido.EstadoPartido.CANCELADO);

        Jugador jugador = new Jugador();

        when(repositorioPartidoMock.buscarPorId(partidoId)).thenReturn(partido);

        Exception exception = assertThrows(RuntimeException.class, () -> servicioPartido.unirseAPartido(partidoId, jugador));

        assertEquals("No se puede unir a un partido finalizado o cancelado.", exception.getMessage());
    }

    @Test
    public void queLanceExcepcionSiElPartidoEstaFinalizado() {

        Long partidoId = 1L;

        Partido partido = new Partido();

        partido.setId(partidoId);

        partido.setEstado(Partido.EstadoPartido.FINALIZADO);

        Jugador jugador = new Jugador();

        when(repositorioPartidoMock.buscarPorId(partidoId)).thenReturn(partido);

        Exception exception = assertThrows(RuntimeException.class, () -> servicioPartido.unirseAPartido(partidoId, jugador));

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

        Jugador jugador = new Jugador();

        jugador.setId(100L);

        InscripcionPartido inscripcionExistente = new InscripcionPartido();

        inscripcionExistente.setPartido(partido);

        inscripcionExistente.setJugador(jugador);

        partido.getInscripciones().add(inscripcionExistente);

        when(repositorioPartidoMock.buscarPorId(partidoId)).thenReturn(partido);

        Exception exception = assertThrows(RuntimeException.class, () -> servicioPartido.unirseAPartido(partidoId, jugador));

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

        Exception exception = assertThrows(RuntimeException.class, () -> servicioPartido.unirseAPartido(partidoId, jugadorNuevo));

        assertEquals("Cupo maximo alcanzado.", exception.getMessage());

        verify(servicioBalanceoEquipoMock, never()).balancearEquipos(any(Partido.class));
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

        when(repositorioPartidoMock.buscarPartidosPorJugador(jugador.getId())).thenReturn(List.of(partidoExistente));

        servicioPartido.unirseAPartido(partidoIdNuevo, jugador);

        assertThat(partidoNuevo.getInscripciones(), hasSize(1));

        assertEquals(jugador, partidoNuevo.getInscripciones().get(0).getJugador());

        verify(repositorioPartidoMock, times(1)).modificar(partidoNuevo);
    }

    @Test
    public void queNoPermitaUnirseSiHayMenosDeTresHorasDeDiferencia() {

        Long partidoIdNuevo = 1L;

        Partido partidoNuevo = new Partido();

        partidoNuevo.setId(partidoIdNuevo);

        partidoNuevo.setCupoMaximo(10);

        partidoNuevo.setEstado(Partido.EstadoPartido.ACTIVO);

        partidoNuevo.setFecha(LocalDateTime.of(2026, 6, 6, 17, 0));

        partidoNuevo.setInscripciones(new ArrayList<>());

        Partido partidoExistente = new Partido();

        partidoExistente.setFecha(LocalDateTime.of(2026, 6, 6, 15, 0));

        Jugador jugador = new Jugador();

        jugador.setId(50L);

        when(repositorioPartidoMock.buscarPorId(partidoIdNuevo)).thenReturn(partidoNuevo);

        when(repositorioPartidoMock.buscarPartidosPorJugador(jugador.getId())).thenReturn(List.of(partidoExistente));

        Exception exception = assertThrows(RuntimeException.class, () -> servicioPartido.unirseAPartido(partidoIdNuevo, jugador));

        assertTrue(exception.getMessage().contains("No podes unirte"));

        verify(repositorioPartidoMock, never()).modificar(partidoNuevo);
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

        Cancha cancha = new Cancha();

        cancha.setId(1L);

        when(repositorioCanchaMock.buscarPorNombre("Cancha Los Leones")).thenReturn(cancha);

        when(repositorioPartidoMock.buscarPartidosEnRango(any(), any(), any())).thenReturn(new ArrayList<>());

        DatosCrearPartido datos = new DatosCrearPartido("Cancha Los Leones", LocalDateTime.of(2026, 10, 20, 18, 30), 10, "INTERMEDIO");

        servicioPartido.crearPartido(datos, new Usuario());

        verify(repositorioPartidoMock).guardar(any(Partido.class));
    }

    @Test
    public void noDeberiaCrearElPartidoSiLaCanchaIngresadaNoExiste() {

        DatosCrearPartido datos = new DatosCrearPartido("Inexistente", LocalDateTime.of(2026, 10, 20, 18, 30), 10, "INTERMEDIO");

        Exception exception = assertThrows(RuntimeException.class, () -> servicioPartido.crearPartido(datos, new Usuario()));

        assertEquals("La cancha ingresada no existe.", exception.getMessage());

        verify(repositorioPartidoMock, never()).guardar(any());
    }

    @Test
    public void noDeberiaCrearElPartidoSiElCupoEsCeroONegativo() {

        DatosCrearPartido datos = new DatosCrearPartido("Cancha Los Leones", LocalDateTime.of(2026, 10, 20, 18, 30), 0, "INTERMEDIO");

        Exception exception = assertThrows(RuntimeException.class, () -> servicioPartido.crearPartido(datos, new Usuario()));

        assertEquals("El cupo debe ser mayor a cero.", exception.getMessage());

        verify(repositorioPartidoMock, never()).guardar(any());
    }

    @Test
    public void noDeberiaCrearElPartidoSiLaFechaEstaVacia() {

        DatosCrearPartido datos = new DatosCrearPartido("Cancha Los Leones", null, 10, "INTERMEDIO");

        Exception exception = assertThrows(RuntimeException.class, () -> servicioPartido.crearPartido(datos, new Usuario()));

        assertEquals("Debe ingresar una fecha y hora.", exception.getMessage());

        verify(repositorioPartidoMock, never()).guardar(any());
    }

    @Test
    public void noDeberiaCrearPartidoSiLaCanchaEstaOcupada() {

        Cancha cancha = new Cancha();

        cancha.setId(1L);

        Partido partidoExistente = new Partido();

        when(repositorioCanchaMock.buscarPorNombre("Cancha Los Leones")).thenReturn(cancha);

        when(repositorioPartidoMock.buscarPartidosEnRango(any(), any(), any())).thenReturn(List.of(partidoExistente));

        DatosCrearPartido datos = new DatosCrearPartido("Cancha Los Leones", LocalDateTime.of(2026, 10, 20, 18, 30), 10, "INTERMEDIO");

        Exception exception = assertThrows(RuntimeException.class, () -> servicioPartido.crearPartido(datos, new Usuario()));

        assertTrue(exception.getMessage().contains("La cancha no está disponible"));

        verify(repositorioPartidoMock, never()).guardar(any());
    }

    @Test
    public void deberiaBalancearYAsignarCapitanesCuandoSeCompletaElCupo() {

        Long partidoId = 1L;

        Partido partido = new Partido();

        partido.setId(partidoId);

        partido.setCupoMaximo(2);

        partido.setEstado(Partido.EstadoPartido.ACTIVO);

        partido.setInscripciones(new ArrayList<>());

        Jugador jugadorExistente = new Jugador();

        jugadorExistente.setId(10L);

        InscripcionPartido inscripcionExistente = new InscripcionPartido(partido, jugadorExistente);

        partido.getInscripciones().add(inscripcionExistente);

        partido.setCantJugadores(1);

        Jugador jugadorNuevo = new Jugador();

        jugadorNuevo.setId(20L);

        when(repositorioPartidoMock.buscarPorId(partidoId)).thenReturn(partido);

        when(repositorioPartidoMock.buscarPartidosPorJugador(jugadorNuevo.getId())).thenReturn(new ArrayList<>());

        servicioPartido.unirseAPartido(partidoId, jugadorNuevo);

        assertEquals(2, partido.getCantJugadores());

        verify(servicioBalanceoEquipoMock, times(1)).balancearEquipos(partido);

        verify(servicioBalanceoEquipoMock, times(1)).asignarCapitanes(partido);

        verify(repositorioPartidoMock, times(1)).modificar(partido);
    }

    @Test
    public void noDeberiaBalancearSiElCupoTodaviaNoEstaCompleto() {

        Long partidoId = 1L;

        Partido partido = new Partido();

        partido.setId(partidoId);

        partido.setCupoMaximo(10);

        partido.setEstado(Partido.EstadoPartido.ACTIVO);

        partido.setInscripciones(new ArrayList<>());

        for (int i = 0; i < 8; i++) {

            Jugador jugador = new Jugador();

            jugador.setId((long) i);

            InscripcionPartido inscripcion = new InscripcionPartido(partido, jugador);

            partido.getInscripciones().add(inscripcion);
        }

        partido.setCantJugadores(8);

        Jugador nuevoJugador = new Jugador();

        nuevoJugador.setId(100L);

        when(repositorioPartidoMock.buscarPorId(partidoId)).thenReturn(partido);

        when(repositorioPartidoMock.buscarPartidosPorJugador(nuevoJugador.getId())).thenReturn(new ArrayList<>());

        servicioPartido.unirseAPartido(partidoId, nuevoJugador);

        assertEquals(9, partido.getCantJugadores());

        verify(servicioBalanceoEquipoMock, never()).balancearEquipos(any(Partido.class));

        verify(servicioBalanceoEquipoMock, never()).asignarCapitanes(any(Partido.class));

        verify(repositorioPartidoMock, times(1)).modificar(partido);
    }

    @Test
    public void deberiaObtenerFechasDisponiblesSinRepetir() {

        Partido partido1 = new Partido();

        partido1.setFecha(LocalDateTime.of(2026, 10, 20, 18, 0));

        Partido partido2 = new Partido();

        partido2.setFecha(LocalDateTime.of(2026, 10, 20, 20, 0));

        Partido partido3 = new Partido();

        partido3.setFecha(LocalDateTime.of(2026, 10, 21, 18, 0));

        when(repositorioPartidoMock.buscarPartidosPorFiltro(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString())).thenReturn(List.of(partido1, partido2, partido3));

        List<String> fechas = servicioPartido.obtenerFechasDisponibles();

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

        when(repositorioPartidoMock.buscarPartidosPorFiltro(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString())).thenReturn(List.of(partido1, partido2, partido3));

        List<String> horas = servicioPartido.obtenerHorasDisponibles();

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

        when(repositorioPartidoMock.buscarPartidosPorFiltro(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString())).thenReturn(List.of(partido1, partido2, partido3));

        List<Integer> cupos = servicioPartido.obtenerCuposDisponibles();

        assertEquals(2, cupos.size());

        assertTrue(cupos.contains(10));

        assertTrue(cupos.contains(14));
    }
}
