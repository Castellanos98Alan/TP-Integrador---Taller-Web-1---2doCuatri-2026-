package com.tallerwebi.dominio;

import com.tallerwebi.presentacion.DatosCrearPartido;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("servicioPartido")
@Transactional
public class ServicioPartidoImpl implements ServicioPartido {

  private final RepositorioPartido repositorioPartido;
  private final RepositorioUsuario repositorioUsuario;

  @Autowired
  public ServicioPartidoImpl(
    RepositorioPartido repositorioPartido,
    RepositorioUsuario repositorioUsuario
  ) {
    this.repositorioPartido = repositorioPartido;
    this.repositorioUsuario = repositorioUsuario;
  }

  @Override
  public void crearPartido(DatosCrearPartido datos, Usuario creador) {}

  @Override
  public void cancelarPartido(Long partidoId, Usuario usuarioLogueado) {
    Partido partido = repositorioPartido.buscarPorId(partidoId);

    if (partido == null) {
      throw new RuntimeException("El partido no existe.");
    }

    if (partido.getOrganizador() == null || !partido.getOrganizador().equals(usuarioLogueado)) {
      throw new RuntimeException("No estas autorizado para cancelar este partido.");
    }

    if (partido.getEstado() == Partido.EstadoPartido.FINALIZADO) {
      throw new RuntimeException("No se puede cancelar un partido que ya finalizo.");
    }

    partido.cancelarPartido();
    repositorioPartido.modificar(partido);
  }

  //Utilice el Transactional para que no se me cierre la sesion al momento de ejecutar el metodo,
  //con esto puedo consultar correctamente el partido mandandole el id por parametro.
  //Tambien inicio la lista de confirmados ya que esta mapeado con LAZY en la clase Partido.
  @Transactional
  @Override
  public Partido buscarPartidoPorId(Long id) {
    Partido partido = repositorioPartido.buscarPorId(id);
    if (partido != null && partido.getConfirmados() != null) {
      org.hibernate.Hibernate.initialize(partido.getConfirmados());
    }
    return partido;
  }

  //Primero utilice directamente el metodo buscarPartidoPorId en lugar de usar el repositorioPartido directamente
  //para que cada vez que lo use me traiga el partido con su lista de confirmados inicializada. Tambien hice una validacion
  //rapida para ver si el partido existe.
  //Por ultimo decidi separar las validaciones en metodos privados debido a la regla de PMD y que quede mejor organizado.
  @Override
  public void unirseAPartido(Long partidoId, Usuario usuarioLogueado) {
    Partido partido = buscarPartidoPorId(partidoId);

    if (partido == null) {
      throw new RuntimeException("El partido no existe.");
    }

    validarEstadoDelPartido(partido);
    validarCuposYDisponibilidad(partido, usuarioLogueado);

    partido.getConfirmados().add(usuarioLogueado);
    partido.setCantJugadores(partido.getConfirmados().size());

    repositorioPartido.modificar(partido);
  }

  //Metodo privado que valida el estado del partido, con esto logro ver si el partido tiene estado Finalizado o Cancelado
  //si es asi, entonces el usuario no podra ingresar al partido.
  private void validarEstadoDelPartido(Partido partido) {
    if (
      partido.getEstado() == Partido.EstadoPartido.FINALIZADO ||
      partido.getEstado() == Partido.EstadoPartido.CANCELADO
    ) {
      throw new RuntimeException("No se puede unir a un partido finalizado o cancelado.");
    }
  }

  //Metodo privado que valida los cupos de la lista de confirmados, donde primero valido si la lista de confirmados es nula
  //si es asi, entonces inicializo la lista de confirmados. Luego de eso valido si el usuarioLogueado esta dentro de la lista
  //confirmados, si es asi significa que el usuario ya esta dentro de la sala de espera.
  //Por ultimo valido que el partido tenga un cupo maximo de jugadores, para luego validar si los usuarios anotados supera el
  //cupo maximo de jugadores
  private void validarCuposYDisponibilidad(Partido partido, Usuario usuarioLogueado) {
    if (partido.getConfirmados() == null) {
      partido.setConfirmados(new ArrayList<>());
    }

    if (partido.getConfirmados().contains(usuarioLogueado)) {
      throw new RuntimeException("Ya estas anotado en la sala de espera de este partido.");
    }

    if (
      partido.getCupoMaximo() != null && partido.getConfirmados().size() >= partido.getCupoMaximo()
    ) {
      throw new RuntimeException("Cupo maximo alcanzado.");
    }
  }

  public static final String FILTRO_TODOS = "TODOS";

  @Override
  public List<Partido> listarPartidosSegunFiltro(
    String filtroNivel,
    String filtroTipo,
    String filtrocupoMaximo,
    String filtroFecha,
    String filtroHora,
    String FiltroDistancia
  ) {
    List<Partido> partidosFalsos = new ArrayList<>();
    Cancha cancha = new Cancha(1L, "Pedro");
    cancha.setId(1L);
    cancha.setNombre("Cancha de fútbol los leones");

    Partido partido01 = new Partido();
    partido01.setId(1L);
    partido01.setEsPrivado(false);
    partido01.setNivel("PRINCIPIANTE");
    partido01.setcupoMaximo(22);
    partido01.setCancha(cancha);
    partido01.setCodigoDeAcceso("123");
    partido01.setDistanciaKm(5);
    LocalDateTime fechaFalsa = LocalDateTime.of(2026, 10, 2, 14, 30);
    partido01.setFecha(fechaFalsa);

    Partido partido02 = new Partido();
    partido02.setId(2L);
    partido02.setEsPrivado(true);
    partido02.setNivel("INTERMEDIO");
    partido02.setcupoMaximo(22);
    partido02.setCancha(cancha);
    LocalDateTime fechaFalsa2 = LocalDateTime.of(2026, 10, 2, 18, 00);
    partido02.setFecha(fechaFalsa2);
    partido02.setDistanciaKm(3);

    partidosFalsos.add(partido01);
    partidosFalsos.add(partido02);

    List<Partido> partidosFiltrados = new ArrayList<>();

    for (Partido p : partidosFalsos) {
      Boolean coincideNivel = FILTRO_TODOS.equals(filtroNivel) || filtroNivel.equals(p.getNivel());
      Boolean coincideTipo =
        FILTRO_TODOS.equals(filtroTipo) ||
        ("PUBLICO".equals(filtroTipo) && !p.getEsPrivado()) ||
        ("PRIVADO".equals(filtroTipo) && p.getEsPrivado());
      Boolean coincideCupo =
        FILTRO_TODOS.equals(filtrocupoMaximo) ||
        (p.getCupoMaximo() != null && p.getCupoMaximo().toString().equals(filtrocupoMaximo));
      Boolean coincideFecha =
        FILTRO_TODOS.equals(filtroFecha) ||
        (p.getFecha() != null && p.getFecha().toLocalDate().toString().equals(filtroFecha));
      Boolean coincideHora =
        FILTRO_TODOS.equals(filtroHora) ||
        (p.getFecha().toLocalTime() != null &&
          p.getFecha().toLocalTime().toString().equals(filtroHora));
      Boolean coincideDistancia =
        FILTRO_TODOS.equals(FiltroDistancia) ||
        (p.getDistanciaKm() != null && p.getDistanciaKm().toString().equals(FiltroDistancia));

      if (
        coincideNivel &&
        coincideTipo &&
        coincideCupo &&
        coincideFecha &&
        coincideHora &&
        coincideDistancia
      ) {
        partidosFiltrados.add(p);
      }
    }
    return partidosFiltrados;
  }
}
