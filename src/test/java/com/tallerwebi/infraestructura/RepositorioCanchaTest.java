package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import com.tallerwebi.dominio.Cancha;
import com.tallerwebi.dominio.RepositorioCancha;
import com.tallerwebi.infraestructura.config.HibernateInfraestructuraTestConfig;
import jakarta.transaction.Transactional;
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
public class RepositorioCanchaTest {

  @Autowired
  private SessionFactory sessionFactory;

  private RepositorioCancha repositorioCancha;

  @BeforeEach
  public void init() {
    repositorioCancha = new RepositorioCanchaImpl(sessionFactory);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaEncontrarUnaCanchaGuardadaPorSuNombre() {
    // Given
    givenExisteUnaCancha("Cancha Los Leones");

    // When
    Cancha canchaObtenida = repositorioCancha.buscarPorNombre("Cancha Los Leones");

    // Then
    assertThat(canchaObtenida.getNombre(), is(equalTo("Cancha Los Leones")));
  }

  @Test
  @Transactional
  @Rollback
  public void noDeberiaEncontrarUnaCanchaInexistente() {
    // When
    Cancha canchaObtenida = repositorioCancha.buscarPorNombre("No existe");

    // Then
    assertThat(canchaObtenida, is(nullValue()));
  }

  //metodoss given

  private Cancha givenExisteUnaCancha(String nombre) {
    Cancha cancha = new Cancha();
    cancha.setNombre(nombre);
    sessionFactory.getCurrentSession().persist(cancha);
    return cancha;
  }
}
