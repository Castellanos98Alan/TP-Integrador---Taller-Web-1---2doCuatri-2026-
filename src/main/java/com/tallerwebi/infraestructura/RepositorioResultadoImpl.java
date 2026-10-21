package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Partido;
import com.tallerwebi.dominio.RepositorioResultado;
import com.tallerwebi.dominio.Resultado;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioResultadoImpl implements RepositorioResultado {

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioResultadoImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(Resultado resultado) {
    sessionFactory.getCurrentSession().persist(resultado);
  }

  @Override
  public void modificar(Resultado resultado) {
    sessionFactory.getCurrentSession().merge(resultado);
  }

  @Override
  public Resultado buscarPorId(Integer id) {
    return sessionFactory.getCurrentSession().get(Resultado.class, id);
  }

  @Override
  public Resultado buscarPorPartido(Partido partido) {
    String query = "SELECT p.resultado FROM Partido p WHERE p = :partido";

    return sessionFactory
      .getCurrentSession()
      .createQuery(query, Resultado.class)
      .setParameter("partido", partido)
      .uniqueResult();
  }
}
