package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Partido;
import com.tallerwebi.dominio.repositorio.RepositorioPartido;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioPartido")
public class RepositorioPartidoImpl implements RepositorioPartido {

  private SessionFactory sessionFactory;

  @PersistenceContext
  private EntityManager entityManager;

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

  private static final String FILTRO_TODOS = "TODOS";
  private static final String FILTRO_PRIVADO = "PRIVADO";
  private static final String FILTRO_PUBLICO = "PUBLICO";

  @Override
  public List<Partido> buscarPartidosPorFiltro(
    String filtroNivel,
    String filtroTipo,
    String filtroCupoMaximo,
    String filtroFecha,
    String filtroHora,
    String filtroProvincia,
    String filtroLocalidad
  ) {
    String hql = armarConsultaHql(
      filtroNivel,
      filtroTipo,
      filtroCupoMaximo,
      filtroFecha,
      filtroHora,
      filtroProvincia,
      filtroLocalidad
    );

    Query<Partido> query = sessionFactory
      .getCurrentSession()
      .createQuery(hql.toString(), Partido.class);

    inyectarParametrosFiltro(
      query,
      filtroNivel,
      filtroCupoMaximo,
      filtroFecha,
      filtroHora,
      filtroProvincia,
      filtroLocalidad
    );

    return query.getResultList();
  }

  @Override
  public void guardar(Partido partido) {
    sessionFactory.getCurrentSession().persist(partido);
  }

  @Override
  public void modificar(Partido partido) {
    sessionFactory.getCurrentSession().merge(partido);
  }

  //Este metodo lo que hace es comunicarse con la base de datos para poder ver los partidos
  //que tiene un usuario en especifico. Utilice una query para poder consultarle a la tabla intermedia
  //InscripcionPartido filtrandolo por el ID de usuario. Esto lo que devuelve es la lista de partidos
  //asociado a las inscripciones del jugador.
  @Override
  public List<Partido> buscarPartidosPorJugador(Long idJugador) {
    if (idJugador == null) {
      return new ArrayList<>();
    }
    String query = "SELECT i.partido FROM InscripcionPartido i WHERE i.jugador.id = :idJugador";
    return entityManager
      .createQuery(query, Partido.class)
      .setParameter("idJugador", idJugador)
      .getResultList();
  }

  private void inyectarParametrosFiltro(
    Query<Partido> query,
    String filtroNivel,
    String filtroCupoMaximo,
    String filtroFecha,
    String filtroHora,
    String filtroProvincia,
    String filtroLocalidad
  ) {
    if (!FILTRO_TODOS.equals(filtroNivel)) {
      query.setParameter("nivel", filtroNivel);
    }

    if (!FILTRO_TODOS.equals(filtroCupoMaximo)) {
      query.setParameter("cupoMaximo", Integer.parseInt(filtroCupoMaximo));
    }

    if (!FILTRO_TODOS.equals(filtroProvincia)) {
      query.setParameter("provincia", filtroProvincia);
    }
    if (!FILTRO_TODOS.equals(filtroLocalidad)) {
      query.setParameter("localidad", filtroLocalidad);
    }

    if (!FILTRO_TODOS.equals(filtroFecha)) {
      LocalDate fechaParsed = LocalDate.parse(filtroFecha);
      query.setParameter("anio", fechaParsed.getYear());
      query.setParameter("mes", fechaParsed.getMonthValue());
      query.setParameter("dia", fechaParsed.getDayOfMonth());
    }

    if (!FILTRO_TODOS.equals(filtroHora)) {
      LocalTime horaParsed = LocalTime.parse(filtroHora);
      query.setParameter("hora", horaParsed.getHour());
      query.setParameter("minuto", horaParsed.getMinute());
    }
  }

  private String armarConsultaHql(
    String filtroNivel,
    String filtroTipo,
    String filtroCupoMaximo,
    String filtroFecha,
    String filtroHora,
    String filtroProvincia,
    String filtroLocalidad
  ) {
    StringBuilder hql = new StringBuilder("from Partido p where 1=1 ");
    if (!FILTRO_TODOS.equals(filtroNivel)) {
      hql.append("and p.nivel = :nivel ");
    }
    if (FILTRO_PRIVADO.equals(filtroTipo)) {
      hql.append("and p.esPrivado = true ");
    } else if (FILTRO_PUBLICO.equals(filtroTipo)) {
      hql.append("and p.esPrivado = false ");
    }

    if (!FILTRO_TODOS.equals(filtroCupoMaximo)) {
      hql.append("and p.cupoMaximo = :cupoMaximo ");
    }

    if (!FILTRO_TODOS.equals(filtroProvincia)) {
      hql.append("and p.cancha.provincia = :provincia ");
    }
    if (!FILTRO_TODOS.equals(filtroLocalidad)) {
      hql.append("and p.cancha.localidad = :localidad ");
    }

    if (!FILTRO_TODOS.equals(filtroFecha)) {
      hql.append("and year(p.fecha) = :anio and month(p.fecha) = :mes and day(p.fecha) = :dia ");
    }

    if (!FILTRO_TODOS.equals(filtroHora)) {
      hql.append(
        "and EXTRACT(HOUR FROM p.fecha) = :hora and EXTRACT(MINUTE FROM p.fecha) = :minuto "
      );
    }

    return hql.toString();
  }
}
