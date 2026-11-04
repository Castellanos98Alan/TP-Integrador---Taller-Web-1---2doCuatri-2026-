package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.repositorio.RepositorioJugador;
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
public class RepositorioJugadorImplTest {

  @Autowired
  private SessionFactory sessionFactory;

  private RepositorioJugador repositorioJugador;

  @BeforeEach
  public void init() {
    repositorioJugador = new RepositorioJugadorImpl(sessionFactory);
  }

  @Test
  @Transactional
  @Rollback
  public void queSePuedaBuscarRankingGeneralOrdenadoPorPuntos() {
    Jugador j1 = new Jugador();
    j1.setPuntos(10);
    sessionFactory.getCurrentSession().persist(j1);

    Jugador j2 = new Jugador();
    j2.setPuntos(50);
    sessionFactory.getCurrentSession().persist(j2);

    List<Jugador> ranking = repositorioJugador.buscarRankingFiltrado(null, null, null, null, null);

    assertThat(ranking.size(), equalTo(2));
    assertThat(ranking.get(0).getPuntos(), equalTo(50));
  }

  @Test
  @Transactional
  @Rollback
  public void queSePuedaBuscarRankingPorNivel() {
    Jugador j1 = new Jugador();
    j1.setNivel("B");
    j1.setPuntos(40);
    sessionFactory.getCurrentSession().persist(j1);

    Jugador j2 = new Jugador();
    j2.setNivel("A");
    j2.setPuntos(60);
    sessionFactory.getCurrentSession().persist(j2);

    Jugador j3 = new Jugador();
    j3.setNivel("B");
    j3.setPuntos(35);
    sessionFactory.getCurrentSession().persist(j3);

    List<Jugador> ranking = repositorioJugador.buscarRankingFiltrado("B", null, null, null, null);

    assertThat(ranking.size(), equalTo(2));
    assertThat(ranking.get(0).getNivel(), equalTo("B"));
    assertThat(ranking.get(1).getNivel(), equalTo("B"));
  }

  @Test
  @Transactional
  @Rollback
  public void queSePuedaBuscarRankingPorPosicion() {
    Jugador j1 = new Jugador();
    j1.setPosicion1("Defensor");
    j1.setPuntos(30);
    sessionFactory.getCurrentSession().persist(j1);

    Jugador j2 = new Jugador();
    j2.setPosicion1("Delantero");
    j2.setPuntos(40);
    sessionFactory.getCurrentSession().persist(j2);

    Jugador j3 = new Jugador();
    j3.setPosicion1("Defensor");
    j3.setPuntos(20);
    sessionFactory.getCurrentSession().persist(j3);

    List<Jugador> ranking = repositorioJugador.buscarRankingFiltrado(
      null,
      "Defensor",
      null,
      null,
      null
    );

    assertThat(ranking.size(), equalTo(2));
    assertThat(ranking.get(0).getPosicion1(), equalTo("Defensor"));
    assertThat(ranking.get(1).getPosicion1(), equalTo("Defensor"));
  }

  @Test
  @Transactional
  @Rollback
  public void queSePuedaBuscarRankingPorPosicionYNivel() {
    Jugador j1 = new Jugador();
    j1.setNivel("B");
    j1.setPosicion1("Defensor");
    j1.setPuntos(40);
    sessionFactory.getCurrentSession().persist(j1);

    Jugador j2 = new Jugador();
    j2.setNivel("B");
    j2.setPosicion1("Delantero");
    j2.setPuntos(35);
    sessionFactory.getCurrentSession().persist(j2);

    Jugador j3 = new Jugador();
    j3.setNivel("A");
    j3.setPosicion1("Defensor");
    j3.setPuntos(60);
    sessionFactory.getCurrentSession().persist(j3);

    List<Jugador> ranking = repositorioJugador.buscarRankingFiltrado(
      "B",
      "Defensor",
      null,
      null,
      null
    );

    assertThat(ranking.size(), equalTo(1));
    assertThat(ranking.get(0).getNivel(), equalTo("B"));
    assertThat(ranking.get(0).getPosicion1(), equalTo("Defensor"));
  }

  @Test
  @Transactional
  @Rollback
  public void queSePuedaBuscarRankingPorRangoPrincipiante() {
    Jugador j1 = new Jugador();
    j1.setNivel("C");
    j1.setPuntos(20);
    sessionFactory.getCurrentSession().persist(j1);

    Jugador j2 = new Jugador();
    j2.setNivel("D");
    j2.setPuntos(5);
    sessionFactory.getCurrentSession().persist(j2);

    Jugador j3 = new Jugador();
    j3.setNivel("B");
    j3.setPuntos(40);
    sessionFactory.getCurrentSession().persist(j3);

    List<Jugador> ranking = repositorioJugador.buscarRankingFiltrado(
      null,
      null,
      "principiante",
      null,
      null
    );

    assertThat(ranking.size(), equalTo(2));
  }

  @Test
  @Transactional
  @Rollback
  public void queSePuedaBuscarRankingPorRangoIntermedio() {
    Jugador j1 = new Jugador();
    j1.setNivel("B");
    j1.setPuntos(40);
    sessionFactory.getCurrentSession().persist(j1);

    Jugador j2 = new Jugador();
    j2.setNivel("A");
    j2.setPuntos(60);
    sessionFactory.getCurrentSession().persist(j2);

    Jugador j3 = new Jugador();
    j3.setNivel("C");
    j3.setPuntos(20);
    sessionFactory.getCurrentSession().persist(j3);

    List<Jugador> ranking = repositorioJugador.buscarRankingFiltrado(
      null,
      null,
      "intermedio",
      null,
      null
    );

    assertThat(ranking.size(), equalTo(2));
  }

  @Test
  @Transactional
  @Rollback
  public void queSePuedaBuscarRankingPorRangoAvanzado() {
    Jugador j1 = new Jugador();
    j1.setNivel("S");
    j1.setPuntos(90);
    sessionFactory.getCurrentSession().persist(j1);

    Jugador j2 = new Jugador();
    j2.setNivel("SS");
    j2.setPuntos(120);
    sessionFactory.getCurrentSession().persist(j2);

    Jugador j3 = new Jugador();
    j3.setNivel("A");
    j3.setPuntos(60);
    sessionFactory.getCurrentSession().persist(j3);

    List<Jugador> ranking = repositorioJugador.buscarRankingFiltrado(
      null,
      null,
      "avanzado",
      null,
      null
    );

    assertThat(ranking.size(), equalTo(2));
  }

  @Test
  @Transactional
  @Rollback
  public void queSePuedaBuscarRankingPorRangoYPosicion() {
    Jugador j1 = new Jugador();
    j1.setNivel("B");
    j1.setPosicion1("Defensor");
    j1.setPuntos(40);
    sessionFactory.getCurrentSession().persist(j1);

    Jugador j2 = new Jugador();
    j2.setNivel("A");
    j2.setPosicion1("Defensor");
    j2.setPuntos(60);
    sessionFactory.getCurrentSession().persist(j2);

    Jugador j3 = new Jugador();
    j3.setNivel("C");
    j3.setPosicion1("Defensor");
    j3.setPuntos(20);
    sessionFactory.getCurrentSession().persist(j3);

    Jugador j4 = new Jugador();
    j4.setNivel("B");
    j4.setPosicion1("Delantero");
    j4.setPuntos(45);
    sessionFactory.getCurrentSession().persist(j4);

    List<Jugador> ranking = repositorioJugador.buscarRankingFiltrado(
      null,
      "Defensor",
      "intermedio",
      null,
      null
    );

    assertThat(ranking.size(), equalTo(2));
    assertThat(ranking.get(0).getPosicion1(), equalTo("Defensor"));
    assertThat(ranking.get(1).getPosicion1(), equalTo("Defensor"));
  }

  @Test
  @Transactional
  @Rollback
  public void queSiSeSeleccionaNivelElRangoNoInterfiera() {
    Jugador j1 = new Jugador();
    j1.setNivel("B");
    j1.setPuntos(40);
    sessionFactory.getCurrentSession().persist(j1);

    Jugador j2 = new Jugador();
    j2.setNivel("A");
    j2.setPuntos(60);
    sessionFactory.getCurrentSession().persist(j2);

    Jugador j3 = new Jugador();
    j3.setNivel("S");
    j3.setPuntos(90);
    sessionFactory.getCurrentSession().persist(j3);

    List<Jugador> ranking = repositorioJugador.buscarRankingFiltrado(
      "B",
      null,
      "avanzado",
      null,
      null
    );

    assertThat(ranking.size(), equalTo(1));
    assertThat(ranking.get(0).getNivel(), equalTo("B"));
  }

  @Test
  @Transactional
  @Rollback
  public void queSePuedaBuscarTodosLosJugadores() {
    Jugador jugador = new Jugador();
    jugador.setPuntos(0);

    sessionFactory.getCurrentSession().persist(jugador);

    List<Jugador> todos = repositorioJugador.buscarTodos();

    assertThat(todos.isEmpty(), equalTo(false));
  }

  @Test
  @Transactional
  @Rollback
  public void queSePuedaModificarUnJugador() {
    Jugador jugador = new Jugador();
    jugador.setNombre("Original");
    jugador.setPuntos(0);

    sessionFactory.getCurrentSession().persist(jugador);

    jugador.setNombre("Modificado");

    repositorioJugador.modificar(jugador);

    sessionFactory.getCurrentSession().flush();

    sessionFactory.getCurrentSession().clear();

    List<Jugador> encontrados = repositorioJugador.buscarTodos();

    assertThat(encontrados.get(0).getNombre(), equalTo("Modificado"));
  }
}
