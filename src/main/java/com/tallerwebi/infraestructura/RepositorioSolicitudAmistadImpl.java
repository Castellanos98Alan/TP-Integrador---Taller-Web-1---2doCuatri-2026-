package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.SolicitudAmistad;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.repositorio.RepositorioSolicitudAmistad;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioSolicitudAmistadImpl implements RepositorioSolicitudAmistad {

  SessionFactory sessionFactory;

  @Autowired
  public RepositorioSolicitudAmistadImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public SolicitudAmistad buscarPorId(Integer id) {
    String query = "SELECT s FROM SolicitudAmistad s where s.id = :id";

    return sessionFactory
      .getCurrentSession()
      .createQuery(query, SolicitudAmistad.class)
      .setParameter("id", id)
      .uniqueResult();
  }

  @Override
  public void guardar(SolicitudAmistad solicitud) {
    sessionFactory.getCurrentSession().persist(solicitud);
  }

  @Override
  public void modificar(SolicitudAmistad solicitud) {
    sessionFactory.getCurrentSession().merge(solicitud);
  }

  @Override
  public List<SolicitudAmistad> buscarPendientesDe(Usuario receptor) {
    String query =
      "SELECT s FROM SolicitudAmistad s WHERE s.receptor = :receptor AND s.estado = :estado";

    return sessionFactory
      .getCurrentSession()
      .createQuery(query, SolicitudAmistad.class)
      .setParameter("receptor", receptor)
      .setParameter("estado", SolicitudAmistad.EstadoSolicitud.PENDIENTE)
      .getResultList();
  }

  @Override
  public boolean existeSolicitudPendienteEntre(Usuario emisor, Usuario receptor) {
    String query =
      "SELECT COUNT(s) FROM SolicitudAmistad s" +
      " WHERE s.emisor = :emisor " +
      "AND s.receptor = :receptor " +
      "AND s.estado = :estado";

    Long cantidad = sessionFactory
      .getCurrentSession()
      .createQuery(query, Long.class)
      .setParameter("emisor", emisor)
      .setParameter("receptor", receptor)
      .setParameter("estado", SolicitudAmistad.EstadoSolicitud.PENDIENTE)
      .getSingleResult();

    return cantidad > 0;
  }
}
