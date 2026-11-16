package com.tallerwebi.dominio;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.repositorio.RepositorioPartido;
import com.tallerwebi.dominio.servicio.ServicioEmail;
import com.tallerwebi.dominio.servicio.ServicioRecordatorio;
import com.tallerwebi.dominio.servicio.ServicioRecordatorioImpl;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioRecordatorioTest {

  private RepositorioPartido repositorioPartidoMock;
  private ServicioEmail servicioEmailMock;
  private ServicioRecordatorio servicioRecordatorio;

  @BeforeEach
  public void init() {
    repositorioPartidoMock = mock(RepositorioPartido.class);
    servicioEmailMock = mock(ServicioEmail.class);

    servicioRecordatorio = new ServicioRecordatorioImpl(repositorioPartidoMock, servicioEmailMock);
  }

  @Test
  public void queSeEnvieUnRecordatorioTresHorasAntesDelPartido() {
    // Preparación
    LocalDateTime ahora = LocalDateTime.of(2026, 11, 11, 17, 0);

    LocalDateTime fechaPartido = ahora.plusHours(3);

    Usuario usuario = new Usuario();
    usuario.setEmail("jugador@ejemplo.com");

    Jugador jugador = new Jugador();
    jugador.setUsuario(usuario);

    Partido partido = new Partido();
    partido.setFecha(fechaPartido);
    partido.setEstado(Partido.EstadoPartido.ACTIVO);

    InscripcionPartido inscripcion = new InscripcionPartido(partido, jugador);

    inscripcion.setConfirmado(true);

    partido.setInscripciones(Arrays.asList(inscripcion));

    when(
      repositorioPartidoMock.buscarPartidosParaRecordatorio(
        eq(fechaPartido),
        eq(fechaPartido.plusMinutes(1))
      )
    )
      .thenReturn(Arrays.asList(partido));

    // Ejecución
    servicioRecordatorio.enviarRecordatoriosPendientes(ahora);

    // Verificación
    verify(servicioEmailMock, times(1))
      .enviarCorreo(eq("jugador@ejemplo.com"), anyString(), contains("19:50"));
  }

  @Test
  public void queNoSeEnvieRecordatorioAUnJugadorNoConfirmado() {
    LocalDateTime ahora = LocalDateTime.of(2026, 11, 11, 17, 0);

    LocalDateTime fechaPartido = ahora.plusHours(3);

    Usuario usuario = new Usuario();
    usuario.setEmail("jugador@ejemplo.com");

    Jugador jugador = new Jugador();
    jugador.setUsuario(usuario);

    Partido partido = new Partido();
    partido.setFecha(fechaPartido);
    partido.setEstado(Partido.EstadoPartido.ACTIVO);

    InscripcionPartido inscripcion = new InscripcionPartido(partido, jugador);

    inscripcion.setConfirmado(false);

    partido.setInscripciones(Arrays.asList(inscripcion));

    when(
      repositorioPartidoMock.buscarPartidosParaRecordatorio(
        any(LocalDateTime.class),
        any(LocalDateTime.class)
      )
    )
      .thenReturn(Arrays.asList(partido));

    servicioRecordatorio.enviarRecordatoriosPendientes(ahora);

    verify(servicioEmailMock, never()).enviarCorreo(anyString(), anyString(), anyString());
  }

  @Test
  public void queNoSeEnvieDosVecesElMismoRecordatorio() {
    // Preparación
    LocalDateTime ahora = LocalDateTime.of(2026, 11, 11, 17, 0);

    LocalDateTime fechaPartido = ahora.plusHours(3);

    Usuario usuario = new Usuario();
    usuario.setEmail("juan@ejemplo.com");

    Jugador jugador = new Jugador();
    jugador.setUsuario(usuario);

    Partido partido = new Partido();
    partido.setFecha(fechaPartido);
    partido.setEstado(Partido.EstadoPartido.ACTIVO);

    InscripcionPartido inscripcion = new InscripcionPartido(partido, jugador);

    inscripcion.setConfirmado(true);
    inscripcion.setRecordatorioEnviado(false);

    partido.setInscripciones(Arrays.asList(inscripcion));

    when(
      repositorioPartidoMock.buscarPartidosParaRecordatorio(
        any(LocalDateTime.class),
        any(LocalDateTime.class)
      )
    )
      .thenReturn(Arrays.asList(partido));

    // Primera ejecución
    servicioRecordatorio.enviarRecordatoriosPendientes(ahora);

    // Segunda ejecución
    servicioRecordatorio.enviarRecordatoriosPendientes(ahora);

    // Verificación
    verify(servicioEmailMock, times(1))
      .enviarCorreo(eq("juan@ejemplo.com"), anyString(), anyString());
  }
}
