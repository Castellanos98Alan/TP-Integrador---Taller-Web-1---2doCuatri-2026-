package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.tallerwebi.dominio.Cancha;
import com.tallerwebi.dominio.Enumeration.TipoDeNotificacion;
import com.tallerwebi.dominio.Notificacion;
import com.tallerwebi.dominio.Partido;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.repositorio.RepositorioNotificacion;
import com.tallerwebi.dominio.repositorio.RepositorioPartido;
import com.tallerwebi.infraestructura.config.HibernateInfraestructuraTestConfig;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
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
public class RepositorioNotificacionTest {

  @Autowired
  private SessionFactory sessionFactory;

  private RepositorioNotificacion repositorioNotificacion;

  @BeforeEach
  public void init() {
    repositorioNotificacion = new RepositorioNotificacionImpl(sessionFactory);
  }

  @Transactional
  @Rollback
  @Test
  public void dadoQueTengoDosNotificacionesCuandoBuscoPorIdDebeRetornarLaCorrecta() {
    // Given

    Usuario usuario = givenDadoQueTengoUnUsuario();

    Notificacion notificacion1 = givenDadoQueTengoUnaNotificacion(
      usuario,
      "Primera notificacion",
      TipoDeNotificacion.INVITACION_PARTIDO,
      LocalDateTime.of(2026, 11, 3, 18, 0)
    );

    Notificacion notificacion2 = givenDadoQueTengoUnaNotificacion(
      usuario,
      "Segunda notificacion",
      TipoDeNotificacion.PARTIDO_CANCELADO,
      LocalDateTime.of(2026, 11, 3, 20, 0)
    );

    // When

    Notificacion notificacionBuscada = repositorioNotificacion.buscarPorID(notificacion2.getId());

    // Then

    assertThat(notificacionBuscada.getId(), equalTo(notificacion2.getId()));

    assertThat(notificacionBuscada.getMensaje(), equalTo("Segunda notificacion"));

    assertThat(
      notificacionBuscada.getTipoDeNotificacion(),
      equalTo(TipoDeNotificacion.PARTIDO_CANCELADO)
    );
  }

  @Transactional
  @Rollback
  @Test
  public void dadoQueTengoUnaNotificacionDebeGuardarseCorrectamente() {
    // Given

    Usuario usuario = givenDadoQueTengoUnUsuario();

    Notificacion notificacion = givenDadoQueTengoUnaNotificacion(
      usuario,
      "Te invitaron a un partido",
      TipoDeNotificacion.INVITACION_PARTIDO,
      LocalDateTime.of(2026, 11, 3, 20, 0)
    );

    // When

    Notificacion notificacionBuscada = repositorioNotificacion.buscarPorID(notificacion.getId());

    // Then

    assertThat(notificacionBuscada.getMensaje(), equalTo("Te invitaron a un partido"));

    assertThat(
      notificacionBuscada.getTipoDeNotificacion(),
      equalTo(TipoDeNotificacion.INVITACION_PARTIDO)
    );

    assertThat(notificacionBuscada.getLeida(), equalTo(false));
  }

  @Transactional
  @Rollback
  @Test
  public void dadoQueTengoNotificacionesDeDistintosUsuariosDebeRetornarSoloLasDelUsuarioBuscado() {
    // Given

    Usuario usuario1 = givenDadoQueTengoUnUsuario();
    Usuario usuario2 = givenDadoQueTengoUnUsuario();

    givenDadoQueTengoUnaNotificacion(
      usuario1,
      "Notificacion usuario 1",
      TipoDeNotificacion.INVITACION_PARTIDO,
      LocalDateTime.of(2026, 11, 3, 20, 0)
    );

    givenDadoQueTengoUnaNotificacion(
      usuario1,
      "Partido cancelado usuario 1",
      TipoDeNotificacion.PARTIDO_CANCELADO,
      LocalDateTime.of(2026, 11, 3, 21, 0)
    );

    givenDadoQueTengoUnaNotificacion(
      usuario2,
      "Notificacion usuario 2",
      TipoDeNotificacion.INVITACION_PARTIDO,
      LocalDateTime.of(2026, 11, 3, 22, 0)
    );

    // When

    List<Notificacion> notificaciones = repositorioNotificacion.buscarPorUsuario(usuario1.getId());

    // Then

    assertThat(notificaciones.size(), equalTo(2));
  }

  @Transactional
  @Rollback
  @Test
  public void dadoQueTengoNotificacionesDebeRetornarPrimeroLaMasNueva() {
    // Given

    Usuario usuario = givenDadoQueTengoUnUsuario();

    givenDadoQueTengoUnaNotificacion(
      usuario,
      "Notificacion vieja",
      TipoDeNotificacion.INVITACION_PARTIDO,
      LocalDateTime.of(2026, 11, 3, 18, 0)
    );

    givenDadoQueTengoUnaNotificacion(
      usuario,
      "Notificacion nueva",
      TipoDeNotificacion.PARTIDO_CANCELADO,
      LocalDateTime.of(2026, 11, 3, 22, 0)
    );

    // When

    List<Notificacion> notificaciones = repositorioNotificacion.buscarPorUsuario(usuario.getId());

    // Then

    assertThat(notificaciones.getFirst().getMensaje(), equalTo("Notificacion nueva"));
  }

  private Notificacion givenDadoQueTengoUnaNotificacion(
    Usuario usuario,
    String mensaje,
    TipoDeNotificacion tipo,
    LocalDateTime fecha
  ) {
    Notificacion notificacion = new Notificacion();

    notificacion.setUsuario(usuario);
    notificacion.setMensaje(mensaje);
    notificacion.setTipoDeNotificacion(tipo);
    notificacion.setLeida(false);
    notificacion.setFecha(fecha);

    repositorioNotificacion.guardar(notificacion);

    return notificacion;
  }

  private Usuario givenDadoQueTengoUnUsuario() {
    Usuario usuario = new Usuario();

    sessionFactory.getCurrentSession().persist(usuario);

    return usuario;
  }

  @Transactional
  @Rollback
  @Test
  public void dadoQueTengoUnUsuarioSinNotificacionesDebeRetornarUnaListaVacia() {
    // Given

    Usuario usuario = givenDadoQueTengoUnUsuario();

    // When

    List<Notificacion> notificaciones = repositorioNotificacion.buscarPorUsuario(usuario.getId());

    // Then

    assertThat(notificaciones.size(), equalTo(0));
  }

  @Transactional
  @Rollback
  @Test
  public void dadoQueTengoNotificacionesLeidasYNoLeidasDebeRetornarSoloLasNoLeidas() {
    // Given

    Usuario usuario = givenDadoQueTengoUnUsuario();

    Notificacion notificacionNoLeida = givenDadoQueTengoUnaNotificacion(
      usuario,
      "Notificacion nueva",
      TipoDeNotificacion.INVITACION_PARTIDO,
      LocalDateTime.of(2026, 11, 3, 20, 0)
    );

    Notificacion notificacionLeida = givenDadoQueTengoUnaNotificacion(
      usuario,
      "Notificacion vieja",
      TipoDeNotificacion.PARTIDO_CANCELADO,
      LocalDateTime.of(2026, 11, 3, 19, 0)
    );

    notificacionLeida.setLeida(true);

    // When

    List<Notificacion> notificaciones = repositorioNotificacion.buscarNoLeidasPorUsuario(
      usuario.getId()
    );

    // Then

    assertThat(notificaciones.size(), equalTo(1));

    assertThat(notificaciones.get(0), equalTo(notificacionNoLeida));
  }
}
