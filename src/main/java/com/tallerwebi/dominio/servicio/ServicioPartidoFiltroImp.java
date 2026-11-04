package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.Partido;
import com.tallerwebi.dominio.repositorio.RepositorioPartido;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServicioPartidoFiltroImp implements ServicioPartidoFiltro {

  private RepositorioPartido repositorioPartido;

  @Autowired
  public ServicioPartidoFiltroImp(RepositorioPartido repositorioPartido) {
    this.repositorioPartido = repositorioPartido;
  }

  public static final String FILTRO_TODOS = "TODOS";

  @Override
  public List<Partido> listarPartidosSegunFiltro(
    String filtroNivel,
    String filtroTipo,
    String filtrocupoMaximo,
    String filtroFecha,
    String filtroHora,
    String filtroPartido,
    String filtroLocalidad
  ) {
    return repositorioPartido.buscarPartidosPorFiltro(
      filtroNivel,
      filtroTipo,
      filtrocupoMaximo,
      filtroFecha,
      filtroHora,
      filtroPartido,
      filtroLocalidad
    );
  }

  @Override
  public List<String> obtenerFechasDisponibles() {
    List<Partido> partidos = repositorioPartido.buscarPartidosPorFiltro(
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS
    );

    Set<String> fechasUnicas = new TreeSet<>();
    for (Partido p : partidos) {
      if (p.getFecha() != null) {
        fechasUnicas.add(p.getFecha().toLocalDate().toString());
      }
    }
    return new ArrayList<>(fechasUnicas);
  }

  @Override
  public List<String> obtenerHorasDisponibles() {
    List<Partido> partidos = repositorioPartido.buscarPartidosPorFiltro(
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS
    );

    Set<String> horasYMinutosUnicas = new TreeSet<>();
    for (Partido p : partidos) {
      if (p.getFecha() != null) {
        String horaFormateada = String.format(
          "%02d:%02d",
          p.getFecha().getHour(),
          p.getFecha().getMinute()
        );
        horasYMinutosUnicas.add(horaFormateada);
      }
    }
    return new ArrayList<>(horasYMinutosUnicas);
  }

  @Override
  public List<String> obtenerProvinciasDisponibles() {
    Set<String> provincias = new TreeSet<>();
    List<Partido> partidos = repositorioPartido.buscarPartidosPorFiltro(
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS
    );
    for (Partido partido : partidos) {
      if (partido.getCancha() != null && partido.getCancha().getProvincia() != null) {
        provincias.add(partido.getCancha().getProvincia());
      }
    }
    return new ArrayList<>(provincias);
  }

  @Override
  public List<String> obtenerLocalidadesDisponibles() {
    Set<String> localidades = new TreeSet<>();
    List<Partido> partidos = repositorioPartido.buscarPartidosPorFiltro(
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS
    );
    for (Partido partido : partidos) {
      if (partido.getCancha() != null && partido.getCancha().getLocalidad() != null) {
        localidades.add(partido.getCancha().getLocalidad());
      }
    }
    return new ArrayList<>(localidades);
  }

  @Override
  public List<Integer> obtenerCuposDisponibles() {
    List<Partido> todosLosPartidos = repositorioPartido.buscarPartidosPorFiltro(
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS
    );

    Set<Integer> cuposUnicos = new TreeSet<>();

    for (Partido partido : todosLosPartidos) {
      if (partido.getCupoMaximo() != null) {
        cuposUnicos.add(partido.getCupoMaximo());
      }
    }
    return new ArrayList<>(cuposUnicos);
  }
}
