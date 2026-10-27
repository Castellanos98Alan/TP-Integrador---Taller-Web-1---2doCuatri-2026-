package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Estadistica;
import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.Partido;
import com.tallerwebi.dominio.repositorio.RepositorioEstadistica;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioEstadisticaImpl implements RepositorioEstadistica {

  private SessionFactory sessionFactory;

  public RepositorioEstadisticaImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(Estadistica estadistica) {
    sessionFactory.getCurrentSession().persist(estadistica);
  }

  @Override
  public void modificar(Estadistica estadistica) {
    sessionFactory.getCurrentSession().merge(estadistica);
  }

  @Override
  public List<Estadistica> buscarTodasLasEstadisticasPorJugador(Jugador jugador) {
    String query = "Select e from Estadistica e where e.jugador = :Jugador";
    return sessionFactory
      .getCurrentSession()
      .createQuery(query, Estadistica.class)
      .setParameter("Jugador", jugador)
      .getResultList();
  }

  @Override
  public List<Estadistica> buscarTodasLasEstadisticasPorPartido(Partido partido) {
    String query = "SELECT e FROM Estadistica e where e.partido = :Partido";

    return sessionFactory
      .getCurrentSession()
      .createQuery(query, Estadistica.class)
      .setParameter("Partido", partido)
      .getResultList();
  }

  @Override
  public List<Estadistica> buscarPorJugadorYCondicion(
    Jugador jugador,
    Estadistica.CondicionJugador condicion
  ) {
    String query =
      "SELECT e FROM Estadistica e WHERE e.jugador = :jugador AND e.condicion = :condicion";

    return sessionFactory
      .getCurrentSession()
      .createQuery(query, Estadistica.class)
      .setParameter("jugador", jugador)
      .setParameter("condicion", condicion)
      .getResultList();
  }

  @Override
  public Estadistica buscarPorPartidoYJugador(Partido partido, Jugador jugador) {
    return (Estadistica) sessionFactory
      .getCurrentSession()
      .createQuery(
        "SELECT e FROM Estadistica e WHERE e.partido = :partido AND e.jugador = :jugador"
      )
      .setParameter("partido", partido)
      .setParameter("jugador", jugador)
      .uniqueResult(); // O getSingleResult() dependiendo de la versión
  }
}
