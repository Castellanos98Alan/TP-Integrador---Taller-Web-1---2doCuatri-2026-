package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.RepositorioJugador;
import com.tallerwebi.dominio.Usuario;
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
}
