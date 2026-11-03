package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.SolicitudAmistad;
import com.tallerwebi.dominio.repositorio.RepositorioJugador;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioJugador")
public class RepositorioJugadorImpl implements RepositorioJugador {

  private static final String PARAM_NIVEL = "nivel";
  private static final String PARAM_POSICION = "posicion";
  private static final String PARAM_ESTADO = "estado";
  private static final String PARAM_USUARIO_ID = "usuarioId";

  private static final String VISTA_AMIGOS = "amigos";

  private static final String RANGO_PRINCIPIANTE = "principiante";
  private static final String RANGO_INTERMEDIO = "intermedio";
  private static final String RANGO_AVANZADO = "avanzado";

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

  @Override
  public List<Jugador> buscarRanking() {
    String query = "SELECT j FROM Jugador j ORDER BY j.puntos DESC";

    return sessionFactory.getCurrentSession().createQuery(query, Jugador.class).getResultList();
  }

  @Override
  public Jugador buscarPorUsuarioId(Long usuarioId) {
    String query = "SELECT j FROM Jugador j WHERE j.usuario.id = :usuarioId";

    List<Jugador> jugadores = sessionFactory
      .getCurrentSession()
      .createQuery(query, Jugador.class)
      .setParameter(PARAM_USUARIO_ID, usuarioId)
      .getResultList();

    return jugadores.isEmpty() ? null : jugadores.get(0);
  }

  @Override
  public Jugador buscarPorId(Long id) {
    String query = "SELECT j FROM Jugador j WHERE j.id = :id";
    List<Jugador> jugadores = sessionFactory
      .getCurrentSession()
      .createQuery(query, Jugador.class)
      .setParameter("id", id)
      .getResultList();
    return jugadores.isEmpty() ? null : jugadores.get(0);
  }

  @Override
  public void modificar(Jugador jugador) {
    sessionFactory.getCurrentSession().merge(jugador);
  }

  @Override
  public List<Jugador> buscarRankingFiltrado(
    String nivel,
    String posicion,
    String rango,
    String vista,
    Long usuarioId
  ) {
    String query = getQuery(nivel, posicion, rango, vista);

    if (VISTA_AMIGOS.equals(vista)) {
      return ejecutarConsultaAmigos(query, nivel, posicion, usuarioId);
    }

    return ejecutarConsultaRanking(query, nivel, posicion);
  }

  private List<Jugador> ejecutarConsultaAmigos(
    String query,
    String nivel,
    String posicion,
    Long usuarioId
  ) {
    if (nivel != null && !nivel.isEmpty() && posicion != null && !posicion.isEmpty()) {
      return sessionFactory
        .getCurrentSession()
        .createQuery(query, Jugador.class)
        .setParameter(PARAM_NIVEL, nivel)
        .setParameter(PARAM_POSICION, posicion)
        .setParameter(PARAM_ESTADO, SolicitudAmistad.EstadoSolicitud.ACEPTADA)
        .setParameter(PARAM_USUARIO_ID, usuarioId)
        .getResultList();
    }

    if (nivel != null && !nivel.isEmpty()) {
      return sessionFactory
        .getCurrentSession()
        .createQuery(query, Jugador.class)
        .setParameter(PARAM_NIVEL, nivel)
        .setParameter(PARAM_ESTADO, SolicitudAmistad.EstadoSolicitud.ACEPTADA)
        .setParameter(PARAM_USUARIO_ID, usuarioId)
        .getResultList();
    }

    if (posicion != null && !posicion.isEmpty()) {
      return sessionFactory
        .getCurrentSession()
        .createQuery(query, Jugador.class)
        .setParameter(PARAM_POSICION, posicion)
        .setParameter(PARAM_ESTADO, SolicitudAmistad.EstadoSolicitud.ACEPTADA)
        .setParameter(PARAM_USUARIO_ID, usuarioId)
        .getResultList();
    }

    return sessionFactory
      .getCurrentSession()
      .createQuery(query, Jugador.class)
      .setParameter(PARAM_ESTADO, SolicitudAmistad.EstadoSolicitud.ACEPTADA)
      .setParameter(PARAM_USUARIO_ID, usuarioId)
      .getResultList();
  }

  private String getQuery(String nivel, String posicion, String rango, String vista) {
    String query = "SELECT j FROM Jugador j WHERE 1 = 1 ";

    query += getFiltroNivel(nivel);
    query += getFiltroPosicion(posicion);
    query += getFiltroRango(nivel, rango);
    query += getFiltroVista(vista);

    query += "ORDER BY j.puntos DESC";

    return query;
  }

  private String getFiltroNivel(String nivel) {
    if (nivel != null && !nivel.isEmpty()) {
      return "AND j.nivel = :nivel ";
    }

    return "";
  }

  private String getFiltroPosicion(String posicion) {
    if (posicion != null && !posicion.isEmpty()) {
      return "AND j.posicion1 = :posicion ";
    }

    return "";
  }

  private String getFiltroRango(String nivel, String rango) {
    if (nivel != null && !nivel.isEmpty()) {
      return "";
    }

    if (rango == null || rango.isEmpty()) {
      return "";
    }

    switch (rango) {
      case RANGO_PRINCIPIANTE:
        return "AND (j.nivel = 'C' OR j.nivel = 'D') ";
      case RANGO_INTERMEDIO:
        return "AND (j.nivel = 'B' OR j.nivel = 'A') ";
      case RANGO_AVANZADO:
        return "AND (j.nivel = 'S' OR j.nivel = 'SS') ";
      default:
        return "";
    }
  }

  private String getFiltroVista(String vista) {
    if (VISTA_AMIGOS.equals(vista)) {
      return (
        "AND j.usuario IS NOT NULL " +
        "AND EXISTS (" +
        "SELECT s FROM SolicitudAmistad s " +
        "WHERE s.estado = :estado " +
        "AND (" +
        "(s.emisor.id = :usuarioId AND s.receptor = j.usuario) " +
        "OR " +
        "(s.receptor.id = :usuarioId AND s.emisor = j.usuario)" +
        ")" +
        ") "
      );
    }

    return "";
  }

  private List<Jugador> ejecutarConsultaRanking(String query, String nivel, String posicion) {
    if (nivel != null && !nivel.isEmpty() && posicion != null && !posicion.isEmpty()) {
      return sessionFactory
        .getCurrentSession()
        .createQuery(query, Jugador.class)
        .setParameter(PARAM_NIVEL, nivel)
        .setParameter(PARAM_POSICION, posicion)
        .getResultList();
    }

    if (nivel != null && !nivel.isEmpty()) {
      return sessionFactory
        .getCurrentSession()
        .createQuery(query, Jugador.class)
        .setParameter(PARAM_NIVEL, nivel)
        .getResultList();
    }

    if (posicion != null && !posicion.isEmpty()) {
      return sessionFactory
        .getCurrentSession()
        .createQuery(query, Jugador.class)
        .setParameter(PARAM_POSICION, posicion)
        .getResultList();
    }

    return sessionFactory.getCurrentSession().createQuery(query, Jugador.class).getResultList();
  }
}
