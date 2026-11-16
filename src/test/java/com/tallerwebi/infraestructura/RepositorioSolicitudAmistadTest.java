package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.Enumeration.EstadoSolicitud;
import com.tallerwebi.dominio.repositorio.RepositorioSolicitudAmistad;
import com.tallerwebi.dominio.repositorio.RepositorioUsuario;
import com.tallerwebi.infraestructura.config.HibernateInfraestructuraTestConfig;
import jakarta.transaction.Transactional;
import java.util.List;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { HibernateInfraestructuraTestConfig.class })
public class RepositorioSolicitudAmistadTest {

  @Autowired
  private SessionFactory sessionFactory;

  private RepositorioSolicitudAmistad repositorioSolicitudAmistad;
  private RepositorioUsuario repositorioUsuario;

  @BeforeEach
  public void init() {
    repositorioSolicitudAmistad = new RepositorioSolicitudAmistadImpl(sessionFactory);
    repositorioUsuario = new RepositorioUsuarioImpl(sessionFactory);
  }

  @Transactional
  @Rollback
  @Test
  public void dadoQueTengoUnaSolicitudPendienteDebeRetornarlaParaElReceptor() {
    //GIVEN
    Usuario usuario1 = givenDadoQueTengoUnUsuario("Ivan@gmail.com", true);
    Usuario usuario2 = givenDadoQueTengoUnUsuario("Pedro@gmail.com", true);

    SolicitudAmistad solicitudAmistad1 = givenDadoQueTengoUnaSolicitudDeAmistad(
      usuario1,
      usuario2,
      EstadoSolicitud.PENDIENTE
    );

    //WHEN
    List<SolicitudAmistad> solicitudAmistadBuscadas =
      repositorioSolicitudAmistad.buscarPendientesDe(usuario2);

    //THEN
    assertThat(solicitudAmistadBuscadas.size(), equalTo(1));
  }

  @Transactional
  @Rollback
  @Test
  public void dadoQueTengoVariasSolicitudesDebeRetornarSoloLasPendientesDelReceptor() {
    //GIVEN
    Usuario Ivan = givenDadoQueTengoUnUsuario("Ivan@gmail.com", true);
    Usuario Pedro = givenDadoQueTengoUnUsuario("Pedro@gmail.com", true);
    Usuario Juana = givenDadoQueTengoUnUsuario("Juana@gmail.com", true);
    Usuario Lean = givenDadoQueTengoUnUsuario("Lean@gmail.com", true);

    SolicitudAmistad solicitudAmistad1 = givenDadoQueTengoUnaSolicitudDeAmistad(
      Ivan,
      Pedro,
      EstadoSolicitud.PENDIENTE
    );
    SolicitudAmistad solicitudAmistad2 = givenDadoQueTengoUnaSolicitudDeAmistad(
      Pedro,
      Juana,
      EstadoSolicitud.ACEPTADA
    );
    SolicitudAmistad solicitudAmistad3 = givenDadoQueTengoUnaSolicitudDeAmistad(
      Juana,
      Pedro,
      EstadoSolicitud.PENDIENTE
    );
    SolicitudAmistad solicitudAmistad4 = givenDadoQueTengoUnaSolicitudDeAmistad(
      Lean,
      Pedro,
      EstadoSolicitud.ACEPTADA
    );

    //WHEN
    List<SolicitudAmistad> solicitudAmistadBuscadas =
      repositorioSolicitudAmistad.buscarPendientesDe(Pedro);

    //THEN
    assertThat(solicitudAmistadBuscadas.size(), equalTo(2));
  }

  @Transactional
  @Rollback
  @Test
  public void dadoQueElUsuarioNoTieneSolicitudesPendientesDebeRetornarUnaListaVacia() {
    // Given

    Usuario pedro = givenDadoQueTengoUnUsuario("pedro@gmail.com", true);

    // When

    List<SolicitudAmistad> solicitudes = repositorioSolicitudAmistad.buscarPendientesDe(pedro);

    // Then

    assertThat(solicitudes.size(), equalTo(0));
  }

  @Transactional
  @Rollback
  @Test
  public void dadoQueTengoUnaSolicitudPendienteDebePoderCambiarSuEstado() {
    // Given

    Usuario juan = givenDadoQueTengoUnUsuario("juan@gmail.com", true);

    Usuario pedro = givenDadoQueTengoUnUsuario("pedro@gmail.com", true);

    SolicitudAmistad solicitud = givenDadoQueTengoUnaSolicitudDeAmistad(
      juan,
      pedro,
      EstadoSolicitud.PENDIENTE
    );

    solicitud.setEstado(EstadoSolicitud.ACEPTADA);

    // When

    repositorioSolicitudAmistad.modificar(solicitud);

    SolicitudAmistad solicitudEncontrada = repositorioSolicitudAmistad.buscarPorId(
      solicitud.getId()
    );

    // Then

    assertThat(solicitudEncontrada.getEstado(), equalTo(EstadoSolicitud.ACEPTADA));
  }

  @Transactional
  @Rollback
  @Test
  public void dadoQueTengoDosAmigosAceptadosDebeRetornarLasDosAmistades() {
    // Given

    Usuario juan = givenDadoQueTengoUnUsuario("juan@gmail.com", true);
    Usuario pedro = givenDadoQueTengoUnUsuario("pedro@gmail.com", true);
    Usuario lucas = givenDadoQueTengoUnUsuario("lucas@gmail.com", true);

    givenDadoQueTengoUnaSolicitudDeAmistad(juan, pedro, EstadoSolicitud.ACEPTADA);

    givenDadoQueTengoUnaSolicitudDeAmistad(juan, lucas, EstadoSolicitud.ACEPTADA);

    // When

    List<SolicitudAmistad> amistades = repositorioSolicitudAmistad.buscarAmistadesAceptadas(juan);

    // Then

    assertThat(amistades.size(), equalTo(2));
  }

  @Transactional
  @Rollback
  @Test
  public void dadoQueTengoSolicitudesDeDistintosEstadosDebeRetornarSoloLasAceptadas() {
    // Given

    Usuario juan = givenDadoQueTengoUnUsuario("juan@gmail.com", true);
    Usuario pedro = givenDadoQueTengoUnUsuario("pedro@gmail.com", true);
    Usuario lucas = givenDadoQueTengoUnUsuario("lucas@gmail.com", true);
    Usuario martin = givenDadoQueTengoUnUsuario("martin@gmail.com", true);

    givenDadoQueTengoUnaSolicitudDeAmistad(juan, pedro, EstadoSolicitud.ACEPTADA);

    givenDadoQueTengoUnaSolicitudDeAmistad(juan, lucas, EstadoSolicitud.PENDIENTE);

    givenDadoQueTengoUnaSolicitudDeAmistad(juan, martin, EstadoSolicitud.RECHAZADA);

    // When

    List<SolicitudAmistad> amistades = repositorioSolicitudAmistad.buscarAmistadesAceptadas(juan);

    // Then

    assertThat(amistades.size(), equalTo(1));

    assertThat(amistades.get(0).getReceptor(), equalTo(pedro));
  }

  @Transactional
  @Rollback
  @Test
  public void dadoQueRecibiUnaSolicitudAceptadaDebeRetornarAlEmisorComoAmigo() {
    // Given

    Usuario juan = givenDadoQueTengoUnUsuario("juan@gmail.com", true);
    Usuario pedro = givenDadoQueTengoUnUsuario("pedro@gmail.com", true);

    givenDadoQueTengoUnaSolicitudDeAmistad(pedro, juan, EstadoSolicitud.ACEPTADA);

    // When

    List<SolicitudAmistad> amistades = repositorioSolicitudAmistad.buscarAmistadesAceptadas(juan);

    // Then

    assertThat(amistades.size(), equalTo(1));

    assertThat(amistades.get(0).getEmisor(), equalTo(pedro));
  }

  @Transactional
  @Rollback
  @Test
  public void dadoQueUnUsuarioNoTieneAmigosDebeRetornarUnaListaVacia() {
    // Given

    Usuario juan = givenDadoQueTengoUnUsuario("juan@gmail.com", true);

    // When

    List<SolicitudAmistad> amistades = repositorioSolicitudAmistad.buscarAmistadesAceptadas(juan);

    // Then

    assertThat(amistades.size(), equalTo(0));
  }

  private SolicitudAmistad givenDadoQueTengoUnaSolicitudDeAmistad(
    Usuario emisor,
    Usuario receptor,
    EstadoSolicitud estadoSolicitud
  ) {
    SolicitudAmistad solicitud = new SolicitudAmistad(emisor, receptor, estadoSolicitud);

    repositorioSolicitudAmistad.guardar(solicitud);

    return solicitud;
  }

  private Usuario givenDadoQueTengoUnUsuario(String email, Boolean estaActivo) {
    Usuario usuario = new Usuario(email, estaActivo);

    repositorioUsuario.guardar(usuario);

    return usuario;
  }
}
