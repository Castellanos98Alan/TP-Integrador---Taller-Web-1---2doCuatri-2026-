package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import com.tallerwebi.dominio.Cancha;
import com.tallerwebi.dominio.repositorio.RepositorioCancha;
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

  @Test
  @Transactional
  @Rollback
  public void deberiaBuscarTodasLasCanchasGuardadas() {
    // Given
    givenExisteUnaCancha("Cancha Los Leones");
    givenExisteUnaCancha("Cancha El Potrero");
    givenExisteUnaCancha("Cancha 5");

    // When
    List<Cancha> canchasObtenidas = repositorioCancha.buscarTodas();

    // Then
    assertThat(canchasObtenidas, is(notNullValue()));
    assertThat(canchasObtenidas.size(), is(equalTo(3)));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaBuscarCanchasDestacadasRespetandoElLimite() {
    // Given
    givenExisteUnaCancha("Cancha 1");
    givenExisteUnaCancha("Cancha 2");
    givenExisteUnaCancha("Cancha 3");
    givenExisteUnaCancha("Cancha 4");
    givenExisteUnaCancha("Cancha 5"); // Guardamos 5 canchas

    Integer limiteEsperado = 4;

    // When
    List<Cancha> destacadas = repositorioCancha.buscarDestacadas(limiteEsperado);

    // Then
    assertThat(destacadas, is(notNullValue()));
    assertThat(destacadas.size(), is(equalTo(limiteEsperado))); // Solo debe traer 4
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaObtenerLasLocalidadesSinDuplicar() {
    // Given: Guardamos 3 canchas en solo 2 localidades distintas
    givenExisteUnaCanchaConLocalidad("Cancha 1", "San Justo");
    givenExisteUnaCanchaConLocalidad("Cancha 2", "San Justo");
    givenExisteUnaCanchaConLocalidad("Cancha 3", "Laferrere");

    // When
    List<String> localidadesObtenidas = repositorioCancha.buscarLocalidades();

    // Then
    assertThat(localidadesObtenidas, is(notNullValue()));
    assertThat(localidadesObtenidas.size(), is(equalTo(2))); // "San Justo" y "Laferrere"
  }

  //metodoss given

  private Cancha givenExisteUnaCancha(String nombre) {
    Cancha cancha = new Cancha();
    cancha.setNombre(nombre);
    sessionFactory.getCurrentSession().persist(cancha);
    return cancha;
  }

  private Cancha givenExisteUnaCanchaConLocalidad(String nombre, String localidad) {
    Cancha cancha = new Cancha();
    cancha.setNombre(nombre);
    cancha.setLocalidad(localidad);
    sessionFactory.getCurrentSession().persist(cancha);
    return cancha;
  }
}
