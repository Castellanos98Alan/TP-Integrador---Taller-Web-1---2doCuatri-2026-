package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Cancha;
import com.tallerwebi.dominio.RepositorioCancha;
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
}
