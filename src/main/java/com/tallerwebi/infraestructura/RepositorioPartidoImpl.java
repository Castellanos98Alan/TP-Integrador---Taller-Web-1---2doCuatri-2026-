package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Partido;
import com.tallerwebi.dominio.RepositorioPartido;
import com.tallerwebi.dominio.Usuario;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioPartido")
public class RepositorioPartidoImpl implements RepositorioPartido {

  private SessionFactory sessionFactory;

  @Autowired
  public RepositorioPartidoImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Partido buscarPorId(Long id) {
    return sessionFactory.getCurrentSession().get(Partido.class, id);
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
    String filtroDistancia
  ) {
    String hql = armarConsultaHql(
      filtroNivel,
      filtroTipo,
      filtroCupoMaximo,
      filtroFecha,
      filtroHora,
      filtroDistancia
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
      filtroDistancia
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

  private void inyectarParametrosFiltro(
    Query<Partido> query,
    String filtroNivel,
    String filtroCupoMaximo,
    String filtroFecha,
    String filtroHora,
    String filtroDistancia
  ) {
    if (!FILTRO_TODOS.equals(filtroNivel)) {
      query.setParameter("nivel", filtroNivel);
    }

    if (!FILTRO_TODOS.equals(filtroCupoMaximo)) {
      query.setParameter("cupoMaximo", Integer.parseInt(filtroCupoMaximo));
    }

    if (!FILTRO_TODOS.equals(filtroDistancia)) {
      query.setParameter("distancia", Integer.parseInt(filtroDistancia));
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
    String filtroDistancia
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

    if (!FILTRO_TODOS.equals(filtroDistancia)) {
      hql.append("and p.distancia = :distancia ");
    }

    if (!FILTRO_TODOS.equals(filtroFecha)) {
      hql.append("and year(p.fecha) = :anio and month(p.fecha) = :mes and day(p.fecha) = :dia ");
    }

    if (!FILTRO_TODOS.equals(filtroHora)) {
      hql.append("and hour(p.fecha) = :hora and minute(p.fecha) = :minuto ");
    }

    return hql.toString();
  }
}
