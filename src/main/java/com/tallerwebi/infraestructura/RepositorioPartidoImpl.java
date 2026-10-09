package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Partido;
import com.tallerwebi.dominio.RepositorioPartido;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioPartido")
public class RepositorioPartidoImpl implements RepositorioPartido {

  private SessionFactory sessionFactory;

  @Autowired
  public RepositorioPartidoImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  /*@Override
    public Partido buscarPorId(Long id) {
        return sessionFactory.getCurrentSession().get(Partido.class, id);
    } este es de juanpa del sprint 1 */

  @Override
  public Partido buscarPorId(Long id) {
    String query = "select p from Partido p where p.id = :id";
    return sessionFactory
      .getCurrentSession()
      .createQuery(query, Partido.class)
      .setParameter("id", id)
      .uniqueResult();
  }

  @Override
  public void guardar(Partido partido) {
    sessionFactory.getCurrentSession().persist(partido);
  }

  @Override
  public void modificar(Partido partido) {
    sessionFactory.getCurrentSession().merge(partido);
  }
}
