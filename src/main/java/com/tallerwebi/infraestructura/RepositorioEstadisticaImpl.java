package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Estadistica;
import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.Partido;
import com.tallerwebi.dominio.RepositorioEstadistica;
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
}
