package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.repositorio.RepositorioJugador;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioJugador")
public class RepositorioJugadorImpl implements RepositorioJugador {

  private SessionFactory sessionFactory;

  @Autowired
  public RepositorioJugadorImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(Jugador jugador) {
    sessionFactory.getCurrentSession().persist(jugador);
  }

  @Override
  public List<Jugador> buscarTodos() {
    return sessionFactory
      .getCurrentSession()
      .createQuery("from Jugador", Jugador.class)
      .getResultList();
  }

  @Override
  public List<Jugador> buscarRanking() {
    String query = "SELECT j FROM Jugador j ORDER BY j.puntos DESC";

    return sessionFactory.getCurrentSession().createQuery(query, Jugador.class).getResultList();
  }

  @Override
  public List<Jugador> buscarRankingPorNivel(String nivel) {
    String query = "SELECT j FROM Jugador j WHERE j.nivel = :nivel ORDER BY j.puntos DESC";

    return sessionFactory
      .getCurrentSession()
      .createQuery(query, Jugador.class)
      .setParameter("nivel", nivel)
      .getResultList();
  }

  @Override
  public List<Jugador> buscarRankingPorPosicion(String posicion1) {
    String query = "SELECT j FROM Jugador j WHERE j.posicion1 = :posicion ORDER BY j.puntos DESC";

    return sessionFactory
      .getCurrentSession()
      .createQuery(query, Jugador.class)
      .setParameter("posicion", posicion1)
      .getResultList();
  }

  @Override
  public List<Jugador> buscarRankingPorPosicionYNivel(String nivel, String posicion) {
    String query =
      "SELECT j FROM Jugador j WHERE j.nivel = :nivel AND j.posicion1 = :posicion ORDER BY j.puntos DESC";

    return sessionFactory
      .getCurrentSession()
      .createQuery(query, Jugador.class)
      .setParameter("nivel", nivel)
      .setParameter("posicion", posicion)
      .getResultList();
  }

  //  @Override
  //  public List<Jugador> buscarRankingPorVista(String vista) {
  //    String query = "SELECT j FROM Jugador j WHERE j.vista = :nivel ORDER BY j.puntos DESC";
  //
  //    return sessionFactory.getCurrentSession()
  //            .createQuery(query, Jugador.class)
  //            .setParameter("vista", vista)
  //            .getResultList();
  //  }

  @Override
  public Jugador buscarPorUsuarioId(Long usuarioId) {
    String query = "SELECT j FROM Jugador j WHERE j.usuario.id = :usuarioId";

    List<Jugador> jugadores = sessionFactory
      .getCurrentSession()
      .createQuery(query, Jugador.class)
      .setParameter("usuarioId", usuarioId)
      .getResultList();

    return jugadores.isEmpty() ? null : jugadores.get(0);
  }

  @Override
  public void modificar(Jugador jugador) {
    sessionFactory.getCurrentSession().merge(jugador);
  }
}
