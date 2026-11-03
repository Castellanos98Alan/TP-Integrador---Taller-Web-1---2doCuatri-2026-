package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Enumeration.TipoDeNotificacion;
import com.tallerwebi.dominio.Notificacion;
import com.tallerwebi.dominio.repositorio.RepositorioNotificacion;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioNotificacionImpl implements RepositorioNotificacion {

  SessionFactory sessionFactory;

  public RepositorioNotificacionImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(Notificacion notificacion) {
    sessionFactory.getCurrentSession().persist(notificacion);
  }

  @Override
  public List<Notificacion> buscarPorUsuario(Long idUsuario) {
    String query =
      "Select n from Notificacion n where n.usuario.id = :idUsuario" + " ORDER BY n.fecha DESC";

    return sessionFactory
      .getCurrentSession()
      .createQuery(query, Notificacion.class)
      .setParameter("idUsuario", idUsuario)
      .getResultList();
  }

  @Override
  public Notificacion buscarPorID(Long id) {
    String query = "Select n from Notificacion n where id = :id";

    return sessionFactory
      .getCurrentSession()
      .createQuery(query, Notificacion.class)
      .setParameter("id", id)
      .uniqueResult();
  }

  @Override
  public List<Notificacion> buscarNoLeidasPorUsuario(Long idUsuario) {
    String query =
      "SELECT n FROM Notificacion n " +
      "WHERE n.usuario.id = :idUsuario " +
      "AND n.leida = false " +
      "ORDER BY n.fecha DESC";

    return sessionFactory
      .getCurrentSession()
      .createQuery(query, Notificacion.class)
      .setParameter("idUsuario", idUsuario)
      .getResultList();
  }

  @Override
  public boolean existeInvitacionParaPartido(Long idUsuario, Long idPartido) {
    String query =
      "SELECT COUNT(n) FROM Notificacion n " +
      "WHERE n.usuario.id = :idUsuario " +
      "AND n.partido.id = :idPartido " +
      "AND n.tipoDeNotificacion = :tipo";

    Long cantidad = sessionFactory
      .getCurrentSession()
      .createQuery(query, Long.class)
      .setParameter("idUsuario", idUsuario)
      .setParameter("idPartido", idPartido)
      .setParameter("tipo", TipoDeNotificacion.INVITACION_PARTIDO)
      .uniqueResult();

    return cantidad != null && cantidad > 0;
  }
}
