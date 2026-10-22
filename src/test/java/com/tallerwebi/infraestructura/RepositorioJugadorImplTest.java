package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.RepositorioJugador;
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
    j1.setPuntos(10); // Asegurate de que esta línea esté antes del persist
    sessionFactory.getCurrentSession().persist(j1);

    Jugador j2 = new Jugador();
    j2.setPuntos(50);
    sessionFactory.getCurrentSession().persist(j2);

    List<Jugador> ranking = repositorioJugador.buscarRanking();

    assertThat(ranking.size(), equalTo(2));
    assertThat(ranking.get(0).getPuntos(), equalTo(50));
  }

  @Test
  @Transactional
  @Rollback
  public void queSePuedaBuscarRankingPorNivel() {
    Jugador j1 = new Jugador();
    j1.setNivel("PRINCIPIANTE");
    j1.setPuntos(20);
    sessionFactory.getCurrentSession().persist(j1);

    Jugador j2 = new Jugador();
    j2.setNivel("AVANZADO");
    j2.setPuntos(0); // Agregamos puntos acá para evitar el error
    sessionFactory.getCurrentSession().persist(j2);

    List<Jugador> ranking = repositorioJugador.buscarRankingPorNivel("PRINCIPIANTE");

    assertThat(ranking.size(), equalTo(1));
    assertThat(ranking.get(0).getNivel(), equalTo("PRINCIPIANTE"));
  }

  @Test
  @Transactional
  @Rollback
  public void queSePuedaBuscarRankingPorPosicionYNivel() {
    Jugador j1 = new Jugador();
    j1.setNivel("PRINCIPIANTE");
    j1.setPosicion1("Defensor");
    j1.setPuntos(0); // Obligatorio
    sessionFactory.getCurrentSession().persist(j1);

    Jugador j2 = new Jugador();
    j2.setNivel("PRINCIPIANTE");
    j2.setPosicion1("Delantero");
    j2.setPuntos(0); // Obligatorio
    sessionFactory.getCurrentSession().persist(j2);

    List<Jugador> ranking = repositorioJugador.buscarRankingPorPosicionYNivel(
      "PRINCIPIANTE",
      "Defensor"
    );

    assertThat(ranking.size(), equalTo(1));
    assertThat(ranking.get(0).getPosicion1(), equalTo("Defensor"));
  }

  @Test
  @Transactional
  @Rollback
  public void queSePuedaBuscarTodosLosJugadores() {
    Jugador jugador = new Jugador();
    jugador.setPuntos(0); // Obligatorio
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
    jugador.setPuntos(0); // Obligatorio
    sessionFactory.getCurrentSession().persist(jugador);

    jugador.setNombre("Modificado");
    repositorioJugador.modificar(jugador);

    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

    List<Jugador> encontrados = repositorioJugador.buscarTodos();
    assertThat(encontrados.get(0).getNombre(), equalTo("Modificado"));
  }
}
