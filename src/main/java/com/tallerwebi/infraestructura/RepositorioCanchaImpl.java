package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Cancha;
import com.tallerwebi.dominio.RepositorioCancha;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioCancha")
public class RepositorioCanchaImpl implements RepositorioCancha {

  private SessionFactory sessionFactory;

  @Autowired
  public RepositorioCanchaImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Cancha buscarPorNombre(String nombre) {
    String query = "select c from Cancha c where c.nombre = :nombre";
    return sessionFactory
      .getCurrentSession()
      .createQuery(query, Cancha.class)
      .setParameter("nombre", nombre)
      .uniqueResult();
  }

  @Override
  public List<Cancha> buscarTodas() {
    String query = "select c from Cancha c order by c.nombre";
    return sessionFactory.getCurrentSession().createQuery(query, Cancha.class).getResultList();
  }

  @Override
  public List<String> buscarLocalidades() {
    String query = "select distinct c.localidad from Cancha c order by c.localidad";
    return sessionFactory.getCurrentSession().createQuery(query, String.class).getResultList();
  }
}
