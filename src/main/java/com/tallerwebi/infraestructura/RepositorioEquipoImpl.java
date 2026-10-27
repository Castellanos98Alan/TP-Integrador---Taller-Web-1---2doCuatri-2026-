package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Equipo;
import com.tallerwebi.dominio.repositorio.RepositorioEquipo;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioEquipoImpl implements RepositorioEquipo {

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioEquipoImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(Equipo equipo) {
    sessionFactory.getCurrentSession().persist(equipo);
  }

  @Override
  public Equipo buscarPorId(Long id) {
    return sessionFactory.getCurrentSession().get(Equipo.class, id);
  }
}
