package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.Cancha;
import com.tallerwebi.dominio.InscripcionPartido;
import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.Partido;
import com.tallerwebi.dominio.Partido.EstadoPartido;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.*;
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
  private ServicioJugador servicioJugador;
  private ServicioNotificacion servicioNotificacion;
  private ServicioSolicitudAmistad servicioSolicitudAmistad;
  private ServicioInvitacionPartido servicioInvitacionPartido;
  private final ServicioBalanceoEquipo servicioBalanceoEquipo;
  private static final int HORAS_MINIMAS_ENTRE_PARTIDOS = 3;
  private static final String MENSAJE_PARTIDO_NO_EXISTE = "El partido no existe.";

  @Autowired
  public ServicioPartidoImpl(
    RepositorioPartido repositorioPartido,
    RepositorioCancha repositorioCancha,
    RepositorioUsuario repositorioUsuario,
    ServicioJugador servicioJugador,
    ServicioBalanceoEquipo servicioBalanceoEquipo,
    ServicioNotificacion servicioNotificacion,
    ServicioSolicitudAmistad servicioSolicitudAmistad,
    ServicioInvitacionPartido servicioInvitacionPartido
  ) {
    this.repositorioCancha = repositorioCancha;
    this.repositorioPartido = repositorioPartido;
    this.repositorioUsuario = repositorioUsuario;
    this.servicioJugador = servicioJugador;
    this.servicioBalanceoEquipo = servicioBalanceoEquipo;
    this.servicioNotificacion = servicioNotificacion;
    this.servicioSolicitudAmistad = servicioSolicitudAmistad;
    this.servicioInvitacionPartido = servicioInvitacionPartido;
  }

  //cambiar los runtime a por excepcion por ser
  @Override
  public void crearPartido(DatosCrearPartido datos, Usuario creador) {
    if (datos.getCupo() == null || datos.getCupo() <= 0) {
      throw new CupoInvalidoException("El cupo debe ser mayor a cero.");
    }

    if (datos.getFechaHora() == null) {
      throw new FechaPartidoInvalidaException("Debe ingresar una fecha y hora.");
    }

    Cancha cancha = repositorioCancha.buscarPorNombre(datos.getCancha());

    if (cancha == null) {
      throw new CanchaNoEncontradaException("La cancha ingresada no existe.");
    }

    validarCanchaDisponible(cancha, datos.getFechaHora());

    Partido partido = new Partido();

    partido.setCancha(cancha);
    partido.setFecha(datos.getFechaHora());
    partido.setCupoMaximo(datos.getCupo());
    partido.setNivel(datos.getNivel());
    partido.setOrganizador(creador);
    partido.setEsPrivado(datos.getEsPrivado());
    partido.setEstado(EstadoPartido.PENDIENTE);

    if (Boolean.TRUE.equals(datos.getEsPrivado())) {
      partido.setCodigoDeAcceso(UUID.randomUUID().toString().substring(0, 8));
    }

    repositorioPartido.guardar(partido);
  }

  private void validarCanchaDisponible(Cancha cancha, LocalDateTime fechaHora) {
    LocalDateTime inicioVentana = fechaHora.minusHours(1);
    LocalDateTime finVentana = fechaHora.plusHours(1);

    List<Partido> partidosEnLaCancha = repositorioPartido.buscarPartidosEnRango(
      cancha.getId(),
      inicioVentana,
      finVentana
    );

    if (!partidosEnLaCancha.isEmpty()) {
      throw new CanchaNoDisponibleException(
        "La cancha no está disponible en esa franja horaria (los partidos duran 1 hora)."
      );
    }
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
    if (partido == null) throw new RuntimeException("El partido no existe.");

    if (
      partido.getOrganizador() == null ||
      !partido.getOrganizador().getId().equals(usuarioLogueado.getId())
    ) {
      throw new RuntimeException("No estas autorizado para cancelar este partido.");
    }

    if (partido.getEstado() == Partido.EstadoPartido.FINALIZADO) {
      throw new RuntimeException("No se puede cancelar un partido que ya finalizo.");
    }

    partido.cancelarPartido();
    repositorioPartido.modificar(partido);

    List<Usuario> jugadores = partido.getConfirmados();

    for (Usuario jugador : jugadores) {
      servicioNotificacion.notificarCancelacion(jugador, partido);
    }
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
      throw new PartidoNoEncontrado("El partido" + partidoId + " no existe.");
    }

    validarEstadoDelPartido(partido);
    validarRangoJugador(partido, usuarioLogueadoJugador); // Axel sprint 4
    validarCuposYDisponibilidad(partido, usuarioLogueadoJugador);
    validarHorarioParaInscripcion(partido, usuarioLogueadoJugador);

    InscripcionPartido nuevaInscripcion = new InscripcionPartido(partido, usuarioLogueadoJugador);

    nuevaInscripcion.setFechaInscripcion(LocalDateTime.now());

    partido.getInscripciones().add(nuevaInscripcion);
    partido.setCantJugadores(partido.getInscripciones().size());

    // Disparador del balanceo automático
    int cupoMaximo = partido.getCupoMaximo();
    if (partido.getCantJugadores() == cupoMaximo) {
      servicioBalanceoEquipo.balancearEquipos(partido);
      servicioBalanceoEquipo.asignarCapitanes(partido);
      partido.setEstado(EstadoPartido.ACTIVO);
    }

    repositorioPartido.modificar(partido);
  }

  // Axel sprint 4
  private void validarRangoJugador(Partido partido, Jugador jugador) {
    String categoriaJugador = obtenerCategoriaRango(jugador.getNivel());
    String categoriaPartido = obtenerCategoriaRango(partido.getNivel());

    if (categoriaJugador == null || categoriaPartido == null) {
      throw new RangoJugadorIncompatibleException(
        "El rango del jugador o del partido no es válido."
      );
    }

    if (!categoriaJugador.equals(categoriaPartido)) {
      throw new RangoJugadorIncompatibleException(
        "No podes unirte a este partido porque no corresponde a tu rango."
      );
    }
  }

  private String obtenerCategoriaRango(String nivel) {
    if (nivel == null) {
      return null;
    }

    String rango = nivel.trim().toUpperCase(Locale.ROOT);

    if (esRangoAvanzado(rango)) {
      return "AVANZADO";
    }

    if (esRangoIntermedio(rango)) {
      return "INTERMEDIO";
    }

    if (esRangoBajo(rango)) {
      return "BAJO";
    }

    return null;
  }

  private boolean esRangoAvanzado(String rango) {
    return Set.of("SS", "S", "AVANZADO").contains(rango);
  }

  private boolean esRangoIntermedio(String rango) {
    return Set.of("A", "B", "INTERMEDIO").contains(rango);
  }

  private boolean esRangoBajo(String rango) {
    return Set.of("C", "D", "BAJO", "PRINCIPIANTE").contains(rango);
  }

  //Metodo privado que valida el estado del partido, con esto logro ver si el partido tiene estado Finalizado o Cancelado
  //si es asi, entonces el usuario no podra ingresar al partido.
  private void validarEstadoDelPartido(Partido partido) {
    if (
      partido.getEstado() != EstadoPartido.PENDIENTE && partido.getEstado() != EstadoPartido.ACTIVO
    ) {
      throw new EstadoPartidoInvalido("No se puede unir a un partido finalizado o cancelado.");
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
      .anyMatch(i -> i.getJugador().getId().equals(usuarioLogueadoJugador.getId()));

    if (inscripto) {
      throw new JugadorInscripto("Ya estas anotado en la sala de espera de este partido.");
    }

    if (
      partido.getCupoMaximo() != null &&
      partido.getInscripciones().size() >= partido.getCupoMaximo()
    ) {
      throw new CupoMaximoAlcanzado("Cupo maximo alcanzado.");
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
      throw new DiferenciaHoraria(
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
    List<Partido> partidosUsuarioJugador = repositorioPartido.buscarPartidosPorJugador(
      usuarioLogueadoJugador.getId()
    );
    LocalDateTime fechaHoraNuevoPartido = partidoNuevo.getFecha();

    if (fechaHoraNuevoPartido == null) {
      return;
    }

    for (Partido partidoExistente : partidosUsuarioJugador) {
      validarDiferenciaHoraria(partidoExistente.getFecha(), fechaHoraNuevoPartido);
    }
  }

  @Override
  public void invitarJugador(Long idPartido, Long idJugador, Usuario usuarioQueInvita)
    throws InvitacionAlPartidoException {
    servicioInvitacionPartido.invitarJugador(idPartido, idJugador, usuarioQueInvita);
  }

  @Override
  public List<Partido> buscarPartidosPorJugador(Long idJugador) {
    return repositorioPartido.buscarPartidosPorJugador(idJugador);
  }

  @Override
  public void abandonarPartido(Long partidoId, Jugador jugador) {
    Partido partido = obtenerPartidoValidado(partidoId);
    validarPartidoModificable(
      partido,
      "No puedes abandonar un partido que ya está en curso o finalizado."
    );
    InscripcionPartido inscripcion = obtenerInscripcionJugador(
      partido,
      jugador.getId(),
      "No estás inscripto en este partido."
    );
    removerInscripcionYGuardar(partido, inscripcion);
  }

  @Override
  public void expulsarJugador(Long partidoId, Long idJugadorAExpulsar, Usuario organizador) {
    Partido partido = obtenerPartidoValidado(partidoId);
    validarOrganizador(partido, organizador);
    validarPartidoModificable(
      partido,
      "No puedes expulsar jugadores de un partido que ya está en curso o finalizado."
    );
    InscripcionPartido inscripcion = obtenerInscripcionJugador(
      partido,
      idJugadorAExpulsar,
      "El jugador no está inscripto en este partido."
    );
    removerInscripcionYGuardar(partido, inscripcion);
  }

  private Partido obtenerPartidoValidado(Long partidoId) {
    Partido partido = buscarPartidoPorId(partidoId);
    if (partido == null) throw new RuntimeException(MENSAJE_PARTIDO_NO_EXISTE);
    return partido;
  }

  private void validarOrganizador(Partido partido, Usuario organizador) {
    if (
      partido.getOrganizador() == null ||
      !partido.getOrganizador().getId().equals(organizador.getId())
    ) {
      throw new RuntimeException("Solo el organizador puede expulsar jugadores.");
    }
  }

  private void validarPartidoModificable(Partido partido, String mensajeError) {
    if (partido.getEstado() != EstadoPartido.PENDIENTE) {
      throw new RuntimeException(mensajeError);
    }
  }

  private InscripcionPartido obtenerInscripcionJugador(
    Partido partido,
    Long idJugador,
    String mensajeError
  ) {
    if (partido.getInscripciones() == null) throw new RuntimeException(mensajeError);
    return partido
      .getInscripciones()
      .stream()
      .filter(i -> i.getJugador().getId().equals(idJugador))
      .findFirst()
      .orElseThrow(() -> new RuntimeException(mensajeError));
  }

  private void removerInscripcionYGuardar(Partido partido, InscripcionPartido inscripcion) {
    partido.getInscripciones().remove(inscripcion);
    partido.setCantJugadores(partido.getInscripciones().size());
    repositorioPartido.modificar(partido);
  }
}
