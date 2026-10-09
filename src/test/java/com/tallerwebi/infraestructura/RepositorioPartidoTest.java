package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.tallerwebi.dominio.Cancha;
import com.tallerwebi.dominio.Partido;
import com.tallerwebi.dominio.RepositorioPartido;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.infraestructura.config.HibernateInfraestructuraTestConfig;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
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
public class RepositorioPartidoTest {

  @Autowired
  private SessionFactory sessionFactory;

  private RepositorioPartido repositorioPartido;

  @BeforeEach
  public void init() {
    repositorioPartido = new RepositorioPartidoImpl(sessionFactory);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaGuardarUnPartidoYBuscarloPorId() {
    // Given
    Partido partido = givenTengoUnPartido("PRINCIPIANTE", 10);

    // When
    repositorioPartido.guardar(partido);
    Partido partidoObtenido = repositorioPartido.buscarPorId(partido.getId());

    // Then
    assertThat(partidoObtenido.getNivel(), is(equalTo("PRINCIPIANTE")));
    assertThat(partidoObtenido.getCupoMaximo(), is(equalTo(10)));
    assertThat(partidoObtenido.getId(), is(equalTo(partido.getId())));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaGuardarUnPartidoConCanchaYOrganizador() {
    // Given
    Cancha cancha = givenExisteUnaCancha("Cancha Los Leones");
    Usuario organizador = givenExisteUnUsuario("organizador@test.com");

    Partido partido = givenTengoUnPartido("INTERMEDIO", 22);
    partido.setCancha(cancha);
    partido.setOrganizador(organizador);

    // When
    repositorioPartido.guardar(partido);
    Partido partidoObtenido = repositorioPartido.buscarPorId(partido.getId());

    // Then
    assertThat(partidoObtenido.getCancha().getNombre(), is(equalTo("Cancha Los Leones")));
    assertThat(partidoObtenido.getOrganizador().getEmail(), is(equalTo("organizador@test.com")));
  }

  //metodps

  private Partido givenTengoUnPartido(String nivel, Integer cupo) {
    Partido partido = new Partido();
    partido.setNivel(nivel);
    partido.setCupoMaximo(cupo);
    partido.setFecha(LocalDateTime.of(2026, 10, 20, 18, 0));
    return partido;
  }

  private Cancha givenExisteUnaCancha(String nombre) {
    Cancha cancha = new Cancha();
    cancha.setNombre(nombre);
    sessionFactory.getCurrentSession().persist(cancha);
    return cancha;
  }

  private Usuario givenExisteUnUsuario(String email) {
    Usuario usuario = new Usuario();
    usuario.setEmail(email);
    usuario.setPassword("1234");
    usuario.setRol("Organizador");
    sessionFactory.getCurrentSession().persist(usuario);
    return usuario;
  }
}
