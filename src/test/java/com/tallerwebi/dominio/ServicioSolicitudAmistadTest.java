package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.excepcion.SolicitudAmistadInvalida;
import com.tallerwebi.dominio.repositorio.RepositorioSolicitudAmistad;
import com.tallerwebi.dominio.servicio.ServicioSolicitudAmistad;
import com.tallerwebi.dominio.servicio.ServicioSolicitudAmistadImpl;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioSolicitudAmistadTest {

  private ServicioSolicitudAmistad servicioSolicitudAmistad;
  private RepositorioSolicitudAmistad repositorioSolicitudAmistadMock;

  @BeforeEach
  public void init() {
    repositorioSolicitudAmistadMock = mock(RepositorioSolicitudAmistad.class);

    servicioSolicitudAmistad = new ServicioSolicitudAmistadImpl(repositorioSolicitudAmistadMock);
  }

  //GUARDAR LAS SOLICITUDES

  @Test
  public void enviarSolicitudDebeGuardarlaComoPendiente() {
    Usuario juan = givenDadoQueTengounUsuario("Juan@gmail.com", true);
    Usuario ivan = givenDadoQueTengounUsuario("ivan@gmail.com", true);

    servicioSolicitudAmistad.enviarSolicitud(juan, ivan);

    verify(repositorioSolicitudAmistadMock).guardar(any(SolicitudAmistad.class));
  }

  //OBTENER LAS SOLICITUDES PENDIENTE

  @Test
  public void obtenerSolicitudesPendientesDebeRetornarLasDelRepositorio() {
    // Given
    Usuario matias = givenDadoQueTengounUsuario("Matias@gmail.com", true);
    Usuario ivana = givenDadoQueTengounUsuario("Ivana@gmail.com", true);

    SolicitudAmistad solicitud = givenDadoqueTengoUnaSolicitudDeAmistad(
      matias,
      ivana,
      SolicitudAmistad.EstadoSolicitud.PENDIENTE
    );

    List<SolicitudAmistad> solicitudes = new ArrayList<>();
    solicitudes.add(solicitud);

    when(repositorioSolicitudAmistadMock.buscarPendientesDe(ivana)).thenReturn(solicitudes);

    // When

    List<SolicitudAmistad> resultado = servicioSolicitudAmistad.obtenerSolicitudesPendientes(ivana);

    // Then

    assertThat(resultado.size(), equalTo(1));

    verify(repositorioSolicitudAmistadMock).buscarPendientesDe(ivana);
  }

  //ACEPTAR SOLICITUD

  @Test
  public void aceptarSolicitudDebeCambiarElEstadoAAceptada() {
    // Given

    SolicitudAmistad solicitud = new SolicitudAmistad();
    solicitud.setEstado(SolicitudAmistad.EstadoSolicitud.PENDIENTE);

    when(repositorioSolicitudAmistadMock.buscarPorId(1)).thenReturn(solicitud);

    // When

    servicioSolicitudAmistad.aceptarSolicitud(1);

    // Then
    assertThat(solicitud.getEstado(), equalTo(SolicitudAmistad.EstadoSolicitud.ACEPTADA));

    verify(repositorioSolicitudAmistadMock).modificar(solicitud);
  }

  // RECHAZAR SOLICITUD

  @Test
  public void rechazarSolicitudDebeCambiarElEstadoARechazada() {
    // Given

    SolicitudAmistad solicitud = new SolicitudAmistad();

    solicitud.setEstado(SolicitudAmistad.EstadoSolicitud.PENDIENTE);

    when(repositorioSolicitudAmistadMock.buscarPorId(1)).thenReturn(solicitud);

    // When

    servicioSolicitudAmistad.rechazarSolicitud(1);

    // Then

    assertThat(solicitud.getEstado(), equalTo(SolicitudAmistad.EstadoSolicitud.RECHAZADA));

    verify(repositorioSolicitudAmistadMock).modificar(solicitud);
  }

  @Test
  public void unUsuarioNoPuedeEnviarseSolicitudASiMismo() {
    // Given
    Usuario matias = new Usuario();

    // When - Then

    assertThrows(
      SolicitudAmistadInvalida.class,
      () -> servicioSolicitudAmistad.enviarSolicitud(matias, matias)
    );

    verify(repositorioSolicitudAmistadMock, never()).guardar(any(SolicitudAmistad.class));
  }

  @Test
  public void noDebePermitirEnviarUnaSolicitudDuplicada() {
    // Given

    Usuario matias = givenDadoQueTengounUsuario("matias@gmail.com", true);

    Usuario ivana = givenDadoQueTengounUsuario("ivana@gmail.com", true);

    when(repositorioSolicitudAmistadMock.existeSolicitudPendienteEntre(matias, ivana))
      .thenReturn(true);

    // When

    assertThrows(
      SolicitudAmistadInvalida.class,
      () -> servicioSolicitudAmistad.enviarSolicitud(matias, ivana)
    );

    //then

    verify(repositorioSolicitudAmistadMock, never()).guardar(any(SolicitudAmistad.class));
  }

  @Test
  public void aceptarSolicitudInexistenteDebeLanzarExcepcion() {
    // Given
    when(repositorioSolicitudAmistadMock.buscarPorId(1)).thenReturn(null);

    // When

    assertThrows(
      SolicitudAmistadInvalida.class,
      () -> servicioSolicitudAmistad.aceptarSolicitud(1)
    );

    verify(repositorioSolicitudAmistadMock, never()).modificar(any(SolicitudAmistad.class));
  }

  @Test
  public void rechazarSolicitudInexistenteDebeLanzarExcepcion() {
    // Given

    when(repositorioSolicitudAmistadMock.buscarPorId(1)).thenReturn(null);

    // When

    assertThrows(
      SolicitudAmistadInvalida.class,
      () -> servicioSolicitudAmistad.rechazarSolicitud(1)
    );

    verify(repositorioSolicitudAmistadMock, never()).modificar(any(SolicitudAmistad.class));
  }

  private Usuario givenDadoQueTengounUsuario(String email, boolean estaActivo) {
    Usuario usuario = new Usuario(email, estaActivo);

    return usuario;
  }

  private SolicitudAmistad givenDadoqueTengoUnaSolicitudDeAmistad(
    Usuario emisor,
    Usuario receptor,
    SolicitudAmistad.EstadoSolicitud estadoSolicitud
  ) {
    SolicitudAmistad solicitudAmistad = new SolicitudAmistad(emisor, receptor, estadoSolicitud);

    return solicitudAmistad;
  }
}
