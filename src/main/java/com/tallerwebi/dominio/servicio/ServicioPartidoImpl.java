package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.Cancha;
import com.tallerwebi.dominio.Equipo;
import com.tallerwebi.dominio.InscripcionPartido;
import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.Partido;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.Partido.EstadoPartido;
import com.tallerwebi.dominio.repositorio.RepositorioCancha;
import com.tallerwebi.dominio.repositorio.RepositorioPartido;
import com.tallerwebi.dominio.repositorio.RepositorioUsuario;
import com.tallerwebi.presentacion.DatosCrearPartido;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("servicioPartido")
@Transactional
public class ServicioPartidoImpl implements ServicioPartido {

  private final RepositorioPartido repositorioPartido;
  private final RepositorioCancha repositorioCancha;
  private final RepositorioUsuario repositorioUsuario;
  private static final int HORAS_MINIMAS_ENTRE_PARTIDOS = 3;

  @Autowired
  public ServicioPartidoImpl(
    RepositorioPartido repositorioPartido,
    RepositorioCancha repositorioCancha,
    RepositorioUsuario repositorioUsuario
  ) {
    this.repositorioCancha = repositorioCancha;
    this.repositorioPartido = repositorioPartido;
    this.repositorioUsuario = repositorioUsuario;
  }

  //spint 2
  @Override
  public void crearPartido(DatosCrearPartido datos, Usuario creador) {
    if (datos.getCupo() == null || datos.getCupo() <= 0) {
      throw new RuntimeException("El cupo debe ser mayor a cero.");
    }
    if (datos.getFechaHora() == null) {
      throw new RuntimeException("Debe ingresar una fecha y hora.");
    }

    Cancha cancha = repositorioCancha.buscarPorNombre(datos.getCancha());
    if (cancha == null) {
      throw new RuntimeException("La cancha ingresada no existe.");
    }

    Partido partido = new Partido();
    partido.setCancha(cancha);
    partido.setFecha(datos.getFechaHora());
    partido.setCupoMaximo(datos.getCupo());
    partido.setNivel(datos.getNivel());
    partido.setOrganizador(creador);
    partido.setEsPrivado(datos.getEsPrivado());

    if (Boolean.TRUE.equals(datos.getEsPrivado())) {
      partido.setCodigoDeAcceso(UUID.randomUUID().toString().substring(0, 8));
    }

    repositorioPartido.guardar(partido);
  }

  @Override
  public List<Cancha> listarCanchas() {
    return repositorioCancha.buscarTodas();
  }

  @Override
  public List<String> listarLocalidades() {
    return repositorioCancha.buscarLocalidades();
  }

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
  //Utilizo el repositorioPartido que ya tiene un metodo que es buscarPorId.
  @Transactional
  @Override
  public Partido buscarPartidoPorId(Long id) {
    return repositorioPartido.buscarPorId(id);
  }

  //Primero utilice directamente el metodo buscarPartidoPorId en lugar de usar el repositorioPartido directamente
  //para que cada vez que lo use me traiga el partido con su lista de confirmados inicializada. Tambien hice una validacion
  //rapida para ver si el partido existe.
  //Por ultimo decidi separar las validaciones en metodos privados debido a la regla de PMD y que quede mejor organizado..
  @Override
  public void unirseAPartido(Long partidoId, Jugador usuarioLogueadoJugador) {
    Partido partido = buscarPartidoPorId(partidoId);

    if (partido == null) {
      throw new RuntimeException("El partido no existe.");
    }

    validarEstadoDelPartido(partido);
    validarCuposYDisponibilidad(partido, usuarioLogueadoJugador);
    validarHorarioParaInscripcion(partido, usuarioLogueadoJugador);

    InscripcionPartido nuevaInscripcion = new InscripcionPartido(partido, usuarioLogueadoJugador);
    partido.getInscripciones().add(nuevaInscripcion);
    partido.setCantJugadores(partido.getInscripciones().size());

    // Disparador del balanceo automático
    int cupoMaximo = partido.getCupoMaximo();
    if (partido.getCantJugadores() == cupoMaximo) {
        balancearEquipos(partido);
        partido.setEstado(EstadoPartido.ACTIVO);
    }

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

  //Metodo privado que valida los cupos de la lista de Inscripciones, donde primero valido si la lista es nula
  //si es asi, entonces inicializo la lista de inscripciones. Luego de eso valido si el usuarioLogueado esta dentro de la lista
  //confirmados, si es asi significa que el usuario ya esta dentro de la sala de espera.
  //Por ultimo valido que el partido tenga un cupo maximo de jugadores, para luego validar si los usuarios anotados supera el
  //cupo maximo de jugadores
  private void validarCuposYDisponibilidad(Partido partido, Jugador usuarioLogueadoJugador) {
    if (partido.getInscripciones() == null) {
      partido.setInscripciones(new ArrayList<>());
    }

    boolean inscripto = partido
      .getInscripciones()
      .stream()
      .anyMatch(i -> i.getJugador().equals(usuarioLogueadoJugador));

    if (inscripto) {
      throw new RuntimeException("Ya estas anotado en la sala de espera de este partido.");
    }

    if (
      partido.getCupoMaximo() != null &&
      partido.getInscripciones().size() >= partido.getCupoMaximo()
    ) {
      throw new RuntimeException("Cupo maximo alcanzado.");
    }
  }

  //Este metodo valida la diferencia horaria entre un partido que el usuario ya esta inscripto y el partido nuevo al cual
  //se quiere inscribir, esto para manejar mejor la logica de negocio y que un usuario pueda ingresar a varios partidos en
  //el mismo dia, solamente que tendra una diferencia horaria de al menos 3 horas para poder inscribirse.
  //Utilice ChronoUnit.HOURS para poder comparar la fecha del partido donde ya esta inscripto y la fecha del nuevo partido.
  //Luego hago una validacion donde utilizo el metodo abs de la clase Math para poder manejar los numeros positivos y negativos.
  //Esto para poder compararlo con la CONSTANTE de HORAS_MINIMAS_ENTRE_PARTIDOS que es el minimo de horas que tiene
  //que pasar para que un usuario pueda ingresar a otro partido.
  private void validarDiferenciaHoraria(
    LocalDateTime fechaYHoraExistente,
    LocalDateTime fechaHoraNuevoPartido
  ) {
    if (fechaYHoraExistente == null) {
      return;
    }

    long horasDeDiferencia = java.time.temporal.ChronoUnit.HOURS.between(
      fechaYHoraExistente,
      fechaHoraNuevoPartido
    );

    if (Math.abs(horasDeDiferencia) < HORAS_MINIMAS_ENTRE_PARTIDOS) {
      throw new RuntimeException(
        "No podes unirte, es necesario que haya al menos 3 horas de diferencia (" +
        fechaYHoraExistente.format(
          java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        ) +
        ")."
      );
    }
  }

  //Este metodo sirve para poder validar el horario para que el usuario pueda inscribirse, en este metodo
  //buscamos directamente los partidos del usuario, donde mandaremos todos los datos al momento de utilizar
  //validarDiferenciaHoraria para que haga la comparacion de las fechas.
  private void validarHorarioParaInscripcion(Partido partidoNuevo, Jugador usuarioLogueadoJugador) {
    List<Partido> partidosUsuarioJugador = repositorioPartido.buscarPartidosPorUsuario(
      usuarioLogueadoJugador.getUsuario().getId()
    );
    LocalDateTime fechaHoraNuevoPartido = partidoNuevo.getFecha();

    if (fechaHoraNuevoPartido == null) {
      return;
    }

    for (Partido partidoExistente : partidosUsuarioJugador) {
      validarDiferenciaHoraria(partidoExistente.getFecha(), fechaHoraNuevoPartido);
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

  @Override
  public void balancearEquipos(Partido partido) {
    List<InscripcionPartido> inscripciones = partido.getInscripciones();

    // Ordenar de mayor a menor segun puntos del jugador
    inscripciones.sort((i1, i2) -> Integer.compare(
        i2.getJugador().getPuntos(), 
        i1.getJugador().getPuntos()
    ));

    Equipo equipoLocal = partido.getEquipo1();
    Equipo equipoVisitante = partido.getEquipo2();

    // Reparto Snake Draft: los índices 0, 3, 4, 7, 8 van al Local
    for (int i = 0; i < inscripciones.size(); i++) {
      InscripcionPartido inscripcion = inscripciones.get(i);
      
      if (i % 4 == 0 || i % 4 == 3) {
        inscripcion.setEquipo(equipoLocal);
      } else {
        inscripcion.setEquipo(equipoVisitante);
      }
    }
    
  }
}
